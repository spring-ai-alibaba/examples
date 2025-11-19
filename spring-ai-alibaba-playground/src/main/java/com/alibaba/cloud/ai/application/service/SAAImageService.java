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

package com.alibaba.cloud.ai.application.service;

import com.alibaba.cloud.ai.application.utils.FilesUtils;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.MESSAGE_FORMAT;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Service
public class SAAImageService {

	private static final String DEFAULT_TEXT2IMAGE_MODEL = "qwen-vl-max-latest";

	private static final String DEFAULT_IMAGE_MODEL = "wanx2.1-t2i-turbo";

	/**
	 * Images generate text
	 */
	private final ImageModel imageModel;

	/**
	 * Multimodal support for parsing images
	 */
	private final ChatClient daschScopeChatClient;

	public SAAImageService(
			@Qualifier("dashScopeChatModel") ChatModel chatModel,
			@Qualifier("dashScopeImageModel") ImageModel imageModel
	) {

		this.imageModel = imageModel;
		this.daschScopeChatClient = ChatClient
				.builder(chatModel)
				.build();
	}

	public Flux<String> image2Text(String prompt, MultipartFile file) throws IOException {

		String filePath = FilesUtils.saveTempFile(file, "/tmp/image/");
//		UserMessage message = new UserMessage(
//				prompt,
//				new Media(
//						MimeTypeUtils.IMAGE_PNG,
//						new FileSystemResource(filePath)
//				)
//		);
		List<Media> mediaList = Lists.newArrayList(new Media(
				MimeTypeUtils.IMAGE_PNG,
				new FileSystemResource(filePath)
		));
		UserMessage message =
				UserMessage.builder().text(prompt).media(mediaList).metadata(new HashMap<>()).build();
		message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.IMAGE);

		List<ChatResponse> response = daschScopeChatClient.prompt(
						new Prompt(
								message,
								DashScopeChatOptions.builder()
										.withModel(DEFAULT_TEXT2IMAGE_MODEL)
										.withMultiModel(true)
										.build())
				).stream()
				.chatResponse()
				.collectList()
				.block();

		StringBuilder result = new StringBuilder();
		if (response != null) {
			for (ChatResponse chatResponse : response) {
				String outputContent = chatResponse.getResult().getOutput().getText();
				result.append(outputContent);
			}
		}

		return Flux.just(result.toString());
	}

	/**
	 * 可以基于此接口扩展更多参数，调用不同模型来实现
	 * 图像扩展，反向 Prompt，图像增强，抠图等功能。
	 * 此示例中不做演示。
	 * 文档参考：<a href="https://help.aliyun.com/zh/model-studio/developer-reference/text-to-image-v2-api-reference">...</a>
	 */
	public void text2Image(String prompt, String resolution, String style, HttpServletResponse response) {


		//
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Security-Policy", "img-src 'self' data:;");

		ImageGeneration result = imageModel.call(
				new ImagePrompt(
						prompt,
						DashScopeImageOptions.builder()
								.withHeight(Integer.valueOf(resolution.split("\\*")[0]))
								.withWidth(Integer.valueOf(resolution.split("\\*")[1]))
								.withStyle(style)
								.withModel(DEFAULT_IMAGE_MODEL)
								.build())
		).getResult();

		String imageUrl = result.getOutput().getUrl();

		try {
			URL url = URI.create(imageUrl).toURL();
			InputStream in = url.openStream();

			response.setHeader("Content-Security-Policy", "img-src 'self' data:;");
			response.setHeader("Content-Type", MediaType.IMAGE_PNG_VALUE);
			response.getOutputStream().write(in.readAllBytes());
			response.getOutputStream().flush();
		}
		catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
