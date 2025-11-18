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

package com.alibaba.cloud.ai.graph.node;

import com.alibaba.cloud.ai.graph.GraphResponse;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.streaming.FluxConverter;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Streaming Chat Node
 *
 * Supports real-time streaming output, returning AI responses as a stream. This node
 * processes input data and generates streaming AI responses using ChatClient.
 *
 * Features: - Real-time streaming AI responses - Fallback mechanism for failed streaming
 * - Configurable input/output keys - Comprehensive logging
 *
 * @author sixiyida
 */
public class StreamingChatNode implements NodeAction {

	private static final Logger logger = LoggerFactory.getLogger(StreamingChatNode.class);

	private final String nodeName;

	private final String inputKey;

	private final String outputKey;

	private final ChatClient chatClient;

	private final String prompt;

	/**
	 * Constructor for StreamingChatNode
	 * @param nodeName the name of the node
	 * @param inputKey the key for input data
	 * @param outputKey the key for output data
	 * @param chatClient the chat client for AI processing
	 * @param prompt the prompt template
	 */
	public StreamingChatNode(String nodeName, String inputKey, String outputKey, ChatClient chatClient, String prompt) {
		this.nodeName = nodeName;
		this.inputKey = inputKey;
		this.outputKey = outputKey;
		this.chatClient = chatClient;
		this.prompt = prompt;
	}

	@Override
	public Map<String, Object> apply(OverAllState state) throws Exception {
		logger.info("{} starting streaming processing", nodeName);

		// Get input data
		String inputData = state.value(inputKey).map(Object::toString).orElse("Default input");

		logger.info("{} input data: {}", nodeName, inputData);

		// Build complete prompt
		String fullPrompt = prompt + " Input content: " + inputData;

		// 添加调试信息
		logger.info("{} full prompt length: {} characters", nodeName, fullPrompt.length());
		logger.info("{} using ChatClient: {}", nodeName, chatClient.getClass().getSimpleName());

		try {
			// Create streaming chat response
			Flux<ChatResponse> chatResponseFlux = chatClient.prompt()
				.user(fullPrompt)
				.stream()
				.chatResponse()
				.doOnSubscribe(sub -> logger.info("{}: chatResponseFlux subscribed", nodeName))
				.doOnNext(resp -> logger.info("{}: chatResponseFlux emit: {}", nodeName, resp))
				.doOnError(e -> logger.error("{}: chatResponseFlux error", nodeName, e))
				.doOnComplete(() -> logger.info("{}: chatResponseFlux complete", nodeName))
				.timeout(java.time.Duration.ofMinutes(2)) // 添加超时处理
				.onErrorResume(e -> {
					logger.error("{}: chatResponseFlux timeout or error, using fallback", nodeName, e);
					return Flux.empty();
				});

			// Wrap streaming response with StreamingChatGenerator
			Flux<GraphResponse<StreamingOutput>> generator = FluxConverter.builder()
					.startingNode(nodeName + "_stream")
					.startingState(state)
					.mapResult(resp -> {
						String content = resp.getResult().getOutput().getText();
						logger.info("{} mapResult emit content: {}", nodeName, content);
						return Map.of(outputKey, content);
					})
					.build(chatResponseFlux);

			logger.info("{} streaming processing setup completed", nodeName);
			return Map.of(outputKey, generator);

		}
		catch (Exception e) {
			logger.error("{} streaming processing failed: {}", nodeName, e.getMessage(), e);

			// Fallback processing: return regular synchronous response
			String fallbackResult = String.format("[%s] Streaming failed, fallback processing: %s", nodeName,
					inputData);
			return Map.of(outputKey, fallbackResult);
		}
	}

	/**
	 * Factory method to create StreamingChatNode
	 * @param nodeName the name of the node
	 * @param inputKey the key for input data
	 * @param outputKey the key for output data
	 * @param chatClient the chat client for AI processing
	 * @param prompt the prompt template
	 * @return StreamingChatNode instance
	 */
	public static StreamingChatNode create(String nodeName, String inputKey, String outputKey, ChatClient chatClient,
			String prompt) {
		return new StreamingChatNode(nodeName, inputKey, outputKey, chatClient, prompt);
	}

}
