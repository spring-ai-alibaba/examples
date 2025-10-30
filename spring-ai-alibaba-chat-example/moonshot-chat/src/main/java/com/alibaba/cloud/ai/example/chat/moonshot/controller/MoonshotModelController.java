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

package com.alibaba.cloud.ai.example.chat.moonshot.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.moonshot.MoonshotChatModel;
import org.springframework.ai.moonshot.api.MoonshotApi;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.moonshot.MoonshotChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kuilz
 * @date 2025/1/19
 */
@RestController
@RequestMapping("/moonshot/chat-model")
public class MoonshotModelController {
    private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。";

    private final ChatModel moonshotChatModel;

    public MoonshotModelController(
        @Value("${spring.ai.moonshot.api-key}") String apikey,
        @Value("${spring.ai.moonshot.base-url}") String baseUrl
    ) {
        MoonshotApi api = MoonshotApi.builder()
            .baseUrl(baseUrl)
            .apiKey(apikey)
            .build();

        MoonshotChatOptions defaultOptions = MoonshotChatOptions.builder()
            .topP(0.8)
            .temperature(0.8)
            .model(MoonshotApi.ChatModel.MOONSHOT_V1_8K.getValue())
            .build();

        this.moonshotChatModel = MoonshotChatModel
            .builder()
            .moonshotApi(api)
            .defaultOptions(defaultOptions)
            .build();
    }

    /**
     * ChatModel 简单调用
     */
    @GetMapping("/simple/chat")
    public String simpleChat() {
        return moonshotChatModel.call(new Prompt(DEFAULT_PROMPT)).getResult().getOutput().getText();
    }

    /**
     * ChatModel 流式调用
     */
    @GetMapping("/stream/chat")
    public Flux<String> streamChat(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");

        Flux<ChatResponse> chatResponseFlux = moonshotChatModel.stream(new Prompt(DEFAULT_PROMPT));
        return chatResponseFlux
                .flatMap(resp -> {
                    String content = null;
                    if (resp.getResult() != null && resp.getResult().getOutput() != null) {
                        content = resp.getResult().getOutput().getText();
                    }
                    return content != null ? Mono.just(content) : Mono.empty();  // 如果 content 为 null，返回 empty
                });
    }

    /**
     * 使用编程方式自定义 LLMs ChatOptions 参数， {@link org.springframework.ai.moonshot.MoonshotChatOptions}
     * 优先级高于在 application.yml 中配置的 LLMs 参数！
     */
    @GetMapping("/custom/chat")
    public String customChat() {
        MoonshotChatOptions moonshotChatOptions = MoonshotChatOptions.builder()
                .model("moonshot-v1-8k")
                .topP(0.8)
                .temperature(0.8)
                .build();

        return moonshotChatModel.call(new Prompt(DEFAULT_PROMPT, moonshotChatOptions)).getResult().getOutput().getText();
    }
}
