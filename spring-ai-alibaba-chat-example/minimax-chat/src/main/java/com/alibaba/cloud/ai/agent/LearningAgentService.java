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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cloud.ai.agent.LearningAgentResult.LearningAgentStep;
import com.alibaba.cloud.ai.memory.LearningMemory;
import com.alibaba.cloud.ai.memory.LearningMemoryService;
import com.alibaba.cloud.ai.planner.LearningIntent;
import com.alibaba.cloud.ai.planner.LearningIntentPlanner;
import com.alibaba.cloud.ai.tool.MiniMaxLearningTools;
import com.alibaba.cloud.ai.tool.ToolCallDebugRecorder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Lightweight learning agent that plans the request, chooses an execution strategy,
 * and lets the model call tools when needed.
 */
@Service
public class LearningAgentService {

	private static final String SYSTEM_PROMPT = """
			你是 MiniMax-M2.7 学习助手。
			请始终使用中文回答。
			回答要清晰、直接，适合正在学习 Spring AI Alibaba Agent 和 Skill 开发的 Java 开发者。
			你可以使用工具获取真实时间、生成学习建议、生成今日学习计划、解释 Spring AI Alibaba 相关概念。
			当用户询问当前时间、北京时间、UTC 时间等真实时间问题时，优先调用 getCurrentTime 工具。
			当用户询问学习路线、下一步学习什么、Tool Calling、Skill、Agent、RAG、MCP 或 Graph 时，优先调用 generateLearningAdvice 工具。
			当用户要求今日计划、30 分钟学习安排、每日练习或任务拆分时，优先调用 generateDailyPlan 工具。
			当用户询问概念含义或区别，例如 Tool、Skill、Agent、Graph 是什么时，优先调用 explainConcept 工具。
			不要输出 <think>、</think> 或任何思考标签。
			""";

	private static final int MAX_HISTORY_MESSAGES = 20;

	private final ChatClient chatClient;

	private final MiniMaxLearningTools learningTools;

	private final ToolCallDebugRecorder debugRecorder;

	private final LearningIntentPlanner intentPlanner;

	private final LearningMemoryService memoryService;

	public LearningAgentService(ChatModel chatModel, MiniMaxLearningTools learningTools,
			ToolCallDebugRecorder debugRecorder, LearningIntentPlanner intentPlanner,
			LearningMemoryService memoryService) {
		this.learningTools = learningTools;
		this.debugRecorder = debugRecorder;
		this.intentPlanner = intentPlanner;
		this.memoryService = memoryService;
		this.chatClient = ChatClient.builder(chatModel)
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.defaultOptions(defaultOptions())
				.build();
	}

	public LearningAgentResult chat(String message, List<LearningAgentMessage> history) {
		this.debugRecorder.clear();
		LearningMemory memoryBefore = this.memoryService.read(LearningMemoryService.DEFAULT_USER_ID);
		LearningIntent intent = this.intentPlanner.plan(message);
		List<LearningAgentStep> steps = planSteps(message, intent, memoryBefore);
		try {
			steps.add(new LearningAgentStep("MODEL_CALL", "携带系统提示、历史上下文和可用工具调用 MiniMax-M2.7。"));
			String content = this.chatClient.prompt()
					.messages(buildMessages(message, history, intent, memoryBefore))
					.options(defaultOptions())
					.tools(this.learningTools)
					.call()
					.content();
			List<ToolCallDebugRecorder.ToolCallDebug> toolCalls = this.debugRecorder.snapshot();
			steps.add(toolCalls.isEmpty()
					? new LearningAgentStep("TOOL_RESULT", "本轮没有触发工具，模型直接基于上下文回答。")
					: new LearningAgentStep("TOOL_RESULT",
							"本轮模型触发了 " + toolCalls.size() + " 次工具调用，并基于工具结果生成最终回答。"));
			LearningMemory memoryAfter = this.memoryService.update(LearningMemoryService.DEFAULT_USER_ID, message, intent);
			steps.add(new LearningAgentStep("MEMORY_WRITE", "已更新用户学习阶段、关注主题、最近意图和对话轮次。"));
			return new LearningAgentResult(content, intent, memoryBefore, memoryAfter, List.copyOf(steps), toolCalls);
		}
		finally {
			this.debugRecorder.remove();
		}
	}

	public Flux<String> stream(String message, List<LearningAgentMessage> history) {
		this.debugRecorder.clear();
		LearningMemory memory = this.memoryService.read(LearningMemoryService.DEFAULT_USER_ID);
		LearningIntent intent = this.intentPlanner.plan(message);
		return this.chatClient.prompt()
				.messages(buildMessages(message, history, intent, memory))
				.options(defaultOptions())
				.tools(this.learningTools)
				.stream()
				.content()
				.doOnComplete(() -> this.memoryService.update(LearningMemoryService.DEFAULT_USER_ID, message, intent))
				.doFinally(signalType -> this.debugRecorder.remove());
	}

	private List<LearningAgentStep> planSteps(String message, LearningIntent intent, LearningMemory memory) {
		List<LearningAgentStep> steps = new ArrayList<>();
		steps.add(new LearningAgentStep("RECEIVE", "接收到用户问题：" + normalizeForStep(message)));
		steps.add(new LearningAgentStep("MEMORY_READ", "读取用户学习记忆：" + memory.summary()));
		steps.add(new LearningAgentStep("PLAN", "Planner 识别意图为 " + intent + "。"));
		steps.add(new LearningAgentStep("STRATEGY", strategyFor(intent)));
		return steps;
	}

	private String strategyFor(LearningIntent intent) {
		return switch (intent) {
			case TIME_QUERY -> "优先让模型调用 getCurrentTime，再用中文解释时间结果。";
			case LEARNING_ADVICE -> "优先让模型调用 generateLearningAdvice，生成适合当前阶段的学习建议。";
			case DAILY_PLAN -> "优先让模型调用 generateDailyPlan，把学习目标拆成可执行时间块。";
			case CONCEPT_EXPLAIN -> "优先让模型调用 explainConcept，用学习者能理解的方式解释概念。";
			case MIXED -> "允许模型组合多个工具，把时间、计划、建议或概念解释整合成一个完整回答。";
			case GENERAL_CHAT -> "不强制调用工具，模型可以根据上下文直接回答。";
		};
	}

	private List<Message> buildMessages(String message, List<LearningAgentMessage> history, LearningIntent intent,
			LearningMemory memory) {
		List<Message> messages = new ArrayList<>();
		messages.add(new SystemMessage(SYSTEM_PROMPT + "\n" + this.intentPlanner.instructionFor(intent) + "\n"
				+ "用户学习记忆：" + memory.summary()));

		List<LearningAgentMessage> safeHistory = history == null ? List.of() : history;
		int start = Math.max(0, safeHistory.size() - MAX_HISTORY_MESSAGES);
		for (LearningAgentMessage item : safeHistory.subList(start, safeHistory.size())) {
			Message historyMessage = toMessage(item);
			if (historyMessage != null) {
				messages.add(historyMessage);
			}
		}

		messages.add(new UserMessage(message));
		return messages;
	}

	private Message toMessage(LearningAgentMessage message) {
		if (message == null || message.content() == null || message.content().isBlank()) {
			return null;
		}
		return switch (normalizeRole(message.role())) {
			case "assistant" -> new AssistantMessage(message.content());
			case "system" -> null;
			default -> new UserMessage(message.content());
		};
	}

	private String normalizeRole(String role) {
		if (role == null) {
			return "user";
		}
		return role.trim().toLowerCase();
	}

	private String normalizeForStep(String message) {
		if (message == null || message.isBlank()) {
			return "默认问候";
		}
		String text = message.replaceAll("\\s+", " ").trim();
		return text.length() > 80 ? text.substring(0, 80) + "..." : text;
	}

	private OpenAiChatOptions defaultOptions() {
		return OpenAiChatOptions.builder()
				.model("MiniMax-M2.7")
				.temperature(0.7)
				.build();
	}

	public record LearningAgentMessage(String role, String content) {
	}

}
