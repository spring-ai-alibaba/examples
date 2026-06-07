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
package com.alibaba.cloud.ai.examples.sandbox.browser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Browser Use Fullstack Application
 *
 * A complete fullstack application demonstrating browser automation
 * using Spring AI Alibaba BrowserSandbox.
 */
@SpringBootApplication
public class BrowserAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrowserAgentApplication.class, args);
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Browser Use Fullstack Application Started!");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("Backend API: http://localhost:8080");
        System.out.println("Frontend UI: http://localhost:5173 (run 'npm run dev' in frontend/)");
        System.out.println();
        System.out.println("API Endpoints:");
        System.out.println("  POST /api/chat/stream - Stream chat messages");
        System.out.println("  GET /api/browser/info - Get browser info");
        System.out.println("  WS /ws/chat - WebSocket connection");
        System.out.println();
        System.out.println("=".repeat(80) + "\n");
    }

}
