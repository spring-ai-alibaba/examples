/*
 * Copyright 2024-2025 the original author or authors.
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

package com.alibaba.cloud.ai.example.config;

import com.alibaba.cloud.ai.mcp.router.core.discovery.McpServiceDiscovery;
import com.alibaba.cloud.ai.mcp.router.core.discovery.McpServiceDiscoveryFactory;
import com.alibaba.cloud.ai.mcp.router.model.McpServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * MCP Multi Source Discovery Example
 * supports multiple service discovery sources: file, database, Nacos
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.alibaba.cloud.ai.example.config", "com.alibaba.cloud.ai.mcp.router", "com.alibaba.cloud.ai.autoconfigure.mcp.router"})
public class McpMultiConfigApplication {

	private static final Logger log = LoggerFactory.getLogger(McpMultiConfigApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(McpMultiConfigApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(McpServiceDiscovery mcpServiceDiscovery, McpServiceDiscoveryFactory factory) {
		return args -> {
			log.info("=== MCP 多源服务发现演示 ===");

			// 显示已注册的服务发现类型
			log.info("已注册的服务发现类型: {}", factory.getRegisteredTypes());
			log.info("注册的服务发现实现数量: {}", factory.size());

			// 测试服务查找
			testServiceDiscovery(mcpServiceDiscovery, "weather-service");
			testServiceDiscovery(mcpServiceDiscovery, "dashscope-chat");
			testServiceDiscovery(mcpServiceDiscovery, "search-service");
			testServiceDiscovery(mcpServiceDiscovery, "non-existent-service");

			log.info("=== 演示完成 ===");
		};
	}

	private void testServiceDiscovery(McpServiceDiscovery discovery, String serviceName) {
		log.info("查找服务: {}", serviceName);
		try {
			McpServerInfo serverInfo = discovery.getService(serviceName);
			if (serverInfo != null) {
				log.info("  ✓ 找到服务: {}", serverInfo.getName());
				log.info("    描述: {}", serverInfo.getDescription());
				log.info("    协议: {}", serverInfo.getProtocol());
				log.info("    版本: {}", serverInfo.getVersion());
				log.info("    端点: {}", serverInfo.getEndpoint());
				log.info("    标签: {}", serverInfo.getTags());
			}
			else {
				log.warn("  ✗ 未找到服务: {}", serviceName);
			}
		}
		catch (Exception e) {
			log.error("  ✗ 查找服务时发生错误: {}", serviceName, e);
		}
		log.info("");
	}

}
