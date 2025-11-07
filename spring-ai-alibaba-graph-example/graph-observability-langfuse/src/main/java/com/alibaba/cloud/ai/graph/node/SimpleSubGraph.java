/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.graph.node;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.SubGraphNode;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.springframework.ai.chat.client.ChatClient;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * Simple SubGraph Implementation
 *
 * A subgraph that contains only serial edges, without parallel processing. This subgraph
 * performs sequential processing through multiple internal nodes.
 *
 * Features: - Pure serial processing flow - Three-stage processing pipeline - Independent
 * state management - Configurable processing stages
 *
 * @author sixiyida
 */
public class SimpleSubGraph implements SubGraphNode {

	private final ChatClient chatClient;

	private StateGraph subGraph;

	/**
	 * Constructor for SimpleSubGraph
	 * @param chatClient the chat client for AI processing
	 */
	public SimpleSubGraph(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.subGraph = createSubGraph();
	}

	@Override
	public String id() {
		return "simple_subgraph";
	}

	@Override
	public StateGraph subGraph() {
		return this.subGraph;
	}

	/**
	 * Create the internal subgraph structure
	 * @return configured StateGraph for the subgraph
	 */
	private StateGraph createSubGraph() {
		try {
			// Create internal nodes for the subgraph (serial processing)
			ChatNode subNode1 = ChatNode.create("SubGraphNode1", "sub_input", "sub_output1", chatClient,
					"Please perform the first step processing on the following content:");

			ChatNode subNode2 = ChatNode.create("SubGraphNode2", "sub_output1", "sub_output2", chatClient,
					"Please perform the second step processing on the following content:");

			ChatNode subNode3 = ChatNode.create("SubGraphNode3", "sub_output2", "subgraph_final_output", chatClient,
					"Please perform the final processing on the following content:");

			// Build subgraph (pure serial structure)
			return new StateGraph("Simple SubGraph", () -> {
				Map<String, KeyStrategy> strategies = new HashMap<>();
				strategies.put("sub_input", new ReplaceStrategy());
				strategies.put("sub_output1", new ReplaceStrategy());
				strategies.put("sub_output2", new ReplaceStrategy());
				strategies.put("subgraph_final_output", new ReplaceStrategy());
				strategies.put("logs", new AppendStrategy());
				return strategies;
			})
				// Add subgraph nodes
				.addNode("sub_node1", node_async(subNode1))
				.addNode("sub_node2", node_async(subNode2))
				.addNode("sub_node3", node_async(subNode3))

				// Subgraph edges: pure serial processing
				.addEdge(START, "sub_node1")
				.addEdge("sub_node1", "sub_node2")
				.addEdge("sub_node2", "sub_node3")
				.addEdge("sub_node3", END);

		}
		catch (GraphStateException e) {
			throw new RuntimeException("Failed to create subgraph", e);
		}
	}

}
