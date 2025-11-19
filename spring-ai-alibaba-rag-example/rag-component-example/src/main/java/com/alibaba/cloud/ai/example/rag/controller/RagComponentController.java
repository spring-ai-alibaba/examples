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
package com.alibaba.cloud.ai.example.rag.controller;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeRerankProperties;
import com.alibaba.cloud.ai.model.RerankModel;
import com.alibaba.cloud.ai.rag.advisor.HybridSearchAdvisor;
import com.alibaba.cloud.ai.rag.advisor.MultiQueryRetrieverAdvisor;
import com.alibaba.cloud.ai.rag.postretrieval.DashScopeRerankPostProcessor;
import com.alibaba.cloud.ai.rag.preretrieval.transformation.HyDeTransformer;
import com.alibaba.cloud.ai.rag.retrieval.search.HyDeRetriever;
import com.alibaba.cloud.ai.rag.retrieval.search.HybridElasticsearchRetriever;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the spring ai alibaba rag component example controller
 *
 * @author benym
 */
@RestController
@RequestMapping("/rag/component")
public class RagComponentController {

    @Resource
    private HybridElasticsearchRetriever hybridRetriever;

    @Resource
    private HyDeRetriever hyDeRetriever;

    @Resource
    private HyDeTransformer hyDeTransformer;

    private final ChatClient.Builder chatClientBuilder;

    private final RerankModel rerankModel;

    private final DashScopeRerankProperties dashScopeRerankProperties;

    private final VectorStore vectorStore;

    public RagComponentController(ChatClient.Builder chatClientBuilder, RerankModel rerankModel, DashScopeRerankProperties dashScopeRerankProperties, VectorStore vectorStore) {
        this.chatClientBuilder = chatClientBuilder;
        this.rerankModel = rerankModel;
        this.dashScopeRerankProperties = dashScopeRerankProperties;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/retrieval/hybrid")
    public List<Document> retrievalHybrid() {
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .build();
        return hybridRetriever.retrieve(query);
    }

    @GetMapping("/retrieval/hyde")
    public List<Document> retrievalHyde() {
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .build();
        return hyDeRetriever.retrieve(query);
    }

    @GetMapping("/retrieval/hyde/filter")
    public List<Document> retrievalHydeWithFilter() {
        // (metadata.category == "技术文档" or metadata.category == "HybridSearch")
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression expression = builder.or(
                builder.eq("category", "技术文档"),
                builder.eq("category", "HybridSearch")
        ).build();
        Map<String, Object> context = new HashMap<>();
        context.put(HyDeRetriever.FILTER_EXPRESSION, expression);
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .context(context)
                .build();
        return hyDeRetriever.retrieve(query);
    }

    @GetMapping("/transform/hyde")
    public Query transformHyde() {
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .build();
        return hyDeTransformer.transform(query);
    }

    @GetMapping("/retrieval/hybrid/filter")
    public List<Document> retrievalHybridWithFilter() {
        // (metadata.category == "技术文档" or metadata.category == "HybridSearch") and metadata.word = "什么是hybridSearch"
        // expression会自动添加metadata
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression expression = builder.or(
                builder.eq("category", "技术文档"),
                builder.eq("category", "HybridSearch")
        ).build();
        Map<String, Object> context = new HashMap<>();
        context.put(HybridElasticsearchRetriever.FILTER_EXPRESSION, expression);
        context.put(HybridElasticsearchRetriever.BM25_FILED, "metadata.word");
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .context(context)
                .build();
        return hybridRetriever.retrieve(query);
    }

    @GetMapping("/retrieval/hybrid/esquery")
    public List<Document> retrievalHybridWithEsQuery() {
        Query query = new Query("什么是hybridSearch");
        // (metadata.category == "技术文档" or metadata.category == "其他") and metadata.word = "什么是hybridSearch"
        co.elastic.clients.elasticsearch._types.query_dsl.Query filterQuery = QueryBuilders.bool(b -> b
                .should(QueryBuilders.term(t -> t.field("metadata.category.keyword").value("技术文档")))
                .should(QueryBuilders.term(t -> t.field("metadata.category.keyword").value("其他")))
        ).bool()._toQuery();
        // for bm25 field
        co.elastic.clients.elasticsearch._types.query_dsl.Query textQuery =
                QueryBuilders.match(m -> m
                        .field("metadata.word")
                        .query("什么是hybridSearch")
                );
        return hybridRetriever.retrieve(query, filterQuery, textQuery);
    }

    @GetMapping("/rerank/documents")
    public List<Document> rerankDocuments() {
        List<Document> retrievalDocuments = vectorStore.similaritySearch("什么是hybridSearch");
        DashScopeRerankPostProcessor dashScopeRerankPostProcessor = DashScopeRerankPostProcessor.builder()
                .rerankModel(rerankModel)
                .rerankOptions(dashScopeRerankProperties.getOptions())
                .build();
        return dashScopeRerankPostProcessor.process(Query.builder().text("什么是hybridSearch").build(),
                retrievalDocuments);
    }

    @GetMapping("/call/hybrid/advisor")
    public Flux<ChatResponse> callHybridAdvisor(@RequestParam(value = "message",
            defaultValue = "what is hybrid search?") String message) {
        // 1. transform
        QueryTransformer rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        CompressionQueryTransformer compressionQueryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        TranslationQueryTransformer translationQueryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .targetLanguage("Chinese")
                .build();
        List<QueryTransformer> queryTransformers = List.of(
                rewriteQueryTransformer,
                compressionQueryTransformer,
                translationQueryTransformer
        );
        // 2. expand
        MultiQueryExpander multiQueryExpander = MultiQueryExpander.builder()
                .numberOfQueries(3)
                .chatClientBuilder(chatClientBuilder)
                .build();
        // 3. rerank
        DashScopeRerankPostProcessor dashScopeRerankPostProcessor = DashScopeRerankPostProcessor.builder()
                .rerankModel(rerankModel)
                .rerankOptions(dashScopeRerankProperties.getOptions())
                .build();
        HybridSearchAdvisor hybridSearchAdvisor = HybridSearchAdvisor.builder()
                .queryTransformers(queryTransformers)
                .queryExpander(multiQueryExpander)
                .hyDeTransformer(hyDeTransformer)
                .hybridDocumentRetriever(hybridRetriever)
                .dashScopeRerankPostProcessor(dashScopeRerankPostProcessor)
                .build();
        Prompt prompt = Prompt.builder()
                .messages(new UserMessage(message))
                .build();
        return chatClientBuilder.build()
                .prompt(prompt)
                .advisors(hybridSearchAdvisor)
                .stream()
                .chatResponse();
    }

    @GetMapping("/call/multiquery/advisor")
    public Flux<ChatResponse> callMultiQueryRetrieverAdvisor(@RequestParam(value = "message",
            defaultValue = "what is hybrid search?") String message) {
        MultiQueryExpander multiQueryExpander = MultiQueryExpander.builder()
                .numberOfQueries(3)
                .chatClientBuilder(chatClientBuilder)
                .build();
        MultiQueryRetrieverAdvisor multiQueryRetrieverAdvisor = MultiQueryRetrieverAdvisor.builder()
                .queryExpander(multiQueryExpander)
                .documentRetriever(hybridRetriever)
                .build();
        Prompt prompt = Prompt.builder()
                .messages(new UserMessage(message))
                .build();
        return chatClientBuilder.build()
                .prompt(prompt)
                .advisors(multiQueryRetrieverAdvisor)
                .stream()
                .chatResponse();
    }
}
