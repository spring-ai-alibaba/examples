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

package com.alibaba.cloud.ai.graph.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.async.AsyncGenerator;
import com.alibaba.cloud.ai.graph.controller.process.GraphProcess;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Graph Stream Controller
 *
 * REST controller for streaming graph processing operations. Provides Server-Sent Events
 * (SSE) streaming output interface.
 *
 * Features: - Real-time streaming output - SSE protocol support - Configurable thread
 * management - Error handling and logging
 *
 * @author sixiyida
 */
@RestController
@RequestMapping("/graph/observation")
public class GraphStreamController {

	private static final Logger logger = LoggerFactory.getLogger(GraphStreamController.class);

	@Autowired
	private CompiledGraph compiledGraph;

	@GetMapping(value = "/test-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> testStream() {
		return Flux.interval(Duration.ofSeconds(1)).map(i -> "tick " + i);
	}

	/**
	 * Stream graph processing execution
	 * @param input the input content to process
	 * @param threadId the thread ID for processing isolation
	 * @return SSE streaming output
	 * @throws GraphRunnerException if graph execution fails
	 */
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<String>> stream(
			@RequestParam(value = "prompt", defaultValue = "Hello World") String input,
			@RequestParam(value = "thread_id", defaultValue = "observability", required = false) String threadId)
			throws GraphRunnerException {

		logger.info("Starting streaming graph execution, input: {}, thread ID: {}", input, threadId);

		// Create runnable configuration
		RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();

		// Create initial state
		Map<String, Object> initialState = new HashMap<>();
		initialState.put("input", input);

		// Create graph processor
		GraphProcess graphProcess = new GraphProcess();

		// Get streaming output
//		AsyncGenerator<NodeOutput> resultStream = compiledGraph.stream(initialState, runnableConfig);
		Flux<NodeOutput> resultStream = compiledGraph.fluxStream(initialState, runnableConfig);


		// 直接返回 Reactor 风格的 Flux，保证 trace context 传播
		return graphProcess.processStream(resultStream)
			.doOnCancel(() -> logger.info("Client disconnected from streaming"))
			.doOnError(e -> logger.error("Error occurred during streaming output", e))
			.doOnComplete(() -> logger.info("Streaming output completed"));
	}

}
