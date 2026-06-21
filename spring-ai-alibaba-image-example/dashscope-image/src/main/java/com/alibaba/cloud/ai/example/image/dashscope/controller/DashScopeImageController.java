/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.example.image.dashscope.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ai.image.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/example")
public class DashScopeImageController {

	private final ImageModel imageModel;

	private static final String DEFAULT_PROMPT = "为人工智能生成一张富有科技感的图片！";

	public DashScopeImageController(ImageModel imageModel) {
		this.imageModel = imageModel;
	}

	@GetMapping("/image")
	public void image(HttpServletResponse response) {

		ImageResponse imageResponse = imageModel.call(new ImagePrompt(DEFAULT_PROMPT));
		String imageUrl = imageResponse.getResult().getOutput().getUrl();

		try {
			URL url = URI.create(imageUrl).toURL();
			InputStream in = url.openStream();

			response.setHeader("Content-Type", MediaType.IMAGE_PNG_VALUE);
			response.getOutputStream().write(in.readAllBytes());
			response.getOutputStream().flush();
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Generates multiple images from single prompt.
	 */
	@GetMapping("/image/multiPrompt")
	public ResponseEntity<Collection<String>> generateImageWithMultiPrompt(
			@RequestParam(value = "prompt", defaultValue = "一只会编程的猫") String prompt,
			@RequestParam(defaultValue = "2") int count) {

		ImageOptions options = ImageOptionsBuilder.builder()
				.n(count)
				.build();
		ImageResponse response = imageModel.call(new ImagePrompt(prompt, options));
		Set<String> imageSet = response.getResults().stream().map(result -> result.getOutput().getUrl()).collect(Collectors.toSet());
		return ResponseEntity.ok(imageSet);
	}

	/**
	 * multi condition and safe to generation image
	 */
	@GetMapping("/image/multipleConditions")
	public ResponseEntity<?> multipleConditions(
			@RequestParam(value = "subject", defaultValue = "一只会编程的猫") String subject,
			@RequestParam(value = "environment", defaultValue = "办公室") String environment,
			@RequestParam(value = "height", defaultValue = "1024") Integer height,
			@RequestParam(value = "width", defaultValue = "1024") Integer width,
			@RequestParam(value = "style", defaultValue = "生动") String style) {

		String prompt = String.format(
				"一个%s，置身于%s的环境中，使用%s的艺术风格，高清4K画质，细节精致",
				subject, environment, style
		);

		ImageOptions options = ImageOptionsBuilder.builder()
				.height(height)
				.width(width)
				.build();

		try {
			ImageResponse response = imageModel.call(new ImagePrompt(prompt, options));
			return ResponseEntity.ok(response.getResult().getOutput().getUrl());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of(
							"error", "图像生成失败",
							"message", e.getMessage(),
							"timestamp", LocalDateTime.now()
					));
		}
	}

}
