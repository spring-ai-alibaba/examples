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

package com.alibaba.cloud.ai.memory;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.cloud.ai.planner.LearningIntent;
import org.springframework.stereotype.Service;

/**
 * Stores lightweight learning memory in process.
 */
@Service
public class LearningMemoryService {

	public static final String DEFAULT_USER_ID = "default-user";

	private final Map<String, LearningMemory> memories = new ConcurrentHashMap<>();

	public LearningMemory read(String userId) {
		return this.memories.computeIfAbsent(normalizeUserId(userId), LearningMemory::new).copy();
	}

	public LearningMemory update(String userId, String message, LearningIntent intent) {
		LearningMemory memory = this.memories.computeIfAbsent(normalizeUserId(userId), LearningMemory::new);
		memory.setConversationCount(memory.getConversationCount() + 1);
		memory.setLastQuestion(message == null ? "" : message.trim());
		memory.setLastIntent(intent == null ? LearningIntent.GENERAL_CHAT.name() : intent.name());
		memory.setLevel(detectLevel(message, memory.getLevel()));
		addTopic(memory, message);
		memory.setUpdatedAt(LocalDateTime.now());
		return memory.copy();
	}

	private String normalizeUserId(String userId) {
		if (userId == null || userId.isBlank()) {
			return DEFAULT_USER_ID;
		}
		return userId.trim();
	}

	private String detectLevel(String message, String currentLevel) {
		String text = normalize(message);
		if (containsAny(text, "高级", "熟练", "advanced")) {
			return "熟练";
		}
		if (containsAny(text, "进阶", "中级", "intermediate")) {
			return "进阶";
		}
		if (containsAny(text, "初学", "入门", "beginner")) {
			return "初学者";
		}
		return currentLevel == null || currentLevel.isBlank() ? "初学者" : currentLevel;
	}

	private void addTopic(LearningMemory memory, String message) {
		String text = normalize(message);
		if (containsAny(text, "tool calling", "tool", "工具")) {
			memory.getTopics().add("Tool Calling");
		}
		if (containsAny(text, "skill", "技能")) {
			memory.getTopics().add("Skill");
		}
		if (containsAny(text, "agent", "智能体")) {
			memory.getTopics().add("Agent");
		}
		if (containsAny(text, "graph", "工作流", "图")) {
			memory.getTopics().add("Graph");
		}
		if (containsAny(text, "rag", "检索")) {
			memory.getTopics().add("RAG");
		}
		if (containsAny(text, "mcp")) {
			memory.getTopics().add("MCP");
		}
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
