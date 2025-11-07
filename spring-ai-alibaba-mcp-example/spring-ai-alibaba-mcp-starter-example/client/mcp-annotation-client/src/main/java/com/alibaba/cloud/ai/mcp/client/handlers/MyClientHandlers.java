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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springaicommunity.mcp.annotation.McpProgress;
import org.springaicommunity.mcp.annotation.McpSampling;
import org.springframework.stereotype.Component;

/**
 * @author yingzi
 * @since 2025/10/22
 */
@Component
public class MyClientHandlers {

    private static final Logger logger = LoggerFactory.getLogger(MyClientHandlers.class);

    @McpLogging(clients = "my-mcp-client")
    public void handleLogs(McpSchema.LoggingMessageNotification notification) {
        // Handle logs
        logger.info("Logs: {}", notification.data());
    }

    @McpSampling(clients = "my-mcp-client")
    public McpSchema.CreateMessageResult handleSampling(McpSchema.CreateMessageRequest request) {
        // Handle sampling
        logger.info("Sampling: {}", request.messages());
        return null;
    }

    @McpProgress(clients = "my-mcp-client")
    public void handleProgress(McpSchema.ProgressNotification notification) {
        // Handle progress
        logger.info("Progress: {}", notification);
    }
}
