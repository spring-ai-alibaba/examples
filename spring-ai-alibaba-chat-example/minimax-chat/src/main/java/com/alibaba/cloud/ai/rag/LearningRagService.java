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

package com.alibaba.cloud.ai.rag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simple keyword-based RAG over local MiniMax chat documents.
 */
@Service
public class LearningRagService {

	private static final Logger logger = LoggerFactory.getLogger(LearningRagService.class);

	private static final int DEFAULT_LIMIT = 3;

	private static final int MAX_EXCERPT_LENGTH = 900;

	private static final List<Path> DOCUMENT_PATHS = List.of(
			Path.of("spring-ai-alibaba-chat-example/minimax-chat/README.md"),
			Path.of("spring-ai-alibaba-chat-example/minimax-chat/src/main/java/com/alibaba/cloud/ai/controller/MiniMaxChatClientController.java"),
			Path.of("spring-ai-alibaba-chat-example/minimax-chat/src/main/java/com/alibaba/cloud/ai/agent/LearningAgentService.java"),
			Path.of("spring-ai-alibaba-chat-example/minimax-chat/src/main/java/com/alibaba/cloud/ai/tool/MiniMaxLearningTools.java"),
			Path.of("spring-ai-alibaba-chat-example/minimax-chat/src/main/java/com/alibaba/cloud/ai/skill/LearningSkillService.java"),
			Path.of("spring-ai-alibaba-chat-example/minimax-chat/src/main/java/com/alibaba/cloud/ai/planner/LearningIntentPlanner.java"),
			Path.of("spring-ai-alibaba-chat-example/minimax-chat/src/main/java/com/alibaba/cloud/ai/memory/LearningMemoryService.java"));

	private final List<LearningDocument> documents = new ArrayList<>();

	@PostConstruct
	public void loadDocuments() {
		this.documents.clear();
		for (Path path : DOCUMENT_PATHS) {
			readDocument(path);
		}
		logger.info("Loaded {} local learning documents for simple RAG", this.documents.size());
	}

	public String search(String query, Integer limit) {
		if (this.documents.isEmpty()) {
			return "本地学习文档尚未加载，无法检索。";
		}
		Set<String> keywords = keywords(query);
		int safeLimit = limit == null || limit < 1 ? DEFAULT_LIMIT : Math.min(limit, 5);
		List<SearchResult> results = this.documents.stream()
				.map(document -> new SearchResult(document, score(document, keywords)))
				.filter(result -> result.score() > 0)
				.sorted(Comparator.comparingInt(SearchResult::score).reversed())
				.limit(safeLimit)
				.toList();
		if (results.isEmpty()) {
			return "没有在当前 minimax-chat 本地文档中检索到相关内容。可以尝试使用 Tool、Skill、Agent、Memory、README、调用链等关键词。";
		}
		StringBuilder builder = new StringBuilder("本地文档检索结果：\n");
		for (int i = 0; i < results.size(); i++) {
			SearchResult result = results.get(i);
			builder.append("\n[").append(i + 1).append("] ")
					.append(result.document().title())
					.append("\n路径：")
					.append(result.document().path())
					.append("\n摘要：")
					.append(excerpt(result.document().content(), keywords))
					.append("\n");
		}
		return builder.toString();
	}

	private void readDocument(Path path) {
		Path resolved = resolve(path);
		if (!Files.exists(resolved)) {
			logger.debug("Skip missing learning document: {}", resolved.toAbsolutePath());
			return;
		}
		try {
			String content = Files.readString(resolved, StandardCharsets.UTF_8);
			this.documents.add(new LearningDocument(resolved.getFileName().toString(), path.toString(), content));
		}
		catch (IOException ex) {
			logger.warn("Failed to read learning document: {}", resolved.toAbsolutePath(), ex);
		}
	}

	private Path resolve(Path path) {
		if (Files.exists(path)) {
			return path;
		}
		Path moduleRelative = toModuleRelativePath(path);
		if (Files.exists(moduleRelative)) {
			return moduleRelative;
		}
		return path;
	}

	private Path toModuleRelativePath(Path path) {
		if (path.getNameCount() > 2 && "spring-ai-alibaba-chat-example".equals(path.getName(0).toString())
				&& "minimax-chat".equals(path.getName(1).toString())) {
			return path.subpath(2, path.getNameCount());
		}
		return path;
	}

	private int score(LearningDocument document, Set<String> keywords) {
		String text = (document.title() + " " + document.path() + " " + document.content()).toLowerCase(Locale.ROOT);
		int score = 0;
		for (String keyword : keywords) {
			if (text.contains(keyword)) {
				score++;
			}
		}
		return score;
	}

	private String excerpt(String content, Set<String> keywords) {
		String normalized = content.replace("\r\n", "\n").replaceAll("\\n{3,}", "\n\n").trim();
		int start = firstKeywordIndex(normalized, keywords);
		if (start < 0) {
			return trim(normalized);
		}
		start = Math.max(0, start - 220);
		int end = Math.min(normalized.length(), start + MAX_EXCERPT_LENGTH);
		return trim(normalized.substring(start, end));
	}

	private int firstKeywordIndex(String content, Set<String> keywords) {
		String text = content.toLowerCase(Locale.ROOT);
		int index = -1;
		for (String keyword : keywords) {
			int current = text.indexOf(keyword);
			if (current >= 0 && (index < 0 || current < index)) {
				index = current;
			}
		}
		return index;
	}

	private String trim(String text) {
		String value = text.strip();
		if (value.length() <= MAX_EXCERPT_LENGTH) {
			return value;
		}
		return value.substring(0, MAX_EXCERPT_LENGTH).strip() + "...";
	}

	private Set<String> keywords(String query) {
		Set<String> keywords = new LinkedHashSet<>();
		String text = query == null ? "" : query.toLowerCase(Locale.ROOT);
		for (String token : text.split("[\\s,，。；;：:、/\\\\()（）\\[\\]{}<>《》\"']+")) {
			if (token.length() >= 2) {
				keywords.add(token);
			}
		}
		addIfContains(text, keywords, "readme", "README");
		addIfContains(text, keywords, "文档", "README");
		addIfContains(text, keywords, "源码", "Service", "Controller", "Tool");
		addIfContains(text, keywords, "调用链", "LearningAgentService", "MiniMaxLearningTools");
		addIfContains(text, keywords, "tool", "MiniMaxLearningTools", "Tool Calling");
		addIfContains(text, keywords, "skill", "LearningSkillService");
		addIfContains(text, keywords, "agent", "LearningAgentService");
		addIfContains(text, keywords, "memory", "LearningMemoryService");
		addIfContains(text, keywords, "rag", "LearningRagService");
		if (keywords.isEmpty()) {
			keywords.add("minimax-chat");
		}
		return keywords.stream().map(keyword -> keyword.toLowerCase(Locale.ROOT)).collect(LinkedHashSet::new,
				LinkedHashSet::add, LinkedHashSet::addAll);
	}

	private void addIfContains(String text, Set<String> keywords, String trigger, String... values) {
		if (text.contains(trigger.toLowerCase(Locale.ROOT))) {
			for (String value : values) {
				keywords.add(value);
			}
		}
	}

	private record SearchResult(LearningDocument document, int score) {
	}

}
