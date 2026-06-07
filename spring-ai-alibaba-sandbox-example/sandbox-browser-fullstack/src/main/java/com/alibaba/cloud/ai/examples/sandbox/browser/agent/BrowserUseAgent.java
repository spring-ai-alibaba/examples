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
package com.alibaba.cloud.ai.examples.sandbox.browser.agent;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.sandbox.ToolkitInit;
import com.alibaba.cloud.ai.examples.sandbox.browser.model.BrowserInfo;
import io.agentscope.runtime.sandbox.box.BrowserSandbox;
import io.agentscope.runtime.sandbox.manager.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class BrowserUseAgent {

    private static final Logger logger = LoggerFactory.getLogger(BrowserUseAgent.class);

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private SandboxService sandboxService;

    private BrowserSandbox browserSandbox;
    private ReactAgent agent;
    private List<Message> conversationHistory = new ArrayList<>();

    public synchronized void initialize(String sessionId) throws Exception {
        if (browserSandbox != null) {
            return;
        }

        logger.info("Initializing BrowserUseAgent for session: {}", sessionId);

        browserSandbox = new BrowserSandbox(
                sandboxService,
                "browser-user",
                sessionId
        );

        agent = ReactAgent.builder()
                .name("BrowserAssistant")
                .model(chatModel)
                .description("An AI assistant that can browse the web and perform tasks")
                .instruction("""
                        You are an intelligent web browsing assistant. You can:
                        - Navigate to web pages
                        - Extract information from websites
                        - Fill out forms
                        - Click buttons and links
                        - Take screenshots
                        - Perform web research
                        Always explain what you're doing and provide helpful insights.
                        Be accurate and thorough in your web interactions.
                        """)
                .tools(List.of(ToolkitInit.BrowserNavigateTool(browserSandbox)))
                .build();

        logger.info("BrowserUseAgent initialized successfully");
    }

    public void chat(String userMessage, Consumer<String> onChunk) {
        try {
            UserMessage msg = new UserMessage(userMessage);
            conversationHistory.add(msg);

            logger.info("Processing message: {}", userMessage);

            Optional<OverAllState> result = agent.invoke(msg);

            if (result.isPresent()) {
                String response = extractResponse(result.get());
                String[] words = response.split(" ");
                for (String word : words) {
                    onChunk.accept(word + " ");
                    Thread.sleep(50);
                }
                conversationHistory.add(new AssistantMessage(response));
            } else {
                onChunk.accept("No response from agent.");
            }

        } catch (Exception e) {
            logger.error("Error processing message", e);
            onChunk.accept("Error: " + e.getMessage());
        }
    }

    public BrowserInfo getBrowserInfo() {
        if (browserSandbox == null) {
            return null;
        }

        BrowserInfo info = new BrowserInfo();
        info.setDesktopUrl(browserSandbox.getDesktopUrl());
        info.setSessionId(browserSandbox.getSessionId());
        info.setStatus("active");
        return info;
    }

    private String extractResponse(OverAllState state) {
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

        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            if (msg instanceof AssistantMessage assistantMsg) {
                return assistantMsg.getText();
            }
        }

        return "No response";
    }

    public void cleanup() {
        if (browserSandbox != null) {
            try {
                browserSandbox.close();
            } catch (Exception e) {
                logger.error("Error closing browser sandbox", e);
            }
        }
        conversationHistory.clear();
    }

}
