/*
 * Copyright 2024-2025 the original author or authors.
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
package com.alibaba.cloud.ai.mcp.server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author yingzi
 * @since 2025/9/17
 */
@Component
public class McpServerFilter implements WebFilter {

    private static final String TOKEN_HEADER = "token-1";
    private static final String TOKEN_VALUE = "yingzi-1";

    private static final Logger logger = LoggerFactory.getLogger(McpServerFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 获取请求头中的token值
        HttpHeaders headers = exchange.getRequest().getHeaders();
        // 打印所有请求头信息
        for (String headerName : headers.keySet()) {
            logger.info("Header {}: {}", headerName, headers.getFirst(headerName));
        }

        String token = headers.getFirst(TOKEN_HEADER);
        // 检查token是否存在且值正确
        if (TOKEN_VALUE.equals(token)) {
            logger.info("preHandle: 验证通过");
            logger.info("preHandle: 请求的URL: {}", exchange.getRequest().getURI());
            logger.info("preHandle: 请求的TOKEN: {}", token);
            // token验证通过，继续处理请求
            return chain.filter(exchange);
        } else {
            // token验证失败，返回401未授权错误
            logger.warn("Token验证失败: 请求的URL: {}, 提供的TOKEN: {}", exchange.getRequest().getURI(), token);
            logger.warn("要求的token为：{}", TOKEN_VALUE);
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
