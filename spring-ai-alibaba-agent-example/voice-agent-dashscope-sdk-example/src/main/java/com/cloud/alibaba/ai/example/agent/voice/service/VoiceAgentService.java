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

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.cloud.alibaba.ai.example.agent.voice.event.AgentChunkEvent;
import com.cloud.alibaba.ai.example.agent.voice.event.AgentEndEvent;
import com.cloud.alibaba.ai.example.agent.voice.event.VoiceAgentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

/**
 * Voice Agent Service using Spring AI Alibaba ReactAgent.
 *
 * @author buvidk
 * @since 2026-02-03
 */
@Service
public class VoiceAgentService {
    
    private static final Logger log = LoggerFactory.getLogger(VoiceAgentService.class);
    
    private final ReactAgent reactAgent;
    
    public VoiceAgentService(ReactAgent voiceReactAgent) {
        this.reactAgent = voiceReactAgent;
        log.info("VoiceAgentService initialized with ReactAgent");
    }
    
    /**
     * Stream-based agent chat.
     * Note: ReactAgent executes synchronously, so the stream contains the complete response.
     */
    public Flux<VoiceAgentEvent> chat(String threadId, String userMessage) {
        log.info("Agent chat [{}]: {}", threadId, userMessage);
        
        return Mono.fromCallable(() -> {
            RunnableConfig config = RunnableConfig.builder()
                .threadId(threadId)
                .build();
            
            NodeOutput result = reactAgent.invokeAndGetOutput(userMessage, config).orElse(null);
            return extractResponse(result);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMapMany(response -> {
            log.info("Agent response generated: {}", response);
            return Flux.just(
                (VoiceAgentEvent) AgentChunkEvent.create(response),
                (VoiceAgentEvent) AgentEndEvent.create()
            );
        });
    }
    
    private String extractResponse(NodeOutput result) {
        if (result == null) {
            return "No response generated.";
        }

        OverAllState state = result.state();

        Optional<Object> output = state.value("output");
        if (output.isPresent()) {
            return String.valueOf(output.get());
        }

        Optional<List<AbstractMessage>> messages = state.value("messages");
        if (messages.isPresent() && !messages.get().isEmpty()) {
            List<AbstractMessage> msgList = messages.get();
            return msgList.get(msgList.size() - 1).getText();
        }

        return state.toString();
    }
}
