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

import com.alibaba.cloud.ai.toolcall.component.AddressInformationTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/4/18 10:41
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    private final ChatClient dashScopeChatClient;

    private final ToolCallingAdvisor toolCallingAdvisor;

    private final AddressInformationTools addressTools;

    public AddressController(ChatClient chatClient, ToolCallingAdvisor toolCallingAdvisor,
            AddressInformationTools addressTools) {
        this.dashScopeChatClient = chatClient;
        this.toolCallingAdvisor = toolCallingAdvisor;
        this.addressTools = addressTools;
    }

    /**
     * No Tool
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(value = "address", defaultValue = "北京") String address) {

        return dashScopeChatClient.prompt(address)
                .call()
                .content();
    }

    /**
     * Methods as Tools - MethodToolCallback
     */
    @GetMapping("/chat-method-tool-callback")
    public String chatWithBaiduMap(@RequestParam(value = "address", defaultValue = "北京") String address) {

        Method method = ReflectionUtils.findMethod(AddressInformationTools.class, "getAddressInformation", String.class);

        if (method == null) {
            throw new RuntimeException("Method not found");
        }

        ToolCallback addressToolCallback = MethodToolCallback.builder()
                .toolDefinition(ToolDefinition.builder()
                        .description("Search for places using Baidu Maps API "
                                + "or Get detail information of a address and facility query with baidu map or "
                                + "Get address information of a place with baidu map or "
                                + "Get detailed information about a specific place with baidu map")
                        .name("getAddressInformation")
                        .inputSchema(JsonSchemaGenerator.generateForMethodInput(method))
                        .build())
                .toolMethod(method)
                .toolObject(addressTools)
                .build();

        return dashScopeChatClient.prompt(address)
                .options(ToolCallingChatOptions.builder().toolCallbacks(List.of(addressToolCallback)))
                .advisors(toolCallingAdvisor)
                .call()
                .content();
    }
}
