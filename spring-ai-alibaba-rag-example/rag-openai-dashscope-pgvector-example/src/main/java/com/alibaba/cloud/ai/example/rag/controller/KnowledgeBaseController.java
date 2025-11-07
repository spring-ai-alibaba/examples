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

import com.alibaba.cloud.ai.example.rag.service.KnowledgeBaseService;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 知识库管理操作的控制器。
 * 基于业务类型进行知识库管理，支持广告和AIGC两个业务类型。
 */
@RestController
@RequestMapping("/api/v1/knowledge-base")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 构造一个新的知识库控制器。
     *
     * @param knowledgeBaseService 知识库服务实例
     */
    @Autowired
    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    /**
     * 将字符串内容插入到向量库中。
     *
     * @param content 要插入的文本内容
     * @return 表示成功或失败的响应实体
     */
    @GetMapping("/insert-text")
    public ResponseEntity<String> insertTextContent(@RequestParam("content") String content) {
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文本内容是必需的");
        }
        try {
            knowledgeBaseService.insertTextContent(content);
            return ResponseEntity.ok("文本内容已成功插入");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("插入文本内容失败: " + e.getMessage());
        }
    }

    /**
     * 根据文件类型动态选择Reader加载文件到知识库。
     * 支持的文件类型：PDF、Word、TXT、Text等
     *
     * @param file 上传的文件
     * @return 表示成功或失败的响应实体
     */
    @PostMapping("/upload-file")
    public ResponseEntity<String> uploadFileByType(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件为空");
        }
        try {
            String result = knowledgeBaseService.loadFileByType(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 在指定业务类型的知识库中执行相似性搜索。
     *
     * @param query 搜索查询
     * @param topK  要检索的相似文档数量（默认为5）
     * @return 包含相似文档列表或错误消息的响应实体
     */
    @GetMapping("/search")
    public ResponseEntity<?> similaritySearch(@RequestParam("query") String query,
                                              @RequestParam(value = "topK", defaultValue = "5") int topK) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("查询内容是必需的");
        }
        if (topK <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("topK必须是正整数");
        }

        try {
            List<Document> results = knowledgeBaseService.similaritySearch(query, topK);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("相似性搜索过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 阻塞式LLM对话接口，根据业务类型获取相关知识库数据进行问答。
     *
     * @param query 用户查询问题
     * @param topK  检索的相关文档数量（默认为5）
     * @return LLM生成的回答
     */
    @GetMapping("/chat")
    public ResponseEntity<String> chatWithKnowledge(@RequestParam("query") String query,
                                                    @RequestParam(value = "topK", defaultValue = "5") int topK) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("查询问题是必需的");
        }

        if (topK <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("topK必须是正整数");
        }

        try {
            String answer = knowledgeBaseService.chatWithKnowledge(query, topK);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("对话过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 流式LLM对话接口，根据业务类型获取相关知识库数据进行问答。
     *
     * @param query 用户查询问题
     * @param topK  检索的相关文档数量（默认为5）
     * @return 流式返回的LLM回答
     */
    @GetMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> chatWithKnowledgeStream(@RequestParam("query") String query,
                                                                @RequestParam(value = "topK", defaultValue = "5") int topK) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Flux.just("查询问题是必需的"));
        }
        if (topK <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Flux.just("topK必须是正整数"));
        }

        try {
            Flux<String> answerStream = knowledgeBaseService.chatWithKnowledgeStream(query, topK);
            return ResponseEntity.ok(answerStream);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Flux.just("流式对话过程中发生错误: " + e.getMessage()));
        }
    }
}
