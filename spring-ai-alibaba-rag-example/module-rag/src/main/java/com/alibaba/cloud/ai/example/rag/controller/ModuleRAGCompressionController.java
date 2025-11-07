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

package com.alibaba.cloud.ai.example.rag.controller;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 *
 * CompressionQueryTransformer 使用示例
 * https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_compressionquerytransformer
 */

@RestController
@RequestMapping("/module-rag")
public class ModuleRAGCompressionController {

	private final ChatClient chatClient;

	private final MessageChatMemoryAdvisor chatMemoryAdvisor;

	private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;

	public ModuleRAGCompressionController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
			VectorStore vectorStore) {

		this.chatClient = chatClientBuilder.build();

		this.chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

		var documentRetriever = VectorStoreDocumentRetriever.builder()
			.vectorStore(vectorStore)
			.similarityThreshold(0.50)
			.build();

		var queryTransformer = CompressionQueryTransformer.builder()
			.chatClientBuilder(chatClientBuilder.build().mutate())
			.build();

		this.retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
			.documentRetriever(documentRetriever)
			.queryTransformers(queryTransformer)
			.build();
	}

	@PostMapping("/rag/compression/{chatId}")
	public String rag(@RequestBody String prompt, @PathVariable("chatId") String conversationId) {

		return chatClient.prompt()
			.advisors(chatMemoryAdvisor, retrievalAugmentationAdvisor)
			.advisors(advisors -> advisors.param(CONVERSATION_ID,
					conversationId))
			.user(prompt)
			.call()
			.content();
	}

}
