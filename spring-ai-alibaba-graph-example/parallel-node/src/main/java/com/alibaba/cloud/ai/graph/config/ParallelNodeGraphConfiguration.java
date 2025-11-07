/*
 * Copyright 2025 the original author or authors.
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

package com.alibaba.cloud.ai.graph.config;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.KeyStrategyFactoryBuilder;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.dispatcher.CollectorDispatcher;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.node.CollectorNode;
import com.alibaba.cloud.ai.graph.node.DispatcherNode;
import com.alibaba.cloud.ai.graph.node.ExpanderNode;
import com.alibaba.cloud.ai.graph.node.TranslateNode;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * @author sixiyida
 * @since 2025/6/27
 */

@Configuration
public class ParallelNodeGraphConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ParallelNodeGraphConfiguration.class);

    @Bean
    public StateGraph parallelNodeGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
                .addPatternStrategy("query", new ReplaceStrategy())
                .addPatternStrategy("expander_number", new ReplaceStrategy())
                .addPatternStrategy("expander_content", new ReplaceStrategy())
                .addPatternStrategy("translate_language", new ReplaceStrategy())
                .addPatternStrategy("translate_content", new ReplaceStrategy())
                .addPatternStrategy("collector_next_node", new ReplaceStrategy())
                .addPatternStrategy("expand_status", new ReplaceStrategy())
                .addPatternStrategy("translate_status", new ReplaceStrategy())
                .build();

        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                .addNode("dispatcher", node_async(new DispatcherNode()))
                .addNode("translator", node_async(new TranslateNode(chatClientBuilder)))
                .addNode("expander", node_async(new ExpanderNode(chatClientBuilder)))
                .addNode("collector", node_async(new CollectorNode()))
                
                // 并行边
                .addEdge("dispatcher", "translator")
                .addEdge("dispatcher", "expander")
                .addEdge("translator", "collector")
                .addEdge("expander", "collector")
                
                .addEdge(StateGraph.START, "dispatcher")
                .addConditionalEdges("collector", edge_async(new CollectorDispatcher()),
                        Map.of("dispatcher", "dispatcher", END, END));

        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
                "parallel translator and expander flow");
        logger.info("\n=== Parallel Translator and Expander UML Flow ===");
        logger.info(representation.content());
        logger.info("==================================\n");

        return stateGraph;
    }

}
