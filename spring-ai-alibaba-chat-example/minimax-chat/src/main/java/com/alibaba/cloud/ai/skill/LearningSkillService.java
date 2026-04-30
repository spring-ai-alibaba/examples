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

package com.alibaba.cloud.ai.skill;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * Learning skill implementation.
 *
 * <p>
 * Tool classes expose callable entry points to the model. This service owns the actual
 * learning-domain logic, so it can be reused later by Agent, Graph, REST APIs, or tests.
 */
@Service
public class LearningSkillService {

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

	private static final Set<String> SUPPORTED_ZONES = Set.of("Asia/Shanghai", "UTC", "America/New_York",
			"Europe/London", "Asia/Tokyo");

	private static final Map<String, String> CONCEPTS = Map.of(
			"tool calling", "Tool Calling 是让模型在需要真实能力时调用 Java 方法。它解决的是“模型不会凭空知道实时信息或业务系统数据”的问题。",
			"tool", "Tool 是一个可被模型调用的函数入口，通常用 @Tool 暴露。它应该小而明确，比如获取当前时间、查询订单、检索知识库。",
			"skill", "Skill 是一组围绕同一目标组织起来的能力。它可以包含多个 Tool、提示词模板、业务规则和执行步骤。",
			"agent", "Agent 是能够理解目标、选择工具或技能、执行步骤并根据结果继续推理的智能体。",
			"rag", "RAG 是检索增强生成。它先从知识库查相关资料，再把资料交给模型回答，适合企业知识问答。",
			"mcp", "MCP 是模型上下文协议，用统一方式把外部工具、资源和服务暴露给模型或 Agent。",
			"graph", "Graph 是显式工作流编排。开发者把任务拆成节点和边，适合多步骤、可控、可恢复的 Agent 流程。");

	public String getCurrentTime(String zoneId) {
		String normalizedZoneId = normalizeZoneId(zoneId);
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of(normalizedZoneId));
		return "当前时间：" + now.format(TIME_FORMATTER) + "。时区：" + normalizedZoneId + "。";
	}

	public String generateLearningAdvice(String topic, String level) {
		String safeTopic = hasText(topic) ? topic.trim() : "Spring AI Alibaba Agent";
		String safeLevel = normalizeLevel(level);

		return """
				学习建议：
				1. 当前主题：%s。
				2. 当前阶段：%s。
				3. 推荐顺序：先跑通 ChatClient，再学习 Tool Calling，然后封装 Skill，最后进入 Agent 编排。
				4. 实践任务：先写一个无外部依赖的本地 Java Tool，例如获取当前时间或生成学习建议。
				5. 验收标准：模型能根据用户问题自动决定是否调用工具，并把工具结果整合成自然语言回答。
				""".formatted(safeTopic, safeLevel);
	}

	public String generateDailyPlan(String topic, String level, Integer minutes) {
		String safeTopic = hasText(topic) ? topic.trim() : "Spring AI Alibaba Agent";
		String safeLevel = normalizeLevel(level);
		int safeMinutes = minutes == null || minutes <= 0 ? 30 : Math.min(minutes, 180);

		int first = Math.max(5, safeMinutes / 3);
		int second = Math.max(10, safeMinutes / 2);
		int third = Math.max(5, safeMinutes - first - second);

		return """
				今日学习计划：
				- 主题：%s
				- 阶段：%s
				- 总时长：%d 分钟

				时间安排：
				1. %d 分钟：阅读当前模块代码，重点看 Controller、Tool、Skill Service 的调用链。
				2. %d 分钟：动手改一个小功能，例如新增一个 @Tool 参数或调整 Skill 返回内容。
				3. %d 分钟：用页面和 .http 文件测试，记录模型是否真的调用了工具。

				验收问题：
				- 模型什么时候应该调用 Tool？
				- Tool 和 Skill Service 的边界分别是什么？
				- 如果后面升级到 Agent，哪些逻辑可以继续复用？
				""".formatted(safeTopic, safeLevel, safeMinutes, first, second, third);
	}

	public String explainConcept(String concept, String level) {
		String safeConcept = hasText(concept) ? concept.trim() : "Agent";
		String safeLevel = normalizeLevel(level);
		String key = safeConcept.toLowerCase(Locale.ROOT);
		String explanation = CONCEPTS.getOrDefault(key, "这个概念可以放在 Spring AI Alibaba 的 Chat、Tool、Skill、Agent、Graph 主线里理解。");

		return """
				概念解释：
				- 概念：%s
				- 学习阶段：%s
				- 核心含义：%s
				- 在当前项目中的位置：你现在的 minimax-chat 已经完成 Chat、多轮上下文、Markdown 和 Tool Calling，下一步通过 Skill Service 把工具背后的业务逻辑沉淀成可复用能力。
				""".formatted(safeConcept, safeLevel, explanation);
	}

	private String normalizeZoneId(String zoneId) {
		if (!hasText(zoneId)) {
			return "Asia/Shanghai";
		}
		String candidate = zoneId.trim();
		if ("beijing".equalsIgnoreCase(candidate) || "北京时间".equals(candidate) || "北京".equals(candidate)
				|| "上海".equals(candidate) || "中国".equals(candidate)) {
			return "Asia/Shanghai";
		}
		if (SUPPORTED_ZONES.contains(candidate)) {
			return candidate;
		}
		try {
			ZoneId.of(candidate);
			return candidate;
		}
		catch (Exception ignored) {
			return "Asia/Shanghai";
		}
	}

	private String normalizeLevel(String level) {
		if (!hasText(level)) {
			return "初学者";
		}
		String candidate = level.trim();
		return switch (candidate.toLowerCase(Locale.ROOT)) {
			case "beginner", "basic", "newbie" -> "初学者";
			case "intermediate", "middle" -> "进阶";
			case "advanced", "expert" -> "熟练";
			default -> candidate;
		};
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

}
