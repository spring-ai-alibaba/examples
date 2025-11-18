/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.example.rag.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.alibaba.cloud.ai.rag.preretrieval.transformation.HyDeTransformer;
import com.alibaba.cloud.ai.rag.retrieval.search.HyDeRetriever;
import com.alibaba.cloud.ai.rag.retrieval.search.HybridElasticsearchRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.ai.vectorstore.elasticsearch.autoconfigure.ElasticsearchVectorStoreProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * search configuration
 *
 * @author benym
 */
@Configuration
public class SearchConfig {

    @Bean
    public HybridElasticsearchRetriever hybridElasticsearchRetriever(ElasticsearchVectorStoreProperties vectorStoreProperties,
                                                                     ElasticsearchClient elasticsearchClient,
                                                                     EmbeddingModel embeddingModel) {
        ElasticsearchVectorStoreOptions elasticsearchVectorStoreOptions = new ElasticsearchVectorStoreOptions();
        elasticsearchVectorStoreOptions.setIndexName(vectorStoreProperties.getIndexName());
        elasticsearchVectorStoreOptions.setDimensions(vectorStoreProperties.getDimensions());
        elasticsearchVectorStoreOptions.setSimilarity(vectorStoreProperties.getSimilarity());
        elasticsearchVectorStoreOptions.setEmbeddingFieldName(vectorStoreProperties.getEmbeddingFieldName());
        return HybridElasticsearchRetriever.builder()
                .vectorStoreOptions(elasticsearchVectorStoreOptions)
                .elasticsearchClient(elasticsearchClient)
                .embeddingModel(embeddingModel)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(HyDeTransformer.class)
    public HyDeTransformer hyDeTransformer(ChatClient.Builder chatClientBuilder) {
        return HyDeTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(HyDeRetriever.class)
    public HyDeRetriever hyDeRetriever(HyDeTransformer hyDeTransformer, VectorStore vectorStore) {
        return HyDeRetriever.builder()
                .hyDeTransformer(hyDeTransformer)
                .vectorStore(vectorStore)
                .build();
    }
}
