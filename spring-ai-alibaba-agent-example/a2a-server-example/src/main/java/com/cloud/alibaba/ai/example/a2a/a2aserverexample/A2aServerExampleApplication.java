package com.cloud.alibaba.ai.example.a2a.a2aserverexample;

import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
public class A2aServerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(A2aServerExampleApplication.class, args);
    }

    @Configuration
    public class RootAgentConfiguration {

        private static final String SYSTEM_PROMPT =
            "You are an assistant specializing in Spring AI Alibaba. Your role is to provide accurate, helpful, and context-aware support for any questions related to Spring AI Alibaba, including its features, integration, configuration, usage patterns, and best practices.\n"
                + "\n"
                + "If a question is not related to Spring AI Alibaba, please politely decline to answer with a brief apology.\n"
                + "\n"
                + "When addressing questions about Spring AI Alibaba, leverage available tools to retrieve up-to-date documentation, analyze configurations, or validate code examples. If no suitable tools are available, rely on your internal knowledge to provide a clear and informative response.\n"
                + "\n"
                + "Always aim to assist developers in effectively using Spring AI Alibaba within their applications.\n"
                + "\n";

        @Bean
        @Primary
        public Agent rootAgent(ChatModel chatModel) throws GraphStateException {
            return ReactAgent.builder()
                   .name("SaaAgent")
                   .description(
                    "Answer question about Spring AI ALIbaba and do some maintain and query operation about Spring AI Alibaba by chinese.")
                   .model(chatModel)
                   .instruction(SYSTEM_PROMPT)
                   .outputKey("messages")
                   .build();
        }
    }
}
