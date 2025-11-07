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

package com.alibaba.cloud.ai.application.controller;

import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.cloud.ai.application.config.WebSearchProperties;
import com.alibaba.cloud.ai.application.entity.dashscope.ChatResponseDTO;
import com.alibaba.cloud.ai.application.service.ISAAWebSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 *
 * The deepseek-r1 model is used by default, which works better.
 */

@RestController
@Tag(name = "Web Search APIs")
@RequestMapping("/api/v1")
public class SAAWebSearchController {

	private final ISAAWebSearchService webSearch;

	/**
	 * 可选注入：spring.ai.alibaba.playground.web-search.type
	 * DashScope: 阿里云百炼大模型联网搜索功能
	 * ModuleRag: 基于模块化rag的iqs在线搜索
	 */
	public SAAWebSearchController(WebSearchProperties webSearchProperties, ObjectProvider<ISAAWebSearchService> webSearchServiceObjectProvider) {
		this.webSearch = webSearchServiceObjectProvider.stream()
				.filter(webSearchService -> webSearchService.type() == webSearchProperties.type())
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No ISAAWebSearchService found"));

	}

	@PostMapping("/search")
	public Flux<ChatResponseDTO> search(
			HttpServletResponse response,
			@Validated @RequestBody String prompt
	) {

		response.setCharacterEncoding("UTF-8");
		return webSearch.chat(prompt);
	}

}
