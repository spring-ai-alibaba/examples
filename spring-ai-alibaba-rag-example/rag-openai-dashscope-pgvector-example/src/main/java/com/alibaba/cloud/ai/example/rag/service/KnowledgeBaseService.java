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
package com.alibaba.cloud.ai.example.rag.service;

import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 知识库管理操作的服务接口。
 */
public interface KnowledgeBaseService {
    /**
     * 将字符串内容插入到指定的向量库中。
     * @param content 要插入的文本内容
     */
    void insertTextContent(String content);

    /**
     * 根据文件类型动态选择Reader加载文件到知识库。
     * 支持的文件类型：PDF、Word、TXT、Text等
     *
     * @param file 上传的文件
     * @return 处理结果信息
     */
    String loadFileByType(MultipartFile file);

    /**
     * 基于查询在指定业务类型中搜索相似文档。
     *
     * @param query 查询字符串
     * @param topK 返回的相似文档数量
     * @return 相似文档列表
     */
    List<Document> similaritySearch(String query, int topK);

    /**
     * 阻塞式LLM对话接口，根据业务类型获取相关知识库数据进行问答。
     *
     * @param query 用户查询问题
     * @param topK 检索的相关文档数量
     * @return LLM生成的回答
     */
    String chatWithKnowledge(String query, int topK);

    /**
     * 流式LLM对话接口，根据业务类型获取相关知识库数据进行问答。
     *
     * @param query 用户查询问题
     * @param topK 检索的相关文档数量
     * @return 流式返回的LLM回答
     */
    Flux<String> chatWithKnowledgeStream(String query,  int topK);


}
