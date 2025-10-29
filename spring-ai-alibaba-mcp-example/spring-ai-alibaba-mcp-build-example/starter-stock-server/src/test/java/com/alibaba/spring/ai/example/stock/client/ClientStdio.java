/*
 * Copyright 2025-2026 the original author or authors.
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

package com.alibaba.spring.ai.example.stock.client;

import java.util.Map;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;

/**
 * MCP server using stdio transport, automatically started by the client.
 * You need to build the server jar first:
 *
 * <pre>
 * ./mvnw clean install -DskipTests
 * </pre>
 */
public class ClientStdio {

    public static void main(String[] args) {
        var stdioParams = ServerParameters.builder("java")
                .args("-Dspring.ai.mcp.server.stdio=true",
                        "-Dspring.main.web-application-type=none",
                        "-Dlogging.pattern.console=",
                        "-jar",
                        "spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-build-example/starter-stock-server/target/starter-stock-server-1.0.0.jar")
                .build();

        var transport = new StdioClientTransport(stdioParams, McpJsonMapper.getDefault());
        var client = McpClient.sync(transport).build();

        try {
            client.initialize();

            // List and display available tools
            ListToolsResult toolsList = client.listTools();
            System.out.println("Available tools = " + toolsList);

            // Test Shanghai stock
            System.out.println("\nTesting Shanghai stock (600519):");
            CallToolResult shStockResult = client.callTool(new CallToolRequest("getStockInfo",
                    Map.of("stockCode", "600519")));
            System.out.println("Stock info: " + shStockResult);

            // Test Shenzhen stock
            System.out.println("\nTesting Shenzhen stock (000001):");
            CallToolResult szStockResult = client.callTool(new CallToolRequest("getStockInfo",
                    Map.of("stockCode", "000001")));
            System.out.println("Stock info: " + szStockResult);

            // Test non-existent stock
            System.out.println("\nTesting non-existent stock (999999):");
            try {
                CallToolResult invalidStockResult = client.callTool(new CallToolRequest("getStockInfo",
                        Map.of("stockCode", "999999")));
                System.out.println("Stock info: " + invalidStockResult);
            } catch (Exception e) {
                System.out.println("Expected error: " + e.getMessage());
            }

            // Test invalid stock code
            System.out.println("\nTesting invalid stock code (abc):");
            try {
                CallToolResult invalidCodeResult = client.callTool(new CallToolRequest("getStockInfo",
                        Map.of("stockCode", "abc")));
                System.out.println("Stock info: " + invalidCodeResult);
            } catch (Exception e) {
                System.out.println("Expected error: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error during testing: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.closeGracefully();
        }
    }
}
