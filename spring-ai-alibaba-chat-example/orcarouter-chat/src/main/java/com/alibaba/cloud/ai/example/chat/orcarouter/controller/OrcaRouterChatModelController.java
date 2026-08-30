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

package com.alibaba.cloud.ai.example.chat.orcarouter.controller;

import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Chat model examples backed by OrcaRouter, an OpenAI-compatible AI gateway. The OpenAI
 * Spring AI starter is used, with the base URL and API key pointing at OrcaRouter (see
 * application.yml). Model names use the "provider/model" format, e.g.
 * "anthropic/claude-sonnet-4.5".
 */
@RestController
@RequestMapping("/orcarouter/chat-model")
public class OrcaRouterChatModelController {

	private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。";

	private final ChatModel orcaRouterChatModel;

	public OrcaRouterChatModelController(ChatModel chatModel) {
		this.orcaRouterChatModel = chatModel;
	}

	/**
	 * Simplest usage, without any LLM parameter injection.
	 * @return String types.
	 */
	@GetMapping("/simple/chat")
	public String simpleChat() {
		return orcaRouterChatModel.call(new Prompt(DEFAULT_PROMPT)).getResult().getOutput().getText();
	}

	/**
	 * Stream call. The typewriter effect can be achieved by streaming output.
	 * @return Flux<String> types.
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(HttpServletResponse response) {
		// avoid garbled characters
		response.setCharacterEncoding("UTF-8");
		Flux<ChatResponse> chatResponseFlux = orcaRouterChatModel.stream(new Prompt(DEFAULT_PROMPT));
		return chatResponseFlux.map(resp -> resp.getResult().getOutput().getText());
	}

	/**
	 * Programmatically customize LLM ChatOptions. OrcaRouter accepts any model id in the
	 * "provider/model" format, e.g. "openai/gpt-4o" or "orcarouter/auto". These options
	 * take precedence over the LLM parameters configured in application.yml.
	 * @return String types.
	 */
	@GetMapping("/custom/chat")
	public String customChat(@RequestParam(value = "model", defaultValue = "openai/gpt-4o-mini") String model) {
		OpenAiChatOptions customOptions = OpenAiChatOptions.builder()
			.model(model)
			.temperature(0.8)
			.maxTokens(1000)
			.build();
		return orcaRouterChatModel.call(new Prompt(DEFAULT_PROMPT, customOptions)).getResult().getOutput().getText();
	}

}
