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

package com.alibaba.cloud.ai.graph.conf;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.KeyStrategyFactoryBuilder;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.node.AnswerNode;
import com.alibaba.cloud.ai.graph.node.ToolNode;
import com.alibaba.cloud.ai.graph.state.AgentStateFactory;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.alibaba.cloud.ai.graph.dispatcher.HumanFeedbackDispatcher;
import com.alibaba.cloud.ai.graph.dispatcher.SensitiveDispatcher;
import com.alibaba.cloud.ai.graph.nodes.ClftNode;
import com.alibaba.cloud.ai.graph.nodes.HumanFeedbackNode;
import com.alibaba.cloud.ai.graph.nodes.SensitiveWordDecNode;
import com.alibaba.cloud.ai.graph.tools.FieldSaveTool;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/19 15:44
 */
@Configuration
@Slf4j
public class SecGraphBuilder {

    @Bean
    public StateGraph secGraph(ChatClient.Builder chatClientBuilder,
                               @Qualifier("classificationVectorStore") VectorStore classificationVectorStore,
                               FieldSaveTool toolBack,
                               ToolCallbackResolver toolCallbackResolver
    ) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
                .addPatternStrategy("field", new ReplaceStrategy())
                .addPatternStrategy("is_sensitive", new ReplaceStrategy())
                .addPatternStrategy("clft_res", new ReplaceStrategy())
                .addPatternStrategy("save_result", new ReplaceStrategy())
                .addPatternStrategy("thread_id", new ReplaceStrategy())
                .addPatternStrategy("feed_back", new ReplaceStrategy())
                .addPatternStrategy("feedback_reason", new ReplaceStrategy())
                .addPatternStrategy("human_next_node", new ReplaceStrategy())
                .build();

        AgentStateFactory<OverAllState> factory = OverAllState::new;
        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 注册反序列化器
        SimpleModule module = new SimpleModule();
        module.addDeserializer(AssistantMessage.class, new AssistantMessageDeserializer());
        module.addDeserializer(ToolResponseMessage.class, new ToolResponseMessageDeserializer());
        mapper.registerModule(module);

        JsonStateSerializerWithTypeInfo serializer = new JsonStateSerializerWithTypeInfo(factory, mapper);

        StateGraph stateGraph = new StateGraph(keyStrategyFactory, serializer);
        stateGraph.addEdge(START, "sensitive")
                .addNode("sensitive", node_async(new SensitiveWordDecNode()))
                .addNode("answer", node_async(AnswerNode.builder().answer("您的输入{{field}}包含了敏感内容！").build()))
                .addEdge("answer", StateGraph.END)
                .addNode("clft", node_async(new ClftNode(chatClientBuilder, classificationVectorStore, toolBack)))
                .addConditionalEdges("sensitive", AsyncEdgeAction.edge_async(new SensitiveDispatcher()), Map.of("yes", "answer", "no", "clft"))
                .addNode("human", node_async(new HumanFeedbackNode()))
                .addEdge("clft", "human")
                .addConditionalEdges("human", AsyncEdgeAction.edge_async(new HumanFeedbackDispatcher()), Map.of("clft", "clft", "saveTool", "saveTool"))
                .addNode("saveTool", node_async(ToolNode.builder().llmResponseKey("clft_res")
                        .toolCallbacks(List.of(toolBack)).toolCallbackResolver(toolCallbackResolver).outputKey("save_result").build()))
                .addEdge("saveTool", StateGraph.END);

        // 添加 PlantUML 打印
        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
                "sec flow");
        log.info("\n=== expander UML Flow ===");
        log.info(representation.content());
        log.info("==================================\n");

        return stateGraph;
    }


}
