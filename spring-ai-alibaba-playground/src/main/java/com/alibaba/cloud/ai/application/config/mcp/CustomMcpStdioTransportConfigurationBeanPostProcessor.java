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

package com.alibaba.cloud.ai.application.config.mcp;

import com.alibaba.cloud.ai.application.entity.mcp.McpServerConfig;
import com.alibaba.cloud.ai.application.exception.SAAAppException;
import com.alibaba.cloud.ai.application.mcp.McpServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.client.common.autoconfigure.StdioTransportAutoConfiguration;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpStdioClientProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.alibaba.cloud.ai.application.mcp.McpServerUtils.getMcpLibsAbsPath;

/**
 * @author brianxiadong
 * @author yuluo
 *
 * MCP configuration enhancements to handle the path to the jar in the server with more friendly yaml semantic definitions.
 */

@Component
public class CustomMcpStdioTransportConfigurationBeanPostProcessor implements BeanPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(CustomMcpStdioTransportConfigurationBeanPostProcessor.class);

	private final ObjectMapper objectMapper;

	private final McpStdioClientProperties mcpStdioClientProperties;

	public CustomMcpStdioTransportConfigurationBeanPostProcessor(
			ObjectMapper objectMapper,
			McpStdioClientProperties mcpStdioClientProperties
	) {
		this.objectMapper = objectMapper;
		this.mcpStdioClientProperties = mcpStdioClientProperties;
	}

	@NotNull
	@Override
	public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {

		if (bean instanceof StdioTransportAutoConfiguration) {

			logger.debug("Enhancement McpStdioTransportConfiguration bean start: {}", beanName);

			McpServerConfig mcpServerConfig;
			try {
				mcpServerConfig = McpServerUtils.getMcpServerConfig();

				// Handle the jar relative path issue in the configuration file.
				for (Map.Entry<String, McpStdioClientProperties.Parameters> entry : mcpServerConfig.getMcpServers()
						.entrySet()) {

					if (entry.getValue() != null && entry.getValue().command().startsWith("java")) {

						McpStdioClientProperties.Parameters serverConfig = entry.getValue();
						String oldMcpLibsPath = McpServerUtils.getLibsPath(serverConfig.args());
						String rewriteMcpLibsAbsPath = getMcpLibsAbsPath(McpServerUtils.getLibsPath(serverConfig.args()));
						if (rewriteMcpLibsAbsPath != null) {
							serverConfig.args().remove(oldMcpLibsPath);
							serverConfig.args().add(rewriteMcpLibsAbsPath);
						}
					}
				}

				String msc = objectMapper.writeValueAsString(mcpServerConfig);
				logger.debug("Registry McpServer config: {}", msc);

				// write mcp client
				mcpStdioClientProperties.setServersConfiguration(new ByteArrayResource(msc.getBytes()));
				((StdioTransportAutoConfiguration) bean).stdioTransports(this.mcpStdioClientProperties);
			}
			catch (IOException e) {
				throw new SAAAppException(e.getMessage());
			}

			logger.debug("Enhancement McpStdioTransportConfiguration bean end: {}", beanName);
		}

		return bean;
	}

}
