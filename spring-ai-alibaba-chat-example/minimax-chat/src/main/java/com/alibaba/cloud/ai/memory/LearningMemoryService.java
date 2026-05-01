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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.cloud.ai.planner.LearningIntent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Stores lightweight learning memory and persists it to a JSON file.
 */
@Service
public class LearningMemoryService {

	private static final Logger logger = LoggerFactory.getLogger(LearningMemoryService.class);

	public static final String DEFAULT_USER_ID = "default-user";

	private final ObjectMapper objectMapper;

	private final Path memoryFile;

	private final Map<String, LearningMemory> memories = new ConcurrentHashMap<>();

	public LearningMemoryService(ObjectMapper objectMapper,
			@Value("${minimax.memory.file:spring-ai-alibaba-chat-example/minimax-chat/memory/learning-memory.json}") String memoryFile) {
		this.objectMapper = objectMapper;
		this.memoryFile = Path.of(memoryFile);
	}

	@PostConstruct
	public void load() {
		if (!Files.exists(this.memoryFile)) {
			persist();
			return;
		}
		try {
			Map<String, StoredLearningMemory> storedMemories = this.objectMapper.readValue(this.memoryFile.toFile(),
					new TypeReference<>() {
					});
			storedMemories.forEach((userId, storedMemory) -> this.memories.put(userId,
					toLearningMemory(userId, storedMemory)));
			logger.info("Loaded {} learning memories from {}", this.memories.size(), this.memoryFile.toAbsolutePath());
		}
		catch (IOException ex) {
			logger.warn("Failed to load learning memory from {}", this.memoryFile.toAbsolutePath(), ex);
		}
	}

	public LearningMemory read(String userId) {
		return this.memories.computeIfAbsent(normalizeUserId(userId), LearningMemory::new).copy();
	}

	public synchronized LearningMemory clear(String userId) {
		String safeUserId = normalizeUserId(userId);
		LearningMemory memory = new LearningMemory(safeUserId);
		this.memories.put(safeUserId, memory);
		persist();
		return memory.copy();
	}

	public synchronized LearningMemory update(String userId, String message, LearningIntent intent) {
		LearningMemory memory = this.memories.computeIfAbsent(normalizeUserId(userId), LearningMemory::new);
		memory.setConversationCount(memory.getConversationCount() + 1);
		memory.setLastQuestion(message == null ? "" : message.trim());
		memory.setLastIntent(intent == null ? LearningIntent.GENERAL_CHAT.name() : intent.name());
		memory.setLevel(detectLevel(message, memory.getLevel()));
		addTopic(memory, message);
		memory.setUpdatedAt(LocalDateTime.now());
		persist();
		return memory.copy();
	}

	private synchronized void persist() {
		try {
			if (this.memoryFile.getParent() != null) {
				Files.createDirectories(this.memoryFile.getParent());
			}
			this.objectMapper.writerWithDefaultPrettyPrinter()
					.writeValue(this.memoryFile.toFile(), toStoredMemories());
			logger.debug("Persisted {} learning memories to {}", this.memories.size(), this.memoryFile.toAbsolutePath());
		}
		catch (IOException ex) {
			logger.warn("Failed to persist learning memory to {}", this.memoryFile.toAbsolutePath(), ex);
		}
	}

	private Map<String, StoredLearningMemory> toStoredMemories() {
		Map<String, StoredLearningMemory> stored = new LinkedHashMap<>();
		this.memories.forEach((userId, memory) -> stored.put(userId, new StoredLearningMemory(memory.getLevel(),
				List.copyOf(memory.getTopics()), memory.getLastIntent(), memory.getLastQuestion(),
				memory.getConversationCount(), memory.getUpdatedAt())));
		return stored;
	}

	private LearningMemory toLearningMemory(String userId, StoredLearningMemory storedMemory) {
		LearningMemory memory = new LearningMemory(userId);
		if (storedMemory == null) {
			return memory;
		}
		memory.setLevel(defaultText(storedMemory.level(), "初学者"));
		memory.getTopics().addAll(new LinkedHashSet<>(storedMemory.topics() == null ? List.of()
				: storedMemory.topics()));
		memory.setLastIntent(defaultText(storedMemory.lastIntent(), LearningIntent.GENERAL_CHAT.name()));
		memory.setLastQuestion(defaultText(storedMemory.lastQuestion(), ""));
		memory.setConversationCount(storedMemory.conversationCount());
		memory.setUpdatedAt(storedMemory.updatedAt() == null ? LocalDateTime.now() : storedMemory.updatedAt());
		return memory;
	}

	private String defaultText(String text, String defaultValue) {
		return text == null || text.isBlank() ? defaultValue : text;
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

	public record StoredLearningMemory(String level, List<String> topics, String lastIntent, String lastQuestion,
			int conversationCount, LocalDateTime updatedAt) {
	}

}
