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
package com.cloud.alibaba.ai.example.agent.voice.controller;

import com.cloud.alibaba.ai.example.agent.voice.event.TTSChunkEvent;
import com.cloud.alibaba.ai.example.agent.voice.event.VoiceAgentEvent;
import com.cloud.alibaba.ai.example.agent.voice.service.VoiceAgentPipeline;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Voice WebSocket Handler
 * <p>
 * Supports two modes:
 * 1. Audio mode (BinaryMessage): Receive PCM audio -> Stream processing -> Return events and audio
 * 2. Text mode (TextMessage): Receive text -> Agent -> TTS -> Return events and audio
 * </p>
 *
 * @author buvidk
 * @since 2026-02-03
 */
@Component
public class VoiceWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(VoiceWebSocketHandler.class);
    
    private final VoiceAgentPipeline pipeline;
    private final ObjectMapper objectMapper;
    
    private final Map<String, Sinks.Many<ByteBuffer>> audioSinks = new ConcurrentHashMap<>();

    public VoiceWebSocketHandler(VoiceAgentPipeline pipeline, ObjectMapper objectMapper) {
        this.pipeline = pipeline;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connected: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            if (message instanceof BinaryMessage binaryMessage) {
                handleBinaryMessage(session, binaryMessage);
            } else if (message instanceof TextMessage textMessage) {
                handleTextMessage(session, textMessage);
            }
        } catch (Exception e) {
            log.error("Error handling message", e);
            sendErrorEvent(session, e.getMessage());
        }
    }
    
    private void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String sessionId = session.getId();
        ByteBuffer buffer = message.getPayload();
        byte[] audioBytes = new byte[buffer.remaining()];
        buffer.get(audioBytes);
        
        Sinks.Many<ByteBuffer> sink = audioSinks.get(sessionId);
        if (sink == null) {
            log.info("Starting new audio stream for session: {}", sessionId);
            sink = Sinks.many().unicast().onBackpressureBuffer();
            audioSinks.put(sessionId, sink);
            processAudioStream(session, sink.asFlux());
        }
        
        Sinks.EmitResult result = sink.tryEmitNext(ByteBuffer.wrap(audioBytes));
        if (result.isFailure()) {
            log.warn("Failed to emit audio data: {}", result);
            if (result == Sinks.EmitResult.FAIL_TERMINATED || result == Sinks.EmitResult.FAIL_CANCELLED) {
                audioSinks.remove(sessionId);
            }
        }
    }
    
    private void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String text = message.getPayload();
        
        if (text.startsWith("{")) {
            Map<String, Object> command = objectMapper.readValue(text, Map.class);
            String type = (String) command.get("type");
            
            if ("audio_end".equals(type)) {
                log.info("Audio stream ended for session: {}", session.getId());
                // Complete the sink to signal end of audio stream
                Sinks.Many<ByteBuffer> sink = audioSinks.get(session.getId());
                if (sink != null) {
                    sink.tryEmitComplete();
                    log.info("Audio sink completed for session: {}", session.getId());
                }
                return;
            } else if ("text".equals(type)) {
                text = (String) command.get("content");
            }
        }
        
        processTextStream(session, text);
    }
    
    private void processAudioStream(WebSocketSession session, Flux<ByteBuffer> audioFlux) {
        pipeline.processStream(audioFlux, session.getId())
            .subscribe(
                event -> sendEvent(session, event),
                error -> {
                    log.error("Pipeline error", error);
                    sendErrorEvent(session, error.getMessage());
                },
                () -> log.info("Pipeline completed for session: {}", session.getId())
            );
    }
    
    private void processTextStream(WebSocketSession session, String text) {
        pipeline.processTextStream(text, session.getId())
            .subscribe(
                event -> sendEvent(session, event),
                error -> {
                    log.error("Pipeline error", error);
                    sendErrorEvent(session, error.getMessage());
                },
                () -> log.info("Text pipeline completed for session: {}", session.getId())
            );
    }
    
    private void sendEvent(WebSocketSession session, VoiceAgentEvent event) {
        if (!session.isOpen()) {
            log.warn("Session closed, cannot send event");
            return;
        }
        
        try {
            String json = objectMapper.writeValueAsString(event);
            synchronized (session) {
                session.sendMessage(new TextMessage(json));
            }
            
            if (event instanceof TTSChunkEvent ttsEvent) {
                synchronized (session) {
                    session.sendMessage(new BinaryMessage(ttsEvent.audio()));
                }
            }
        } catch (IOException e) {
            log.error("Error sending event", e);
        }
    }
    
    private void sendErrorEvent(WebSocketSession session, String errorMessage) {
        if (!session.isOpen()) return;
        
        try {
            Map<String, Object> error = Map.of(
                "type", "error",
                "message", errorMessage,
                "timestamp", System.currentTimeMillis()
            );
            String json = objectMapper.writeValueAsString(error);
            synchronized (session) {
                session.sendMessage(new TextMessage(json));
            }
        } catch (IOException e) {
            log.error("Error sending error event", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket error: {}", session.getId(), exception);
        cleanup(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket closed: {} ({})", session.getId(), status);
        cleanup(session.getId());
    }
    
    private void cleanup(String sessionId) {
        Sinks.Many<ByteBuffer> sink = audioSinks.remove(sessionId);
        if (sink != null) {
            sink.tryEmitComplete();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
