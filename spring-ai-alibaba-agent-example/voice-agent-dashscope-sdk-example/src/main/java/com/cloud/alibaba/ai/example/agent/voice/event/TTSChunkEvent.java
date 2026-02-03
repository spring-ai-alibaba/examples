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
package com.cloud.alibaba.ai.example.agent.voice.event;

import java.util.Base64;

/**
 * TTS audio chunk event for streaming playback.
 *
 * @author buvidk
 * @since 2026-02-03
 */
public record TTSChunkEvent(
    String type,
    byte[] audio,
    long timestamp
) implements VoiceAgentEvent {
    
    public static TTSChunkEvent create(byte[] audio) {
        return new TTSChunkEvent("tts_chunk", audio, System.currentTimeMillis());
    }
    
    public String audioBase64() {
        return Base64.getEncoder().encodeToString(audio);
    }
}
