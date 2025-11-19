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

import com.alibaba.cloud.ai.application.entity.dashscope.ChatResponseDTO;
import com.alibaba.cloud.ai.application.enums.WebSearchEnum;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Service
public class SAADashScopeWebSearchService implements ISAAWebSearchService {

    private final ChatClient chatClient;

    private final SimpleLoggerAdvisor simpleLoggerAdvisor;

    public SAADashScopeWebSearchService(
            SimpleLoggerAdvisor simpleLoggerAdvisor,
            @Qualifier("dashScopeChatModel")ChatModel chatModel)
    {
        this.simpleLoggerAdvisor = simpleLoggerAdvisor;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(simpleLoggerAdvisor)
                .build();
    }

    @Override
    public WebSearchEnum type() {
        return WebSearchEnum.DashScope;
    }

    public Flux<ChatResponseDTO> chat(String prompt) {
        var searchOptions = DashScopeApiSpec.SearchOptions.builder()
                .forcedSearch(true)
                .enableSource(true)
                .searchStrategy("pro")
                .enableCitation(true)
                .citationFormat("[<number>]")
                .build();

        var options = DashScopeChatOptions.builder()
                .withEnableSearch(true)
                .withModel(DashScopeModel.ChatModel.DEEPSEEK_V3.getValue())
                .withSearchOptions(searchOptions)
                .withTemperature(0.7)
                .build();

        // todo: 优化下直接用 stream 返回
        return Flux.defer(() -> {
            // Call the chat client and retrieve the chat response
            ChatResponse chatResponse = this.chatClient
                    .prompt(new Prompt(prompt, options))
                    .advisors(simpleLoggerAdvisor)
                    .call()
                    .chatResponse();

            // Extract the result and metadata
            String llmRes = chatResponse.getResult().getOutput().getText();
            var searchInfo = chatResponse.getResult().getOutput().getMetadata().get("search_info");

            // Create and return a new ChatResponseDTO object wrapped in a Flux
            return Flux.just(new ChatResponseDTO(llmRes, searchInfo));
        });
    }

}
