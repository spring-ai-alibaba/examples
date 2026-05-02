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

import java.util.function.Function;

import com.alibaba.cloud.ai.tool.MiniMaxLearningTools;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Component;

/**
 * Adapts the existing learning tools to official Agent Framework ToolCallback
 * instances.
 */
@Component
public class OfficialLearningToolCallbacks {

	private final MiniMaxLearningTools learningTools;

	public OfficialLearningToolCallbacks(MiniMaxLearningTools learningTools) {
		this.learningTools = learningTools;
	}

	public ToolCallback[] all() {
		return new ToolCallback[] { currentTime(), learningAdvice(), dailyPlan(), conceptExplain(),
				learningDocsSearch() };
	}

	private ToolCallback currentTime() {
		Function<GetCurrentTimeRequest, String> function = request -> this.learningTools
				.getCurrentTime(request.zoneId());
		return FunctionToolCallback.builder("getCurrentTime", function)
				.description("获取指定时区的当前时间。用户询问当前时间、北京时间或 UTC 时间时使用。")
				.inputType(GetCurrentTimeRequest.class)
				.build();
	}

	private ToolCallback learningAdvice() {
		Function<GenerateLearningAdviceRequest, String> function = request -> this.learningTools
				.generateLearningAdvice(request.topic(), request.level());
		return FunctionToolCallback.builder("generateLearningAdvice", function)
				.description("生成 Spring AI Alibaba 学习建议。用户询问学习路线、下一步、Tool、Skill、Agent、RAG、MCP 或 Graph 时使用。")
				.inputType(GenerateLearningAdviceRequest.class)
				.build();
	}

	private ToolCallback dailyPlan() {
		Function<GenerateDailyPlanRequest, String> function = request -> this.learningTools
				.generateDailyPlan(request.topic(), request.level(), request.minutes());
		return FunctionToolCallback.builder("generateDailyPlan", function)
				.description("生成当天学习计划。用户要求今日计划、30 分钟学习安排、任务拆分或每日练习时使用。")
				.inputType(GenerateDailyPlanRequest.class)
				.build();
	}

	private ToolCallback conceptExplain() {
		Function<ExplainConceptRequest, String> function = request -> this.learningTools
				.explainConcept(request.concept(), request.level());
		return FunctionToolCallback.builder("explainConcept", function)
				.description("解释 Spring AI Alibaba 相关概念。用户询问 Tool、Skill、Agent、RAG、MCP、Graph 的含义或区别时使用。")
				.inputType(ExplainConceptRequest.class)
				.build();
	}

	private ToolCallback learningDocsSearch() {
		Function<SearchLearningDocsRequest, String> function = request -> this.learningTools
				.searchLearningDocs(request.query(), request.limit());
		return FunctionToolCallback.builder("searchLearningDocs", function)
				.description("检索当前 minimax-chat 项目的本地文档和关键源码。用户询问 README、项目结构、调用链或当前实现细节时使用。")
				.inputType(SearchLearningDocsRequest.class)
				.build();
	}

	@JsonClassDescription("Request to get current time by zone id")
	public record GetCurrentTimeRequest(
			@JsonProperty(value = "zoneId", required = true)
			@JsonPropertyDescription("时区 ID，例如 Asia/Shanghai、UTC、America/New_York。") String zoneId) {
	}

	@JsonClassDescription("Request to generate Spring AI Alibaba learning advice")
	public record GenerateLearningAdviceRequest(
			@JsonProperty(value = "topic", required = true)
			@JsonPropertyDescription("学习主题，例如 Tool Calling、Skill、Agent、RAG、MCP、Graph。") String topic,
			@JsonProperty(value = "level")
			@JsonPropertyDescription("学习者阶段，例如 初学者、进阶、熟练。") String level) {
	}

	@JsonClassDescription("Request to generate a daily learning plan")
	public record GenerateDailyPlanRequest(
			@JsonProperty(value = "topic", required = true)
			@JsonPropertyDescription("学习主题，例如 Spring AI Alibaba Agent。") String topic,
			@JsonProperty(value = "level")
			@JsonPropertyDescription("学习者阶段，例如 初学者、进阶、熟练。") String level,
			@JsonProperty(value = "minutes")
			@JsonPropertyDescription("计划总时长，单位分钟，例如 30、60、90。") Integer minutes) {
	}

	@JsonClassDescription("Request to explain a learning concept")
	public record ExplainConceptRequest(
			@JsonProperty(value = "concept", required = true)
			@JsonPropertyDescription("要解释的概念，例如 Tool、Skill、Agent、RAG、MCP、Graph。") String concept,
			@JsonProperty(value = "level")
			@JsonPropertyDescription("学习者阶段，例如 初学者、进阶、熟练。") String level) {
	}

	@JsonClassDescription("Request to search local minimax-chat learning docs")
	public record SearchLearningDocsRequest(
			@JsonProperty(value = "query", required = true)
			@JsonPropertyDescription("检索问题或关键词。") String query,
			@JsonProperty(value = "limit")
			@JsonPropertyDescription("返回结果数量，建议 1 到 5。") Integer limit) {
	}

}
