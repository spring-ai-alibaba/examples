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
 * @since 2025/11/2
 */
@SpringBootApplication
public class StreamableClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamableClientApplication.class, args);
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
