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

import com.alibaba.cloud.ai.tool.MiniMaxLearningTools;
import com.alibaba.cloud.ai.tool.ToolCallDebugRecorder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
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

	private static final String SYSTEM_PROMPT = """
			你是 MiniMax-M2.7 学习助手。
			请始终使用中文回答。
			回答要清晰、直接，适合正在学习 Spring AI Alibaba Agent 和 Skill 开发的 Java 开发者。
			你可以使用工具获取真实时间、生成学习建议、生成今日学习计划、解释 Spring AI Alibaba 相关概念。
			当用户询问当前时间、北京时间、UTC 时间等真实时间问题时，优先调用 getCurrentTime 工具。
			当用户询问学习路线、下一步学习什么、Tool Calling、Skill、Agent、RAG、MCP 或 Graph 时，
			优先调用 generateLearningAdvice 工具。
			当用户要求今日计划、30 分钟学习安排、每日练习或任务拆分时，优先调用 generateDailyPlan 工具。
			当用户询问概念含义或区别，例如 Tool、Skill、Agent、Graph 是什么时，优先调用 explainConcept 工具。
			不要输出 <think>、</think> 或任何思考标签。
			""";

	private static final int MAX_HISTORY_MESSAGES = 20;

	private final ChatClient chatClient;

	private final MiniMaxLearningTools learningTools;

	private final ToolCallDebugRecorder debugRecorder;

	public MiniMaxChatClientController(ChatModel chatModel, MiniMaxLearningTools learningTools,
			ToolCallDebugRecorder debugRecorder) {
		this.learningTools = learningTools;
		this.debugRecorder = debugRecorder;
		this.chatClient = ChatClient.builder(chatModel)
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.defaultOptions(defaultOptions())
				.build();
	}

	/**
	 * Single-turn ChatClient call.
	 */
	@GetMapping("/simple/chat")
	public String simpleChat(@RequestParam(value = "message", defaultValue = DEFAULT_PROMPT) String message) {
		return this.chatClient.prompt(message).call().content();
	}

	/**
	 * Single-turn streaming ChatClient call.
	 */
	@GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> streamChat(@RequestParam(value = "message", defaultValue = DEFAULT_PROMPT) String message,
			HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		return this.chatClient.prompt(message).stream().content();
	}

	/**
	 * Multi-turn chat with Tool Calling.
	 */
	@PostMapping(value = "/conversation/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ChatResponse conversationChat(@RequestBody ChatRequest request) {
		this.debugRecorder.clear();
		try {
			String content = this.chatClient.prompt()
					.messages(buildMessages(request))
					.options(defaultOptions())
					.tools(this.learningTools)
					.call()
					.content();
			return new ChatResponse(content, this.debugRecorder.snapshot());
		}
		finally {
			this.debugRecorder.remove();
		}
	}

	/**
	 * Multi-turn streaming chat with Tool Calling.
	 *
	 * <p>
	 * Streaming responses keep Tool debug details in backend logs. The sync endpoint
	 * returns toolCalls in JSON for easy UI display.
	 */
	@PostMapping(value = "/conversation/stream", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> conversationStream(@RequestBody ChatRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		this.debugRecorder.clear();
		return this.chatClient.prompt()
				.messages(buildMessages(request))
				.options(defaultOptions())
				.tools(this.learningTools)
				.stream()
				.content()
				.doFinally(signalType -> this.debugRecorder.remove());
	}

	private List<Message> buildMessages(ChatRequest request) {
		List<Message> messages = new ArrayList<>();
		messages.add(new SystemMessage(SYSTEM_PROMPT));

		List<ChatMessage> history = request == null || request.history() == null ? List.of() : request.history();
		int start = Math.max(0, history.size() - MAX_HISTORY_MESSAGES);
		for (ChatMessage item : history.subList(start, history.size())) {
			Message message = toMessage(item);
			if (message != null) {
				messages.add(message);
			}
		}

		String message = request == null || request.message() == null || request.message().isBlank()
				? DEFAULT_PROMPT : request.message();
		messages.add(new UserMessage(message));
		return messages;
	}

	private Message toMessage(ChatMessage message) {
		if (message == null || message.content() == null || message.content().isBlank()) {
			return null;
		}
		return switch (normalizeRole(message.role())) {
			case "assistant" -> new AssistantMessage(message.content());
			case "system" -> new SystemMessage(message.content());
			default -> new UserMessage(message.content());
		};
	}

	private String normalizeRole(String role) {
		if (role == null) {
			return "user";
		}
		return role.trim().toLowerCase();
	}

	private OpenAiChatOptions defaultOptions() {
		return OpenAiChatOptions.builder()
				.model("MiniMax-M2.7")
				.temperature(0.7)
				.build();
	}

	public record ChatRequest(String message, List<ChatMessage> history) {
	}

	public record ChatMessage(String role, String content) {
	}

	public record ChatResponse(String content, List<ToolCallDebugRecorder.ToolCallDebug> toolCalls) {
	}

}
