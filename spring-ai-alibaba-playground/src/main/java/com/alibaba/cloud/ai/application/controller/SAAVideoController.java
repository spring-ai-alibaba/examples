/*
 * Copyright 2024-2026 the original author or authors.
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

package com.alibaba.cloud.ai.application.controller;

import com.alibaba.cloud.ai.application.service.SAAVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;

/**
 * @author huangzhen
 */
@RestController
@Tag(name = "视频问答 APIs")
@RequestMapping("/api/v1")
public class SAAVideoController {

    private final SAAVideoService videoService;

    public SAAVideoController(SAAVideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * 视频问答接口
     * @param prompt 用户问题（可选）
     * @param video 上传的视频文件（必传）
     * @return 视频内容分析结果（流式返回）
     */
    @PostMapping("/video-qa")
    @Operation(summary = "基于视频内容的问答接口")
    public Flux<String> videoQuestionAnswering(
            @Validated @RequestParam(value = "prompt", required = false, defaultValue = "请总结这个视频的主要内容") String prompt,
            @NotNull @RequestParam("video") MultipartFile video
    ) {

        // 验证视频文件
        if (video.isEmpty()) {
            return Flux.just("错误：请上传有效的视频文件");
        }

        try {
            // 调用视频分析服务
            String analyzeVideo = videoService.analyzeVideo(prompt, video);
            return Flux.just(analyzeVideo);
        } catch (Exception e) {
            return Flux.just("视频处理失败：" + e.getMessage());
        }
    }

    /**
     * 此处只使用最简单的文生视频接口，更多 example 查阅：
     * <a href="https://github.com/springaialibaba/spring-ai-alibaba-examples/tree/main/spring-ai-alibaba-video-example">Video Example</a>
     *
     * @return video url link
     */
    @GetMapping("/video-gen")
    @Operation(summary = "视频生成接口")
    public String genVideo(String prompt) {

        return videoService.genVideo(prompt);
    }

}
