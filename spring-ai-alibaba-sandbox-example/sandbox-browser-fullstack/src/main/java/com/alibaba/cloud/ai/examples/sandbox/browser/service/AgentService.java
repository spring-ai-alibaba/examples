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
package com.alibaba.cloud.ai.examples.sandbox.browser.service;

import com.alibaba.cloud.ai.examples.sandbox.browser.agent.BrowserUseAgent;
import com.alibaba.cloud.ai.examples.sandbox.browser.model.BrowserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class AgentService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SessionManager sessionManager;

    public void chatWithStreaming(String sessionId, String message, Consumer<String> onChunk) {
        BrowserUseAgent agent = sessionManager.getOrCreateAgent(sessionId);
        try {
            agent.initialize(sessionId);
            agent.chat(message, onChunk);
        } catch (Exception e) {
            onChunk.accept("Error: " + e.getMessage());
        }
    }

    public BrowserInfo getBrowserInfo(String sessionId) {
        BrowserUseAgent agent = sessionManager.getAgent(sessionId);
        if (agent == null) {
            return null;
        }
        return agent.getBrowserInfo();
    }

}
