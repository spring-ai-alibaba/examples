/*
 * Copyright 2025-2026 the original author or authors.
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
package com.cloud.alibaba.ai.example.agent.voice.component.stt;

import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionResult;
import com.cloud.alibaba.ai.example.agent.voice.event.STTChunkEvent;
import com.cloud.alibaba.ai.example.agent.voice.event.STTOutputEvent;
import com.cloud.alibaba.ai.example.agent.voice.event.VoiceAgentEvent;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DashScope Realtime Speech-to-Text Service
 *
 * @author buvidk
 * @since 2026-02-03
 */
@Service
public class DashScopeRealtimeSTT {
    
    private static final Logger log = LoggerFactory.getLogger(DashScopeRealtimeSTT.class);
    
    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;
    
    /**
     * Realtime transcription
     * @param audioStream PCM 16-bit 16kHz mono audio stream
     * @return STT event stream
     */
    public Flux<VoiceAgentEvent> transcribe(Flux<ByteBuffer> audioStream) {
        return Flux.defer(() -> {
            log.info("Starting realtime STT transcription");
            
            try {
                Recognition recognizer = new Recognition();
                RecognitionParam param = RecognitionParam.builder()
                    .model("paraformer-realtime-v2")
                    .format("pcm")
                    .sampleRate(16000)
                    .apiKey(apiKey)
                    .build();
                
                Flowable<ByteBuffer> rxAudioStream = Flowable.fromPublisher(audioStream);
                
                AtomicReference<String> lastText = new AtomicReference<>("");
                AtomicReference<Boolean> lastIsSentenceEnd = new AtomicReference<>(true);
                
                Flowable<RecognitionResult> resultFlowable = recognizer.streamCall(param, rxAudioStream);
                
                return Flux.from(resultFlowable)
                    .flatMap(result -> {
                        String text = result.getSentence() != null 
                            ? result.getSentence().getText() 
                            : "";
                        
                        lastText.set(text);
                        lastIsSentenceEnd.set(result.isSentenceEnd());
                        
                        if (log.isDebugEnabled()) {
                            log.debug("STT update: end={}, text={}", result.isSentenceEnd(), text);
                        }

                        if (result.isSentenceEnd()) {
                            log.info("STT final: {}", text);
                            return Flux.just((VoiceAgentEvent) STTOutputEvent.create(text));
                        } else {
                            return Flux.just((VoiceAgentEvent) STTChunkEvent.create(text));
                        }
                    })
                    .concatWith(Flux.defer(() -> {
                        if (!lastIsSentenceEnd.get() && !lastText.get().isEmpty()) {
                            log.info("STT stream ended with partial text, forcing output: {}", lastText.get());
                            return Flux.just(STTOutputEvent.create(lastText.get()));
                        }
                        return Flux.empty();
                    }))
                    .doOnComplete(() -> log.info("STT transcription completed"))
                    .doOnError(e -> log.error("STT error", e));
                    
            } catch (Exception e) {
                log.error("STT setup error", e);
                return Flux.error(e);
            }
        });
    }
    
    /**
     * Single-shot transcription for complete audio files
     */
    public String transcribeOnce(byte[] audioBytes) {
        log.info("Transcribing audio: {} bytes", audioBytes.length);
        
        try {
            Recognition recognizer = new Recognition();
            RecognitionParam param = RecognitionParam.builder()
                .model("paraformer-realtime-v2")
                .format("pcm")
                .sampleRate(16000)
                .apiKey(apiKey)
                .build();
            
            Flowable<ByteBuffer> audioStream = Flowable.create(emitter -> {
                int chunkSize = 1024;
                try {
                    for (int i = 0; i < audioBytes.length; i += chunkSize) {
                        if (emitter.isCancelled()) break;
                        int end = Math.min(i + chunkSize, audioBytes.length);
                        ByteBuffer buffer = ByteBuffer.wrap(audioBytes, i, end - i);
                        emitter.onNext(buffer);
                        Thread.sleep(20);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }, BackpressureStrategy.BUFFER);
            
            StringBuilder result = new StringBuilder();
            recognizer.streamCall(param, audioStream)
                .blockingForEach(r -> {
                    if (r.isSentenceEnd() && r.getSentence() != null) {
                        result.append(r.getSentence().getText());
                    }
                });
            
            log.info("Transcription result: {}", result);
            return result.toString();
            
        } catch (Exception e) {
            log.error("Transcription error", e);
            throw new RuntimeException("STT failed", e);
        }
    }
}
