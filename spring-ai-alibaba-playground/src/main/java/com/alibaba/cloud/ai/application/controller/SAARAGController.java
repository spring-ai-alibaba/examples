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

import com.alibaba.cloud.ai.application.service.ISAARAGService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@Tag(name = "RAG APIs")
@RequestMapping("/api/v1")
public class SAARAGController {

	private final Map<String,ISAARAGService> ragServiceMap;

	@Value("${spring.ai.alibaba.playground.bailian.enable:false}")
	private Boolean enable;

	public SAARAGController(Map<String,ISAARAGService> ragServiceMap) {
		this.ragServiceMap = ragServiceMap;
	}

	@GetMapping("/rag")
	@Operation(summary = "DashScope RAG")
	public Flux<String> ragChat(
			HttpServletResponse response,
			@Validated @RequestParam("prompt") String prompt,
			@RequestHeader(value = "chatId", required = false, defaultValue = "spring-ai-alibaba-playground-rag") String chatId
	) {

		ISAARAGService ragService;
		if (enable) {
			ragService = ragServiceMap.get("SAARAGService4Bailian");
		} else {
			ragService = ragServiceMap.get("SAARAGService4VectorStore");
		}
		response.setCharacterEncoding("UTF-8");
		return ragService.ragChat(chatId, prompt);
	}

}
