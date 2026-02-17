/*
 * Copyright 2024-2025 Alibaba Group Holding Limited.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.examples.sandbox.browser.controller;

import com.alibaba.cloud.ai.examples.sandbox.browser.model.ChatRequest;
import com.alibaba.cloud.ai.examples.sandbox.browser.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private AgentService agentService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        new Thread(() -> {
            try {
                agentService.chatWithStreaming(
                        request.getSessionId(),
                        request.getMessage(),
                        chunk -> {
                            try {
                                emitter.send(SseEmitter.event()
                                        .data(chunk)
                                        .name("message"));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        }
                );
                emitter.send(SseEmitter.event()
                        .data("[DONE]")
                        .name("done"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChatByGet(@RequestParam String sessionId, @RequestParam String message) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        new Thread(() -> {
            try {
                agentService.chatWithStreaming(
                        sessionId,
                        message,
                        chunk -> {
                            try {
                                emitter.send(SseEmitter.event()
                                        .data(chunk)
                                        .name("message"));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        }
                );
                emitter.send(SseEmitter.event()
                        .data("[DONE]")
                        .name("done"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

}
