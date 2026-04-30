/*
 * Copyright 2026-2027 the original author or authors.
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

package com.alibaba.cloud.ai.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Records Tool Calling details for the current request.
 */
@Component
public class ToolCallDebugRecorder {

	private static final Logger logger = LoggerFactory.getLogger(ToolCallDebugRecorder.class);

	private static final ThreadLocal<List<ToolCallDebug>> TOOL_CALLS = ThreadLocal.withInitial(ArrayList::new);

	public void clear() {
		TOOL_CALLS.get().clear();
	}

	public void record(String name, Map<String, Object> arguments, String result) {
		ToolCallDebug debug = new ToolCallDebug(name, Map.copyOf(arguments), result);
		TOOL_CALLS.get().add(debug);
		logger.info("Tool called: name={}, arguments={}, result={}", name, arguments, result);
	}

	public List<ToolCallDebug> snapshot() {
		return List.copyOf(TOOL_CALLS.get());
	}

	public void remove() {
		TOOL_CALLS.remove();
	}

	public record ToolCallDebug(String name, Map<String, Object> arguments, String result) {
	}

}
