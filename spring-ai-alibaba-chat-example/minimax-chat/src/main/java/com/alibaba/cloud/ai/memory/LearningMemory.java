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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple learning profile for one user.
 */
public class LearningMemory {

	private final String userId;

	private String level = "初学者";

	private final Set<String> topics = new LinkedHashSet<>();

	private String lastIntent = "GENERAL_CHAT";

	private String lastQuestion = "";

	private int conversationCount;

	private LocalDateTime updatedAt;

	public LearningMemory(String userId) {
		this.userId = userId;
		this.updatedAt = LocalDateTime.now();
	}

	public LearningMemory copy() {
		LearningMemory memory = new LearningMemory(this.userId);
		memory.level = this.level;
		memory.topics.addAll(this.topics);
		memory.lastIntent = this.lastIntent;
		memory.lastQuestion = this.lastQuestion;
		memory.conversationCount = this.conversationCount;
		memory.updatedAt = this.updatedAt;
		return memory;
	}

	public String summary() {
		String topicText = this.topics.isEmpty() ? "暂无" : String.join("、", this.topics);
		return "用户阶段：" + this.level + "；关注主题：" + topicText + "；历史对话轮次：" + this.conversationCount
				+ "；上次意图：" + this.lastIntent + "。";
	}

	public String getUserId() {
		return this.userId;
	}

	public String getLevel() {
		return this.level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Set<String> getTopics() {
		return this.topics;
	}

	public String getLastIntent() {
		return this.lastIntent;
	}

	public void setLastIntent(String lastIntent) {
		this.lastIntent = lastIntent;
	}

	public String getLastQuestion() {
		return this.lastQuestion;
	}

	public void setLastQuestion(String lastQuestion) {
		this.lastQuestion = lastQuestion;
	}

	public int getConversationCount() {
		return this.conversationCount;
	}

	public void setConversationCount(int conversationCount) {
		this.conversationCount = conversationCount;
	}

	public LocalDateTime getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
