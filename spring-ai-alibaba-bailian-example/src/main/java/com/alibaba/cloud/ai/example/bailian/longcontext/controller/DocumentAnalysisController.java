/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.example.bailian.longcontext.controller;

import com.alibaba.cloud.ai.advisor.DashScopeDocumentAnalysisAdvisor;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 分析文件中的内容并回答问题
 * @author HunterPorter
 * @author <a href="mailto:zongpeng_hzp@163.com">HunterPorter</a>
 */
@RestController
public class DocumentAnalysisController {

	private final ChatClient chatClient;

	private static final String DEFAULT_SUMMARY_PROMPT = "总结文本";

	public DocumentAnalysisController(ChatClient.Builder chatClientBuilder,
									  @Value("${spring.ai.dashscope.api-key}") String apiKey) {
		this.chatClient = chatClientBuilder.defaultAdvisors(
				new DashScopeDocumentAnalysisAdvisor(new SimpleApiKey(apiKey))
		).build();
	}

	@PostMapping(path = "/analyzeByUpload", produces = "text/plain")
	public String analyzeByUpload(@RequestParam("file") MultipartFile file) {

		return chatClient.prompt()
				.advisors(a -> a.param(DashScopeDocumentAnalysisAdvisor.RESOURCE, file.getResource()))
				.user(DEFAULT_SUMMARY_PROMPT)
				.options(DashScopeChatOptions.builder().model("qwen-long"))
				.call()
				.content();
	}

	@GetMapping(path = "/analyzeByUrl", produces = "text/plain")
	public String analyzeByUrl(@RequestParam("url") String url) {

		return chatClient.prompt()
				.advisors(a -> a.param(DashScopeDocumentAnalysisAdvisor.RESOURCE, UrlResource.from(url)))
				.user(DEFAULT_SUMMARY_PROMPT)
				.options(DashScopeChatOptions.builder().model("qwen-long"))
				.call()
				.content();
	}

}
