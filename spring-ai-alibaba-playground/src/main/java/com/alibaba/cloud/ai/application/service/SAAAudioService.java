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

package com.alibaba.cloud.ai.application.service;

import com.alibaba.cloud.ai.application.utils.FilesUtils;
import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Service
public class SAAAudioService {

	private final AudioTranscriptionModel transcriptionModel;

	private final TextToSpeechModel speechSynthesisModel;

	public SAAAudioService(AudioTranscriptionModel transcriptionModel,
		@Qualifier("dashScopeSpeechSynthesisModel") TextToSpeechModel speechSynthesisModel) {

		this.transcriptionModel = transcriptionModel;
		this.speechSynthesisModel = speechSynthesisModel;
	}

	/**
	 * Convert text to speech
	 */
	public byte[] text2audio(String prompt) {

		return speechSynthesisModel.call(prompt);
	}

	/**
	 * Convert speech to text
	 * Emm~, has error.
	 */
	public String audio2text(MultipartFile file) throws IOException {

		String filePath = FilesUtils.saveTempFile(file, "/tmp/audio/");
		return transcriptionModel.call(new FileUrlResource(filePath));
	}

}
