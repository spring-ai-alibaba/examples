/*
 * Copyright 2024-2025 Alibaba Group Holding Limited.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.examples.sandbox.browser.websocket;

import com.alibaba.cloud.ai.examples.sandbox.browser.service.AgentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final AgentService agentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatWebSocketHandler(AgentService agentService) {
        this.agentService = agentService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String sessionId = payload.get("sessionId");
        String userMessage = payload.get("message");

        agentService.chatWithStreaming(sessionId, userMessage, chunk -> {
            try {
                session.sendMessage(new TextMessage(chunk));
            } catch (Exception e) {
                // Handle error
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Cleanup
    }

}
