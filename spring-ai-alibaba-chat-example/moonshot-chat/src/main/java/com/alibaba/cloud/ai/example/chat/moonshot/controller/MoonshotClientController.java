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
    import org.springframework.ai.chat.messages.UserMessage;
    import org.springframework.ai.chat.model.ChatResponse;
    import org.springframework.ai.chat.prompt.Prompt;
    import org.springframework.ai.moonshot.MoonshotChatModel;
    import org.springframework.ai.moonshot.MoonshotChatOptions;
    import org.springframework.ai.moonshot.api.MoonshotApi;
    import org.springframework.beans.factory.annotation.Value;
    import reactor.core.publisher.Flux;

    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    /**
     * @author kuilz
     * @date 2025/1/19
     */
    @RestController
    @RequestMapping("/client")
    public class MoonshotClientController {
        private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。";

        private final MoonshotChatModel moonshotChatModel;

        public MoonshotClientController(@Value("${spring.ai.moonshot.api-key}") String apiKey) {
            MoonshotApi api = MoonshotApi.builder()
                .apiKey(apiKey)
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
         * Moonshot的 简单调用
         */
        @GetMapping("/simple/chat")
        public String simpleChat() {
            Prompt prompt = new Prompt(DEFAULT_PROMPT);
            ChatResponse response = moonshotChatModel.call(prompt);
            return response.getResult().getOutput().getText();
        }

        /**
         * Moonshot的 流式调用
         */
        @GetMapping("/stream/chat")
        public Flux<String> streamChat(HttpServletResponse response) {
            response.setCharacterEncoding("UTF-8");
            Prompt prompt = new Prompt(new UserMessage(DEFAULT_PROMPT));
            return moonshotChatModel.stream(prompt)
                .map(chatResponse -> chatResponse.getResult().getOutput().getText());
        }
    }
