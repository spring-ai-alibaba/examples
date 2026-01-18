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

package com.alibaba.cloud.ai.application.modulerag;

import com.alibaba.cloud.ai.application.entity.iqs.IQSSearchResponse;
import com.alibaba.cloud.ai.application.exception.SAAAppException;
import com.alibaba.cloud.ai.application.modulerag.core.IQSSearchEngine;
import com.alibaba.cloud.ai.application.modulerag.data.DataClean;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.lang.Nullable;

import java.net.URISyntaxException;
import java.util.List;

/**
 * spring-ai 从 0.8.0 版本开始不支持 DocumentRanker.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class WebSearchRetriever implements DocumentRetriever {

	private static final Logger logger = LoggerFactory.getLogger(WebSearchRetriever.class);

	private final int maxResults;

	private final DataClean dataCleaner;

	private final IQSSearchEngine searchEngine;

	private WebSearchRetriever(Builder builder) {

		this.searchEngine = builder.searchEngine;
		this.maxResults = builder.maxResults;
		this.dataCleaner = builder.dataCleaner;
	}

	@NotNull
	@Override
	public List<Document> retrieve(
			@Nullable Query query
	) {

		// 搜索
		IQSSearchResponse searchResp;
		try {
			searchResp = searchEngine.search(query.text());
		} catch (JsonProcessingException e) {
			throw new SAAAppException("json process error" + e.getMessage());
		}

		// 清洗数据
        List<Document> cleanerData;
        try {
            cleanerData = dataCleaner.getData(searchResp);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        // logger.debug("cleaner data: {}", cleanerData);

		// 返回结果
		List<Document> documents = dataCleaner.limitResults(cleanerData, maxResults);

		logger.debug("WebSearchRetriever#retrieve() document size: {}, raw documents: {}",
				documents.size(),
				documents.stream().map(Document::getId).toArray()
		);

		return documents;
	}

	public static WebSearchRetriever.Builder builder() {
		return new WebSearchRetriever.Builder();
	}


	public static final class Builder {

		private IQSSearchEngine searchEngine;

		private int maxResults;

		private DataClean dataCleaner;

		public WebSearchRetriever.Builder searchEngine(IQSSearchEngine searchEngine) {

			this.searchEngine = searchEngine;
			return this;
		}

		public WebSearchRetriever.Builder dataCleaner(DataClean dataCleaner) {

			this.dataCleaner = dataCleaner;
			return this;
		}

		public WebSearchRetriever.Builder maxResults(int maxResults) {

			this.maxResults = maxResults;
			return this;
		}

		public WebSearchRetriever build() {

			return new WebSearchRetriever(this);
		}
	}

}
