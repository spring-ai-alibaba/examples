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
package com.cloud.alibaba.ai.example.agent.voice.component.tts;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.tts.SpeechSynthesizer;
import com.alibaba.dashscope.common.ResultCallback;
import com.cloud.alibaba.ai.example.agent.voice.event.TTSChunkEvent;
import com.cloud.alibaba.ai.example.agent.voice.event.VoiceAgentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * DashScope Streaming Text-to-Speech Service
 *
 * @author buvidk
 * @since 2026-02-03
 */
@Service
public class DashScopeRealtimeTTS {
    
    private static final Logger log = LoggerFactory.getLogger(DashScopeRealtimeTTS.class);
    
    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;
    
    public DashScopeRealtimeTTS() {
        log.info("DashScopeRealtimeTTS initialized");
    }
    
    /**
     * Streaming text-to-speech synthesis
     */
    public Flux<VoiceAgentEvent> synthesize(String text) {
        if (text == null || text.isBlank()) {
            log.warn("TTS: empty text, skipping");
            return Flux.empty();
        }
        
        log.info("TTS synthesizing: {}", text);
        
        return Flux.create(emitter -> {
            SpeechSynthesizer synthesizer = new SpeechSynthesizer();
            
            SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                .apiKey(apiKey)
                .model("sambert-zhiting-v1")
                .format(SpeechSynthesisAudioFormat.PCM)
                .sampleRate(16000)
                .text(text)
                .build();
            
            try {
                synthesizer.call(param, new ResultCallback<SpeechSynthesisResult>() {
                    @Override
                    public void onEvent(SpeechSynthesisResult result) {
                        if (result.getAudioFrame() != null) {
                            byte[] audio = result.getAudioFrame().array();
                            if (audio != null && audio.length > 0) {
                                emitter.next(TTSChunkEvent.create(audio));
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        log.info("TTS completed for text: {}...", text.substring(0, Math.min(text.length(), 10)));
                        emitter.complete();
                    }

                    @Override
                    public void onError(Exception e) {
                        log.error("TTS error", e);
                        emitter.error(e);
                    }
                });
            } catch (Exception e) {
                log.error("TTS call failed", e);
                emitter.error(e);
            }
        });
    }
}
