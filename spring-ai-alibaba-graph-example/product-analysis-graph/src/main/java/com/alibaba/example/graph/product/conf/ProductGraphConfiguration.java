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

package com.alibaba.example.graph.product.conf;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.KeyStrategyFactoryBuilder;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.AgentStateFactory;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.alibaba.example.graph.product.model.Product;
import com.alibaba.example.graph.product.serializer.ProductStateSerializer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

@Configuration
public class ProductGraphConfiguration {

    @Bean
    public StateGraph productAnalysisGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        ChatClient client = chatClientBuilder.build();

        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
                .addPatternStrategy("productDesc", new ReplaceStrategy())
                .addPatternStrategy("slogan", new ReplaceStrategy())
                .addPatternStrategy("productSpec", new ReplaceStrategy())
                .addPatternStrategy("finalProduct", new ReplaceStrategy())
                .build();

        // Create custom serializer to handle Product object serialization
        AgentStateFactory<OverAllState> stateFactory = OverAllState::new;
        ProductStateSerializer serializer = new ProductStateSerializer(stateFactory);

        NodeAction marketingCopyNode = state -> {
            String productDesc = (String) state.value("productDesc").orElseThrow();
            String slogan = client.prompt()
                    .user("Generate a catchy slogan for a product with the following description: " + productDesc)
                    .call()
                    .content();
            return Map.of("slogan", slogan);
        };

        NodeAction specificationExtractionNode = state -> {
            String productDesc = (String) state.value("productDesc").orElseThrow();
            Product productSpec = client.prompt()
                    .user("Extract product specifications from the following description: " + productDesc)
                    .call()
                    .entity(Product.class);
            return Map.of("productSpec", productSpec);
        };

        NodeAction mergeNode = state -> {
            String slogan = (String) state.value("slogan").orElseThrow();
            Product productSpec = (Product) state.value("productSpec").orElseThrow();
            Product finalProduct = new Product(slogan, productSpec.material(), productSpec.colors(), productSpec.season());
            return Map.of("finalProduct", finalProduct);
        };

        StateGraph graph = new StateGraph(keyStrategyFactory, serializer);
        graph.addNode("marketingCopy", node_async(marketingCopyNode))
                .addNode("specificationExtraction", node_async(specificationExtractionNode))
                .addNode("merge", node_async(mergeNode))
                .addEdge(START, "marketingCopy")
                .addEdge(START, "specificationExtraction")
                .addEdge("marketingCopy", "merge")
                .addEdge("specificationExtraction", "merge")
                .addEdge("merge", END);

        GraphRepresentation representation = graph.getGraph(GraphRepresentation.Type.PLANTUML, "Product Analysis Graph");
        System.out.println("\n=== Product Analysis Graph UML Flow ===");
        System.out.println(representation.content());
        System.out.println("======================================\n");

        return graph;
    }
}