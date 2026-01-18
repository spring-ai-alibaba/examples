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

package com.alibaba.cloud.ai.application.modulerag.data;

import com.alibaba.cloud.ai.application.entity.iqs.IQSSearchResponse;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URISyntaxException;
import java.util.*;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 *
 * Data Cleansing: Filters out useless data and converts it into Spring AI's Document objects
 */

@Component
public class DataClean {

	private static final Map<Integer, String> WebLink_MAP = new HashMap<>();

	public List<Document> getData(IQSSearchResponse respData) throws URISyntaxException {

		List<Document> documents = new ArrayList<>();
		Map<String, Object> metadata = getQueryMetadata(respData);

		for (int i = 0; i < respData.pageItems().size(); i++) {

			IQSSearchResponse.PageItem pageItem = respData.pageItems().get(i);
			Map<String, Object> pageItemMetadata = getPageItemMetadata(pageItem);

			metadata.putAll(pageItemMetadata);

			if (!StringUtils.hasText(pageItem.mainText()) || pageItem.mainText().length() < 10) {
				// Skip items with main text that is too short
				continue;
			}

			Document document = Document.builder()
					.metadata(metadata)
					.text(pageItem.mainText())
					.score(pageItem.rerankScore())
					.build();

			if (Objects.nonNull(pageItem.link())) {
				int index = i;
				WebLink_MAP.put(index + 1, pageItem.link());
			}

			documents.add(document);
		}

		return documents;
	}

	public Map<Integer, String> getWebLink() {

		return WebLink_MAP;
	}

	private Map<String, Object> getQueryMetadata(IQSSearchResponse respData) {

		HashMap<String, Object> docsMetadata = new HashMap<>();

		if (Objects.nonNull(respData.queryContext())) {
			docsMetadata.put("query", respData.queryContext().originalQuery().query());

			if (Objects.nonNull(respData.queryContext().originalQuery().timeRange())) {
				docsMetadata.put("timeRange", respData.queryContext().originalQuery().timeRange());
			}

			if (Objects.nonNull(respData.queryContext().originalQuery().timeRange())) {
				docsMetadata.put("filters", respData.queryContext().originalQuery().timeRange());
			}
		}

		return docsMetadata;
	}

	private Map<String, Object> getPageItemMetadata(IQSSearchResponse.PageItem pageItem) {

		HashMap<String, Object> pageItemMetadata = new HashMap<>();

		if (Objects.nonNull(pageItem)) {

			if (Objects.nonNull(pageItem.hostname())) {
				pageItemMetadata.put("hostname", pageItem.hostname());
			}

			if (Objects.nonNull(pageItem.title())) {
				pageItemMetadata.put("title", pageItem.title());
			}

			if (Objects.nonNull(pageItem.markdownText())) {
				pageItemMetadata.put("markdownText", pageItem.markdownText());
			}

			if (Objects.nonNull(pageItem.link())) {
				pageItemMetadata.put("link", pageItem.link());
			}

			if (Objects.nonNull(pageItem.mainText())) {
				pageItemMetadata.put("mainText", pageItem.mainText());
			}

			if (Objects.nonNull(pageItem.rerankScore())) {
				pageItemMetadata.put("rerankScore", pageItem.rerankScore());
			}

			if (Objects.nonNull(pageItem.publishedTime())) {
				pageItemMetadata.put("publishedTime", pageItem.publishedTime());
			}

			if (Objects.nonNull(pageItem.snippet())) {
				pageItemMetadata.put("snippet", pageItem.snippet());
			}
		}

		return pageItemMetadata;
	}

	public List<Document> limitResults(List<Document> documents, int minResults) {

		int limit = Math.min(documents.size(), minResults);

		return documents.subList(0, limit);
	}

}
