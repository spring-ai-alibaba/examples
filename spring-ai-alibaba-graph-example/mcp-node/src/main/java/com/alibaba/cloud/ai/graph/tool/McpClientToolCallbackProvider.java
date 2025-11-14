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

package com.alibaba.cloud.ai.graph.tool;

import com.alibaba.cloud.ai.graph.config.McpNodeProperties;
import org.apache.commons.compress.utils.Lists;
import org.glassfish.jersey.internal.guava.Sets;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpClientCommonProperties;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author yingzi
 * @since 2025/6/14
 */
@Service
public class McpClientToolCallbackProvider {

    private final ToolCallbackProvider toolCallbackProvider;

    private final McpClientCommonProperties commonProperties;

    private final McpNodeProperties mcpNodeProperties;

    public McpClientToolCallbackProvider(ToolCallbackProvider toolCallbackProvider,
                                         McpClientCommonProperties commonProperties, McpNodeProperties mcpNodeProperties) {
        this.toolCallbackProvider = toolCallbackProvider;
        this.commonProperties = commonProperties;
        this.mcpNodeProperties = mcpNodeProperties;
    }

    public Set<ToolCallback> findToolCallbacks(String nodeName) {
        Set<ToolCallback> defineCallback = Sets.newHashSet();
        Set<String> mcpClients = mcpNodeProperties.getNode2servers().get(nodeName);
        if (mcpClients == null || mcpClients.isEmpty()) {
            return defineCallback;
        }

        List<String> exceptMcpClientNames = Lists.newArrayList();
        for (String mcpClient : mcpClients) {
            // my-mcp-client
            String name = commonProperties.getName();
            // my_mcp_client_server1
            String prefixedMcpClientName = McpToolUtils.prefixedToolName(name, mcpClient);
            exceptMcpClientNames.add(prefixedMcpClientName);
        }

        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
        for (ToolCallback toolCallback : toolCallbacks) {
            ToolDefinition toolDefinition = toolCallback.getToolDefinition();
            // my_mcp_client_server1_getCityTimeMethod
            String name = toolDefinition.name();
            for (String exceptMcpClientName : exceptMcpClientNames) {
                if (name.startsWith(exceptMcpClientName)) {
                    defineCallback.add(toolCallback);
                }
            }
        }
        return defineCallback;
    }
}
