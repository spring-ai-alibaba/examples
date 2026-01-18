/*
 * Copyright 2024-2026 the original author or authors.
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

package com.alibaba.cloud.ai.application.modulerag.preretrieval.query.expansion;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.util.PromptAssert;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * User Prompt Query Expander
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class MultiQueryExpander implements QueryExpander {

	private static final Logger logger = LoggerFactory.getLogger(MultiQueryExpander.class);

	private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate(
			"""
			You are an expert in information retrieval and search optimization.
			Generate {number} different versions of a given query.
						
			Each variation should cover a different perspective or aspect of the topic while maintaining the core intent of
			the original query. The goal is to broaden your search and improve your chances of finding relevant information.
						
			Don't interpret the selection or add additional text.
			Query variants are provided, separated by line breaks.
						
			Original query: {query}
									
			Query variants:
			"""
	);

	private static final Boolean DEFAULT_INCLUDE_ORIGINAL = true;

	private static final Integer DEFAULT_NUMBER_OF_QUERIES = 3;

	private final ChatClient chatClient;

	private final PromptTemplate promptTemplate;

	private final boolean includeOriginal;

	private final int numberOfQueries;

	public MultiQueryExpander(
			ChatClient.Builder chatClientBuilder,
			@Nullable PromptTemplate promptTemplate,
			@Nullable Boolean includeOriginal,
			@Nullable Integer numberOfQueries
	) {

		Assert.notNull(chatClientBuilder, "ChatClient.Builder must not be null");

		this.chatClient = chatClientBuilder.build();
		this.promptTemplate = promptTemplate == null ? DEFAULT_PROMPT_TEMPLATE : promptTemplate;
		this.includeOriginal = includeOriginal == null ? DEFAULT_INCLUDE_ORIGINAL : includeOriginal;
		this.numberOfQueries = numberOfQueries == null ? DEFAULT_NUMBER_OF_QUERIES : numberOfQueries;

		PromptAssert.templateHasRequiredPlaceholders(this.promptTemplate, "number", "query");
	}

	@NotNull
	@Override
	public List<Query> expand(@Nullable Query query) {

		Assert.notNull(query, "Query must not be null");

		logger.debug("Generating {} queries for query: {}", this.numberOfQueries, query.text());

		String resp = this.chatClient.prompt()
				.user(user -> user.text(this.promptTemplate.getTemplate())
						.param("number", this.numberOfQueries)
						.param("query", query.text()))
				.call()
				.content();

		logger.debug("MultiQueryExpander#expand() Response from chat client: {}", resp);

		if (Objects.isNull(resp)) {

			logger.warn("No response from chat client for query: {}. is return.", query.text());
			return List.of(query);
		}

		List<String> queryVariants = Arrays.stream(resp.split("\n")).filter(StringUtils::hasText).toList();

		if (CollectionUtils.isEmpty(queryVariants) || this.numberOfQueries != queryVariants.size()) {

			logger.warn("Query expansion result dose not contain the requested {} variants for query: {}. is return.",
					this.numberOfQueries, query.text());

			return List.of(query);
		}

		List<Query> queries = queryVariants.stream()
				.filter(StringUtils::hasText)
				.map(queryText -> query.mutate().text(queryText).build())
				.collect(Collectors.toList());

		if (this.includeOriginal) {

			logger.debug("Including original query in the expanded queries for query: {}", query.text());
			queries.add(0, query);
		}
		
		logger.debug("Rewrite queries: {}", queries);

		return queries;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private ChatClient.Builder chatClientBuilder;

		private PromptTemplate promptTemplate;

		private Boolean includeOriginal;

		private Integer numberOfQueries;

		private Builder() {
		}

		public Builder chatClientBuilder(ChatClient.Builder chatClientBuilder) {
			this.chatClientBuilder = chatClientBuilder;
			return this;
		}

		public Builder promptTemplate(PromptTemplate promptTemplate) {
			this.promptTemplate = promptTemplate;
			return this;
		}

		public Builder includeOriginal(Boolean includeOriginal) {
			this.includeOriginal = includeOriginal;
			return this;
		}

		public Builder numberOfQueries(Integer numberOfQueries) {
			this.numberOfQueries = numberOfQueries;
			return this;
		}

		public MultiQueryExpander build() {
			return new MultiQueryExpander(this.chatClientBuilder, this.promptTemplate, this.includeOriginal, this.numberOfQueries);
		}

	}

}
