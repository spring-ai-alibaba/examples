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

package com.alibaba.cloud.ai.graph.config;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.KeyStrategyFactoryBuilder;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.constant.SaverEnum;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.node.ChatNode;
import com.alibaba.cloud.ai.graph.node.MergeNode;
import com.alibaba.cloud.ai.graph.node.SimpleSubGraph;
import com.alibaba.cloud.ai.graph.node.StreamingChatNode;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.google.common.collect.Lists;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * Graph Configuration
 *
 * Configures the observability graph with various node types and edge types: - Start
 * node: Initial processing - Parallel nodes: Concurrent sentiment and topic analysis -
 * SubGraph node: Internal serial processing - Streaming node: Real-time AI response
 * streaming - Summary node: Result aggregation - End node: Final output formatting
 *
 * @author sixiyida
 */
@Configuration
public class GraphConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(GraphConfiguration.class);

	@Bean
	public RestClient.Builder createRestClient() {

		// 2. 创建 RequestConfig 并设置超时
		RequestConfig requestConfig = RequestConfig.custom()
			.setConnectTimeout(Timeout.of(10, TimeUnit.MINUTES)) // 设置连接超时
			.setResponseTimeout(Timeout.of(10, TimeUnit.MINUTES))
			.setConnectionRequestTimeout(Timeout.of(10, TimeUnit.MINUTES))
			.build();

		// 3. 创建 CloseableHttpClient 并应用配置
		HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

		// 4. 使用 HttpComponentsClientHttpRequestFactory 包装 HttpClient
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

		// 5. 创建 RestClient 并设置请求工厂
		return RestClient.builder().requestFactory(requestFactory);
	}

	/**
	 * Configure ChatClient with logging advisor
	 * @param chatModel the chat model to use
	 * @return configured ChatClient
	 */
	@Bean
	public ChatClient chatClient(ChatModel chatModel) {
		// 添加 AI 模型可用性测试
		try {
			logger.info("Testing AI model availability...");
			logger.info("ChatModel type: {}", chatModel.getClass().getSimpleName());
			logger.info("ChatModel default options: {}", chatModel.getDefaultOptions());

			// 尝试一个简单的同步调用测试
			ChatClient testClient = ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();
			String testResponse = testClient.prompt().user("Hello").call().content();
			logger.info("AI model test successful, response: {}",
					testResponse.substring(0, Math.min(50, testResponse.length())));
		}
		catch (Exception e) {
			logger.error("AI model test failed: {}", e.getMessage(), e);
		}

		return ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();
	}

	/**
	 * Configure the observability graph
	 * @param chatClient the chat client for AI processing
	 * @return configured StateGraph
	 * @throws GraphStateException if graph configuration fails
	 */
	@Bean
	public StateGraph observabilityGraph(ChatClient chatClient) throws GraphStateException {

		// Start node - initial processing
		ChatNode startNode = ChatNode.create("StartNode", "input", "start_output", chatClient,
				"Please perform initial processing on the input content:");

		// Parallel nodes - concurrent processing
		ChatNode parallelNode1 = ChatNode.create("ParallelNode1", "start_output", "parallel_output1", chatClient,
				"Please perform sentiment analysis on the content:");

		ChatNode parallelNode2 = ChatNode.create("ParallelNode2", "start_output", "parallel_output2", chatClient,
				"Please perform topic analysis on the content:");

		// Summary node - aggregates streaming output
		ChatNode summaryNode = ChatNode.create("SummaryNode", "streaming_output", "summary_output", chatClient,
				"Please summarize the streaming analysis results:");

		// Merge node - combine parallel outputs for subgraph input
		MergeNode mergeNode = new MergeNode(Lists.newArrayList("parallel_output1", "parallel_output2"), "sub_input");

		// Streaming node - real-time AI response
		StreamingChatNode streamingNode = StreamingChatNode.create("StreamingNode", "subgraph_final_output", "streaming_output",
				chatClient, "Please perform detailed analysis on the subgraph results:");

		// End node - final output formatting
		ChatNode endNode = ChatNode.create("EndNode", "summary_output", "end_output", chatClient,
				"Please format the final results for output:");

		// Create subgraph
		SimpleSubGraph subGraph = new SimpleSubGraph(chatClient);

		// Define key strategies for state management
		KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
				.addPatternStrategy("input", new ReplaceStrategy())
				.addPatternStrategy("start_output", new ReplaceStrategy())
				.addPatternStrategy("parallel_output1", new ReplaceStrategy())
				.addPatternStrategy("parallel_output2", new ReplaceStrategy())
				.addPatternStrategy("sub_input", new ReplaceStrategy())
				.addPatternStrategy("sub_output1", new ReplaceStrategy())
				.addPatternStrategy("sub_output2", new ReplaceStrategy())
				.addPatternStrategy("subgraph_final_output", new ReplaceStrategy())
				.addPatternStrategy("streaming_output", new ReplaceStrategy())
				.addPatternStrategy("summary_output", new ReplaceStrategy())
				.addPatternStrategy("end_output", new ReplaceStrategy())
				.addPatternStrategy("logs", new AppendStrategy())
				.build();

		// Build the main graph
		StateGraph graph = new StateGraph(keyStrategyFactory)

			// Add nodes
			.addNode("start", node_async(startNode))
			.addNode("parallel1", node_async(parallelNode1))
			.addNode("parallel2", node_async(parallelNode2))
			.addNode("merge", node_async(mergeNode)) // 使用自定义MergeNode并包裹为异步
			.addNode("subgraph", subGraph.subGraph()) // Add subgraph
			.addNode("streaming", node_async(streamingNode)) // Add streaming node
			.addNode("summary", node_async(summaryNode))
			.addNode("end", node_async(endNode))

			// Serial edge: START -> start
			.addEdge(START, "start")

			// Parallel edges: start -> parallel1 and parallel2 (concurrent execution)
			.addEdge("start", "parallel1")
			.addEdge("start", "parallel2")

			// Aggregation edges: both parallel nodes complete -> merge
			.addEdge("parallel1", "merge")
			.addEdge("parallel2", "merge")

			// Serial edge: merge -> subgraph
			.addEdge("merge", "subgraph")

			// Serial edges: subgraph -> streaming -> summary
			.addEdge("subgraph", "streaming")
			.addEdge("streaming", "summary")

			// Serial edge: summary -> end
			.addEdge("summary", "end")

			// Serial edge: end -> END
			.addEdge("end", END);

		// Print graph structure
		GraphRepresentation representation = graph.getGraph(GraphRepresentation.Type.PLANTUML, "Observability Demo");

		System.out.println("\n=== Observability Demo Graph ===");
		System.out.println(representation.content());
		System.out.println("================================\n");

		return graph;
	}

	/**
	 * Compile the graph with observability configuration
	 * @param observabilityGraph the state graph to compile
	 * @param observationCompileConfig the compile configuration
	 * @return compiled graph
	 * @throws GraphStateException if compilation fails
	 */
	@Bean
	public CompiledGraph compiledGraph(StateGraph observabilityGraph, CompileConfig observationCompileConfig)
			throws GraphStateException {
		// 为子图添加 checkpoint saver 配置，确保子图能正确接收输入
		CompileConfig subgraphCompileConfig = CompileConfig.builder(observationCompileConfig)
			.saverConfig(SaverConfig.builder().register(SaverEnum.MEMORY.getValue(), new MemorySaver()).build())
			.build();

		return observabilityGraph.compile(subgraphCompileConfig);
	}

}
