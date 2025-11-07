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

package com.alibaba.example.summarizer.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 总结文件文本内容
 */

@RestController
public class SummaryController {

	@Value("classpath:/text-summarize.st")
	private Resource summarizeTemplate;

	private final ChatClient chatClient;

	private static final String DEFAULT_SUMMARY_PROMPT = "总结文本";

	public SummaryController(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	@PostMapping(path = "/summarize", produces = "text/plain")
	public String summarize(@RequestParam("file") MultipartFile file) {

		List<Document> documents = new TikaDocumentReader(file.getResource()).get();

		String documentText = documents.stream()
				.map(Document::getFormattedContent)
				.collect(Collectors.joining("\n\n"));

		return chatClient.prompt()
				.user(DEFAULT_SUMMARY_PROMPT)
				.system(systemSpec ->
						systemSpec.text(summarizeTemplate).param("document", documentText)
				).call()
				.content();
	}

}
