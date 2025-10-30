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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import com.alibaba.cloud.ai.application.exception.SAAAIException;
import com.alibaba.cloud.ai.application.exception.SAAAppException;
import com.alibaba.cloud.ai.application.utils.FilesUtils;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import reactor.core.publisher.Flux;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Service
public class SAAAudioService {

	private final AudioTranscriptionModel transcriptionModel;

	private final SpeechSynthesisModel speechSynthesisModel;

	private final String DEFAULT_MODEL = "paraformer-realtime-v2";

	private static final String DEFAULT_MODEL_1 = "sensevoice-v1";

	public SAAAudioService(
			AudioTranscriptionModel transcriptionModel,
			SpeechSynthesisModel speechSynthesisModel
	) {

		this.transcriptionModel = transcriptionModel;
		this.speechSynthesisModel = speechSynthesisModel;
	}

	/**
	 * Convert text to speech
	 */
	public byte[] text2audio(String prompt) {

		Flux<SpeechSynthesisResponse> response = speechSynthesisModel.stream(
				new SpeechSynthesisPrompt(prompt)
		);

		CountDownLatch latch = new CountDownLatch(1);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			response.doFinally(
					signal -> latch.countDown()
			).subscribe(synthesisResponse -> {

				ByteBuffer byteBuffer = synthesisResponse.getResult().getOutput().getAudio();
				byte[] bytes = new byte[byteBuffer.remaining()];
				byteBuffer.get(bytes);

				try {
					outputStream.write(bytes);
				}
				catch (IOException e) {
					throw new SAAAIException("Error writing to output stream " + e.getMessage());
				}
			});

			latch.await();
		}
		catch (InterruptedException e) {
			throw new SAAAppException("Operation interrupted. " + e.getMessage());
		}

		return outputStream.toByteArray();
	}

	/**
	 * Convert speech to text
	 * Emm~, has error.
	 */
	public String audio2text(MultipartFile file) throws IOException {

		String filePath = FilesUtils.saveTempFile(file, "/tmp/audio/");
		System.out.println(filePath);
		return transcriptionModel.call(
				new AudioTranscriptionPrompt(
						new FileUrlResource(filePath),
						DashScopeAudioTranscriptionOptions.builder()
								.withModel(DEFAULT_MODEL_1)
								.build()
				)
		).getResult().getOutput();
	}

}
