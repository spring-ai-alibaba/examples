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

package com.alibaba.cloud.ai.planner;

import java.util.Locale;

import org.springframework.stereotype.Component;

/**
 * Lightweight rule-based planner for the learning chat flow.
 */
@Component
public class LearningIntentPlanner {

	public LearningIntent plan(String message) {
		String text = normalize(message);
		int matched = 0;
		LearningIntent lastIntent = LearningIntent.GENERAL_CHAT;

		if (containsAny(text, "时间", "几点", "北京时间", "utc", "当前时间", "现在")) {
			matched++;
			lastIntent = LearningIntent.TIME_QUERY;
		}
		if (containsAny(text, "学习建议", "学习路线", "路线", "下一步", "怎么学", "如何学习", "实践路径")) {
			matched++;
			lastIntent = LearningIntent.LEARNING_ADVICE;
		}
		if (containsAny(text, "计划", "今日", "今天", "30分钟", "30 分钟", "安排", "每日", "任务拆分")) {
			matched++;
			lastIntent = LearningIntent.DAILY_PLAN;
		}
		if (containsAny(text, "是什么", "区别", "解释", "概念", "tool", "skill", "agent", "rag", "mcp", "graph")) {
			matched++;
			lastIntent = LearningIntent.CONCEPT_EXPLAIN;
		}
		if (containsAny(text, "readme", "文档", "源码", "项目结构", "当前实现", "调用链", "minimax-chat")) {
			matched++;
			lastIntent = LearningIntent.CONCEPT_EXPLAIN;
		}

		if (matched > 1) {
			return LearningIntent.MIXED;
		}
		return lastIntent;
	}

	public String instructionFor(LearningIntent intent) {
		return switch (intent) {
			case TIME_QUERY -> "本轮 Planner 识别为 TIME_QUERY。回答时优先调用 getCurrentTime 工具获取真实时间。";
			case LEARNING_ADVICE -> "本轮 Planner 识别为 LEARNING_ADVICE。回答时优先调用 generateLearningAdvice 工具生成学习建议。";
			case DAILY_PLAN -> "本轮 Planner 识别为 DAILY_PLAN。回答时优先调用 generateDailyPlan 工具生成可执行计划。";
			case CONCEPT_EXPLAIN -> "本轮 Planner 识别为 CONCEPT_EXPLAIN。回答概念问题时优先调用 explainConcept 工具；涉及当前项目文档或源码实现时优先调用 searchLearningDocs 工具。";
			case MIXED -> "本轮 Planner 识别为 MIXED。回答时可以按需组合调用多个工具，并把结果整合成一个自然回答。涉及当前项目实现时优先调用 searchLearningDocs 工具。";
			case GENERAL_CHAT -> "本轮 Planner 识别为 GENERAL_CHAT。若不需要工具，可以直接回答。";
		};
	}

	private String normalize(String message) {
		return message == null ? "" : message.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
	}

	private boolean containsAny(String text, String... keywords) {
		for (String keyword : keywords) {
			if (text.contains(keyword.toLowerCase(Locale.ROOT))) {
				return true;
			}
		}
		return false;
	}

}
