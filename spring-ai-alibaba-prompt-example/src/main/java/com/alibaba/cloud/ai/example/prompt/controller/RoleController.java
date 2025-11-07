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

package com.alibaba.cloud.ai.example.prompt.controller;

import java.util.List;
import java.util.Map;

import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example/ai")
public class RoleController {

	private final ChatClient chatClient;

	/**
	 * 加载 System prompt tmpl.
	 */
	@Value("classpath:/prompts/system-message.st")
	private Resource systemResource;

	@Autowired
	public RoleController(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}

	@GetMapping("/roles")
	public Flux<String> generate(
			@RequestParam(
					value = "message",
					required = false,
					defaultValue = "Tell me about three famous pirates from the Golden Age of Piracy and why they did.  Write at least a sentence for each pirate.") String message,
			@RequestParam(value = "name", required = false, defaultValue = "Bob") String name,
			@RequestParam(value = "voice", required = false, defaultValue = "pirate") String voice
	) {

		// 用户输入
		UserMessage userMessage = new UserMessage(message);

		// 使用 System prompt tmpl
		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
		// 填充 System prompt 中的变量值
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));

		// 调用大模型
		return chatClient.prompt(
						new Prompt(List.of(
								userMessage,
								systemMessage)))
				.stream().content();
	}

}
