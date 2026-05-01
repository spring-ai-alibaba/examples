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

package com.alibaba.cloud.ai.graph;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cloud.ai.planner.LearningIntent;
import org.springframework.stereotype.Service;

/**
 * Builds a lightweight workflow graph for the current learning agent request.
 */
@Service
public class LearningGraphService {

	public LearningGraphResult plan(String userId, String message, LearningIntent intent) {
		List<LearningGraphStep> steps = new ArrayList<>();
		steps.add(step("receive", "Receive", "DONE", "接收用户 " + normalizeUserId(userId) + " 的问题。"));
		steps.add(step("memory-read", "Memory Read", "DONE", "按 userId 从 JSON 文件读取长期学习记忆。"));
		steps.add(step("planner", "Planner", "DONE", "识别学习意图：" + intent + "。"));
		steps.add(step("strategy", "Strategy", "DONE", strategyDetail(intent)));
		steps.add(step("model-call", "Model Call", "DONE", "调用 MiniMax-M2.7，并暴露 Tool、Skill、RAG 能力。"));
		steps.add(step("tool-execute", "Tool Execute", "WAITING", "由模型按需触发工具，例如时间、学习计划、概念解释或本地文档检索。"));
		steps.add(step("memory-write", "Memory Write", "DONE", "回答完成后更新当前用户长期记忆。"));
		steps.add(step("response", "Response", "DONE", "返回回答、Graph 节点、Tool 调用和 Memory 调试信息。"));
		return new LearningGraphResult(List.copyOf(steps));
	}

	private LearningGraphStep step(String id, String name, String status, String detail) {
		return new LearningGraphStep(id, name, status, detail);
	}

	private String strategyDetail(LearningIntent intent) {
		return switch (intent) {
			case TIME_QUERY -> "时间问题优先进入 getCurrentTime 工具路径。";
			case LEARNING_ADVICE -> "学习建议问题优先进入 generateLearningAdvice 工具路径。";
			case DAILY_PLAN -> "计划类问题优先进入 generateDailyPlan 工具路径。";
			case CONCEPT_EXPLAIN -> "概念或项目实现问题优先进入 explainConcept 或 searchLearningDocs 工具路径。";
			case MIXED -> "混合问题允许组合多个工具路径。";
			case GENERAL_CHAT -> "普通聊天可以直接由模型基于上下文回答。";
		};
	}

	private String normalizeUserId(String userId) {
		if (userId == null || userId.isBlank()) {
			return "default-user";
		}
		return userId.trim();
	}

}
