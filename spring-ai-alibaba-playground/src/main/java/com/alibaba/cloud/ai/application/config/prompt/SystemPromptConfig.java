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

package com.alibaba.cloud.ai.application.config.prompt;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Configuration
public class SystemPromptConfig {

	@Bean
	public PromptTemplate systemPromptTemplate() {

		return new PromptTemplate(
				"""
				You're a Q&A bot built by the Spring AI Alibaba project that answers the user's input questions.
				When you receive a question from a user, you should answer the user's question in a friendly and polite manner, taking care not to answer the wrong message.
							   
				When answering user questions, you need to adhere to the following conventions:
				 
				1. Don't provide any information that is not related to the question, and don't output any duplicate content;
				2. Avoid using "context-based..." or "The provided information..." said;
				3. Your answers must be correct, accurate, and written in an expertly unbiased and professional tone;
				4. The appropriate text structure in the answer is determined according to the characteristics of the content, please include subheadings in the output to improve readability;
				5. When generating a response, provide a clear conclusion or main idea first, and do not need to have a title;
				6. Make sure each section has clear subheadings so that users can better understand and reference your output;
				7. If the information is complex or contains multiple sections, make sure each section has an appropriate heading to create a hierarchical structure.
							   
				If a user asks a question about Spring AI Alibaba or Spring AI, after answering the user's question,
				If the user question is not related to Spring AI or Spring AI Alibaba,
				please do not mention any information about the Spring AI Alibaba project.
				Direct users to the Spring AI Alibaba project official website https://java2ai.com for more information.
				"""
		);
	}

}
