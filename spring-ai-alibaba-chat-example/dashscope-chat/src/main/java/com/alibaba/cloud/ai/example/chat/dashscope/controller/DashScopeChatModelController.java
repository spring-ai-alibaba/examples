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
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
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
		ChatResponse call = dashScopeChatModel.call(new Prompt(DEFAULT_PROMPT, DashScopeChatOptions
				.builder()
				.model(DashScopeModel.ChatModel.QWEN_PLUS.getValue())
				.build()));
		System.out.println(call.getMetadata());
		return call.getResult().getOutput().getText();
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
				.model(DashScopeModel.ChatModel.QWEN_PLUS.getValue())
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
				.model(DashScopeModel.ChatModel.QWEN_PLUS.getValue())
				.build()));

		Map<String, Object> res = new HashMap<>();
		res.put("output", chatResponse.getResult().getOutput().getText());
		res.put("output_token", chatResponse.getMetadata().getUsage().getCompletionTokens());
		res.put("input_token", chatResponse.getMetadata().getUsage().getPromptTokens());
		res.put("total_token", chatResponse.getMetadata().getUsage().getTotalTokens());

		return res;
	}

    /**
     * $ curl http://localhost:10000/model/search/info/streams
     * 近期量子物理领域取得了多项重要研究进展。2026年初，以色列魏茨曼研究所首次在实验中观测到能通过交换顺序“记住”量子态的非阿贝尔任意子，这一发现为拓扑量子计算机提供了物理载体，其信息存储受拓扑保护，抗环境干扰能力显著优于传统量子比特[4]。与此同时，中国科 学技术大学潘建伟院士团队在《自然》杂志发表的研究指出，中国量子计算系统的相干时间已突破5分钟，相较于2020年提升了60倍，这主要得益于对量子环境的精密控制技术而非单纯依赖人的专注力[1]。
     *
     * 此外，北京计算科学研究中心的薛鹏教授团队在非厄米系统中捕捉到了两种截然不同的动力学量子相变，挑战了传统上追求封闭系统的观念，提出应在开放和损耗中寻找新的秩序，并强调了使用“双正交”基底来观察非厄米系统内部复杂动态的重要性[6]。波兰科学院团队则从量子力 学基本法则层面揭示了极端碰撞中量子信息的守恒本质，在大型强子对撞机的数据分析中发现了高能碰撞下信息完美守恒的现象，支持了量子力学幺正性原理[4]。
     *
     * 在中国，王亚愚团队成功改善了器件质量与可重复性，在7层MnBi2Te4器件中获得了零场量子化霍尔电阻平台，揭示了二维反铁磁体系特有的多种自旋构型对拓扑输运的调制作用[2]。同时，中国科学院高能物理研究所岩斌副研究员团队利用Belle实验数据首次验证了轻味夸克之间可 能存在的量子纠缠现象，并在6.2的高置信度水平上确认了量子非局域性[5]。这些成果共同推动着量子科技的发展，预示着未来在量子计算、通信以及材料科学等领域的广泛应用潜力[3]。%
     */
	@GetMapping("/search/info/streams")
	public Flux<String> searchInfoStreams(HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");
		
		var searchOptions = DashScopeApiSpec.SearchOptions.builder()
				.forcedSearch(true)
				.enableSource(true)
				.searchStrategy("pro")
				.enableCitation(true)
				.citationFormat("[<number>]")
				.build();
		
		var options = DashScopeChatOptions.builder()
				.enableSearch(true)
				.model(DashScopeModel.ChatModel.QWEN_PLUS.getValue())
				.searchOptions(searchOptions)
				.temperature(0.7)
				.build();
		
		String prompt = "hi, 搜索下关于量子物理的最新研究进展";
		
		Flux<ChatResponse> stream = dashScopeChatModel.stream(new Prompt(prompt, options));
		
		return stream.map(resp -> {
			String text = resp.getResult().getOutput().getText();
			// 打印调试信息到控制台
			System.out.println("Response: " + text);
			
			// 打印使用量信息
            //if (resp.getMetadata() != null && resp.getMetadata().getUsage() != null) {
            //    System.out.println("Usage - Completion: " + resp.getMetadata().getUsage().getCompletionTokens());
            //    System.out.println("Usage - Prompt: " + resp.getMetadata().getUsage().getPromptTokens());
            //    System.out.println("Usage - Total: " + resp.getMetadata().getUsage().getTotalTokens());
            //}

			if (resp.getResult().getOutput().getMetadata() != null) {
				Object searchInfo = resp.getResult().getOutput().getMetadata().get("search_info");
				if (searchInfo != null) {
					System.out.println("Search info: " + searchInfo);
				}
			}
			
			return text;
		});
	}

	/**
	 * 使用编程方式自定义 LLMs ChatOptions 参数， {@link com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions}
	 * 优先级高于在 application.yml 中配置的 LLMs 参数！
	 */
	@GetMapping("/custom/chat")
	public String customChat() {

		DashScopeChatOptions customOptions = DashScopeChatOptions.builder()
				.topP(0.7)
				.topK(50)
				.temperature(0.8)
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

		var searchOptions = DashScopeApiSpec.SearchOptions.builder()
				.forcedSearch(true)
				.enableSource(true)
				.searchStrategy("pro")
				.enableCitation(true)
				.citationFormat("[<number>]")
				.build();

		var options = DashScopeChatOptions.builder()
				.enableSearch(true)
				.model(DashScopeModel.ChatModel.DEEPSEEK_V3.getValue())
				.searchOptions(searchOptions)
				.temperature(0.7)
				.build();

		return dashScopeChatModel.stream(new Prompt(prompt, options)).map(resp -> resp.getResult().getOutput().getText());
	}

	// search_info stream demo，将以下代码放在 main 中执行
	// public static void main(String[] args) {
	//
	//    DashScopeChatModel.builder()
	//            .dashScopeApi(DashScopeApi.builder()
	//                    .apiKey("sk-xxx")
	//                    .build()
	//            ).defaultOptions(
	//                    DashScopeChatOptions.builder()
	//                            .model("qwen-plus")
	//                            .enableSearch(true)
	//                            .searchOptions(DashScopeApiSpec.SearchOptions.builder()
	//                                    .enableSource(true)
	//                                    .forcedSearch(true)
	//                                    .searchStrategy("turbo")
	//                                    .build()
	//                            ).build()
	//            ).build().stream(new Prompt("委内瑞拉总统新闻")).log()
	//            .subscribe(
	//            res -> {
	//                System.out.println(res.getResult().getOutput().getText());
	//                System.out.println("search_info -> " + res.getResult().getOutput().getMetadata().get("search_info"));
	//            },
	//            err -> System.out.println("err ->" + err),
	//            () -> System.out.println("done")
	//    );
	//
	//    try {
	//        Thread.sleep(10000);
	//    } catch (InterruptedException e) {
	//        Thread.currentThread().interrupt();
	//    }
	//
	//}

	@GetMapping("/dashscope/web-search/2")
	public Map<String, Object> dashScopeWebSearch2(HttpServletResponse response) {

		String prompt = "搜索下关于 Spring AI 的介绍";
		response.setCharacterEncoding("UTF-8");

		var searchOptions = DashScopeApiSpec.SearchOptions.builder()
				.forcedSearch(true)
				.enableSource(true)
				.searchStrategy("pro")
				.enableCitation(true)
				.citationFormat("[<number>]")
				.build();

		var options = DashScopeChatOptions.builder()
				.enableSearch(true)
				.model(DashScopeModel.ChatModel.DEEPSEEK_V3.getValue())
				.searchOptions(searchOptions)
				.temperature(0.7)
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
				.model(DashScopeModel.ChatModel.DEEPSEEK_V3.getValue())
				.temperature(0.7)
				.httpHeaders(headers)
				.build();

		return dashScopeChatModel.stream(new Prompt(prompt, options)).map(resp -> resp.getResult().getOutput().getText());

	}

}
