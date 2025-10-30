/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.graph;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.graph.async.AsyncGenerator;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController("/run")
public class GraphRunController {

    private CompiledGraph graph;

    public GraphRunController(@Qualifier("buildGraph") CompiledGraph graph){
        this.graph = graph;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NodeOutput> stream(@RequestBody Map<String, Object> inputs) throws Exception {
        return graph.fluxStream(inputs);
    }


    @PostMapping(value = "/invoke")
    public OverAllState invoke(@RequestBody Map<String, Object> inputs) {
        OverAllState state = graph.call(inputs).orElse(null);
        return state;
    }


}
