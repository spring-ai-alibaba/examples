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

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.cloud.ai.rag.LearningRagService;
import com.alibaba.cloud.ai.skill.LearningSkillService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Tool Calling entry points for MiniMax.
 *
 * <p>
 * Keep this class thin: it only exposes model-callable methods. The real learning
 * logic lives in {@link LearningSkillService}.
 */
@Component
public class MiniMaxLearningTools {

	private final LearningSkillService learningSkillService;

	private final LearningRagService learningRagService;

	private final ToolCallDebugRecorder debugRecorder;

	public MiniMaxLearningTools(LearningSkillService learningSkillService, LearningRagService learningRagService,
			ToolCallDebugRecorder debugRecorder) {
		this.learningSkillService = learningSkillService;
		this.learningRagService = learningRagService;
		this.debugRecorder = debugRecorder;
	}

	@Tool(description = "获取指定时区的当前时间。当用户询问当前时间、北京时间、UTC 时间或真实时钟值时使用。")
	public String getCurrentTime(
			@ToolParam(description = "时区 ID，例如 Asia/Shanghai、UTC、America/New_York。用户说北京时间时使用 Asia/Shanghai。") String zoneId) {
		String result = this.learningSkillService.getCurrentTime(zoneId);
		this.debugRecorder.record("getCurrentTime", arguments("zoneId", zoneId), result);
		return result;
	}

	@Tool(description = "生成 Spring AI Alibaba 学习建议。当用户询问学习路线、下一步计划、Tool Calling、Skill、Agent、RAG、MCP 或 Graph 时使用。")
	public String generateLearningAdvice(
			@ToolParam(description = "学习主题，例如 Tool Calling、Skill、Agent、RAG、MCP、Graph、Spring AI Alibaba Agent。") String topic,
			@ToolParam(description = "学习者阶段，例如 beginner、intermediate、advanced，也可以传中文：初学者、进阶、熟练。") String level) {
		String result = this.learningSkillService.generateLearningAdvice(topic, level);
		this.debugRecorder.record("generateLearningAdvice", arguments("topic", topic, "level", level), result);
		return result;
	}

	@Tool(description = "生成当天学习计划。当用户要求今日计划、30 分钟学习安排、学习任务拆分或每日练习时使用。")
	public String generateDailyPlan(
			@ToolParam(description = "学习主题，例如 Tool Calling、Skill、Agent、RAG、MCP、Graph。") String topic,
			@ToolParam(description = "学习者阶段，例如 初学者、进阶、熟练。") String level,
			@ToolParam(description = "计划总时长，单位是分钟。例如 30、60、90。") Integer minutes) {
		String result = this.learningSkillService.generateDailyPlan(topic, level, minutes);
		this.debugRecorder.record("generateDailyPlan", arguments("topic", topic, "level", level, "minutes", minutes),
				result);
		return result;
	}

	@Tool(description = "解释 Spring AI Alibaba 相关概念。当用户询问 Tool、Tool Calling、Skill、Agent、RAG、MCP、Graph 是什么或它们区别时使用。")
	public String explainConcept(
			@ToolParam(description = "要解释的概念，例如 Tool Calling、Tool、Skill、Agent、RAG、MCP、Graph。") String concept,
			@ToolParam(description = "学习者阶段，例如 初学者、进阶、熟练。") String level) {
		String result = this.learningSkillService.explainConcept(concept, level);
		this.debugRecorder.record("explainConcept", arguments("concept", concept, "level", level), result);
		return result;
	}

	@Tool(description = "检索当前 minimax-chat 项目的本地文档和关键源码。当用户询问 README、项目结构、当前实现、调用链、Controller、Agent、Tool、Skill、Memory 或 RAG 代码细节时使用。")
	public String searchLearningDocs(
			@ToolParam(description = "检索问题或关键词，例如 当前项目 Tool Skill Agent Memory 调用链。") String query,
			@ToolParam(description = "返回结果数量，建议 1 到 5。") Integer limit) {
		String result = this.learningRagService.search(query, limit);
		this.debugRecorder.record("searchLearningDocs", arguments("query", query, "limit", limit), result);
		return result;
	}

	private Map<String, Object> arguments(Object... pairs) {
		Map<String, Object> arguments = new LinkedHashMap<>();
		for (int i = 0; i < pairs.length; i += 2) {
			arguments.put(String.valueOf(pairs[i]), pairs[i + 1] == null ? "" : pairs[i + 1]);
		}
		return arguments;
	}

}
