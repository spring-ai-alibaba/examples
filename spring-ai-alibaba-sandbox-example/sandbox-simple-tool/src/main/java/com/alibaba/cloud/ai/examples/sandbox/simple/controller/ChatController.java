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
package com.alibaba.cloud.ai.examples.sandbox.simple.controller;

import com.alibaba.cloud.ai.examples.sandbox.simple.model.ChatRequest;
import com.alibaba.cloud.ai.examples.sandbox.simple.model.ChatResponse;
import com.alibaba.cloud.ai.examples.sandbox.simple.model.ToolInfo;
import com.alibaba.cloud.ai.examples.sandbox.simple.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Chat Controller
 *
 * Provides REST API endpoints for interacting with the Agent.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private AgentService agentService;

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Spring AI Alibaba Sandbox Simple Tool Example"
        ));
    }

    /**
     * Send message to agent
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        logger.info("Received chat request: {}", request.getMessage());
        try {
            ChatResponse response = agentService.chat(request);
            logger.info("Chat response generated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing chat request", e);
            return ResponseEntity.internalServerError()
                    .body(ChatResponse.builder()
                            .message("Error: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    /**
     * List available tools
     */
    @GetMapping("/tools")
    public ResponseEntity<List<ToolInfo>> listTools() {
        List<ToolInfo> tools = agentService.listTools();
        return ResponseEntity.ok(tools);
    }

    /**
     * Reset conversation session
     */
    @DeleteMapping("/reset")
    public ResponseEntity<Map<String, String>> reset() {
        agentService.reset();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Session reset successfully"
        ));
    }

}
