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

package com.alibaba.cloud.ai.agent;

import java.util.List;

import com.alibaba.cloud.ai.agent.LearningAgentResult.LearningAgentStep;
import com.alibaba.cloud.ai.graph.LearningGraphStep;
import com.alibaba.cloud.ai.memory.LearningMemory;
import com.alibaba.cloud.ai.planner.LearningIntent;
import com.alibaba.cloud.ai.tool.ToolCallDebugRecorder;

/**
 * One Server-Sent Event payload for streaming chat with debug data.
 */
public record LearningStreamEvent(String type, String content, LearningIntent intent, LearningMemory memoryBefore,
		LearningMemory memoryAfter, List<LearningGraphStep> graphSteps, List<LearningAgentStep> agentSteps,
		List<ToolCallDebugRecorder.ToolCallDebug> toolCalls) {

	public static LearningStreamEvent debug(LearningIntent intent, LearningMemory memoryBefore,
			List<LearningGraphStep> graphSteps, List<LearningAgentStep> agentSteps) {
		return new LearningStreamEvent("debug", "", intent, memoryBefore, null, graphSteps, agentSteps, List.of());
	}

	public static LearningStreamEvent message(String content) {
		return new LearningStreamEvent("message", content, null, null, null, List.of(), List.of(), List.of());
	}

	public static LearningStreamEvent done(LearningMemory memoryAfter, List<LearningGraphStep> graphSteps,
			List<LearningAgentStep> agentSteps, List<ToolCallDebugRecorder.ToolCallDebug> toolCalls) {
		return new LearningStreamEvent("done", "", null, null, memoryAfter, graphSteps, agentSteps, toolCalls);
	}

}
