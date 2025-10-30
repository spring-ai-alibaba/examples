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

package com.alibaba.cloud.ai.example.chat.deepseek.controller;

import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 北极星
 */
@RestController
@RequestMapping("/client")
public class DeepSeekChatClientController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    private final ChatClient DeepSeekChatClient;

    public DeepSeekChatClientController (DeepSeekChatModel chatModel) {

        this.DeepSeekChatClient = ChatClient.builder(chatModel).defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                // 实现 Logger 的 Advisor
                .defaultAdvisors(new SimpleLoggerAdvisor())
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(DeepSeekChatOptions.builder().temperature(0.7d).build()).build();
    }

    /**
     * 使用自定义参数调用DeepSeek模型
     *
     * @return ChatResponse 包含模型响应结果的封装对象
     * @apiNote 当前硬编码指定模型为deepseek-chat，温度参数0.7以平衡生成结果的创造性和稳定性
     */
    @GetMapping(value = "/ai/customOptions")
    public ChatResponse customOptions () {

        return this.DeepSeekChatClient.prompt(new Prompt(
                "Generate the names of 5 famous pirates.",
                        DeepSeekChatOptions.builder().temperature(0.75).build())
                ).call()
                .chatResponse();
    }

    /**
     * 执行默认提示语的 AI 生成请求
     */
    @GetMapping("/ai/generate")
    public String chat () {

        return this.DeepSeekChatClient.prompt(DEFAULT_PROMPT)
                .call()
                .content();
    }

    /**
     * 流式生成接口 - 支持实时获取生成过程的分块响应
     */
    @GetMapping("/ai/stream")
    public Flux<String> stream (HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        return this.DeepSeekChatClient.prompt(DEFAULT_PROMPT)
                .stream()
                .content();
    }
}
