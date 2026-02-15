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
package com.alibaba.cloud.ai.examples.sandbox.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring AI Alibaba Sandbox Simple Tool Example Application
 *
 * This application demonstrates how to integrate Sandbox tools with Spring AI Alibaba.
 *
 * Features:
 * - BaseSandbox for Python/Shell code execution
 * - BrowserSandbox for browser automation
 * - Custom tools (Calculator, Weather)
 * - REST API for chat interaction
 *
 * @author Spring AI Alibaba Team
 */
@SpringBootApplication
public class SimpleSandboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSandboxApplication.class, args);
        printWelcomeMessage();
    }

    private static void printWelcomeMessage() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🎉 Spring AI Alibaba Sandbox Simple Tool Example Started Successfully!");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("📡 Available API Endpoints:");
        System.out.println("  • Health Check: http://localhost:8080/api/chat/health");
        System.out.println("  • Send Message: POST http://localhost:8080/api/chat");
        System.out.println("  • List Tools: http://localhost:8080/api/chat/tools");
        System.out.println("  • Reset Session: DELETE http://localhost:8080/api/chat/reset");
        System.out.println();
        System.out.println("🛠️ Available Tools:");
        System.out.println("  • Calculator Tool - Mathematical operations (add, subtract, multiply, etc.)");
        System.out.println("  • Weather Tool - Query weather information for cities");
        System.out.println("  • Python Runner - Execute Python code in sandbox");
        System.out.println("  • Shell Runner - Execute shell commands in sandbox");
        System.out.println("  • Browser Navigator - Automate browser operations");
        System.out.println();
        System.out.println("💡 Quick Test:");
        System.out.println("  curl -X POST http://localhost:8080/api/chat \\");
        System.out.println("    -H \"Content-Type: application/json\" \\");
        System.out.println("    -d '{\"message\": \"Use Python to calculate the 10th Fibonacci number\"}'");
        System.out.println();
        System.out.println("📚 Documentation:");
        System.out.println("  • README.md for detailed documentation");
        System.out.println();
        System.out.println("=".repeat(80) + "\n");
    }

}
