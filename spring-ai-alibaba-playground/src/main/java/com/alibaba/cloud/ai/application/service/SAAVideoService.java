/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.application.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.dashscope.video.VideoModel;
import com.alibaba.cloud.ai.dashscope.video.VideoPrompt;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author huangzhen
 */
@Service
public class SAAVideoService  {

    private final VideoModel videoModel;

    private static final String TEMP_IMAGE_DIR = "temp-frames";

    private final ChatClient daschScopeChatClient;

    public SAAVideoService(
            VideoModel videoModel,
            @Qualifier("dashScopeChatModel") ChatModel chatModel
    ) {

        this.videoModel = videoModel;
        this.daschScopeChatClient = ChatClient
                .builder(chatModel)
                .build();
    }

    /**
     * 分析视频内容并回答用户问题
     * @param prompt 用户问题
     * @param videoFile 上传的视频文件
     * @return AI分析结果
     */
    public String analyzeVideo(String prompt, MultipartFile videoFile) throws Exception {
        // 1. 验证视频格式
        if (!isSupportedFormat(videoFile)) {
            throw new IllegalArgumentException("不支持的视频格式");
        }

        // 2. 保存视频到临时文件
        Path tempVideoPath = Paths.get(System.getProperty("java.io.tmpdir"), videoFile.getOriginalFilename());
        videoFile.transferTo(tempVideoPath.toFile());

        // 3. 从视频中提取10帧
        List<File> frames = extractFrames(tempVideoPath.toFile(), 10);

        // 4. 准备AI分析所需的媒体列表
        List<Media> mediaList = new ArrayList<>();
        for (File frame : frames) {
            mediaList.add(new Media(
                    MimeTypeUtils.IMAGE_PNG,
                    new FileSystemResource(frame)
            ));
        }

        // 5. 创建包含问题和帧图片的用户消息
//        UserMessage message = new UserMessage(prompt, mediaList);
        UserMessage message =
                UserMessage.builder().text(prompt).media(mediaList).metadata(new HashMap<>()).build();

        // 6. 调用AI服务进行分析
        List<ChatResponse> response = daschScopeChatClient.prompt(
                        new Prompt(
                                message,
                                DashScopeChatOptions.builder()
                                        .withModel(DashScopeModel.ChatModel.QWEN_VL_MAX.getValue())
                                        .withMultiModel(true)
                                        .build()
                        ))
                .stream()
                .chatResponse()
                .collectList()
                .block();

        // 7. 处理并返回响应
        StringBuilder result = new StringBuilder();
        if (response != null) {
            for (ChatResponse chatResponse : response) {
                String outputContent = chatResponse.getResult().getOutput().getText();
                result.append(outputContent).append("\n");
            }
        }

        // 8. 清理临时文件
        cleanUpFiles(frames);
        Files.deleteIfExists(tempVideoPath);

        return result.toString();
    }

    /**
     * 从视频中提取指定数量的帧
     * @param videoFile 视频文件
     * @param frameCount 要提取的帧数
     * @return 提取的帧图片文件列表
     */
    private List<File> extractFrames(File videoFile, int frameCount) throws IOException {
        List<File> frames = new ArrayList<>();
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
        grabber.start();

        try {
            // 创建临时目录（如果不存在）
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), TEMP_IMAGE_DIR);
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }

            int totalFrames = grabber.getLengthInFrames();
            int step = totalFrames / frameCount; // 计算帧间隔

            Java2DFrameConverter converter = new Java2DFrameConverter();
            for (int i = 0; i < frameCount; i++) {
                int frameNumber = i * step; // 计算当前帧位置
                grabber.setFrameNumber(frameNumber);
                Frame frame = grabber.grabImage();
                BufferedImage image = converter.convert(frame);

                // 保存帧为PNG图片
                File outputFile = tempDir.resolve(UUID.randomUUID() + ".png").toFile();
                ImageIO.write(image, "png", outputFile);
                frames.add(outputFile);
            }
        } finally {
            grabber.stop();
        }

        return frames;
    }

    /**
     * 清理临时文件
     * @param files 要删除的文件列表
     */
    private void cleanUpFiles(List<File> files) {
        for (File file : files) {
            file.delete();
        }
    }

    /**
     * 检查视频格式是否支持
     * @param file 上传的文件
     * @return 是否支持
     */
    private boolean isSupportedFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.startsWith("video/mp4") ||
                        contentType.startsWith("video/quicktime"));
    }

    public String genVideo(String prompt) {

        if (Objects.isNull(prompt)) {
            prompt = "一只小猫在草地奔跑";
        }

        return this.videoModel.call(new VideoPrompt(prompt))
                .getResult()
                .getOutput()
                .getVideoUrl();
    }

}
