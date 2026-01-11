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

import com.alibaba.cloud.ai.application.advisor.ReasoningContentAdvisor;
import com.alibaba.cloud.ai.application.entity.dashscope.ChatResponseDTO;
import com.alibaba.cloud.ai.application.enums.WebSearchEnum;
import com.alibaba.cloud.ai.application.modulerag.WebSearchRetriever;
import com.alibaba.cloud.ai.application.modulerag.core.IQSSearchEngine;
import com.alibaba.cloud.ai.application.modulerag.data.DataClean;
import com.alibaba.cloud.ai.application.modulerag.join.ConcatenationDocumentJoiner;
import com.alibaba.cloud.ai.application.modulerag.prompt.CustomContextQueryAugmenter;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Service
public class SAAModuleRagWebSearchService implements ISAAWebSearchService {

    private final DataClean dataCleaner;

    private final ChatClient chatClient;

    private final QueryExpander queryExpander;

    private final QueryTransformer queryTransformer;

    private final WebSearchRetriever webSearchRetriever;

    private final SimpleLoggerAdvisor simpleLoggerAdvisor;

    private final PromptTemplate queryArgumentPromptTemplate;

    private final ReasoningContentAdvisor reasoningContentAdvisor;

    public SAAModuleRagWebSearchService(
            DataClean dataCleaner,
            QueryExpander queryExpander,
            IQSSearchEngine searchEngine,
            QueryTransformer queryTransformer,
            SimpleLoggerAdvisor simpleLoggerAdvisor,
            @Qualifier("dashScopeChatModel") ChatModel chatModel,
            @Qualifier("queryArgumentPromptTemplate") PromptTemplate queryArgumentPromptTemplate
    ) {

        this.dataCleaner = dataCleaner;
        this.queryTransformer = queryTransformer;
        this.queryExpander = queryExpander;
        this.queryArgumentPromptTemplate = queryArgumentPromptTemplate;

        // reasoning content for DeepSeek-r1 is integrated into the output
        this.reasoningContentAdvisor = new ReasoningContentAdvisor(1);

        // Build chatClient
        this.chatClient = ChatClient.builder(chatModel)
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withModel(DashScopeModel.ChatModel.DEEPSEEK_R1.getValue())
                                // stream 模式下是否开启增量输出
                                .withIncrementalOutput(true)
                                .build()
                ).build();

        this.simpleLoggerAdvisor = simpleLoggerAdvisor;

        this.webSearchRetriever = WebSearchRetriever.builder()
                .searchEngine(searchEngine)
                .dataCleaner(dataCleaner)
                .maxResults(2)
                .build();
    }


    @Override
    public WebSearchEnum type() {
        return WebSearchEnum.ModuleRag;
    }

    @Override
    public Flux<ChatResponseDTO> chat(String prompt) {

        return chatClient.prompt()
                .advisors(
                        createRetrievalAugmentationAdvisor(),
                        reasoningContentAdvisor,
                        simpleLoggerAdvisor
                ).user(prompt)
                .stream().chatResponse().map(chatResponse
                        -> new ChatResponseDTO(chatResponse.getResult().getOutput().getText())
                );
    }

    private RetrievalAugmentationAdvisor createRetrievalAugmentationAdvisor() {

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(webSearchRetriever)
                .queryTransformers(queryTransformer)
                .queryAugmenter(
                        new CustomContextQueryAugmenter(
                                queryArgumentPromptTemplate,
                                null,
                                true)
                ).queryExpander(queryExpander)
                .documentJoiner(new ConcatenationDocumentJoiner())
                .build();
    }

}
