/*
 * Copyright 2024-2026 the original author or authors.
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

package com.alibaba.cloud.ai.application.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 *
 * 全局统一管理 ChatMemory Bean 和 SimpleLoggerAdvisor
 */

@Configuration
public class AppConfiguration {

	private static final String AI_DASHSCOPE_API_KEY_PREFIX = "AI_DASHSCOPE_API_KEY";

	@Bean
	public SimpleLoggerAdvisor simpleLoggerAdvisor() {

		return new SimpleLoggerAdvisor(100);
	}

	@Bean
	public MessageChatMemoryAdvisor messageChatMemoryAdvisor(
			ChatMemory chatMemory
	) {
		return MessageChatMemoryAdvisor.builder(chatMemory).build();
	}

	@Bean
	public ToolCallingManager toolCallingManager() {

		return ToolCallingManager.builder().build();
	}

	/**
	 * For bailian call use.
	 */
	@Bean
	public DashScopeApi dashScopeApi() {

		return DashScopeApi.builder()
				.apiKey(System.getenv(AI_DASHSCOPE_API_KEY_PREFIX))
				.build();
	}

}
