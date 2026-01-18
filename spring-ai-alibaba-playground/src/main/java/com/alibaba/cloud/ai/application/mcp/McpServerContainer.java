/*
 * Copyright 2024-2026 the original author or authors.
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

package com.alibaba.cloud.ai.application.mcp;

import com.alibaba.cloud.ai.application.entity.mcp.McpServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class McpServerContainer {

	private static final List<McpServer> mcpServerContainer = new ArrayList<>();

	public static List<McpServer> getAllServers() {
		return new ArrayList<>(mcpServerContainer);
	}

	public static Optional<McpServer> getServerById(String id) {

		return mcpServerContainer.stream()
				.filter(server -> server.getId().equals(id))
				.findFirst();
	}

	public static void addServer(McpServer server) {
		mcpServerContainer.add(server);
	}

	public static boolean removeServerById(String id) {

		return mcpServerContainer.removeIf(server -> server.getId().equals(id));
	}

}
