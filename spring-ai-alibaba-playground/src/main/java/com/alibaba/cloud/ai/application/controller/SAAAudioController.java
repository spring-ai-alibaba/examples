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

package com.alibaba.cloud.ai.application.controller;

import com.alibaba.cloud.ai.application.entity.result.Result;
import com.alibaba.cloud.ai.application.service.SAAAudioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 浏览器的语音 API 不好交互，此功能仅作为演示，切勿直接用于生产环境。
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@Tag(name = "Audio APIs")
@RequestMapping("/api/v1/")
public class SAAAudioController {

	private final SAAAudioService audioService;

	public SAAAudioController(SAAAudioService audioService) {
		this.audioService = audioService;
	}

	/**
	 * used to convert audio to text output
	 */
	@PostMapping("/audio2text")
	@Operation(summary = "DashScope Audio Transcription")
	public Result<String> audioToText(
			@Validated @RequestParam("audio") MultipartFile audio
	) throws IOException {

		if (audio.isEmpty()) {
			return Result.failed("No audio file provided");
		}

		return Result.success(audioService.audio2text(audio));
	}

	/**
	 * used to convert text into speech output
	 */
	@GetMapping("/text2audio")
	@Operation(summary = "DashScope Speech Synthesis")
	public Result<byte[]> textToAudio(
			@Validated @RequestParam("prompt") String prompt
	) {

		if (prompt.isEmpty()) {
			return Result.failed("No audio text provided");
		}

		return Result.success(audioService.text2audio(prompt));
	}

}
