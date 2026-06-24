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
package com.alibaba.cloud.ai.toolcall.config;

import com.alibaba.cloud.ai.toolcall.service.time.function.TimeService;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ToolConfiguration {

    @Bean
    public ToolCallingAdvisor toolCallingAdvisor() {
        return ToolCallingAdvisor.builder().build();
    }

    @Bean
    public ToolCallback getCityTimeFunctionToolCallback() {
        return FunctionToolCallback.builder("getCityTimeFunction", new TimeService())
                .description("Get the time of a specified city.")
                .inputType(TimeService.Request.class)
                .build();
    }

}
