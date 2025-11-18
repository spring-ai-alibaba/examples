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
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

/**
 * Graph Controller
 *
 * REST controller for executing graph processing operations. Provides synchronous
 * execution of the observability graph.
 *
 * Features: - Synchronous graph execution - Input parameter handling - Result formatting
 * - Error handling
 *
 * @author sixiyida
 */
@RestController
@RequestMapping("/graph/observation")
public class GraphController {

	@Autowired
	private CompiledGraph compiledGraph;

	private final static Logger logger = LoggerFactory.getLogger(GraphController.class);

	/**
	 * Execute graph processing
	 * @param input the input content to process
	 * @return processing result with success status and output
	 */
	@GetMapping("/execute")
	public Mono<Map<String, Object>> execute(@RequestParam(value = "prompt", defaultValue = "Hello World") String input) {
		return Mono.fromCallable(() -> {
					// Create initial state
					Map<String, Object> initialState = Map.of("input", input);
					RunnableConfig runnableConfig = RunnableConfig.builder().build();

					// Execute graph
					OverAllState result = compiledGraph.call(initialState, runnableConfig).get();

					// Return result
					Map<String, Object> response = new HashMap<>();
					response.put("success", true);
					response.put("input", input);
					response.put("output", result.value("end_output").orElse("No output"));
					response.put("logs", result.value("logs").orElse("No logs"));

					logger.info("分析成功：{}", response);
					return response;
				})
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorResume(e -> {
					logger.error("异常结束 [{}]", e.getMessage(), e);
					Map<String, Object> errorResponse = new HashMap<>();
					errorResponse.put("success", false);
					errorResponse.put("error", e.getMessage());
					return Mono.just(errorResponse);
				});
	}

}
