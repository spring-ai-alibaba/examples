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

package org.springframework.ai.mcp.samples.sqlite;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;

import io.modelcontextprotocol.json.McpJsonMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner interactiveChat(ChatClient.Builder chatClientBuilder,
			List<McpSyncClient> mcpClients,
			ConfigurableApplicationContext context) {
		return args -> {

			var chatClient = chatClientBuilder
					.defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpClients))
					.defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
					.build();

			var scanner = new Scanner(System.in);
			System.out.println("\nStarting interactive chat session. Type 'exit' to quit.");

			try {
				while (true) {
					System.out.print("\nUSER: ");
					String input = scanner.nextLine();

					if (input.equalsIgnoreCase("exit")) {
						System.out.println("Ending chat session.");
						break;
					}

					System.out.print("ASSISTANT: ");
					System.out.println(chatClient.prompt(input).call().content());
				}
			} finally {
				scanner.close();
				context.close();
			}

		};
	}


	@Bean(destroyMethod = "close")
	public McpSyncClient mcpClient() {

		// Windows 需要替换 uvx.exe
		var stdioParams = ServerParameters.builder("uvx")
				.args("mcp-server-sqlite", "--db-path",
						getDbPath())
				.build();

		var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams, McpJsonMapper.getDefault()))
				.requestTimeout(Duration.ofSeconds(10)).build();

		var init = mcpClient.initialize();

		System.out.println("MCP Initialized: " + init);

		return mcpClient;

	}

	// 如果使用 IDEA 一键启动，需要配置 working dir 或者调整此路径
	// spring-ai-alibaba-mcp-example/spring-ai-alibaba-manual-mcp-example/sqlite/ai-mcp-sqlite/test.db
	private static String getDbPath() {
		return Paths.get(System.getProperty("user.dir"), "test.db").toString();
	}

}
