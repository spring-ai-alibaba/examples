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
package com.cloud.alibaba.ai.example.agent.voice.service;

import com.cloud.alibaba.ai.example.agent.voice.component.stt.DashScopeRealtimeSTT;
import com.cloud.alibaba.ai.example.agent.voice.component.tts.DashScopeRealtimeTTS;
import com.cloud.alibaba.ai.example.agent.voice.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Voice Agent Pipeline: STT -> Agent -> TTS
 *
 * @author buvidk
 * @since 2026-02-03
 */
@Component
public class VoiceAgentPipeline {

    private static final Logger log = LoggerFactory.getLogger(VoiceAgentPipeline.class);

    private final DashScopeRealtimeSTT realtimeSTT;
    private final VoiceAgentService agentService;
    private final DashScopeRealtimeTTS realtimeTTS;

    public VoiceAgentPipeline(
            DashScopeRealtimeSTT realtimeSTT,
            VoiceAgentService agentService,
            DashScopeRealtimeTTS realtimeTTS) {
        this.realtimeSTT = realtimeSTT;
        this.agentService = agentService;
        this.realtimeTTS = realtimeTTS;
        log.info("VoiceAgentPipeline initialized");
    }

    /**
     * Stream processing pipeline: Audio -> STT -> Agent -> TTS
     */
    public Flux<VoiceAgentEvent> processStream(Flux<ByteBuffer> audioInput, String threadId) {
        log.info("Starting streaming pipeline for thread: {}", threadId);
        
        AtomicReference<StringBuilder> agentTextBuffer = new AtomicReference<>(new StringBuilder());
        
        return realtimeSTT.transcribe(audioInput)
            .flatMap(event -> {
                if (event instanceof STTOutputEvent sttOutput) {
                    log.info("STT completed, calling agent: {}", sttOutput.transcript());
                    return Flux.concat(
                        Flux.just(event),
                        agentService.chat(threadId, sttOutput.transcript())
                            .doOnNext(agentEvent -> {
                                if (agentEvent instanceof AgentChunkEvent chunk) {
                                    agentTextBuffer.get().append(chunk.text());
                                }
                            })
                    );
                } else {
                    return Flux.just(event);
                }
            })
            .flatMap(event -> {
                if (event instanceof AgentEndEvent) {
                    String agentText = agentTextBuffer.get().toString();
                    agentTextBuffer.set(new StringBuilder());
                    
                    if (!agentText.isBlank()) {
                        log.info("Agent completed, calling TTS: {}", agentText);
                        return Flux.concat(
                            Flux.just(event),
                            realtimeTTS.synthesize(agentText)
                        );
                    }
                }
                return Flux.just(event);
            })
            .doOnComplete(() -> log.info("Pipeline completed for thread: {}", threadId))
            .doOnError(e -> log.error("Pipeline error for thread: {}", threadId, e));
    }
    
    /**
     * Text input pipeline: Text -> Agent -> TTS
     */
    public Flux<VoiceAgentEvent> processTextStream(String userMessage, String threadId) {
        log.info("Processing text stream for thread {}: {}", threadId, userMessage);
        
        AtomicReference<StringBuilder> agentTextBuffer = new AtomicReference<>(new StringBuilder());
        
        return agentService.chat(threadId, userMessage)
            .doOnNext(event -> {
                if (event instanceof AgentChunkEvent chunk) {
                    agentTextBuffer.get().append(chunk.text());
                }
            })
            .flatMap(event -> {
                if (event instanceof AgentEndEvent) {
                    String agentText = agentTextBuffer.get().toString();
                    if (!agentText.isBlank()) {
                        return Flux.concat(
                            Flux.just(event),
                            realtimeTTS.synthesize(agentText)
                        );
                    }
                }
                return Flux.just(event);
            });
    }
}
