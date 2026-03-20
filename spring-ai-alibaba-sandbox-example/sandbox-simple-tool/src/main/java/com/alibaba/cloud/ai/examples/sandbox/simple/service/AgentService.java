/*
 * Copyright 2024-2026 the original author or authors.
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
package com.alibaba.cloud.ai.examples.sandbox.simple.service;

import com.alibaba.cloud.ai.examples.sandbox.simple.model.ChatRequest;
import com.alibaba.cloud.ai.examples.sandbox.simple.model.ChatResponse;
import com.alibaba.cloud.ai.examples.sandbox.simple.model.ToolInfo;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Agent Service
 *
 * Business logic for handling chat interactions with the Agent.
 */
@Service
public class AgentService {

    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Process chat message
     */
    public ChatResponse chat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            // Create a new agent instance for this request
            ReactAgent agent = applicationContext.getBean(ReactAgent.class);

            // Create user message
            UserMessage userMessage = new UserMessage(request.getMessage());

            // Invoke agent
            logger.debug("Invoking agent with message: {}", request.getMessage());
            Optional<OverAllState> result = agent.invoke(userMessage);

            if (result.isEmpty()) {
                logger.warn("Agent returned empty result");
                return ChatResponse.builder()
                        .message("No response from agent")
                        .success(false)
                        .timestamp(getCurrentTimestamp())
                        .processingTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }

            // Extract response message
            OverAllState state = result.get();
            String responseText = extractResponseText(state);

            long processingTime = System.currentTimeMillis() - startTime;
            logger.info("Agent processing completed in {}ms", processingTime);

            return ChatResponse.builder()
                    .message(responseText)
                    .success(true)
                    .timestamp(getCurrentTimestamp())
                    .processingTimeMs(processingTime)
                    .build();

        } catch (Exception e) {
            logger.error("Error during agent invocation", e);
            throw new RuntimeException("Failed to process chat request", e);
        }
    }

    /**
     * List available tools
     */
    public List<ToolInfo> listTools() {
        List<ToolInfo> tools = new ArrayList<>();
        tools.add(ToolInfo.builder()
                .name("add")
                .description("Add two numbers")
                .category("calculator")
                .build());
        tools.add(ToolInfo.builder()
                .name("subtract")
                .description("Subtract two numbers")
                .category("calculator")
                .build());
        tools.add(ToolInfo.builder()
                .name("multiply")
                .description("Multiply two numbers")
                .category("calculator")
                .build());
        tools.add(ToolInfo.builder()
                .name("divide")
                .description("Divide two numbers")
                .category("calculator")
                .build());
        tools.add(ToolInfo.builder()
                .name("getWeather")
                .description("Get weather information for a city")
                .category("weather")
                .build());
        tools.add(ToolInfo.builder()
                .name("runPythonCode")
                .description("Execute Python code in sandbox")
                .category("sandbox")
                .build());
        tools.add(ToolInfo.builder()
                .name("runShellCommand")
                .description("Execute shell command in sandbox")
                .category("sandbox")
                .build());
        tools.add(ToolInfo.builder()
                .name("browserNavigate")
                .description("Navigate browser to URL")
                .category("browser")
                .build());
        return tools;
    }

    /**
     * Reset conversation
     */
    public void reset() {
        logger.info("Resetting conversation session");
        // In this simple example, each request creates a new agent,
        // so there's no persistent state to reset.
        // In a production app, you might clear session storage here.
    }

    /**
     * Extract response text from agent state
     */
    private String extractResponseText(OverAllState state) {
        Optional<Object> messagesOpt = state.value("messages");
        if (messagesOpt.isEmpty()) {
            return "No response";
        }

        Object messagesObj = messagesOpt.get();
        if (!(messagesObj instanceof List)) {
            return messagesObj.toString();
        }

        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) messagesObj;

        // Find the last assistant message
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            if (msg instanceof AssistantMessage assistantMsg) {
                return assistantMsg.getText();
            }
        }

        return "No response from assistant";
    }

    /**
     * Get current timestamp
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
