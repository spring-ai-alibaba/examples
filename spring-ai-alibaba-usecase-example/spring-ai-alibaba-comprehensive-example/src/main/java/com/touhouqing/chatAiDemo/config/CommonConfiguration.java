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

package com.touhouqing.chatAiDemo.config;

import com.touhouqing.chatAiDemo.constants.SystemConstants;
import com.touhouqing.chatAiDemo.tools.CourseTools;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

@Configuration
public class CommonConfiguration {

    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(DashScopeChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultOptions(DashScopeChatOptions.builder().withModel("qwen-omni-turbo-latest").withMultiModel(true).build())
                .defaultSystem("你是一个可爱的傻白甜萝莉，你会用可爱的语言和我聊天。")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @Bean
    public ChatClient gameChatClient(DashScopeChatModel model, ChatMemory chatMemory) {
        return ChatClient
               .builder(model)
               .defaultSystem(SystemConstants.GAME_SYSTEM_PROMPT)
               .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
               )
               .build();
    }

    @Bean
    public ChatClient serviceChatClient(DashScopeChatModel model, ChatMemory chatMemory, CourseTools  courseTools) {
        return ChatClient
               .builder(model)
               .defaultSystem(SystemConstants.SERVICE_SYSTEM_PROMPT)
               .defaultTools(courseTools)
               .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
               )
               .build();
    }

    /*
     * @description 针对PDF问答的ChatClient
     */
    @Bean
    public ChatClient pdfChatClient(DashScopeChatModel model, ChatMemory chatMemory, VectorStore vectorStore) {
        return ChatClient
               .builder(model)
               .defaultSystem("你是一个友好且知识渊博的AI助手。基于提供的上下文信息来回答问题，如果上下文中没有相关信息，请明确告知用户。")
               .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(10)
                                        .similarityThreshold(0.2)
                                        .build())
                                .build()
               )
               .build();
    }

}
