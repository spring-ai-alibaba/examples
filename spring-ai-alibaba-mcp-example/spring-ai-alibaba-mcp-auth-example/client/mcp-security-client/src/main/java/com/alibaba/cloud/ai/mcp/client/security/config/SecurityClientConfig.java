package com.alibaba.cloud.ai.mcp.client.security.config;

import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import org.springaicommunity.mcp.security.client.sync.config.McpClientOAuth2Configurer;
import org.springaicommunity.mcp.security.client.sync.oauth2.http.client.OAuth2ClientCredentialsSyncHttpRequestCustomizer;
import org.springaicommunity.mcp.security.client.sync.oauth2.registration.InMemoryMcpClientRegistrationRepository;
import org.springaicommunity.mcp.security.client.sync.oauth2.registration.McpClientRegistrationRepository;
import org.springframework.ai.mcp.customizer.McpClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityClientConfig {

    private static final String REGISTRATION_ID = "mcp-client";

    @Bean
    public McpClientRegistrationRepository mcpClientRegistrationRepository(
            @Value("${mcp.security.authorization-server-url:http://localhost:9000}") String authorizationServerUrl,
            @Value("${mcp.security.server-url:http://localhost:18200/mcp}") String mcpServerUrl) {

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(REGISTRATION_ID)
                .tokenUri(authorizationServerUrl + "/oauth2/token")
                .clientId("mcp-client")
                .clientSecret("mcp-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("mcp:tools")
                .clientName("MCP Security Client")
                .build();

        InMemoryMcpClientRegistrationRepository repository = new InMemoryMcpClientRegistrationRepository();
        repository.addClientRegistration(clientRegistration, mcpServerUrl);
        return repository;
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(McpClientRegistrationRepository registrations) {
        return new InMemoryOAuth2AuthorizedClientService(registrations);
    }

    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager(
            McpClientRegistrationRepository registrations, OAuth2AuthorizedClientService authorizedClientService) {

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, authorizedClientService);
        manager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build());
        return manager;
    }

    @Bean
    public McpSyncHttpClientRequestCustomizer oauth2McpRequestCustomizer(
            AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) {
        return new OAuth2ClientCredentialsSyncHttpRequestCustomizer(authorizedClientManager, REGISTRATION_ID);
    }

    @Bean
    public McpClientCustomizer<HttpClientStreamableHttpTransport.Builder> streamableHttpOAuth2Customizer(
            McpSyncHttpClientRequestCustomizer requestCustomizer) {
        return (clientName, builder) -> {
            if ("security-webmvc-server".equals(clientName)) {
                builder.httpRequestCustomizer(requestCustomizer);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityClientFilterChain(HttpSecurity http,
            @Value("${mcp.security.server-url:http://localhost:18200/mcp}") String mcpServerUrl) throws Exception {

        return http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .with(McpClientOAuth2Configurer.mcpClientOAuth2(),
                        mcp -> mcp.registerMcpOAuth2Client(REGISTRATION_ID, mcpServerUrl).cimd(false))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

}
