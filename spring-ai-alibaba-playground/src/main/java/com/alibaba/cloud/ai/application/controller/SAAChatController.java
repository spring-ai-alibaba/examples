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

package com.alibaba.cloud.ai.application.controller;

import com.alibaba.cloud.ai.application.service.SAABaseService;
import com.alibaba.cloud.ai.application.service.SAAChatService;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@Tag(name = "Chat APIs")
@RequestMapping("/api/v1")
public class SAAChatController {

	private final SAAChatService chatService;

	private final SAABaseService baseService;

	public SAAChatController(SAAChatService chatService, SAABaseService baseService) {
		this.chatService = chatService;
		this.baseService = baseService;
	}

	/**
	 * Send the specified parameters to get the model response.
	 * 1. When the send prompt is empty, an error message is returned.
	 * 2. When sending a model, it is allowed to be empty, and when the parameter has a value and
	 * 	  is in the model configuration list, the corresponding model is called. If there is no return error.
	 * 	  If the model parameter is empty, set the default model. qwen-plus
	 * 3. The chatId chat memory, passed by the front-end, is of type Object and cannot be repeated
	 */
	@PostMapping("/chat")
	@Operation(summary = "DashScope Flux Chat")
	public Flux<String> chat(
			HttpServletResponse response,
			@Validated @RequestBody String prompt,
			@RequestHeader(value = "model", required = false) String model,
			@RequestHeader(value = "chatId", required = false, defaultValue = "spring-ai-alibaba-playground-chat") String chatId
	) {

		Set<Map<String, String>> dashScope = baseService.getDashScope();
		List<String> modelName = dashScope.stream()
				.flatMap(map -> map.keySet().stream().map(map::get))
				.distinct()
				.toList();

		if (StringUtils.hasText(model)) {
			if (!modelName.contains(model)) {
				return Flux.just("Input model not support.");
			}
		}
		else {
			model = DashScopeModel.ChatModel.QWEN_PLUS.getValue();
		}

		response.setCharacterEncoding("UTF-8");
		return chatService.chat(chatId, model, prompt);
	}

	@PostMapping("/deep-thinking/chat")
	public Flux<String> deepThinkingChat(
			HttpServletResponse response,
			@Validated @RequestBody String prompt,
			@RequestHeader(value = "model", required = false) String model,
			@RequestHeader(value = "chatId", required = false, defaultValue = "spring-ai-alibaba-playground-deepthink-chat") String chatId
	) {

		Set<Map<String, String>> dashScope = baseService.getDashScope();
		List<String> modelName = dashScope.stream()
				.flatMap(map -> map.keySet().stream().map(map::get))
				.distinct()
				.toList();

		if (StringUtils.hasText(model)) {
			if (!modelName.contains(model)) {
				return Flux.just("Input model not support.");
			}
		}
		else {
			model = DashScopeModel.ChatModel.QWEN_PLUS.getValue();
		}

		response.setCharacterEncoding("UTF-8");
		return chatService.deepThinkingChat(chatId, model, prompt);
	}

}
