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

package com.alibaba.cloud.ai.example.controller;

import com.alibaba.cloud.ai.example.controller.helper.FrameExtraHelper;
import org.apache.catalina.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ark Multi-Model REST Controller
 * 提供聊天、图片生成、文本向量等多个模型能力的API接口
 *
 * @author brian xiadong
 */
@RestController
@RequestMapping("/api")
public class MultiModelController {

    private static final String DEFAULT_PROMPT = "这些是什么？";

    private static final String DEFAULT_VIDEO_PROMPT = "这是一组从视频中提取的图片帧，请描述此视频中的内容。";


    @Autowired
    private ChatModel chatModel;

    private ChatClient openAiChatClient;

    public MultiModelController(ChatModel chatModel) {

        this.chatModel = chatModel;

        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.openAiChatClient = ChatClient.builder(chatModel)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build()
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .topP(0.7)
                                .build()
                )
                .build();
    }

    @GetMapping("/image")
    public String image(
            @RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_PROMPT)
            String prompt
    ) throws Exception {

        List<Media> mediaList = List.of(
                new Media(
                        MimeTypeUtils.IMAGE_PNG,
						new URI("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg").toURL()
								.toURI()
                )
        );

        UserMessage message = UserMessage.builder().text(prompt).media(mediaList).build();

        ChatResponse response = openAiChatClient.prompt(
                new Prompt(
                        message
                )
        ).call().chatResponse();

        return response.getResult().getOutput().getText();
    }

    @GetMapping("/stream/image")
    public String streamImage(
            @RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_PROMPT)
            String prompt
    ) {

        UserMessage message = UserMessage.builder().text(prompt).media(
                new Media(
                        MimeTypeUtils.IMAGE_JPEG,
                        new ClassPathResource("multimodel/dog_and_girl.jpeg")
                )).build();

        List<ChatResponse> response = openAiChatClient.prompt(
                new Prompt(
                        message
                )
        ).stream().chatResponse().collectList().block();

        StringBuilder result = new StringBuilder();
        if (response != null) {
            for (ChatResponse chatResponse : response) {
                String outputContent = chatResponse.getResult().getOutput().getText();
                result.append(outputContent);
            }
        }

        return result.toString();
    }

    @GetMapping("/video")
    public String video(
            @RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_VIDEO_PROMPT)
            String prompt
    ) {

        List<Media> mediaList = FrameExtraHelper.createMediaList(10);

        UserMessage message = UserMessage.builder().text(prompt).media(mediaList).build();

        ChatResponse response = openAiChatClient.prompt(
                new Prompt(
                        message
                )
        ).call().chatResponse();

        return response.getResult().getOutput().getText();
    }
}

