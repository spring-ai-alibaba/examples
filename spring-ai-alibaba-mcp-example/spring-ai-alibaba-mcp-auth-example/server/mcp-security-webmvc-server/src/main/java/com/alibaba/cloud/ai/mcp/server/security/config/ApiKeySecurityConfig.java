package com.alibaba.cloud.ai.mcp.server.security.config;

import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntityRepository;
import org.springaicommunity.mcp.security.server.apikey.memory.ApiKeyEntityImpl;
import org.springaicommunity.mcp.security.server.apikey.memory.InMemoryApiKeyEntityRepository;
import org.springaicommunity.mcp.security.server.config.McpApiKeyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@Profile("api-key")
public class ApiKeySecurityConfig {

    @Bean
    public SecurityFilterChain apiKeySecurityFilterChain(HttpSecurity http,
            ApiKeyEntityRepository<ApiKeyEntityImpl> apiKeyRepository) throws Exception {

        return http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .with(McpApiKeyConfigurer.mcpServerApiKey(),
                        apiKey -> apiKey.apiKeyRepository(apiKeyRepository).headerName("X-API-Key"))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public ApiKeyEntityRepository<ApiKeyEntityImpl> apiKeyRepository() {
        ApiKeyEntityImpl apiKey = ApiKeyEntityImpl.builder()
                .name("MCP demo API key")
                .id("api01")
                .secret("mycustomapikey")
                .build();
        return new InMemoryApiKeyEntityRepository<>(List.of(apiKey));
    }

}
