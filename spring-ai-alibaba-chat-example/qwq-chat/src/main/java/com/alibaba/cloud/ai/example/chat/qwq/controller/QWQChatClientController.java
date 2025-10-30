/*
 * Copyright 2024-2025 the original author or authors.
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

package com.alibaba.cloud.ai.example.chat.qwq.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.example.chat.qwq.advisor.ReasoningContentAdvisor;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/client")
public class QWQChatClientController {

	private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

	private final ChatClient chatClient;

	private final ChatModel chatModel;

	public QWQChatClientController(ChatModel chatModel) {

		this.chatModel = chatModel;

		// 构造时，可以设置 ChatClient 的参数
		// {@link org.springframework.ai.chat.client.ChatClient};
		this.chatClient = ChatClient.builder(chatModel)
				// 实现 Chat Memory 的 Advisor
				// 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
				.defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build(),

						// 整合 QWQ 的思考过程到输出中
						new ReasoningContentAdvisor(0)
				)
				// 实现 Logger 的 Advisor
				.defaultAdvisors(
						new SimpleLoggerAdvisor()
				)
				// 设置 ChatClient 中 ChatModel 的 Options 参数
				.defaultOptions(
						DashScopeChatOptions.builder()
								.withTopP(0.7)
								// 设置enable_thinking 为 true，启用思考过程
								.withEnableThinking(true)
								.build()
				)
				.build();
	}



	/**
	 * QWQ 模型目前只支持 Stream 调用，如果使用非 stream 调用时会出现如下错误：
	 * 	400 - {"code":"InvalidParameter","message":"This model only support stream mode,
	 * 	please enable the stream parameter to access the model.
	 * QWQ 模型的其他限制
	 * 不支持功能：
	 * 		工具调用（Function Call）、
	 * 		结构化输出（JSON Mode）、
	 * 		前缀续写（Partial Mode）、
	 * 		上下文缓存（Context Cache）
	 * 不支持的参数：
	 * 		temperature、
	 * 		top_p、
	 * 		presence_penalty、
	 * 		frequency_penalty、
	 * 		logprobs、
	 * 		top_logprobs
	 * 	设置这些参数都不会生效，即使没有输出错误提示。
	 * System Message：
	 * 	为了达到模型的最佳推理效果，不建议设置 System Message。
	 */
	@GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> streamChat(HttpServletResponse response) {

		// 避免返回乱码
		response.setCharacterEncoding("UTF-8");

		// QWQ 模型其他限制，当使用 DashScope API 调用时：
		// 1. incremental_output 参数默认为 true，且不支持设置为 false，仅支持增量流式返回；
		// 2. response_format参数默认为"message"。
		return chatClient.prompt(DEFAULT_PROMPT)
				.stream()
				.content();
	}

}
