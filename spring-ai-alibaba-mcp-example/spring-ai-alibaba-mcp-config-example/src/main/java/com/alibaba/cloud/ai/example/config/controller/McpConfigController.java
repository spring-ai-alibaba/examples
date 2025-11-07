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

package com.alibaba.cloud.ai.example.config.controller;

import com.alibaba.cloud.ai.mcp.router.config.McpRouterProperties;
import com.alibaba.cloud.ai.mcp.router.core.discovery.McpServiceDiscovery;
import com.alibaba.cloud.ai.mcp.router.model.McpServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.StringJoiner;


@RestController
public class McpConfigController {

    private final McpRouterProperties properties;
    private final McpServiceDiscovery discovery;

    @Autowired
    public McpConfigController(McpRouterProperties properties, McpServiceDiscovery discovery) {
        this.properties = properties;
        this.discovery = discovery;
    }

    @GetMapping("/discovery-type")
    public String getDiscoveryType() {
        String className = discovery.getClass().getSimpleName();
        return "当前使用的 McpServiceDiscovery 实现类是: " + className;
    }

    @GetMapping("/file/services")
    public String getMcpServices() {
        StringJoiner result = new StringJoiner("\n");
        result.add("=== MCP服务配置信息 ===");

        List<McpServerInfo> services = properties.getServices();

        if (services.isEmpty()) {
            return result.add("未找到配置的服务信息").toString();
        }

        for (int i = 0; i < services.size(); i++) {
            McpServerInfo service = services.get(i);
            result.add("\n--- 服务 #" + (i+1) + " ---")
                    .add("名称: " + service.getName())
                    .add("描述: " + service.getDescription())
                    .add("协议: " + service.getProtocol())
                    .add("版本: " + service.getVersion())
                    .add("端点: " + service.getEndpoint())
                    .add("是否启用: " + service.getEnabled())
                    .add("标签: " + service.getTags());

            McpServerInfo discoveredService = discovery.getService(service.getName());
            result.add("通过发现服务获取: " + (discoveredService != null ? "成功" : "失败"));
        }

        return result.toString();
    }

    @GetMapping("/query/{serviceName}")
    public McpServerInfo getMcpServiceInfo(@PathVariable String serviceName) {
        return discovery.getService(serviceName);
    }

}
