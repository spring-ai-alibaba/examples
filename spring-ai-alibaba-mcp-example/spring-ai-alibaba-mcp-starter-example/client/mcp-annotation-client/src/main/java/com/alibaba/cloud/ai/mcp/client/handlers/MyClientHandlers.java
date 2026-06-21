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

package com.alibaba.cloud.ai.mcp.client.handlers;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CreateMessageRequest;
import io.modelcontextprotocol.spec.McpSchema.CreateMessageResult;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ProgressNotification;
import io.modelcontextprotocol.spec.McpSchema.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpLogging;
import org.springframework.ai.mcp.annotation.McpProgress;
import org.springframework.ai.mcp.annotation.McpSampling;
import org.springframework.ai.mcp.annotation.McpToolListChanged;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yingzi
 * @since 2025/10/22
 */
@Component
public class MyClientHandlers {

    private static final Logger logger = LoggerFactory.getLogger(MyClientHandlers.class);

    @McpLogging(clients = "annotation-server")
    public void handleLogs(LoggingMessageNotification notification) {
        logger.info("MCP LOG [{}] {}", notification.level(), notification.data());
    }

    @McpSampling(clients = "annotation-server")
    public CreateMessageResult handleSampling(CreateMessageRequest request) {
        logger.info("Sampling: {}", request.messages());
        String systemPrompt = request.systemPrompt() == null ? "" : request.systemPrompt();
        String response = "annotation-client local sampling response. " + systemPrompt;
        return CreateMessageResult.builder(Role.ASSISTANT, response, "local-sampling").build();
    }

    @McpProgress(clients = "annotation-server")
    public void handleProgress(ProgressNotification notification) {
        logger.info("Progress: {}", notification);
    }

    @McpToolListChanged(clients = "annotation-server")
    public void handleToolListChanged(List<McpSchema.Tool> tools) {
        logger.info("MCP tools changed: {}", tools.size());
    }
}
