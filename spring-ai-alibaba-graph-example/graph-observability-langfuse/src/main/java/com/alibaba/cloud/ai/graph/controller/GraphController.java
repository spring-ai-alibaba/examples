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
import com.alibaba.cloud.ai.graph.observation.metric.SpringAiAlibabaObservationMetricAttributes;
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
	public Mono<Map<String, Object>> execute(
			@RequestParam(value = "prompt", defaultValue = "Hello World") String input) {
		// Capture HTTP span in the main request thread
		final io.opentelemetry.api.trace.Span httpSpan = io.opentelemetry.api.trace.Span.current();
		final String httpSpanId = httpSpan != null ? httpSpan.getSpanContext().getSpanId() : "unknown";
		logger.info("Captured HTTP span ID: {}", httpSpanId);

		return Mono.fromCallable(() -> {
			// Create initial state with input
			Map<String, Object> initialState = new HashMap<>();
			initialState.put("input", input);

			try {
				RunnableConfig runnableConfig = RunnableConfig.builder().build();

				// Execute graph
				OverAllState result = compiledGraph.invoke(initialState, runnableConfig).get();

				// Get final output
				Object finalOutput = result.value("end_output").orElse("No output");

				// Return result
				Map<String, Object> response = new HashMap<>();
				response.put("success", true);
				response.put("input", input);
				response.put("output", finalOutput);
				response.put("logs", result.value("logs").orElse("No logs"));

				logger.info("Graph execution completed successfully");
				return response;
			} catch (Exception e) {
				logger.error("Graph execution failed inside callable", e);
				throw e;
			}
		})
				.subscribeOn(Schedulers.boundedElastic())
				.doOnSuccess(response -> {
					// Set HTTP observation input/output attributes using captured span
					try {
						if (httpSpan != null && response != null) {
							// Set HTTP-level input
							httpSpan.setAttribute(
									SpringAiAlibabaObservationMetricAttributes.LANGFUSE_INPUT.value(),
									input);
							httpSpan.setAttribute(
									SpringAiAlibabaObservationMetricAttributes.GEN_AI_PROMPT.value(),
									input);

							// Set HTTP-level output
							Object output = response.get("output");
							if (output != null) {
								String outputText = output.toString();
								httpSpan.setAttribute(
										SpringAiAlibabaObservationMetricAttributes.LANGFUSE_OUTPUT.value(),
										outputText);
								httpSpan.setAttribute(
										SpringAiAlibabaObservationMetricAttributes.GEN_AI_COMPLETION.value(),
										outputText);
								logger.info("Set HTTP span {} attributes - input: {}, output: {} chars",
										httpSpanId, input, outputText.length());
							}
						}
					} catch (Exception e) {
						logger.warn("Failed to set HTTP span attributes: {}", e.getMessage());
					}
				})
				.onErrorResume(e -> {
					logger.error("Graph execution failed: {}", e.getMessage(), e);
					Map<String, Object> errorResponse = new HashMap<>();
					errorResponse.put("success", false);
					errorResponse.put("error", e.getMessage());
					return Mono.just(errorResponse);
				});
	}

}
