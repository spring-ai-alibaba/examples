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

package com.alibaba.cloud.ai.example.chat.ollama.controller;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/model")
public class OllamaChatModelController {

	private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。请用中文回答。";

	private final ChatModel ollamaChatModel;

	public OllamaChatModelController(ChatModel chatModel) {
		this.ollamaChatModel = chatModel;
	}

	/**
	 * 最简单的使用方式，没有任何 LLMs 参数注入。
	 *
	 * @return String types.
	 */
	@GetMapping("/simple/chat")
	public String simpleChat() {

		return ollamaChatModel.call(new Prompt(DEFAULT_PROMPT)).getResult().getOutput().getText();
	}

	/**
	 * Stream 流式调用。可以使大模型的输出信息实现打字机效果。
	 *
	 * @return Flux<String> types.
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(HttpServletResponse response) {

		// 避免返回乱码
		response.setCharacterEncoding("UTF-8");

		Flux<ChatResponse> stream = ollamaChatModel.stream(new Prompt(DEFAULT_PROMPT));
		return stream.map(resp -> resp.getResult().getOutput().getText());
	}

	/**
	 * 使用编程方式自定义 LLMs ChatOptions 参数， {@link OllamaChatOptions}。
	 * 优先级高于在 application.yml 中配置的 LLMs 参数！
	 */
	@GetMapping("/custom/chat")
	public String customChat() {

		OllamaChatOptions customOptions = OllamaChatOptions.builder()
			.topP(0.7)
			.model("llama3")
			.temperature(0.8)
			.build();

		return ollamaChatModel.call(new Prompt(DEFAULT_PROMPT, customOptions)).getResult().getOutput().getText();
	}

}
