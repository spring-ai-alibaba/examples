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

package com.alibaba.example.multimodelchat.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;

import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @Description:  Multi-model request handler
 * @Author: xiaoyuntao
 * @Date: 2025/3/14
 */
@RestController
@CrossOrigin
public class MultiModelChatController {

    private final ChatClient ollamaChatClient;

    private final ChatClient dashScopeChatClient;

    public MultiModelChatController(OllamaChatModel ollamaChatModel, DashScopeChatModel dashScopeChatModel) {
        this.ollamaChatClient = ChatClient.builder(ollamaChatModel).build();
        this.dashScopeChatClient = ChatClient.builder(dashScopeChatModel).build();
    }

    /**
     * Streams responses from two large models simultaneously using Server-Sent Events (SSE).
     *
     * @param chatRequest The user input prompt and conversation ID
     * @param httpResponse The HTTP response object, used to set the character encoding to prevent garbled text
     * @return A merged SSE stream containing responses from both models
     */
    @PostMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(
            @RequestBody ChatRequest chatRequest,
            HttpServletResponse httpResponse) {
        String prompt = chatRequest.getPrompt();
        String conversationId = chatRequest.getConversationId();

        // Set response character encoding to avoid garbled text
        httpResponse.setCharacterEncoding("UTF-8");

        // Retrieve response streams from both models
        Flux<String> ollamaStream = ollamaChatClient.prompt()
                .user(prompt)
                .advisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .advisors(memoryAdvisor -> memoryAdvisor
                        .param(CONVERSATION_ID, conversationId)
                        .param(VectorStoreChatMemoryAdvisor.TOP_K, 100))
                .stream().content();

        Flux<String> dashScopeStream = dashScopeChatClient.prompt()
                .user(prompt)
                .advisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .advisors(memoryAdvisor -> memoryAdvisor
                        .param(CONVERSATION_ID, conversationId)
                        .param(VectorStoreChatMemoryAdvisor.TOP_K, 100))
                .stream().content();

        // Wrap each stream in SSE events with source identifiers
        Flux<ServerSentEvent<String>> ollamaSseStream = ollamaStream
                .doOnNext(content -> System.out.println("ollama: " + content))
                .map(content -> ServerSentEvent.builder(content)
                        .event("ollama")
                        .build());

        Flux<ServerSentEvent<String>> dashScopeSseStream = dashScopeStream
                .doOnNext(content -> System.out.println("dashScopeSseStream: " + content))
                .map(content -> ServerSentEvent.builder(content)
                        .event("dashscope")
                        .build());

        // Merge both event streams and return as a single SSE response
        return Flux.merge(ollamaSseStream, dashScopeSseStream);
    }

    public static class ChatRequest {
        private String prompt;
        private String conversationId;

        // Getters and Setters
        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }
    }
}
