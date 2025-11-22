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

package com.alibaba.ai.examples.controller.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>

 */

@RestController
@RequestMapping("/more-platform-chat-client")
public class MorePlatformChatClientController {

	private final ChatClient chatClient;

	private final ChatModel ollamaChatModel;

	private final ChatModel openAIChatModel;

	public MorePlatformChatClientController(
			@Qualifier("dashScopeChatModel") ChatModel dashScopeChatModel,
			@Qualifier("ollamaChatModel") ChatModel ollamaChatModel,
			@Qualifier("openAiChatModel") ChatModel openAIChatModel
	) {

		this.ollamaChatModel = ollamaChatModel;
		this.openAIChatModel = openAIChatModel;

		// 默认使用 DashScopeChatModel 构建
		this.chatClient = ChatClient.builder(dashScopeChatModel).build();
	}

	@GetMapping
	public Flux<String> stream(
			@RequestParam("prompt") String prompt,
			@RequestHeader(value = "platform", required = false) String platform
	) {

		if (!StringUtils.hasText(platform)) {
			return Flux.just("platform not exist");
		}

		if (Objects.equals("dashscope", platform)) {
			System.out.println("命中 dashscope ......");
			return chatClient.prompt(prompt).stream().content();
		}

		if (Objects.equals("ollama", platform)) {
			System.out.println("命中 ollama ......");
			return ChatClient.builder(ollamaChatModel).build().prompt(prompt).stream().content();
		}

		if (Objects.equals("openai", platform)) {
			System.out.println("命中 openai ......");
			return ChatClient.builder(openAIChatModel).build().prompt(prompt).stream().content();
		}

		return Flux.just("platform not exist");
	}

}
