package com.alibaba.cloud.ai.mcp.server.security.config;

import org.springaicommunity.mcp.security.server.config.McpServerOAuth2Configurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!api-key")
public class OAuth2ResourceServerConfig {

    @Bean
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) throws Exception {

        return http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .with(McpServerOAuth2Configurer.mcpServerOAuth2(), oauth2 -> oauth2.authorizationServer(issuerUri))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

}
