package com.alibaba.cloud.ai.mcp.client.config;

import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yingzi
 * @since 2025/11/22
 */

@Configuration
public class HttpClientConfig {

    @Bean
    public McpSyncHttpClientRequestCustomizer mcpAsyncHttpClientRequestCustomizer() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token-1", "yingzi-1");
        headers.put("token-2", "yingzi-2");

        return new HeaderSyncHttpRequestCustomizer(headers);
    }
}
