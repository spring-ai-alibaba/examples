/*
 * Copyright 2025-2026 the original author or authors.
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
 *
 */
package com.alibaba.example.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.example.conf.TodoChatFlowFactory;
import com.alibaba.example.conf.TodoSubGraphFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/assistant")
public class TodoChatflowController {

    private final CompiledGraph mainGraph;

    public TodoChatflowController(ChatClient.Builder chatClientBuilder) throws Exception {
        ChatClient chatClient = chatClientBuilder.build();
        // 构建子图
        CompiledGraph subGraph = TodoSubGraphFactory.build(chatClient);
        // 构建主图
        this.mainGraph = TodoChatFlowFactory.build(chatClient, subGraph);
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(
            @RequestParam(value = "sessionId", defaultValue = "chat-id-001") String sessionId,
            @RequestParam(value = "userInput", defaultValue = "你可以做什么") String userInput
    ) {
        Map<String, Object> input = new HashMap<>();
        input.put("session_id", sessionId);
        input.put("user_input", userInput);

        Optional<OverAllState> invoke = mainGraph.invoke(input, RunnableConfig.builder().threadId(sessionId).build());

        if (invoke.isPresent()){
            OverAllState state = invoke.get();
            Map<String, Object> result = new HashMap<>();
            result.put("reply", state.value("answer").orElse(""));
            result.put("tasks", state.value("tasks").orElse(List.of()));
            return result;
        }
        return Map.of("reply", "");
    }
}
