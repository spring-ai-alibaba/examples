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

package com.alibaba.cloud.ai.application.modulerag.prompt;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.util.PromptAssert;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class CustomContextQueryAugmenter implements QueryAugmenter {

	private static final Logger logger = LoggerFactory.getLogger(CustomContextQueryAugmenter.class);

	private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate(
			"""
			Context information is below:

			{{context}}

			Given the context information and no prior knowledge, answer the query.

			Follow these rules:

			1. If the answer is not in the context, just say that you don't know.
			2. Avoid statements like "Based on the context...." or "The provided information...".

			Query: {query}

			Answer:
			"""
	);

	private static final PromptTemplate DEFAULT_EMPTY_PROMPT_TEMPLATE = new PromptTemplate(
			"""
			The user query is outside your knowledge base.
			Politely inform the user that you cannot answer the query.
			"""
	);

	private static final boolean DEFAULT_ALLOW_EMPTY_PROMPT = false;

	private final PromptTemplate promptTemplate;

	private final PromptTemplate emptyPromptTemplate;

	private final boolean allowEmptyContext;

	public CustomContextQueryAugmenter(
			@Nullable PromptTemplate promptTemplate,
			@Nullable PromptTemplate emptyPromptTemplate,
			@Nullable Boolean allowEmptyContext
	) {
		this.promptTemplate = promptTemplate != null ? promptTemplate : DEFAULT_PROMPT_TEMPLATE;
		this.emptyPromptTemplate = emptyPromptTemplate != null ? emptyPromptTemplate : DEFAULT_EMPTY_PROMPT_TEMPLATE;
		this.allowEmptyContext = allowEmptyContext != null ? allowEmptyContext : DEFAULT_ALLOW_EMPTY_PROMPT;

		logger.debug("CustomContextQueryAugmenter promptTemplate: {}", promptTemplate.getTemplate());
		logger.debug("CustomContextQueryAugmenter emptyPromptTemplate: {}", emptyPromptTemplate);
		logger.debug("CustomContextQueryAugmenter allowEmptyContext: {}", allowEmptyContext);

		PromptAssert.templateHasRequiredPlaceholders(this.promptTemplate, "query", "context");
	}

	@NotNull
	@Override
	public Query augment(
			@Nullable Query query,
			@Nullable List<Document> documents
	) {

		Assert.notNull(query, "Query must not be null");
		Assert.notNull(documents, "Documents must not be null");

		logger.debug("Augmenting query: {}", query);

		if (documents.isEmpty()) {
			logger.debug("No documents found. Augmenting query with empty context.");
			return augmentQueryWhenEmptyContext(query);
		}

		logger.debug("Documents found. Augmenting query with context.");

		// 1. collect content from documents.
		AtomicInteger idCounter = new AtomicInteger(1);
		String documentContext = documents.stream()
				.map(document -> {
					String text = document.getText();
					return "[[" + (idCounter.getAndIncrement()) + "]]" + text;
				})
				.collect(Collectors.joining("\n-----------------------------------------------\n"));

		// 2. Define prompt parameters.
		Map<String, Object> promptParameters = Map.of(
				"query", query.text(),
				"context", documentContext
		);

		// 3. Augment user prompt with document context.
		return new Query(this.promptTemplate.render(promptParameters));
	}

	private Query augmentQueryWhenEmptyContext(Query query) {

		if (this.allowEmptyContext) {
			logger.debug("Empty context is allowed. Returning the original query.");
			return query;
		}

		logger.debug("Empty context is not allowed. Returning a specific query for empty context.");
		return new Query(this.emptyPromptTemplate.render());
	}

	public static final class Builder {

		private PromptTemplate promptTemplate;

		private PromptTemplate emptyPromptTemplate;

		private Boolean allowEmptyContext;

		public Builder() {
		}

		public CustomContextQueryAugmenter.Builder withPromptTemplate(PromptTemplate promptTemplate) {
			this.promptTemplate = promptTemplate;
			return this;
		}

		public CustomContextQueryAugmenter.Builder withEmptyPromptTemplate(PromptTemplate emptyPromptTemplate) {
			this.emptyPromptTemplate = emptyPromptTemplate;
			return this;
		}

		public CustomContextQueryAugmenter.Builder withAllowEmptyContext(Boolean allowEmptyContext) {
			this.allowEmptyContext = allowEmptyContext;
			return this;
		}

		public CustomContextQueryAugmenter build() {
			return new CustomContextQueryAugmenter(promptTemplate, emptyPromptTemplate, allowEmptyContext);
		}

	}

}
