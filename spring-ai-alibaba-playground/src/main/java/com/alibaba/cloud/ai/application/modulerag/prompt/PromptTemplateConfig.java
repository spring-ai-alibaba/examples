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

package com.alibaba.cloud.ai.application.modulerag.prompt;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Prompt:
 * 1. https://zhuanlan.zhihu.com/p/23929522431
 * 2. https://cloud.tencent.com/developer/article/2509465
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Configuration
public class PromptTemplateConfig {

	@Bean
	public PromptTemplate transformerPromptTemplate() {

		return new PromptTemplate(
				"""
				Given a user query, rewrite the user question to provide better results when querying {target}.
								
				You should follow these rules:
								
				1. Remove any irrelevant information and make sure the query is concise and specific;
				2. The output must be consistent with the language of the user's query;
				3. Ensure better understanding and answers from the perspective of large models.
				
				Original query:
				{query}
				
				Query after rewrite:
				"""
		);
	}


	@Bean
	public PromptTemplate queryArgumentPromptTemplate() {

		return new PromptTemplate(
				"""
				You'll get a set of document contexts that are relevant to the issue.
				Each document begins with a reference number, such as [[x]], where x is a number that can be repeated.
				Documents that are not referenced will be marked as [[null]].
				Use context and refer to it at the end of each sentence, if applicable.
				The context information is as follows:
				
				---------------------
				{context}
				---------------------
				
				Generate structured responses to user questions given contextual information and without prior knowledge.
								
				When you answer user questions, follow these rules:
				
				1. If the answer is not in context, say you don't know;
				2. Don't provide any information that is not relevant to the question, and don't output any duplicate content;
				3. Avoid using "context-based..." or "The provided information..." said;
				4. Your answers must be correct, accurate, and written in an expertly unbiased and professional tone;
				5. The appropriate text structure in the answer is determined according to the characteristics of the content, please include subheadings in the output to improve readability;
				6. When generating a response, provide a clear conclusion or main idea first, without a title;
				7. Make sure each section has a clear subtitle so that users can better understand and refer to your output;
				8. If the information is complex or contains multiple sections, make sure each section has an appropriate heading to create a hierarchical structure;
				9. Please refer to the sentence or section with the reference number at the end in [[x]] format;
				10. If a sentence or section comes from more than one context, list all applicable references, e.g. [[x]][[y]];
				11. Your output answers must be in beautiful and rigorous markdown format.
				12. Because your output is in markdown format, please include the link in the reference document in the form of a hyperlink when referencing the context, so that users can click to view it;
				13. If a reference is marked as [[null]], it does not have to be cited;
				14. Except for Code. Aside from the specific name and citation, your answer must be written in the same language as the question.
				
				User Issue:
				
				{query}
								
				Your answer:
				"""
		);
	}
}
