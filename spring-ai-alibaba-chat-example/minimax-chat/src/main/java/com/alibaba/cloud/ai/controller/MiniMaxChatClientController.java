/*
 * Copyright 2026-2027 the original author or authors.
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

package com.alibaba.cloud.ai.controller;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cloud.ai.agent.LearningAgentResult;
import com.alibaba.cloud.ai.agent.LearningAgentService;
import com.alibaba.cloud.ai.agent.LearningAgentService.LearningAgentMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * MiniMax chat examples.
 *
 * @author wangx
 */
@RestController
@RequestMapping("/minimax/chat-client")
public class MiniMaxChatClientController {

	private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。";

	private final ChatClient chatClient;

	private final LearningAgentService learningAgentService;

	public MiniMaxChatClientController(ChatModel chatModel, LearningAgentService learningAgentService) {
		this.learningAgentService = learningAgentService;
		this.chatClient = ChatClient.builder(chatModel)
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.defaultOptions(defaultOptions())
				.build();
	}

	@GetMapping("/simple/chat")
	public String simpleChat(@RequestParam(value = "message", defaultValue = DEFAULT_PROMPT) String message) {
		return this.chatClient.prompt(message).call().content();
	}

	@GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> streamChat(@RequestParam(value = "message", defaultValue = DEFAULT_PROMPT) String message,
			HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		return this.chatClient.prompt(message).stream().content();
	}

	@PostMapping(value = "/conversation/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
	public LearningAgentResult conversationChat(@RequestBody ChatRequest request) {
		return this.learningAgentService.chat(extractUserId(request), extractMessage(request), toAgentHistory(request));
	}

	@PostMapping(value = "/conversation/stream", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> conversationStream(@RequestBody ChatRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		return this.learningAgentService.stream(extractUserId(request), extractMessage(request), toAgentHistory(request));
	}

	private String extractUserId(ChatRequest request) {
		if (request == null || request.userId() == null || request.userId().isBlank()) {
			return "default-user";
		}
		return request.userId().trim();
	}

	private String extractMessage(ChatRequest request) {
		if (request == null || request.message() == null || request.message().isBlank()) {
			return DEFAULT_PROMPT;
		}
		return request.message();
	}

	private List<LearningAgentMessage> toAgentHistory(ChatRequest request) {
		if (request == null || request.history() == null || request.history().isEmpty()) {
			return List.of();
		}
		List<LearningAgentMessage> messages = new ArrayList<>();
		for (ChatMessage item : request.history()) {
			messages.add(new LearningAgentMessage(item.role(), item.content()));
		}
		return messages;
	}

	private OpenAiChatOptions defaultOptions() {
		return OpenAiChatOptions.builder()
				.model("MiniMax-M2.7")
				.temperature(0.7)
				.build();
	}

	public record ChatRequest(String userId, String message, List<ChatMessage> history) {
	}

	public record ChatMessage(String role, String content) {
	}

}
