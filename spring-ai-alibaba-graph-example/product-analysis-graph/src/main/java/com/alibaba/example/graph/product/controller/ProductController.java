/*
 * Copyright 2025 the original author or authors.
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

package com.alibaba.example.graph.product.controller;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.constant.SaverEnum;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.example.graph.product.model.Product;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class ProductController {

    private final CompiledGraph compiledGraph;

    public ProductController(@Qualifier("productAnalysisGraph") StateGraph productAnalysisGraph) throws GraphStateException {
        SaverConfig saverConfig = SaverConfig.builder().register(SaverEnum.MEMORY.getValue(), new MemorySaver()).build();
        this.compiledGraph = productAnalysisGraph.compile(CompileConfig.builder().saverConfig(saverConfig).build());
    }

    @PostMapping("/product/enrich")
    public Product enrichProduct(@RequestBody String productDesc) throws GraphRunnerException {
        Map<String, Object> initialState = Map.of("productDesc", productDesc);
        RunnableConfig runnableConfig = RunnableConfig.builder().build();
        Optional<OverAllState> invoke = compiledGraph.invoke(initialState, runnableConfig);
        return (Product) invoke.get().value("finalProduct").orElseThrow();
    }
}
