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

package com.alibaba.cloud.ai.official;

import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.memory.LearningMemory;
import com.alibaba.cloud.ai.planner.LearningIntent;
import com.alibaba.cloud.ai.tool.ToolCallDebugRecorder;

public record OfficialLearningAgentResult(String content, LearningIntent intent, LearningMemory memoryBefore,
		LearningMemory memoryAfter, List<OfficialAgentStep> agentSteps,
		List<ToolCallDebugRecorder.ToolCallDebug> toolCalls, Map<String, Object> rawState) {

	public record OfficialAgentStep(String name, String detail) {
	}

}
