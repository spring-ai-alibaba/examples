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

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;

import java.net.URI;
import java.util.List;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/client")
public class DashScopeChatClientController {

	private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

	private final ChatClient dashScopeChatClient;

	public DashScopeChatClientController(ChatModel chatModel) {

		// 构造时，可以设置 ChatClient 的参数
		// {@link org.springframework.ai.chat.client.ChatClient};
		this.dashScopeChatClient = ChatClient.builder(chatModel)
				// 实现 Logger 的 Advisor
				.defaultAdvisors(
						new SimpleLoggerAdvisor()
				)
				// 设置 ChatClient 中 ChatModel 的 Options 参数
				.defaultOptions(
						DashScopeChatOptions.builder()
								.withTopP(0.7)
								.build()
				)
				.build();
	}

	// 也可以使用如下的方式注入 ChatClient
	// public DashScopeChatClientController(ChatClient.Builder chatClientBuilder) {
	//
	//  	this.dashScopeChatClient = chatClientBuilder.build();
	// }

	/**
	 * ChatClient 简单调用
	 */
	@GetMapping("/simple/chat")
	public String simpleChat() {

		return dashScopeChatClient.prompt(DEFAULT_PROMPT).call().content();
	}

	/**
	 * ChatClient 流式调用
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");
		return dashScopeChatClient.prompt(DEFAULT_PROMPT).stream().content();
	}


	/**
	 * 图片分析接口 - 通过 URL
	 */
	@GetMapping("/image/analyze/url")
	public String analyzeImageByUrl(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt,
									@RequestParam String imageUrl) {
		try {
			// 创建包含图片的用户消息
			List<Media> mediaList = List.of(new Media(MimeTypeUtils.IMAGE_JPEG, new URI(imageUrl)));
			UserMessage message = UserMessage.builder()
					.text(prompt)
					.media(mediaList)
					.build();

			// 设置消息格式为图片
			message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

			// 创建提示词，启用多模态模型
			Prompt chatPrompt = new Prompt(message,
					DashScopeChatOptions.builder()
							.withModel("qwen-vl-max-latest")  // 使用视觉模型
							.withMultiModel(true)             // 启用多模态
							.withVlHighResolutionImages(true) // 启用高分辨率图片处理
							.withTemperature(0.7)
							.build());
			// 调用模型进行图片分析
			return dashScopeChatClient.prompt(chatPrompt).call().content();
		} catch (Exception e) {
			return "图片分析失败: " + e.getMessage();
		}
	}

	/**
	 * 图片分析接口 - 通过文件上传
	 */
	@PostMapping("/image/analyze/upload")
	public String analyzeImageByUpload(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt,
									   @RequestParam("file") MultipartFile file) {
		try {
			// 验证文件类型
			if (!file.getContentType().startsWith("image/")) {
				return "请上传图片文件";
			}

			// 创建包含图片的用户消息
			Media media = new Media(MimeTypeUtils.parseMimeType(file.getContentType()), file.getResource());
			UserMessage message = UserMessage.builder()
					.text(prompt)
					.media(media)
					.build();

			// 设置消息格式为图片
			message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

			// 创建提示词，启用多模态模型
			Prompt chatPrompt = new Prompt(message,
					DashScopeChatOptions.builder()
							.withModel("qwen-vl-max-latest")  // 使用视觉模型
							.withMultiModel(true)             // 启用多模态
							.withVlHighResolutionImages(true) // 启用高分辨率图片处理
							.withTemperature(0.7)
							.build());

			// 调用模型进行图片分析
			return dashScopeChatClient.prompt(chatPrompt).call().content();

		} catch (Exception e) {
			return "图片分析失败: " + e.getMessage();
		}
	}

}
