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

package com.alibaba.cloud.ai.application.config;

import com.alibaba.cloud.ai.application.modulerag.preretrieval.query.expansion.MultiQueryExpander;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Configuration
@EnableConfigurationProperties(WebSearchProperties.class)
public class WebSearchConfiguration {

	@Bean
	public QueryTransformer queryTransformer(
			@Qualifier("dashScopeChatModel") ChatModel chatModel,
			@Qualifier("transformerPromptTemplate") PromptTemplate transformerPromptTemplate
	) {

		ChatClient chatClient = ChatClient.builder(chatModel)
				.defaultOptions(
						DashScopeChatOptions.builder()
								.withModel("qwen-plus")
								.build()
				).build();

		return RewriteQueryTransformer.builder()
				.chatClientBuilder(chatClient.mutate())
				.promptTemplate(transformerPromptTemplate)
				.targetSearchSystem("Web Search")
				.build();
	}

	@Bean
	public QueryExpander queryExpander(
			@Qualifier("dashScopeChatModel") ChatModel chatModel
	) {

		ChatClient chatClient = ChatClient.builder(chatModel)
				.defaultOptions(
						DashScopeChatOptions.builder()
								.withModel("qwen-plus")
								.build()
				).build();

		return MultiQueryExpander.builder()
				.chatClientBuilder(chatClient.mutate())
				.numberOfQueries(2)
				.build();
	}

}
