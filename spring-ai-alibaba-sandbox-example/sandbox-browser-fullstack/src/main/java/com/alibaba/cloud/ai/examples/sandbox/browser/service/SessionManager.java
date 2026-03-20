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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<String, BrowserUseAgent> sessions = new ConcurrentHashMap<>();

    public BrowserUseAgent getOrCreateAgent(String sessionId) {
        return sessions.computeIfAbsent(sessionId, id ->
                applicationContext.getBean(BrowserUseAgent.class)
        );
    }

    public BrowserUseAgent getAgent(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        BrowserUseAgent agent = sessions.remove(sessionId);
        if (agent != null) {
            agent.cleanup();
        }
    }

}
