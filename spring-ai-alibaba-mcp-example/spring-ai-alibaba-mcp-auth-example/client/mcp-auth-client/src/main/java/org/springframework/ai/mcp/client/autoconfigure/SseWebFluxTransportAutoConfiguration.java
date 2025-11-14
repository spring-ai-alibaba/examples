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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.ai.mcp.client.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;

import org.springframework.ai.mcp.client.autoconfigure.properties.McpSseClientProperties;
import org.springframework.ai.mcp.client.common.autoconfigure.NamedClientMcpTransport;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpClientCommonProperties;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoConfiguration
@ConditionalOnClass({WebFluxSseClientTransport.class})
@EnableConfigurationProperties({McpSseClientProperties.class, McpClientCommonProperties.class})
@ConditionalOnProperty(
        prefix = "spring.ai.mcp.client",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class SseWebFluxTransportAutoConfiguration {
    @Bean
    public List<NamedClientMcpTransport> webFluxClientTransports(McpSseClientProperties sseProperties, ObjectProvider<WebClient.Builder> webClientBuilderProvider, ObjectProvider<ObjectMapper> objectMapperProvider) {
        List<NamedClientMcpTransport> sseTransports = new ArrayList();
        WebClient.Builder webClientBuilderTemplate = (WebClient.Builder)webClientBuilderProvider.getIfAvailable(WebClient::builder);
        ObjectMapper objectMapper = (ObjectMapper)objectMapperProvider.getIfAvailable(ObjectMapper::new);

        for(Map.Entry<String, McpSseClientProperties.SseParameters> serverParameters : sseProperties.getConnections().entrySet()) {
            WebClient.Builder webClientBuilder = webClientBuilderTemplate.clone().baseUrl(((McpSseClientProperties.SseParameters)serverParameters.getValue()).url())
                    // 添加请求头
                    .defaultHeaders((headers) ->
                            {
                                if (serverParameters.getValue().headers() != null) {
                                    serverParameters.getValue().headers().forEach(headers::add);
                                }
                            }
            );
            String sseEndpoint = ((McpSseClientProperties.SseParameters)serverParameters.getValue()).sseEndpoint() != null ? ((McpSseClientProperties.SseParameters)serverParameters.getValue()).sseEndpoint() : "/sse";
            WebFluxSseClientTransport transport = WebFluxSseClientTransport.builder(webClientBuilder).sseEndpoint(sseEndpoint).jsonMapper(new JacksonMcpJsonMapper(objectMapper)).build();
            sseTransports.add(new NamedClientMcpTransport((String)serverParameters.getKey(), transport));
        }

        return sseTransports;
    }
}
