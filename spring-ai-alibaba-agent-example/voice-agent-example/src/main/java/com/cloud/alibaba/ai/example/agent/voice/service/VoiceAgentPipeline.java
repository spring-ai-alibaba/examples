/*
 * Copyright 2026-2027 the original author or authors.
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
package com.cloud.alibaba.ai.example.agent.voice.service;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioSpeechApi;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import com.cloud.alibaba.ai.example.agent.voice.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


import java.util.concurrent.atomic.AtomicReference;

/**
 * Voice Agent Pipeline: STT -> Agent -> TTS
 *
 * @author buvidk
 * @since 2026-02-12
 */
@Component
public class VoiceAgentPipeline {

    private static final Logger log = LoggerFactory.getLogger(VoiceAgentPipeline.class);

    private final VoiceAgentService agentService;
    private final TextToSpeechModel ttsModel;
    private final AudioTranscriptionModel sttModel;

    public VoiceAgentPipeline(
            AudioTranscriptionModel sttModel,
            VoiceAgentService agentService,
            TextToSpeechModel ttsModel
            ) {
        this.sttModel = sttModel;
        this.agentService = agentService;
        this.ttsModel = ttsModel;
        log.info("VoiceAgentPipeline initialized");
    }

    /**
     * Stream processing pipeline: Audio -> STT -> Agent -> TTS
     */
    public Flux<VoiceAgentEvent> processStream(byte[] audioInput, String threadId) {
        log.info("Starting streaming pipeline for thread: {}", threadId);

        AtomicReference<String> sttBuffer = new AtomicReference<>("");
        AtomicReference<StringBuilder> agentBuffer = new AtomicReference<>(new StringBuilder());

        return transcribe(audioInput)
            .flatMap(response -> {
                String chunk = response.getResult().getOutput();
                sttBuffer.set(chunk);  // 替换，不追加：每�?chunk 已包含完整文�?
                return Flux.just((VoiceAgentEvent) STTChunkEvent.create(chunk));
            })
            .concatWith(Flux.defer(() -> {
                String fullStt = sttBuffer.get();
                if (fullStt.isBlank()) {
                    return Flux.empty();
                }
                
                STTOutputEvent sttOutput = STTOutputEvent.create(fullStt);
                log.info("STT completed, calling agent: {}", fullStt);
                
                return Flux.concat(
                    Flux.just((VoiceAgentEvent) sttOutput),
                    agentService.chat(threadId, fullStt)
                        .doOnNext(agentEvent -> {
                            if (agentEvent instanceof AgentChunkEvent chunk) {
                                agentBuffer.get().append(chunk.text());
                            }
                        })
                );
            }))
            .flatMap(event -> {
                if (event instanceof AgentEndEvent) {
                    String fullAgentText = agentBuffer.get().toString();
                    agentBuffer.set(new StringBuilder()); // Reset buffer if needed
                    
                    if (!fullAgentText.isBlank()) {
                        log.info("Agent completed, calling TTS: {}", fullAgentText);
                        
                        return Flux.concat(
                            Flux.just(event),
                            synthesize(fullAgentText)
                                .map(response -> {
                                    byte[] audio = response.getResult().getOutput();
                                    if (audio != null && audio.length > 0) {
                                        return (VoiceAgentEvent) TTSChunkEvent.create(audio);
                                    }
                                    return (VoiceAgentEvent) null; // Filtered out later
                                })
                                .filter(e -> e != null)
                        );
                    }
                }
                return Flux.just(event);
            })
            .doOnComplete(() -> log.info("Pipeline completed for thread: {}", threadId))
            .doOnError(e -> log.error("Pipeline error for thread: {}", threadId, e));
    }

    private Flux<AudioTranscriptionResponse> transcribe(byte[] audioInput) {
        ByteArrayResource resource = new ByteArrayResource(audioInput);
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(resource,
                DashScopeAudioTranscriptionOptions.builder()
                        .model("paraformer-realtime-v2")
                        .format(DashScopeAudioTranscriptionApi.AudioFormat.PCM)
                        .sampleRate(16000)
                        .build());

        return sttModel.stream(prompt).subscribeOn(Schedulers.boundedElastic());
    }

    private Flux<TextToSpeechResponse> synthesize(String text) {
        if (text == null || text.isBlank()) {
            log.warn("TTS: empty text, skipping");
            return Flux.empty();
        }

        log.info("TTS synthesizing: {}", text);

        return ttsModel.stream(new TextToSpeechPrompt(text, DashScopeAudioSpeechOptions.builder()
                        .responseFormat(DashScopeAudioSpeechApi.ResponseFormat.PCM)
                        .sampleRate(16000)
                        .build()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
