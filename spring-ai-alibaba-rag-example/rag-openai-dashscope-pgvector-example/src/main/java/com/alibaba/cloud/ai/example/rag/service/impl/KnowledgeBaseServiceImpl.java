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
package com.alibaba.cloud.ai.example.rag.service.impl;

import com.alibaba.cloud.ai.example.rag.service.KnowledgeBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库服务实现类
 * @author Mxy
 */
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseServiceImpl.class);

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    @Autowired
    public KnowledgeBaseServiceImpl(VectorStore vectorStore, @Qualifier("openAiChatModel")ChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(OpenAiChatOptions.builder().temperature(0.7).build())
                .build();
    }

    /**
     * 相似性搜索
     * @param query 查询字符串
     * @param topK 返回的相似文档数量
     * @return
     */
    @Override
    public List<Document> similaritySearch(String query, int topK) {
        Assert.hasText(query, "查询不能为空");

        logger.info("执行相似性搜索: query={}, businessType={}, topK={}", query, topK);

        // 创建业务类型过滤器
        SearchRequest searchRequest = SearchRequest.builder().query(query).topK(topK).build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);
        logger.info("相似性搜索完成，找到 {} 个相关文档", results.size());

        return results;
    }


    /**
     * 将文本内容插入到向量存储中。
     *
     * @param content      要插入的文本内容
     */
    @Override
    public void insertTextContent(String content) {
        Assert.hasText(content, "文本内容不能为空");
        logger.info("插入文本内容到向量存储: contentLength={}",  content.length());
        // 创建文档并设置ID和元数据
        Document document = new Document(content);
        // 使用文本分割器处理长文本
        List<Document> splitDocuments = new TokenTextSplitter().apply(List.of(document));

        // 添加到向量存储
        vectorStore.add(splitDocuments);

        logger.info("文本内容插入完成: 生成文档片段数: {}",  splitDocuments.size());
    }

    /**
     * 根据文件类型加载文件到向量存储中。
     *
     * @param file         要上传的文件
     * @return 处理结果消息
     */
    @Override
    public String loadFileByType(MultipartFile file) {
        Assert.notNull(file, "文件不能为空");

        logger.info("开始处理文件上传: fileName={}, fileSize={}", file.getOriginalFilename(),  file.getSize());

        try {
            // 创建临时文件
            Path tempFile = Files.createTempFile("upload_", "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            List<Document> documents;
            String fileName = file.getOriginalFilename();

            // 根据文件类型选择合适的文档读取器
            if (fileName.toLowerCase().endsWith(".pdf")) {
                // 使用PDF读取器
                PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(tempFile.toUri().toString());
                documents = pdfReader.get();
                logger.info("使用PDF读取器处理文件: {}", fileName);
            } else {
                // 使用Tika读取器处理其他类型文件
                TikaDocumentReader tikaReader = new TikaDocumentReader(tempFile.toUri().toString());
                documents = tikaReader.get();
                logger.info("使用Tika读取器处理文件: {}", fileName);
            }
            // 添加文档到向量存储
            vectorStore.add(documents);

            // 清理临时文件
            Files.deleteIfExists(tempFile);

            logger.info("文件处理完成: fileName={}, documentsCount={}", fileName,  documents.size());

            return String.format("成功处理文件 %s，共生成 %d 个文档片段", fileName, documents.size());

        } catch (IOException e) {
            logger.error("文件处理失败: fileName={}, error={}", file.getOriginalFilename(),  e.getMessage(), e);
            return "文件处理失败: " + e.getMessage();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String chatWithKnowledge(String query, int topK) {
        Assert.hasText(query, "查询问题不能为空");
        logger.info("开始知识库对话，查询: '{}'", query);

        // 检索相关文档
        List<Document> relevantDocs = similaritySearch(query, topK);

        if (relevantDocs.isEmpty()) {
            logger.warn("未找到与查询相关的文档");
            return "抱歉，我在知识库中没有找到相关信息来回答您的问题。";
        }

        // 构建上下文
        String context = relevantDocs.stream().map(Document::getText).collect(Collectors.joining("\n\n"));

        // 构建提示词
        String prompt = String.format("基于以下知识库内容回答用户问题。如果知识库内容无法回答问题，请明确说明。\n\n" + "知识库内容：\n%s\n\n" + "用户问题：%s\n\n" + "请基于上述知识库内容给出准确、有用的回答：", context, query);

        // 调用LLM生成回答
        String answer = chatClient.prompt(prompt).call().content();

        logger.info("知识库对话完成，查询: '{}'", query);
        return answer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<String> chatWithKnowledgeStream(String query, int topK) {
        Assert.hasText(query, "查询问题不能为空");
        logger.info("开始流式知识库对话，查询: '{}'", query);

        try {
            // 检索相关文档
            List<Document> relevantDocs = similaritySearch(query, topK);

            if (relevantDocs.isEmpty()) {
                logger.warn("未找到与查询相关的文档");
                return Flux.just("抱歉，我在知识库中没有找到相关信息来回答您的问题。");
            }

            // 构建上下文
            String context = relevantDocs.stream().map(Document::getText).collect(Collectors.joining("\n\n"));

            // 构建提示词
            String prompt = String.format("基于以下知识库内容回答用户问题。如果知识库内容无法回答问题，请明确说明。\n\n" + "知识库内容：\n%s\n\n" + "用户问题：%s\n\n" + "请基于上述知识库内容给出准确、有用的回答：", context, query);

            // 调用LLM生成流式回答
            return chatClient.prompt(prompt).stream().content();

        } catch (Exception e) {
            logger.error("流式知识库对话失败，查询: '{}'", query, e);
            return Flux.just("对话过程中发生错误: " + e.getMessage());
        }
    }

}
