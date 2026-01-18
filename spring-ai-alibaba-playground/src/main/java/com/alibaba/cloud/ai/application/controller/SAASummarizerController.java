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

package com.alibaba.cloud.ai.application.controller;

import com.alibaba.cloud.ai.application.service.SAASummarizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Docs Summarize APIs")
public class SAASummarizerController {

	private final SAASummarizerService docsSummaryService;

	public SAASummarizerController(SAASummarizerService docsSummaryService) {
		this.docsSummaryService = docsSummaryService;
	}

	@Operation(summary = "Docs summary")
	@PostMapping("/summarizer")
	public Flux<String> summary(
			HttpServletResponse response,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "url", required = false) String url
	) {

		if (file == null && (url == null || url.isEmpty())) {
			return Flux.just("Either 'file' or 'url' must be provided.");
		}

		response.setCharacterEncoding("UTF-8");
		return docsSummaryService.summary(file, url);
	}

}
