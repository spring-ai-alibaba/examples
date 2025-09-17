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
 *
 */

package com.alibaba.cloud.ai.graph.controller;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.async.AsyncGenerator;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.constant.SaverEnum;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.StateSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/19 15:42
 */
@RestController
@RequestMapping("/sec/graph")
@Slf4j
public class SecGraphController {
    private final CompiledGraph compiledGraph;

    public SecGraphController(@Qualifier("secGraph") StateGraph stateGraph) throws GraphStateException {
        SaverConfig saverConfig = SaverConfig.builder().register(SaverEnum.MEMORY.getValue(), new MemorySaver()).build();

        this.compiledGraph = stateGraph
                .compile(CompileConfig.builder().saverConfig(saverConfig).interruptBefore("human").build());
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> simpleChat(@RequestParam("fieldName") String fieldName,
                                                    @RequestParam(value = "thread_id", defaultValue = "yhong", required = false) String threadId) throws Exception {
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        GraphProcess graphProcess = new GraphProcess(this.compiledGraph);
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();
        AsyncGenerator<NodeOutput> resultFuture = compiledGraph.stream(Map.of("field", fieldName), runnableConfig);
        graphProcess.processStream(resultFuture, sink);
        return sink.asFlux()
                .doOnCancel(() -> log.info("Client disconnected from stream"))
                .doOnError(e -> log.error("Error occurred during streaming", e));
    }

    // resume workflow
    @GetMapping(value = "/resume", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> resume(@RequestParam(value = "thread_id", defaultValue = "yhong", required = false) String threadId,
                                                @RequestParam(value = "feed_back", defaultValue = "true", required = false) boolean feedBack,
                                                @RequestParam(value = "feedback_reason", defaultValue = "", required = false) String humanReason) throws GraphRunnerException {
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        StateSnapshot stateSnapshot = this.compiledGraph.getState(runnableConfig);
        OverAllState state = stateSnapshot.state();
        state.withResume();

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("feed_back", feedBack);
        objectMap.put("feedback_reason", humanReason);
        state.withHumanFeedback(new OverAllState.HumanFeedback(objectMap, "feed_back"));

        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();
        GraphProcess graphProcess = new GraphProcess(this.compiledGraph);
        AsyncGenerator<NodeOutput> resultFuture = compiledGraph.streamFromInitialNode(state, runnableConfig);
        graphProcess.processStream(resultFuture, sink);

        return sink.asFlux()
                .doOnCancel(() -> log.info("Client disconnected from stream"))
                .doOnError(e -> log.error("Error occurred during streaming", e));
    }

}
