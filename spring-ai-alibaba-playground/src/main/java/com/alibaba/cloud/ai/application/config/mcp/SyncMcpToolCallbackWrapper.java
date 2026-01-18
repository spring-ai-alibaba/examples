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

package com.alibaba.cloud.ai.application.config.mcp;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.mcp.SyncMcpToolCallback;

import java.lang.reflect.Field;

/**
 * Get MCP Server name, return to frontend show.
 * There's no good way, let's do it like this first~~~~
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class SyncMcpToolCallbackWrapper {

	private final SyncMcpToolCallback callback;

	public SyncMcpToolCallbackWrapper(SyncMcpToolCallback callback) {
		this.callback = callback;
	}

	public McpSyncClient getMcpClient() {

		try {
			Field field = SyncMcpToolCallback.class.getDeclaredField("mcpClient");
			field.setAccessible(true);
			return (McpSyncClient) field.get(callback);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
