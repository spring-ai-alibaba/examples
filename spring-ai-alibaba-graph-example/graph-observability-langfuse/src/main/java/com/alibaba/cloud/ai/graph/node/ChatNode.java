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

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Chat Node with ChatClient
 *
 * A standard node that processes input using ChatClient for AI interactions. This node
 * handles synchronous AI processing with fallback mechanisms.
 *
 * Features: - Synchronous AI processing - Fallback mechanism for failed requests -
 * Configurable input/output keys - Execution logging
 *
 * @author sixiyida
 */
public class ChatNode implements NodeAction {

	private static final Logger logger = LoggerFactory.getLogger(ChatNode.class);

	private final String nodeName;

	private final String inputKey;

	private final String outputKey;

	private final ChatClient chatClient;

	private final String prompt;

	/**
	 * Constructor for ChatNode
	 * @param nodeName the name of the node
	 * @param inputKey the key for input data
	 * @param outputKey the key for output data
	 * @param chatClient the chat client for AI processing
	 * @param prompt the prompt template
	 */
	public ChatNode(String nodeName, String inputKey, String outputKey, ChatClient chatClient, String prompt) {
		this.nodeName = nodeName;
		this.inputKey = inputKey;
		this.outputKey = outputKey;
		this.chatClient = chatClient;
		this.prompt = prompt;
	}

	@Override
	public Map<String, Object> apply(OverAllState state) throws Exception {
		// Get input data
		String inputData = state.value(inputKey).map(Object::toString).orElse("Default input");

		logger.info("{} is running, inputKey:{}, inputData:{}, state: {}", nodeName, inputKey, inputData,
				JSON.toJSONString(state));

		// Process using ChatClient
		String result;
		try {
			result = chatClient.prompt().user(prompt + " Input content: " + inputData).call().content();
		}
		catch (Exception e) {
			// If ChatClient call fails, use simulated result
			result = String.format("[%s] Simulated processing result: %s", nodeName, inputData);
		}

		logger.info("{} is finished", nodeName);

		// Record execution log
		String logEntry = String.format("[%s] Node execution completed", nodeName);

		Map<String, Object> response = new HashMap<>();
		response.put(outputKey, result);
		response.put("logs", logEntry);

		return response;
	}

	/**
	 * Factory method to create ChatNode
	 * @param nodeName the name of the node
	 * @param inputKey the key for input data
	 * @param outputKey the key for output data
	 * @param chatClient the chat client for AI processing
	 * @param prompt the prompt template
	 * @return ChatNode instance
	 */
	public static ChatNode create(String nodeName, String inputKey, String outputKey, ChatClient chatClient,
			String prompt) {
		return new ChatNode(nodeName, inputKey, outputKey, chatClient, prompt);
	}

}
