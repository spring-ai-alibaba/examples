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

package com.alibaba.cloud.ai.example.evaluation.controller;

import com.alibaba.cloud.ai.evaluation.AnswerCorrectnessEvaluator;
import com.alibaba.cloud.ai.evaluation.AnswerFaithfulnessEvaluator;
import com.alibaba.cloud.ai.evaluation.AnswerRelevancyEvaluator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.Evaluator;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * AI模型评估示例 Controller
 *
 * @author xuguan
 * @since 2025/10/13
 */
@RestController
@RequestMapping("/ai/evaluation")
public class EvaluationController {
    private static final Logger log = LoggerFactory.getLogger(EvaluationController.class);
    private static final double SIMILARITY_THRESHOLD = 0.5d;
    private static final int TOP_K = 3;
    private final ChatClient.Builder chatClientBuilder;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;
    private final Advisor ragAdvisor;
    private final Advisor loggerAdvisor;

    public EvaluationController(ChatClient.Builder chatClientBuilder,
                                EmbeddingModel embeddingModel,
                                ObjectMapper objectMapper) {
        this.chatClientBuilder = chatClientBuilder;
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        this.objectMapper = objectMapper;
        this.ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .topK(TOP_K)
                        .build())
                .build();
        this.loggerAdvisor = SimpleLoggerAdvisor.builder().build();
    }

    @PostConstruct
    public void init() {
        var searchDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .filterExpression(new FilterExpressionBuilder().eq("title", "中国的首都").build())
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .topK(TOP_K)
                        .build()
        );
        if (CollectionUtils.isEmpty(searchDocuments)) {
            var content = """
                    中华人民共和国首都位于北京市，中华人民共和国成立前夕的旧称为北平，
                    是中共中央及中央人民政府所在地，中央四个直辖市之一，
                    全国政治、文化、国际交往和科技创新中心，中国古都、国家历史文化名城和国家中心城市之一。
                    """;
            var document = new Document(content, Map.of("title", "中国的首都"));
            vectorStore.add(List.of(document));
        }
    }

    /**
     * 相关性评估器, 用来评估AI生成的响应与提供的上下文的相关性.
     * 该评估器通过确定AI模型的响应是否与用户关于检索到的上下文的输入相关来帮助评估RAG流的质量.
     */
    @GetMapping("/sa/relevancy")
    public String saRelevancy(@RequestParam(value = "query", defaultValue = "中国的首都是哪里?") String query) {
        var ragChatResponse = ragChat(query);
        var context = ragChatResponse.documents();
        var response = ragChatResponse.response();

        var evaluator = RelevancyEvaluator.builder().chatClientBuilder(chatClientBuilder).build();
        var evaluationRequest = new EvaluationRequest(
                // Query
                query,
                // Context
                context,
                // Response
                response
        );
        var pass = evaluate(evaluator, evaluationRequest);

        return pass ? response : "暂无数据";
    }

    /**
     * 事实性评估器, 根据提供的上下文评估AI生成的响应的事实准确性.
     * 该评估器通过验证给定的陈述(claim)是否在逻辑上得到所提供的上下文(document)的支持, 来帮助检测和减少AI输出中的幻觉.
     */
    @GetMapping("/sa/fact-checking")
    public String saFactChecking(@RequestParam(value = "query", defaultValue = "中国的首都是哪里?") String query) {
        var ragChatResponse = ragChat(query);
        var document = ragChatResponse.documents();
        var claim = ragChatResponse.response();

        var evaluator = FactCheckingEvaluator.builder(chatClientBuilder).build();
        var evaluationRequest = new EvaluationRequest(
                // Document
                document,
                // Claim
                claim
        );
        var pass = evaluate(evaluator, evaluationRequest);

        return pass ? claim : "暂无数据";
    }

    /**
     * 评分评估器, 基于提供的评分标准和内容信息进行评分
     */
    @GetMapping("/saa/answer-relevancy")
    public String saaAnswerRelevancy(@RequestParam(value = "query", defaultValue = "中国的首都是哪里?") String query) {
        var ragChatResponse = ragChat(query);
        var truthAnswer = ragChatResponse.documents();
        var studentAnswer = ragChatResponse.response();

        var evaluator = new AnswerRelevancyEvaluator(chatClientBuilder, objectMapper);
        var evaluationRequest = new EvaluationRequest(
                // QUESTION
                query,
                // TRUTH ANSWER
                truthAnswer,
                // STUDENT ANSWER
                studentAnswer
        );
        var pass = evaluate(evaluator, evaluationRequest);

        return pass ? studentAnswer : "暂无数据";
    }

    /**
     * 正确性评估器, 评估Query返回的Response是否符合提供的Context信息
     */
    @GetMapping("/saa/answer-correctness")
    public String saaAnswerCorrectness(@RequestParam(value = "query", defaultValue = "中国的首都是哪里?") String query) {
        var ragChatResponse = ragChat(query);
        var context = ragChatResponse.documents();
        var response = ragChatResponse.response();

        var evaluator = new AnswerCorrectnessEvaluator(chatClientBuilder);
        var evaluationRequest = new EvaluationRequest(
                // Query
                query,
                // Context
                context,
                // Response
                response
        );
        var pass = evaluate(evaluator, evaluationRequest);

        return pass ? response : "暂无数据";
    }

    /**
     * 评分评估器, 基于提供的评分标准和内容信息进行评分
     */
    @GetMapping("/saa/answer-faithfulness")
    public String saaAnswerFaithfulness(@RequestParam(value = "query", defaultValue = "中国的首都是哪里?") String query) {
        var ragChatResponse = ragChat(query);
        var facts = ragChatResponse.documents();
        var studentAnswer = ragChatResponse.response();

        var evaluator = new AnswerFaithfulnessEvaluator(chatClientBuilder, objectMapper);
        var evaluationRequest = new EvaluationRequest(
                // FACTS
                facts,
                // STUDENT ANSWER
                studentAnswer
        );
        var pass = evaluate(evaluator, evaluationRequest);

        return pass ? studentAnswer : "暂无数据";
    }

    private RagChatResponse ragChat(String query) {
        var chatResponse = chatClient
                .prompt()
                .advisors(ragAdvisor, loggerAdvisor)
                .user(query)
                .call()
                .chatResponse();
        final List<Document> documents = chatResponse.getMetadata().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT);
        var response = chatResponse.getResult().getOutput().getText();
        return new RagChatResponse(query, documents, response);
    }

    private boolean evaluate(Evaluator evaluator, EvaluationRequest evaluationRequest) {
        var evaluationResponse = evaluator.evaluate(evaluationRequest);
        log.debug("AI模型评估响应: {}", evaluationResponse);
        var pass = evaluationResponse.isPass();
        log.info("AI模型评估结果: {}", pass);
        return pass;
    }

    private record RagChatResponse(String query, List<Document> documents, String response) {
    }
}
