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
package com.alibaba.cloud.ai.toolcall.controller;

import com.alibaba.cloud.ai.toolcall.service.time.method.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/basic/tool/time")
public class BasicTimeController {

    private final ChatClient chatClient;

    private final ToolCallingAdvisor toolCallingAdvisor;

    private final ToolCallback getCityTimeFunctionToolCallback;

    public BasicTimeController(ChatClient.Builder builder, ToolCallingAdvisor toolCallingAdvisor,
            @Qualifier("getCityTimeFunctionToolCallback") ToolCallback getCityTimeFunctionToolCallback) {
        this.chatClient = builder.build();
        this.toolCallingAdvisor = toolCallingAdvisor;
        this.getCityTimeFunctionToolCallback = getCityTimeFunctionToolCallback;
    }

    @GetMapping("/call")
    public String call(
            @RequestParam(value = "query", defaultValue = "请告诉我现在北京时间几点了", required = false) String query) {

        return chatClient.prompt(query).call().content();
    }

    @GetMapping("/call/function")
    public String callToolFunction(
            @RequestParam(value = "query", defaultValue = "请告诉我现在北京时间几点了", required = false) String query) {

        return chatClient.prompt(query)
                .options(ToolCallingChatOptions.builder().toolCallbacks(List.of(getCityTimeFunctionToolCallback)))
                .advisors(toolCallingAdvisor)
                .call()
                .content();
    }

    @GetMapping("/call/method")
    public String callToolMethod(
            @RequestParam(value = "query", defaultValue = "请告诉我现在北京时间几点了", required = false) String query) {

        return chatClient.prompt(query)
                .tools(new TimeTools())
                .advisors(toolCallingAdvisor)
                .call()
                .content();
    }

    @GetMapping("/call/method-auto")
    public String callToolMethodAuto(
            @RequestParam(value = "query", defaultValue = "请告诉我现在北京时间几点了", required = false) String query) {

        return chatClient.prompt(query)
                .tools(new TimeTools())
                .call()
                .content();
    }

    @GetMapping("/call/callback")
    public String callToolCallback(
            @RequestParam(value = "query", defaultValue = "请告诉我现在北京时间几点了", required = false) String query) {

        return chatClient.prompt(query)
                .options(ToolCallingChatOptions.builder().toolCallbacks(List.of(getCityTimeFunctionToolCallback)))
                .advisors(toolCallingAdvisor)
                .call()
                .content();
    }

    @GetMapping("/call/method-false")
    public ChatResponse callToolMethodFalse(
            @RequestParam(value = "query", defaultValue = "请告诉我现在北京时间几点了", required = false) String query) {

        return chatClient.prompt(query)
                .tools(new TimeTools())
                .call()
                .chatResponse();
    }

}
