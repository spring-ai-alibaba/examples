package com.alibaba.cloud.ai.controller;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeCloudStore;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentCloudReader;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeStoreOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @description
 * @create 2025/12/14 9:45
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/knowledge")
public class KnowledgeController {

    @Value("classpath:/data/custome_knowledge.md")
    private Resource knowledgeResource;
    @Value("${spring.ai.dashscope.index-name}")
    private String indexName;
    private final DashScopeApi dashscopeApi;

    @GetMapping("/import")
    public String importDocument() throws IOException {
        // 1. import and split documents
        DocumentReader reader = new DashScopeDocumentCloudReader(knowledgeResource.getURL().getPath(), dashscopeApi, null);
        List<Document> documentList = reader.get();
        log.info("{} documents loaded and split", documentList.size());
        // 2. add documents to DashScope cloud storage
        VectorStore vectorStore = new DashScopeCloudStore(dashscopeApi, new DashScopeStoreOptions(indexName));
        vectorStore.add(documentList);
        return "success";
    }
}
