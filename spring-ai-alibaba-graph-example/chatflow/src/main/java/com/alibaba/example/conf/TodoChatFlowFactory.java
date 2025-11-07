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
package com.alibaba.example.conf;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.node.*;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

public class TodoChatFlowFactory {
    public static CompiledGraph build(ChatClient chatClient, CompiledGraph subGraph) throws Exception {

        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
                .addPatternStrategy("session_id", new ReplaceStrategy())
                .addPatternStrategy("user_input", new ReplaceStrategy())
                .addPatternStrategy("intent_type", new ReplaceStrategy())
                .addPatternStrategy("chat_reply", new ReplaceStrategy())
                .addPatternStrategy("tasks", new ReplaceStrategy())
                .addPatternStrategy("created_task", new ReplaceStrategy())
                .addPatternStrategy("answer", new ReplaceStrategy())
                .build();

        StateGraph mainGraph = new StateGraph("chatFlow-demo", keyStrategyFactory);

        // 闲聊/多轮通用 LLM - Lambda 动态 new LlmNode
        mainGraph.addNode("chat", node_async(state -> {
            LlmNode node = LlmNode.builder()
                    .userPromptTemplate("{user_input}")
                    .params(Map.of("user_input", "null"))
                    .outputKey("chat_reply")
                    .chatClient(chatClient)
                    .build();
            return node.apply(state);
        }));

        // 问题分类节点
        QuestionClassifierNode intentClassifier = QuestionClassifierNode.builder()
                .chatClient(chatClient)
                .inputTextKey("user_input")
                .categories(List.of("创建待办", "其它"))
                .classificationInstructions(List.of("判断用户是否想创建一个待办事项。如果是，返回'创建待办'，否则返回'其它'"))
                .outputKey("intent_type")
                .build();
        mainGraph.addNode("intent", node_async(intentClassifier));

        // 调用子图节点
        NodeAction callSubGraphNode = (OverAllState state) -> {
            String mainThreadId = (String) state.value("session_id").orElse("user-001");
            String subThreadId = mainThreadId + "-todo-" + UUID.randomUUID();
            String userInput = (String) state.value("user_input").orElse("");
            // 提取待办内容
            String taskContent = userInput;
            int idx = userInput.indexOf("：");
            if (idx > 0 && idx + 1 < userInput.length()) {
                taskContent = userInput.substring(idx + 1).trim();
            }
            Map<String, Object> input = Map.of("task_content", taskContent);

            var subResult = subGraph.invoke(input, RunnableConfig.builder().threadId(subThreadId).build());
            if (subResult.isPresent()) {
                Object createdTaskObj = subResult.get().value("created_task").orElse(null);
                String createdTask = null;
                if (createdTaskObj instanceof String s) {
                    createdTask = s;
                } else if (createdTaskObj instanceof AssistantMessage am) {
                    createdTask = am.getText();
                } else if (createdTaskObj != null) {
                    createdTask = createdTaskObj.toString();
                }
                List<String> tasks = (List<String>) state.value("tasks").orElse(new java.util.ArrayList<>());
                tasks = new java.util.ArrayList<>(tasks);
                if (createdTask != null && !createdTask.isBlank()) {
                    tasks.add(createdTask);
                }
                return Map.of("tasks", tasks, "created_task", createdTask);
            }
            return Map.of();
        };
        mainGraph.addNode("callSubGraph", node_async(callSubGraphNode));

        // 主流程答复节点
        AnswerNode mainReply = AnswerNode.builder()
                .answer("你当前待办有：{tasks}\n闲聊回复：{chat_reply}")
                .build();
        mainGraph.addNode("mainReply", node_async(mainReply));

        mainGraph.addEdge(StateGraph.START, "intent");
        // intent_type判定：如果为"创建待办"则进入子图，否则普通闲聊
        mainGraph.addConditionalEdges("intent", edge_async(state -> {
            String intentRaw = (String) state.value("intent_type").orElse("");
            String intent = intentRaw;
            try {
                // 去除 markdown code block
                intentRaw = intentRaw.trim();
                if (intentRaw.startsWith("```json")) {
                    intentRaw = intentRaw.replaceFirst("^```json", "").trim();
                }
                if (intentRaw.startsWith("```")) {
                    intentRaw = intentRaw.replaceFirst("^```", "").trim();
                }
                if (intentRaw.endsWith("```")) {
                    intentRaw = intentRaw.replaceAll("```$", "").trim();
                }
                // 解析 JSON
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(intentRaw);
                if (node.has("category_name")) {
                    intent = node.get("category_name").asText();
                }
            } catch (Exception e) {
                
            }
            return "创建待办".equals(intent) ? "callSubGraph" : "chat";
        }), Map.of("callSubGraph", "callSubGraph", "chat", "chat"));

        mainGraph.addEdge("callSubGraph", "mainReply");
        mainGraph.addEdge("chat", "mainReply");
        mainGraph.addEdge("mainReply", StateGraph.END);

        return mainGraph.compile();
    }
}
