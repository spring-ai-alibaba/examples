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

package com.alibaba.cloud.ai.application.modulerag.core;

import com.alibaba.cloud.ai.application.config.WebSearchProperties;
import com.alibaba.cloud.ai.application.entity.iqs.IQSSearchRequest;
import com.alibaba.cloud.ai.application.entity.iqs.IQSSearchResponse;
import com.alibaba.cloud.ai.application.exception.SAAAppException;
import com.alibaba.cloud.ai.application.modulerag.IQSSearchProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * <a href="https://help.aliyun.com/document_detail/2883041.html">通晓搜索</a>
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Component
public class IQSSearchEngine {

	private final RestClient restClient;
	private final IQSSearchProperties iqsSearchProperties;
	private static final String BASE_URL = "https://cloud-iqs.aliyuncs.com/";
	private static final String API_PATH = "/search/unified";
	private static final String DEFAULT_TIME_RANGE = "OneWeek";
	private static final String DEFAULT_ENGINE_TYPE = "Generic";

	public IQSSearchEngine(
			RestClient.Builder restClientBuilder,
			WebSearchProperties webSearchProperties,
			ResponseErrorHandler responseErrorHandler
	) {

		this.iqsSearchProperties = webSearchProperties.iqs();
		Assert.hasText(iqsSearchProperties.apiKey(), "apiKey must not be empty");
		this.restClient = restClientBuilder.baseUrl(BASE_URL)
				.defaultHeaders(getHeaders())
				.defaultStatusHandler(responseErrorHandler)
				.build();
	}

	public IQSSearchResponse search(String query) throws JsonProcessingException {

		// String encodeQ = URLEncoder.encode(query, StandardCharsets.UTF_8);
		final IQSSearchRequest request = IQSSearchRequest.builder()
				.query(query)
				.timeRange(DEFAULT_TIME_RANGE)
				.engineType(DEFAULT_ENGINE_TYPE)
				.contents(IQSSearchRequest.Contents.builder()
						.mainText(true)
						// IQS 目前的 md 文档效果不好, 所以关闭.
						.markdownText(false)
						.rerankScore(true)
						.build())
				.build();

		ResponseEntity<IQSSearchResponse> response = this.restClient.post()
				.uri(API_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.retrieve()
				.toEntity(IQSSearchResponse.class);

		return genericSearchResult(response);
	}

	private IQSSearchResponse genericSearchResult(ResponseEntity<IQSSearchResponse> response) {

		if ((Objects.equals(response.getStatusCode(), HttpStatus.OK)) && Objects.nonNull(response.getBody())) {
			return response.getBody();
		}

		throw new SAAAppException("Failed to search" + response.getStatusCode().value());
	}

	private Consumer<HttpHeaders> getHeaders() {

		return httpHeaders -> {

			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			httpHeaders.set(HttpHeaders.USER_AGENT, userAgent());
			httpHeaders.setBearerAuth(this.iqsSearchProperties.apiKey());
		};
	}

	private static String userAgent() {

		return String.format("%s/%s; java/%s; platform/%s; processor/%s", "SpringAiAlibabaPlayground", "1.0.0", System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"));
	}

}
