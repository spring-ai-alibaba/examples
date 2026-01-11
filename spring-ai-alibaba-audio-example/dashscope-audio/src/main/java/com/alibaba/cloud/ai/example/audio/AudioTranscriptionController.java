/*
 * Copyright 2024 the original author or authors.
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

package com.alibaba.cloud.ai.example.audio;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 语音转文本（语音合成）
 * <a href="https://help.aliyun.com/zh/model-studio/text-to-speech">语音合成</a>
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @see DashScopeAudioTranscriptionModel
 */

@RestController
@RequestMapping("/ai/transcription")
public class AudioTranscriptionController {

	private final AudioTranscriptionModel transcriptionModel;

	private static final Logger log = LoggerFactory.getLogger(AudioTranscriptionController.class);

	public static final String AUDIO_FILE_URL =
		"https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav";

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public AudioTranscriptionController(AudioTranscriptionModel transcriptionModel) {

		this.transcriptionModel = transcriptionModel;
	}

	/**
	 * 录音文件识别
	 */
	@GetMapping("/call")
	public String callSTT() {

		// 录音文件支持HTTP / HTTPS协议
		// 若录音文件存储在阿里云OSS，使用RESTful API方式支持使用以 oss://为前缀的临时 URL
		Resource resource = new DefaultResourceLoader().getResource(AUDIO_FILE_URL);

		AudioTranscriptionResponse response = transcriptionModel.call(
			new AudioTranscriptionPrompt(
				resource,
				DashScopeAudioTranscriptionOptions.builder()
					.model(DashScopeModel.AudioModel.PARAFORMER_V2.getValue())
					.build()
			)
		);

		return response.getResult().getOutput();
	}

	/**
	 * 实时语音识别
	 */
	@GetMapping("/stream")
	public String streamSTT() {

		Resource resource = new DefaultResourceLoader().getResource(AUDIO_FILE_URL);

		Flux<AudioTranscriptionResponse> response = transcriptionModel
			.stream(
				new AudioTranscriptionPrompt(
					resource,
					DashScopeAudioTranscriptionOptions.builder()
						.model(DashScopeModel.AudioModel.GUMMY_REALTIME_V1.getValue())
						.sampleRate(16000)
						.format(DashScopeAudioTranscriptionApi.AudioFormat.WAV)
						.disfluencyRemovalEnabled(false)
						.build()
				)
			);

		return response.map(AudioTranscriptionResponse::getResult)
			.map(AudioTranscription::getOutput)
			.collect(Collectors.joining())
			.block();
	}

}
