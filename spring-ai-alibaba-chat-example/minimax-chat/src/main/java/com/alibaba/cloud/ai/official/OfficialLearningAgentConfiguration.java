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

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Official Spring AI Alibaba Agent Framework configuration for the learning demo.
 */
@Configuration
public class OfficialLearningAgentConfiguration {

	@Bean
	public ReactAgent officialLearningAgent(ChatModel chatModel, OfficialLearningToolCallbacks toolCallbacks)
			throws GraphStateException {
		return ReactAgent.builder()
				.name("official-learning-agent")
				.description("""
						你是基于 Spring AI Alibaba Agent Framework 的 MiniMax 学习 Agent。
						请始终使用中文回答。
						你可以根据问题自主调用学习工具，回答 Spring AI Alibaba 的 Tool、Skill、Agent、Memory、RAG 和 Graph 学习问题。
						当问题涉及当前时间、学习建议、学习计划、概念解释或当前项目实现细节时，优先调用合适的工具。
						不要输出 <think>、</think> 或任何思考标签。
						""")
				.model(chatModel)
				.saver(new MemorySaver())
				.tools(toolCallbacks.all())
				.build();
	}

}
