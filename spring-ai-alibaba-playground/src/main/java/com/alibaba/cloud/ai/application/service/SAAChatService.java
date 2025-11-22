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

import java.util.Objects;

import com.alibaba.cloud.ai.advisor.DocumentRetrievalAdvisor;
import com.alibaba.cloud.ai.application.advisor.ReasoningContentAdvisor;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.api.DashScopeResponseFormat;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Service
public class SAAChatService {

	private static final Logger log = LoggerFactory.getLogger(SAAChatService.class);

	private final ChatClient chatClient;

	private final DashScopeApi dashscopeApi;

	@Value("${spring.ai.alibaba.playground.bailian.enable:false}")
	private Boolean enable;

	@Value("${spring.ai.alibaba.playground.bailian.index-name:default-index}")
	private String indexName;

	private final PromptTemplate deepThinkPromptTemplate;

	private final ReasoningContentAdvisor reasoningContentAdvisor;

	private DocumentRetrievalAdvisor retrievalAdvisor;

	public SAAChatService(
			DashScopeApi dashscopeApi,
			SimpleLoggerAdvisor simpleLoggerAdvisor,
			MessageChatMemoryAdvisor messageChatMemoryAdvisor,
			@Qualifier("dashScopeChatModel") ChatModel chatModel,
			@Qualifier("systemPromptTemplate") PromptTemplate systemPromptTemplate,
			@Qualifier("deepThinkPromptTemplate") PromptTemplate deepThinkPromptTemplate
	) {
		this.dashscopeApi = dashscopeApi;
		this.chatClient = ChatClient.builder(chatModel)
				.defaultSystem(
					systemPromptTemplate.getTemplate()
				).defaultAdvisors(
						simpleLoggerAdvisor,
						messageChatMemoryAdvisor
				).build();

		this.deepThinkPromptTemplate = deepThinkPromptTemplate;
		this.reasoningContentAdvisor = new ReasoningContentAdvisor(1);
	}

	@PostConstruct
	public void init(){
		if(enable) {
			log.info("Initializing DocumentRetrievalAdvisor with index: {}", indexName);
			this.retrievalAdvisor = new DocumentRetrievalAdvisor(
					new DashScopeDocumentRetriever(
							dashscopeApi,
							DashScopeDocumentRetrieverOptions.builder()
									.withIndexName(indexName)
									.build()
					)
			);
		}else {
			log.info("Bailian RAG is disabled, DocumentRetrievalAdvisor will not be initialized");
		}
	}

	public Flux<String> chat(String chatId, String model, String prompt) {

		log.debug("chat model is: {}", model);

		// check if model == "deepseek-r1", output reasoning content.
		if (Objects.equals("deepseek-r1", model)) {
			// add reasoning content advisor.
			chatClient.prompt().advisors(reasoningContentAdvisor);
		}
		var runtimeOptions = DashScopeChatOptions.builder()
				.withModel(model)
				.withTemperature(0.8)
				.withResponseFormat(DashScopeResponseFormat.builder()
						.type(DashScopeResponseFormat.Type.TEXT)
						.build()
				).build();

        ChatClient.ChatClientRequestSpec clientRequestSpec = chatClient.prompt()
                .options(runtimeOptions)
                .user(prompt)
                .advisors(memoryAdvisor -> memoryAdvisor
                        .param(ChatMemory.CONVERSATION_ID, chatId)
                );

        // Only add if enable is true and retrievalAdvisor is initialized
        if (enable && retrievalAdvisor != null) {
            log.debug("Adding DocumentRetrievalAdvisor to chat");
            clientRequestSpec.advisors(retrievalAdvisor);
        }

        return clientRequestSpec.stream().content();
	}

	public Flux<String> deepThinkingChat(String chatId, String model, String prompt) {

        ChatClient.ChatClientRequestSpec clientRequestSpec = chatClient.prompt()
                .options(DashScopeChatOptions.builder()
                        .withModel(model)
                        .withTemperature(0.8)
                        .withResponseFormat(DashScopeResponseFormat.builder()
                                .type(DashScopeResponseFormat.Type.TEXT)
                                .build()
                        ).build()
                ).system(deepThinkPromptTemplate.getTemplate())
                .user(prompt)
                .advisors(memoryAdvisor -> memoryAdvisor
                        .param(ChatMemory.CONVERSATION_ID, chatId)
                );

        // Only add if enable is true and retrievalAdvisor is initialized
        if (enable && retrievalAdvisor != null) {
            log.debug("Adding DocumentRetrievalAdvisor to deepThinkingChat");
            clientRequestSpec.advisors(retrievalAdvisor);
        }

        return clientRequestSpec.stream().content();
    }
}
