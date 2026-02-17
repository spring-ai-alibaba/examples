/*
 * Copyright 2024-2025 Alibaba Group Holding Limited.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.examples.sandbox.browser.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import io.agentscope.runtime.sandbox.manager.ManagerConfig;
import io.agentscope.runtime.sandbox.manager.SandboxService;
import io.agentscope.runtime.sandbox.manager.client.container.docker.DockerClientStarter;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.model.ChatModel;

@Configuration
public class SandboxConfiguration {

    private SandboxService sandboxService;

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public SandboxService sandboxService() {
        ManagerConfig config = ManagerConfig.builder().build();
        this.sandboxService = new SandboxService(config);
        this.sandboxService.start();
        return this.sandboxService;
    }

    @Bean
    public ChatModel chatModel() {
        DashScopeApi api = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();

        return DashScopeChatModel.builder()
                .dashScopeApi(api)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model("qwen-max")
                        .temperature(0.7)
                        .build())
                .build();
    }

    @PreDestroy
    public void cleanup() {
        if (sandboxService != null) {
            sandboxService.cleanupAllSandboxes();
        }
    }

}
