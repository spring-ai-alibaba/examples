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
package com.alibaba.cloud.ai.example.rag.controller;

import com.alibaba.cloud.ai.rag.preretrieval.transformation.HyDeTransformer;
import com.alibaba.cloud.ai.rag.retrieval.search.HyDeRetriever;
import com.alibaba.cloud.ai.rag.retrieval.search.HybridElasticsearchRetriever;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RagAutoConfigureController
 *
 * @author benym
 */
@RestController
@RequestMapping("/rag")
public class RagAutoConfigureController {

    @Resource
    private HybridElasticsearchRetriever hybridRetriever;

    @Resource
    private HyDeRetriever hyDeRetriever;

    @Resource
    private HyDeTransformer hyDeTransformer;

    @GetMapping("/retrieval/hybrid")
    public List<Document> retrievalHybrid() {
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .build();
        return hybridRetriever.retrieve(query);
    }

    @GetMapping("/retrieval/hyde")
    public List<Document> retrievalHyde() {
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .build();
        return hyDeRetriever.retrieve(query);
    }

    @GetMapping("/transform/hyde")
    public Query transformHyde() {
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .build();
        return hyDeTransformer.transform(query);
    }
}
