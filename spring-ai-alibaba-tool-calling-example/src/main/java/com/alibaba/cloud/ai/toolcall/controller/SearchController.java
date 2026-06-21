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

import com.alibaba.cloud.ai.toolcalling.aliyunaisearch.AliyunAiSearchConstants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/basic/tool/search")
public class SearchController {

    private final ChatClient chatClient;

    private final ToolCallingAdvisor toolCallingAdvisor;

    private final ObjectProvider<ToolCallback> aliyunAiSearchToolCallback;

    public SearchController(ChatClient.Builder builder, ToolCallingAdvisor toolCallingAdvisor,
            @Qualifier("aliyunAiSearchToolCallback") ObjectProvider<ToolCallback> aliyunAiSearchToolCallback) {
        this.chatClient = builder.build();
        this.toolCallingAdvisor = toolCallingAdvisor;
        this.aliyunAiSearchToolCallback = aliyunAiSearchToolCallback;
    }

    @GetMapping("/call")
    public String callToolFunction(
            @RequestParam(value = "query", defaultValue = "Java领域的AI工程相关框架有哪些", required = false) String query) {

        return chatClient.prompt(query)
                .options(ToolCallingChatOptions.builder().toolCallbacks(List.of(requiredToolCallback())))
                .advisors(toolCallingAdvisor)
                .call()
                .content();
    }

    private ToolCallback requiredToolCallback() {
        return aliyunAiSearchToolCallback.getIfAvailable(() -> {
            throw new IllegalStateException("Tool callback is not available: " + AliyunAiSearchConstants.TOOL_NAME);
        });
    }

}
