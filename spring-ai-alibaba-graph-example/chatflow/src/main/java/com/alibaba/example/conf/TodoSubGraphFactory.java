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

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.KeyStrategyFactoryBuilder;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.node.LlmNode;
import com.alibaba.cloud.ai.graph.node.AnswerNode;
import com.alibaba.cloud.ai.graph.node.AssignerNode;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.springframework.ai.chat.client.ChatClient;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * 子图定义
 */
public class TodoSubGraphFactory {
    public static CompiledGraph build(ChatClient chatClient) throws Exception {
        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
                .addPatternStrategy("task_content", new ReplaceStrategy())
                .addPatternStrategy("todo_desc", new ReplaceStrategy())
                .addPatternStrategy("created_task", new ReplaceStrategy())
                .build();

        StateGraph subGraph = new StateGraph("create-todo-subgraph", keyStrategyFactory);

        // LLM润色用户输入（每轮动态new）
        subGraph.addNode("llm", node_async(state -> {
            LlmNode node = LlmNode.builder()
                    .userPromptTemplate("请直接用一句话帮我润色成待办事项描述，原内容为: {task_content}，不需要任何解释或格式，只回复润色后的内容。")
                    .params(Map.of("task_content", "null"))
                    .outputKey("todo_desc")
                    .chatClient(chatClient)
                    .build();
            return node.apply(state);
        }));

        // 合并变量 - 可用单例AssignerNode
        AssignerNode assignNode = AssignerNode.builder()
                .addItem("created_task", "todo_desc", AssignerNode.WriteMode.OVER_WRITE)
                .build();
        subGraph.addNode("assign", node_async(assignNode));

        // 回答确认 - 可选
        AnswerNode answerNode = AnswerNode.builder()
                .answer("已创建任务：{todo_desc}")
                .build();
        subGraph.addNode("answer", node_async(answerNode));

        subGraph.addEdge(StateGraph.START, "llm");
        subGraph.addEdge("llm", "assign");
        subGraph.addEdge("assign", "answer");
        subGraph.addEdge("answer", StateGraph.END);

        return subGraph.compile();
    }
}
