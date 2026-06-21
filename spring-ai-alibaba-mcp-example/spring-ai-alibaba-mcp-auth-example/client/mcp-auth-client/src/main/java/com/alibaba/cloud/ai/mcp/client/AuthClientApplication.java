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

package com.alibaba.cloud.ai.mcp.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.Scanner;

/**
 * @author yingzi
 * @since 2025/6/28
 */
@SpringBootApplication
public class AuthClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthClientApplication.class, args);
    }

    @Bean
    public CommandLineRunner predefinedQuestions(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools,
                                                 ConfigurableApplicationContext context,
                                                 @Value("${mcp.client.smoke-question:}") String smokeQuestion) {

        ToolCallback[] toolCallbacks = tools.getToolCallbacks();
        System.out.println("Available Tools:");
        for (ToolCallback toolCallback : toolCallbacks) {
            System.out.println("Tool: " + toolCallback.getToolDefinition().name());
        }

        return args -> {
            var chatClient = chatClientBuilder
                    .defaultToolCallbacks(toolCallbacks)
                    .build();

            if (StringUtils.hasText(smokeQuestion)) {
                System.out.println("\n>>> QUESTION: " + smokeQuestion);
                System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(smokeQuestion).call().content());
                context.close();
                return;
            }

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\n>>> QUESTION: ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
                System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
            }
            scanner.close();
            context.close();
        };
    }
}
