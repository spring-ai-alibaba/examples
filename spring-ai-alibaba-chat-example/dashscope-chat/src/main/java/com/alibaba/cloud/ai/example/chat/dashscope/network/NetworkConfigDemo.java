/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.alibaba.cloud.ai.example.chat.dashscope.network;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 *
 * 演示自定义 httpClient 以解决请求模型过程中的网络问题
 *
 * 对于 Stream，将 WebClient 的底层引擎替换为 NettyHttpClient，并优化 Netty 资源池和连接复用
 * 对于 Call，将 RestClient 替换为 OkHttpClient/JDK HttpClient。合理设置 ConnectionPool 和 ReadTimeOut
 */

@RestController
@RequestMapping("/cfg")
public class NetworkConfigDemo {

    private final ChatClient dashScopeChatClient;

    public NetworkConfigDemo() {

        this.dashScopeChatClient = ChatClient.builder(getDashScopeChatModel())
                .defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    @GetMapping("/call")
    public String testCall() {

        return dashScopeChatClient.prompt("hi").call().content();
    }

    @GetMapping("/stream")
    public Flux<String> testStream(HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        return dashScopeChatClient.prompt("hi").stream().content();
    }

    private DashScopeChatModel getDashScopeChatModel() {

        return DashScopeChatModel.builder()
                .dashScopeApi(getDashscopeAPI()).defaultOptions(
                        DashScopeChatOptions.builder()
                                .model("qwen-plus")
                                .enableSearch(true)
                                .searchOptions(DashScopeApiSpec.SearchOptions.builder()
                                        .enableSource(true)
                                        .forcedSearch(true)
                                        .searchStrategy("turbo")
                                        .build()
                                ).build()
                ).build();
    }

    private static DashScopeApi getDashscopeAPI() {

        // 配置HTTP连接池
        ConnectionProvider provider = ConnectionProvider.builder("dashscope")
                .maxConnections(500)
                .maxIdleTime(Duration.ofMinutes(10))  // 空闲连接保持10分钟
                .maxLifeTime(Duration.ofMinutes(30))  // 连接最大生命周期30分钟
                .evictInBackground(Duration.ofSeconds(60))  // 每60秒清理一次过期连接
                .build();

        // 配置HTTP客户端
        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)  // 连接超时10秒
                .responseTimeout(Duration.ofSeconds(60))  // 响应超时60秒
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60))  // 读超时60秒
                        .addHandlerLast(new WriteTimeoutHandler(10))  // 写超时10秒
                );

        // 构建WebClient实例
        WebClient.Builder webClientbuilder = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
        // 可选配置
        // 添加请求日志记录功能
        //.filter(ExchangeFilterFunction.ofRequestProcessor(
        //        clientRequest -> {
        //            log.debug("Request: {} {}",
        //                    clientRequest.method(),
        //                    clientRequest.url());
        //            return Mono.just(clientRequest);
        //        }
        //))
        // 添加响应日志记录功能
        //.filter(ExchangeFilterFunction.ofResponseProcessor(
        //        clientResponse -> {
        //            log.debug("Response status: {}",
        //                    clientResponse.statusCode());
        //            return Mono.just(clientResponse);
        //        }
        //));

        return DashScopeApi.builder()
                .apiKey("sk-xxx")
                .webClientBuilder(webClientbuilder)
                .restClientBuilder(RestClient.builder().requestFactory(new JdkClientHttpRequestFactory()))
                .build();
    }

}
