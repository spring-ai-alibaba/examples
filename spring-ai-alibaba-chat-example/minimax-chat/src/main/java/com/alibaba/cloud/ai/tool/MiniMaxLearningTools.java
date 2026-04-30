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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * MiniMax 对话示例使用的本地工具。
 */
@Component
public class MiniMaxLearningTools {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

	private static final Set<String> SUPPORTED_ZONES = Set.of("Asia/Shanghai", "UTC", "America/New_York",
			"Europe/London", "Asia/Tokyo");

	@Tool(description = "获取指定时区的当前时间。当用户询问当前时间、北京时间、UTC 时间或真实时钟值时使用。")
	public String getCurrentTime(
			@ToolParam(description = "时区 ID，例如 Asia/Shanghai、UTC、America/New_York。用户说北京时间时使用 Asia/Shanghai。") String zoneId) {

		String normalizedZoneId = normalizeZoneId(zoneId);
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of(normalizedZoneId));
		return "当前时间：" + now.format(FORMATTER) + "。时区：" + normalizedZoneId + "。";
	}

	@Tool(description = "生成 Spring AI Alibaba 学习建议。当用户询问 Spring AI Alibaba、Tool Calling、Skill、Agent、RAG、MCP 或 Graph 的学习路线、下一步计划或实践任务时使用。")
	public String generateLearningAdvice(
			@ToolParam(description = "学习主题，例如 Tool Calling、Skill、Agent、RAG、MCP、Graph、Spring AI Alibaba Agent。") String topic,
			@ToolParam(description = "学习者阶段，例如 beginner、intermediate、advanced，也可以传中文：初学者、进阶、熟练。") String level) {

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
		return switch (candidate.toLowerCase()) {
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
