/*
 * Copyright 2026-2027 the original author or authors.
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

package com.alibaba.cloud.ai.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author wangx
 */

@RestController
@RequestMapping("/minimax/chat-model")
public class MiniMaxChatModelController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。";

    private final ChatModel chatModel;

    public MiniMaxChatModelController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 最简单的使用方式，没有任何 LLMs 参数注入。
     *
     * @return String types.
     */
    @GetMapping("/simple/chat")
    public String simpleChat(@RequestParam(value = "message", defaultValue = DEFAULT_PROMPT) String message) {

        return chatModel.call(new Prompt(message)).getResult().getOutput().getText();
    }

    /**
     * Stream 流式调用。可以使大模型的输出信息实现打字机效果。
     *
     * @return Flux<String> types.
     */
    @GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
            @RequestParam(value = "message", defaultValue = DEFAULT_PROMPT) String message,
            HttpServletResponse response) {

        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");

        return chatModel.stream(new Prompt(message))
                .mapNotNull(resp -> {
                    if (resp.getResult() == null || resp.getResult().getOutput() == null) {
                        return null;
                    }
                    return resp.getResult().getOutput().getText();
                });
    }

    /**
     * 使用编程方式自定义 LLMs ChatOptions 参数， {@link org.springframework.ai.openai.OpenAiChatOptions}
     * 优先级高于在 application.yml 中配置的 LLMs 参数！
     */
    @GetMapping("/custom/chat")
    public String customChat(@RequestParam(value = "message", defaultValue = DEFAULT_PROMPT) String message) {

        OpenAiChatOptions customOptions = OpenAiChatOptions.builder()
                .model("MiniMax-M2.7")
                .maxTokens(1000)
                .temperature(0.8)
                .build();

        return chatModel.call(new Prompt(message, customOptions)).getResult().getOutput().getText();
    }

}
