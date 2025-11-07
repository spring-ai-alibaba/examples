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
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/module-rag")
public class ModuleRAGBasicController {

	private final ChatClient chatClient;

	private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;

	public ModuleRAGBasicController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {

		this.chatClient = chatClientBuilder.build();
		this.retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
			.documentRetriever(
					VectorStoreDocumentRetriever.builder().similarityThreshold(0.50).vectorStore(vectorStore).build())
			.build();
	}

	@GetMapping("/rag/basic")
	public String chatWithDocument(@RequestParam("prompt") String prompt) {

		return chatClient.prompt().advisors(retrievalAugmentationAdvisor).user(prompt).call().content();
	}

}
