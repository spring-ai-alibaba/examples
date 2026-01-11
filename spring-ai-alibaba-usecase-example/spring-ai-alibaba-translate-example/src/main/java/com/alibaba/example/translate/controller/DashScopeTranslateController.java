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

package com.alibaba.example.translate.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.example.translate.controller.service.MarkdownTranslationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DashScope翻译服务控制器
 * 提供基于DashScope大模型的文本翻译API
 */
@RestController
@RequestMapping("/api/dashscope/translate")
public class DashScopeTranslateController {

	private static final String TRANSLATION_PROMPT_TEMPLATE = "请将以下文本从%s翻译成%s：\n\n%s";
	private final MarkdownTranslationService markdownTranslationService;

	private final ChatModel dashScopeChatModel;

	public DashScopeTranslateController(@Qualifier("dashScopeChatModel") ChatModel chatModel,
										MarkdownTranslationService markdownTranslationService) {
		this.dashScopeChatModel = chatModel;
		this.markdownTranslationService = markdownTranslationService;
	}

	/**
	 * 基础翻译服务
	 * @param text 需要翻译的文本
	 * @param sourceLanguage 源语言
	 * @param targetLanguage 目标语言
	 * @return 翻译后的文本
	 */
	@GetMapping("/simple")
	public TranslateResponse simpleTranslation(
			@RequestParam String text,
			@RequestParam(defaultValue = "中文") String sourceLanguage,
			@RequestParam(defaultValue = "英文") String targetLanguage) {

		String prompt = String.format(TRANSLATION_PROMPT_TEMPLATE, sourceLanguage, targetLanguage, text);
		
		String translatedText = dashScopeChatModel.call(new Prompt(prompt, DashScopeChatOptions
				.builder()
				.withModel(DashScopeModel.ChatModel.QWEN_PLUS.getValue())
				.build())).getResult().getOutput().getText();
		
		return new TranslateResponse(translatedText);
	}

	/**
	 * 流式翻译服务
	 * @param text 需要翻译的文本
	 * @param sourceLanguage 源语言
	 * @param targetLanguage 目标语言
	 * @return 翻译后的文本流
	 */
	@GetMapping("/stream")
	public Flux<String> streamTranslation(
			HttpServletResponse response,
			@RequestParam String text,
			@RequestParam(defaultValue = "中文") String sourceLanguage,
			@RequestParam(defaultValue = "英文") String targetLanguage) {

		// 避免返回乱码
		response.setCharacterEncoding("UTF-8");

		String prompt = String.format(TRANSLATION_PROMPT_TEMPLATE, sourceLanguage, targetLanguage, text);
		
		Flux<ChatResponse> stream = dashScopeChatModel.stream(new Prompt(prompt, DashScopeChatOptions
				.builder()
				.withModel(DashScopeModel.ChatModel.QWEN_PLUS.getValue())
				.build()));
		return stream.map(resp -> resp.getResult().getOutput().getText());
	}

	/**
	 * 使用自定义配置的翻译服务
	 * @param text 需要翻译的文本
	 * @param sourceLanguage 源语言
	 * @param targetLanguage 目标语言
	 * @return 翻译后的文本
	 */
	@GetMapping("/custom")
	public TranslateResponse customTranslation(
			@RequestParam String text,
			@RequestParam(defaultValue = "中文") String sourceLanguage,
			@RequestParam(defaultValue = "英文") String targetLanguage) {

		String prompt = String.format(TRANSLATION_PROMPT_TEMPLATE, sourceLanguage, targetLanguage, text);
		
		DashScopeChatOptions customOptions = DashScopeChatOptions.builder()
				.withModel(DashScopeModel.ChatModel.QWEN_PLUS.getValue())
				.withTopP(0.7)
				.withTopK(50)
				.withTemperature(0.5) 
				.build();

		String translatedText = dashScopeChatModel.call(new Prompt(prompt, customOptions))
                .getResult().getOutput().getText();
        
        return new TranslateResponse(translatedText);
	}

	/**
	 * Markdown文件翻译服务
	 * @param file 需要翻译的md文件
	 * @param sourceLanguage 源语言
	 * @param targetLanguage 目标语言
	 * @return 翻译后的md文件
     */
	@PostMapping("/markdown-file")
	public TranslateResponse translateMarkdownFile(
			@RequestParam("file") MultipartFile file,
			@RequestParam(defaultValue = "英文") String sourceLanguage,
			@RequestParam(defaultValue = "中文") String targetLanguage) throws IOException {

		Path tempFile = Files.createTempFile("markdown_", ".md");
		file.transferTo(tempFile);

		try {
			String translatedPath = markdownTranslationService.translateMarkdownFile(
					tempFile.toString(),
					sourceLanguage,
					targetLanguage
			);
			String translatedContent = Files.readString(Paths.get(translatedPath));
			System.out.println(translatedContent);
			return new TranslateResponse(
					"success"
			);
		} finally {
			Files.deleteIfExists(tempFile);
		}
	}

} 
