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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.memory.LearningMemory;
import com.alibaba.cloud.ai.memory.LearningMemoryService;
import com.alibaba.cloud.ai.official.OfficialLearningAgentResult.OfficialAgentStep;
import com.alibaba.cloud.ai.planner.LearningIntent;
import com.alibaba.cloud.ai.planner.LearningIntentPlanner;
import com.alibaba.cloud.ai.tool.ToolCallDebugRecorder;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.stereotype.Service;

/**
 * Runs the same learning scenario through the official Spring AI Alibaba
 * ReactAgent.
 */
@Service
public class OfficialLearningAgentService {

	private final ReactAgent officialLearningAgent;

	private final LearningIntentPlanner intentPlanner;

	private final LearningMemoryService memoryService;

	private final ToolCallDebugRecorder debugRecorder;

	public OfficialLearningAgentService(ReactAgent officialLearningAgent, LearningIntentPlanner intentPlanner,
			LearningMemoryService memoryService, ToolCallDebugRecorder debugRecorder) {
		this.officialLearningAgent = officialLearningAgent;
		this.intentPlanner = intentPlanner;
		this.memoryService = memoryService;
		this.debugRecorder = debugRecorder;
	}

	public OfficialLearningAgentResult chat(String userId, String message) {
		this.debugRecorder.clear();
		LearningMemory memoryBefore = this.memoryService.read(userId);
		LearningIntent intent = this.intentPlanner.plan(message);
		List<OfficialAgentStep> steps = new ArrayList<>();
		steps.add(new OfficialAgentStep("MEMORY_READ", "读取用户长期学习记忆：" + memoryBefore.summary()));
		steps.add(new OfficialAgentStep("PLAN", "Planner 识别意图为 " + intent + "。"));
		steps.add(new OfficialAgentStep("OFFICIAL_REACT_AGENT",
				"调用 Spring AI Alibaba ReactAgent，由官方 Agent Framework 决定是否执行工具。"));
		try {
			RunnableConfig config = RunnableConfig.builder()
					.threadId(normalizeUserId(userId))
					.build();
			Optional<NodeOutput> output = this.officialLearningAgent.invokeAndGetOutput(buildPrompt(message, intent,
					memoryBefore), config);
			OverAllState state = output.map(NodeOutput::state).orElse(null);
			String content = extractContent(state);
			List<ToolCallDebugRecorder.ToolCallDebug> toolCalls = this.debugRecorder.snapshot();
			steps.add(new OfficialAgentStep("TOOL_RESULT",
					toolCalls.isEmpty() ? "官方 ReactAgent 本轮没有触发工具。" : "官方 ReactAgent 本轮触发了 "
							+ toolCalls.size() + " 次工具调用。"));
			LearningMemory memoryAfter = this.memoryService.update(userId, message, intent);
			steps.add(new OfficialAgentStep("MEMORY_WRITE", "根据本轮问题和意图更新用户长期学习记忆。"));
			return new OfficialLearningAgentResult(content, intent, memoryBefore, memoryAfter, List.copyOf(steps),
					toolCalls, state == null ? Map.of() : state.data());
		}
		catch (Exception ex) {
			steps.add(new OfficialAgentStep("ERROR", "官方 ReactAgent 调用失败：" + ex.getMessage()));
			LearningMemory memoryAfter = this.memoryService.read(userId);
			return new OfficialLearningAgentResult("官方 ReactAgent 调用失败：" + ex.getMessage(), intent, memoryBefore,
					memoryAfter, List.copyOf(steps), this.debugRecorder.snapshot(), Map.of());
		}
		finally {
			this.debugRecorder.remove();
		}
	}

	private String buildPrompt(String message, LearningIntent intent, LearningMemory memory) {
		return """
				用户问题：
				%s

				识别意图：
				%s

				用户长期学习记忆：
				%s

				请结合用户问题和记忆回答。需要真实时间、学习建议、学习计划、概念解释或当前项目资料时，请调用可用工具。
				""".formatted(message, intent, memory.summary());
	}

	private String extractContent(OverAllState state) {
		if (state == null) {
			return "官方 ReactAgent 没有返回结果。";
		}
		Optional<Object> output = state.value("output");
		if (output.isPresent()) {
			return String.valueOf(output.get());
		}
		Optional<List<AbstractMessage>> messages = state.value("messages");
		if (messages.isPresent() && !messages.get().isEmpty()) {
			return messages.get().get(messages.get().size() - 1).getText();
		}
		return state.toString();
	}

	private String normalizeUserId(String userId) {
		if (userId == null || userId.isBlank()) {
			return "default-user";
		}
		return userId.trim();
	}

}
