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

package com.alibaba.cloud.ai.example.rag.controller;

import com.alibaba.cloud.ai.example.rag.model.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.writer.FileDocumentWriter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yingzi
 * @date 2025/5/31 01:28
 */
@RestController
@RequestMapping("/writer")
public class WriterController {

    private static final Logger logger = LoggerFactory.getLogger(WriterController.class);

    private final List<Document> documents;

    private final SimpleVectorStore simpleVectorStore;

    public WriterController(EmbeddingModel embeddingModel) {
        logger.info("start read pdf file by page");
        Resource resource = new DefaultResourceLoader().getResource(Constant.PDF_FILE_PATH);
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource); // 只可以传pdf格式文件
        this.documents = pagePdfDocumentReader.read();

        this.simpleVectorStore = SimpleVectorStore
                .builder(embeddingModel).build();
    }

    @GetMapping("/file")
    public void writeFile() {
        logger.info("Writing file...");
        String fileName = "output.txt";
        FileDocumentWriter fileDocumentWriter = new FileDocumentWriter(fileName, true);
        fileDocumentWriter.accept(this.documents);
    }

    @GetMapping("/vector")
    public void writeVector() {
        logger.info("Writing vector...");
        simpleVectorStore.add(documents);
    }

    @GetMapping("/search")
    public List<Document> search() {
        logger.info("start search data");
        return simpleVectorStore.similaritySearch(SearchRequest
                .builder()
                .query("Spring")
                .topK(2)
                .build());
    }
}
