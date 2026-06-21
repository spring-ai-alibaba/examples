/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.example.dcg.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor.TOP_K;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


/**
 * Description: 数据分类分级智能体服务类
 * Author: yhong
 * Date: 2025/4/12
 */
@Service
public class ClassificationAssistant {

	private final ChatClient chatClient;

	public ClassificationAssistant(ChatClient.Builder modelBuilder,
								   VectorStore classificationVectorStore,
								   ChatMemory chatMemory) {

		// 构造 ChatClient，注入 RAG 和 Memory 等 Advisor
		this.chatClient = modelBuilder
				.defaultSystem("""
							你是一个数据安全分类分级助手，请根据用户输入的字段名和下方提供的字段分类知识，判断该字段属于哪个分类路径，分级是多少，并简要说明理由。
							请使用如下格式输出：
								 字段名：...
								 分类路径：一级 > 二级 > 三级 > 四级
								 分级：第X级
								 理由：...
						""")
				.defaultAdvisors(
						MessageChatMemoryAdvisor.builder(chatMemory).build(), // Chat Memory
						QuestionAnswerAdvisor
								.builder(classificationVectorStore)
								.searchRequest(SearchRequest.builder()
										.topK(5)
										.similarityThresholdAll()
										.build())
								.build(),
						new SimpleLoggerAdvisor()
				)
				.build();
	}

	/**
	 * 字段分类与分级推理方法
	 * @param fieldName 用户输入的字段名，如“专利交底书”
	 * @return 返回模型推理结果文本
	 */
	public String classify(String fieldName, String chatId) {

		// 调用 ChatClient 进行推理，并返回内容
		return chatClient.prompt()
				.user(fieldName)
				.advisors(a -> a.param(CONVERSATION_ID, chatId).param(TOP_K, 100)) // 设置advisor参数， 记忆使用chatId， 拉取最近的100条记录
				.call()
				.content();
	}

	public Flux<String> streamClassify(String fieldName, String chatId) {
		return chatClient.prompt()
				.user(fieldName)
				.advisors(a -> a.param(CONVERSATION_ID, chatId).param(TOP_K, 100)) // 设置advisor参数， 记忆使用chatId， 拉取最近的100条记录
				.stream()
				.content();
	}

}
