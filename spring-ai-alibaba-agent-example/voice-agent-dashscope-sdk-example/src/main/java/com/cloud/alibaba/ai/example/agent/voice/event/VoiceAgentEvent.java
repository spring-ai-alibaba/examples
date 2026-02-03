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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Voice Agent Event base interface.
 * All events in the voice pipeline implement this interface.
 *
 * @author buvidk
 * @since 2026-02-03
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = STTChunkEvent.class, name = "stt_chunk"),
    @JsonSubTypes.Type(value = STTOutputEvent.class, name = "stt_output"),
    @JsonSubTypes.Type(value = AgentChunkEvent.class, name = "agent_chunk"),
    @JsonSubTypes.Type(value = AgentEndEvent.class, name = "agent_end"),
    @JsonSubTypes.Type(value = TTSChunkEvent.class, name = "tts_chunk")
})
public sealed interface VoiceAgentEvent permits 
    STTChunkEvent,
    STTOutputEvent, 
    AgentChunkEvent,
    AgentEndEvent,
    TTSChunkEvent {
    
    String type();
    long timestamp();
}
