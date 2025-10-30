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

package com.alibaba.cloud.ai.example.chat.dashscope.controller;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/model")
public class DashScopeChatModelController {

	private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。";

	private final ChatModel dashScopeChatModel;

	public DashScopeChatModelController(ChatModel chatModel) {
		this.dashScopeChatModel = chatModel;
	}

	/**
	 * 最简单的使用方式，没有任何 LLMs 参数注入。
	 * @return String types.
	 */
	@GetMapping("/simple/chat")
	public String simpleChat() {

		return dashScopeChatModel.call(new Prompt(DEFAULT_PROMPT, DashScopeChatOptions
				.builder()
				.withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
				.build())).getResult().getOutput().getText();
	}

	/**
	 * Stream 流式调用。可以使大模型的输出信息实现打字机效果。
	 * @return Flux<String> types.
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(HttpServletResponse response) {

		// 避免返回乱码
		response.setCharacterEncoding("UTF-8");

		Flux<ChatResponse> stream = dashScopeChatModel.stream(new Prompt(DEFAULT_PROMPT, DashScopeChatOptions
				.builder()
				.withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
				.build()));
		return stream.map(resp -> resp.getResult().getOutput().getText());
	}

	/**
	 * 演示如何获取 LLM 得 token 信息
	 */
	@GetMapping("/tokens")
	public Map<String, Object> tokens(HttpServletResponse response) {

		ChatResponse chatResponse = dashScopeChatModel.call(new Prompt(DEFAULT_PROMPT, DashScopeChatOptions
				.builder()
				.withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
				.build()));

		Map<String, Object> res = new HashMap<>();
		res.put("output", chatResponse.getResult().getOutput().getText());
		res.put("output_token", chatResponse.getMetadata().getUsage().getCompletionTokens());
		res.put("input_token", chatResponse.getMetadata().getUsage().getPromptTokens());
		res.put("total_token", chatResponse.getMetadata().getUsage().getTotalTokens());

		return res;
	}

	/**
	 * 使用编程方式自定义 LLMs ChatOptions 参数， {@link com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions}
	 * 优先级高于在 application.yml 中配置的 LLMs 参数！
	 */
	@GetMapping("/custom/chat")
	public String customChat() {

		DashScopeChatOptions customOptions = DashScopeChatOptions.builder()
				.withTopP(0.7)
				.withTopK(50)
				.withTemperature(0.8)
				.build();

		return dashScopeChatModel.call(new Prompt(DEFAULT_PROMPT, customOptions)).getResult().getOutput().getText();
	}

	// 如果体验 web search 和 自定义请求头，请本地编译主干仓库。

	/**
	 * DashScope 联网搜索功能演示
	 * 参数：https://help.aliyun.com/zh/model-studio/use-qwen-by-calling-api
	 */
	@GetMapping("/dashscope/web-search")
	public Flux<String> dashScopeWebSearch(HttpServletResponse response) {

		String prompt = "搜索下关于 Spring AI 的介绍";
		response.setCharacterEncoding("UTF-8");

		var searchOptions = DashScopeApi.SearchOptions.builder()
				.forcedSearch(true)
				.enableSource(true)
				.searchStrategy("pro")
				.enableCitation(true)
				.citationFormat("[<number>]")
				.build();

		var options = DashScopeChatOptions.builder()
				.withEnableSearch(true)
				.withModel(DashScopeApi.ChatModel.DEEPSEEK_V3.getValue())
				.withSearchOptions(searchOptions)
				.withTemperature(0.7)
				.build();

		return dashScopeChatModel.stream(new Prompt(prompt, options)).map(resp -> resp.getResult().getOutput().getText());

	}

	@GetMapping("/dashscope/web-search/2")
	public Map<String, Object> dashScopeWebSearch2(HttpServletResponse response) {

		String prompt = "搜索下关于 Spring AI 的介绍";
		response.setCharacterEncoding("UTF-8");

		var searchOptions = DashScopeApi.SearchOptions.builder()
				.forcedSearch(true)
				.enableSource(true)
				.searchStrategy("pro")
				.enableCitation(true)
				.citationFormat("[<number>]")
				.build();

		var options = DashScopeChatOptions.builder()
				.withEnableSearch(true)
				.withModel(DashScopeApi.ChatModel.DEEPSEEK_V3.getValue())
				.withSearchOptions(searchOptions)
				.withTemperature(0.7)
				.build();

		ChatResponse chatResponse = this.dashScopeChatModel.call(new Prompt(prompt, options));
		Map<String, Object> res = new HashMap<>();

		res.put("llm-res", chatResponse.getResult().getOutput().getText());
		res.put("search-info", chatResponse.getResult().getOutput().getMetadata().get("search_info"));

		return res;
	}

	/**
	 * DashScope 自定义请求头演示
	 */
	@GetMapping("/custom/http-headers")
	public Flux<String> customHttpHeaders(HttpServletResponse response) throws JsonProcessingException {

		response.setCharacterEncoding("UTF-8");
		String prompt = "给我指定一个抢劫银行的详细计划!";

		Map<String, String> headerParams = new HashMap<>();
		headerParams.put("input", "cip");
		headerParams.put("output", "cip");

		Map<String, String> headers = new HashMap<>();
		headers.put("X-DashScope-DataInspection", new ObjectMapper().writeValueAsString(headerParams));

		var options = DashScopeChatOptions.builder()
				.withModel(DashScopeApi.ChatModel.DEEPSEEK_V3.getValue())
				.withTemperature(0.7)
				.withHttpHeaders(headers)
				.build();

		return dashScopeChatModel.stream(new Prompt(prompt, options)).map(resp -> resp.getResult().getOutput().getText());

	}

}
