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

package com.alibaba.cloud.ai.application.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Spring AI Issue:
 * - https://github.com/spring-projects/spring-ai/pull/365
 * - https://github.com/spring-projects/spring-ai/issues/354#issuecomment-1965697951
 *
 * 设置效果：https://github.com/springaialibaba/spring-ai-alibaba-examples/pull/276
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Configuration
public class RestConfiguration {

    /**
     * 在项目中引入 org.apache.httpcomponents.client5.httpclient5，可以像如下方式配置超时时间。
     *
     *    @Bean
     *    public RestClient.Builder createRestClient() {
     *
     *         log.info("Initializing RestClient with custom timeout configuration");
     *
     * 		RequestConfig requestConfig = RequestConfig.custom()
     * 				.setConnectTimeout(Timeout.of(10, TimeUnit.MINUTES))
     * 				.setResponseTimeout(Timeout.of(10, TimeUnit.MINUTES))
     * 				.setConnectionRequestTimeout(Timeout.of(10, TimeUnit.MINUTES))
     * 				.build();
     *
     * 		HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
     *
     * 		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
     *
     * 		return RestClient.builder().requestFactory(requestFactory);
     *    }
     */

    /**
     * 如果使用基于 netty 的 ReactorClientHttpConnector，可以使用如下配置：
     * <p>
     * https://projectreactor.io/docs/netty/release/reference/index.html#response-timeout
     * https://docs.spring.io/spring-framework/reference/web/webflux-webclient/client-builder.html#webflux-client-builder-reactor-timeout
     */
    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .requestFactory(
                        ClientHttpRequestFactoryBuilder.reactor().withCustomizer(
                                factory -> {
                                    factory.setConnectTimeout(Duration.of(10, ChronoUnit.MINUTES));
                                    factory.setReadTimeout(Duration.of(10, ChronoUnit.MINUTES));
                                }
                        ).build()
                );
    }

    /**
     * 如果设置 WebClient 响应超时时间，其底层仍然使用 netty 的 ReactorClientHttpConnector 时
     * 1. 在 build 模型 api 时设置，例如：
     *  WebClient.Builder webClientBuilder = WebClient.builder()
     *         .clientConnector(new ReactorClientHttpConnector(
     *                 HttpClient.create().responseTimeout(Duration.of(60, ChronoUnit.SECONDS))));
     *
     *   OllamaApi ollamaApi = OllamaApi.builder()
     *         .webClientBuilder(webClientBuilder)
     *         .build();
     * 2. 按照如下写法，将其作为配置 bean 注入
     */
    @Bean
    public WebClient.Builder webClientBuilder() {

        return WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create().responseTimeout(Duration.of(600, ChronoUnit.SECONDS))
                        )
                );
    }

}
