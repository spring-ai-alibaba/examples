/*
 * Copyright 2026-2027 the original author or authors.
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
package com.cloud.alibaba.ai.example.agent.rag.controller;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.UUID;

/**
 * Controller for the RAG Agent
 * <p>
 * Provides REST API endpoints for interacting with the RAG agent,
 * as well as a simple web UI for demonstration purposes.
 * </p>
 *
 * @author zth9
 * @since 2026-01-22
 */
@Controller
@RequestMapping("/api/rag")
public class RagAgentController {

	private static final Logger logger = LoggerFactory.getLogger(RagAgentController.class);

	private final ReactAgent ragAgent;

	public RagAgentController(ReactAgent ragAgent) {
		this.ragAgent = ragAgent;
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@PostMapping("/chat")
	@ResponseBody
	public ChatResponse chat(@RequestBody ChatRequest request) {
		logger.info("Received chat request: {}", request.message());

		String threadId = request.threadId();
		if (threadId == null || threadId.isEmpty()) {
			threadId = UUID.randomUUID().toString();
		}

		try {
			RunnableConfig config = RunnableConfig.builder().threadId(threadId).build();
			
			NodeOutput result = ragAgent.invokeAndGetOutput(request.message(), config).orElse(null);

			String response = extractResponse(result);

			logger.info("Agent response: {}", response);
			return new ChatResponse(response, threadId, true);
		}
		catch (Exception e) {
			logger.error("Error processing chat request", e);
			return new ChatResponse("Sorry, an error occurred: " + e.getMessage(), threadId, false);
		}
	}

	@GetMapping("/chat")
	@ResponseBody
	public ChatResponse chatGet(@RequestParam("message") String message,
			@RequestParam(value = "threadId", required = false) String threadId) {
		return chat(new ChatRequest(message, threadId));
	}

	private String extractResponse(NodeOutput result) {
		if (result == null) {
			return "No response generated.";
		}

		OverAllState state = result.state();

		// Try "output" key first (common for ReactAgent)
		Optional<Object> output = state.value("output");
		if (output.isPresent()) {
			return String.valueOf(output.get());
		}

		// Fallback to "messages" key
		Optional<List<AbstractMessage>> messages = state.value("messages");
		if (messages.isPresent() && !messages.get().isEmpty()) {
			List<AbstractMessage> msgList = messages.get();
			return msgList.get(msgList.size() - 1).getText();
		}

		// Last resort: return state string representation
		return state.toString();
	}

	public record ChatRequest(String message, String threadId) {
	}

	public record ChatResponse(String response, String threadId, boolean success) {
	}

}
