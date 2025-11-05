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
package com.alibaba.cloud.ai.example.rag.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * initialize documents into elasticsearch vector store
 *
 * @author benym
 */
@Configuration
public class DocumentInit implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DocumentInit.class);

    private final ElasticsearchVectorStore vectorStore;

    public DocumentInit(ElasticsearchVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Document> documents = new ArrayList<>();
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("category", "技术文档");
        metadata1.put("word", "什么是hybridSearch？它是一种混合检索技术");
        Document document1 = Document.builder()
                .id("1")
                .text("什么是hybridSearch")
                .metadata(metadata1)
                .build();
        Map<String, Object> metadata2 = new HashMap<>();
        metadata2.put("category", "HybridSearch");
        metadata2.put("word", "HybridSearch结合了向量搜索和传统文本检索的优势");
        Document document2 = Document.builder()
                .id("2")
                .text("HybridSearch的优势")
                .metadata(metadata2)
                .build();
        Map<String, Object> metadata3 = new HashMap<>();
        metadata3.put("category", "其他");
        metadata3.put("word", "无关文档内容");
        Document document3 = Document.builder()
                .id("3")
                .text("这是一个无关的文档")
                .metadata(metadata3)
                .build();
        documents.add(document1);
        documents.add(document2);
        documents.add(document3);
        vectorStore.add(documents);
        logger.info("add documents success");
    }
}
