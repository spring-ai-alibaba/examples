/*
 * Copyright 2026-2027 the original author or authors.
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
package com.cloud.alibaba.ai.example.agent.rag.tool;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Knowledge Retrieval Tool for RAG Agent
 * <p>
 * This tool enables the agent to retrieve relevant documents from a knowledge base
 * using vector similarity search. It follows the Agentic RAG pattern where the
 * agent decides when and how to use the retrieval tool.
 * </p>
 *
 * @author zth9
 * @since 2026-01-22
 */
@Component
public class KnowledgeRetrievalTool implements BiFunction<KnowledgeRetrievalTool.Request, ToolContext, String> {

	private static final Logger logger = LoggerFactory.getLogger(KnowledgeRetrievalTool.class);

	private static final int DEFAULT_TOP_K = 4;

	private final SimpleVectorStore vectorStore;

	@Value("${rag.knowledge.sources}")
	private List<String> knowledgeSourceUrls;

	public KnowledgeRetrievalTool(EmbeddingModel embeddingModel) {
		this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
	}

	@PostConstruct
	void initKnowledgeBase() {
		logger.info("Initializing knowledge base from {} sources...", knowledgeSourceUrls.size());

		for (String url : knowledgeSourceUrls) {
			try {
				JsoupDocumentReader reader = new JsoupDocumentReader(url);
				List<Document> documents = reader.get();
				logger.info("Loaded {} documents from {}", documents.size(), url);

				TokenTextSplitter splitter = new TokenTextSplitter();
				List<Document> splitDocuments = splitter.apply(documents);
				logger.info("Split into {} chunks", splitDocuments.size());

				vectorStore.add(splitDocuments);
				logger.info("Added {} chunks to vector store", splitDocuments.size());
			}
			catch (Exception e) {
				logger.warn("Failed to load documents from {}: {}", url, e.getMessage());
			}
		}

		logger.info("Knowledge base initialization completed");
	}

	@Override
	public String apply(Request request, ToolContext toolContext) {
		logger.info("========== Knowledge Retrieval Tool Start ==========");
		logger.info("Query: {}", request.query());

		int topK = request.topK() != null ? request.topK() : DEFAULT_TOP_K;

		SearchRequest searchRequest = SearchRequest.builder().query(request.query()).topK(topK).build();

		List<Document> documents = vectorStore.similaritySearch(searchRequest);

		if (documents.isEmpty()) {
			logger.info("No relevant documents found for query: {}", request.query());
			return "No relevant information found in the knowledge base for the given query.";
		}

		String result = documents.stream()
			.map(doc -> "---\n" + doc.getFormattedContent() + "\n---")
			.collect(Collectors.joining("\n\n"));

		logger.info("Retrieved {} relevant documents", documents.size());
		logger.info("========== Knowledge Retrieval Tool End ==========");

		return result;
	}

	public ToolCallback toolCallback() {
		return FunctionToolCallback.builder("knowledge_retrieval", this)
			.description("Retrieves relevant information from the Spring AI Alibaba knowledge base. "
					+ "Use this tool when you need to answer questions about Spring AI Alibaba, "
					+ "its features, configuration, or usage. " + "The tool performs semantic search to find the most "
					+ "relevant documentation based on the query.")
			.inputType(Request.class)
			.build();
	}

	@JsonClassDescription("Request for knowledge retrieval from the documentation")
	public record Request(@JsonProperty(value = "query", required = true)
	@JsonPropertyDescription("The search query to find relevant documentation. "
			+ "Be specific and include key terms related to your question.") String query,

			@JsonProperty(value = "top_k")
			@JsonPropertyDescription("Number of top results to retrieve (default: 4)") Integer topK) {
	}

}
