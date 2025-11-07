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
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yingzi
 * @date 2025/5/30 22:49
 */
@RestController
@RequestMapping("/reader")
public class ReaderController {

    private static final Logger logger = LoggerFactory.getLogger(ReaderController.class);

    @GetMapping("/text")
    public List<Document> readText() {
        logger.info("start read text file");
        Resource resource = new DefaultResourceLoader().getResource(Constant.TEXT_FILE_PATH);
        TextReader textReader = new TextReader(resource); // 适用于文本数据
        return textReader.read();
    }

    @GetMapping("/json")
    public List<Document> readJson() {
        logger.info("start read json file");
        Resource resource = new DefaultResourceLoader().getResource(Constant.JSON_FILE_PATH);
        JsonReader jsonReader = new JsonReader(resource); // 只可以传json格式文件
        return jsonReader.read();
    }

    @GetMapping("/pdf-page")
    public List<Document> readPdfPage() {
        logger.info("start read pdf file by page");
        Resource resource = new DefaultResourceLoader().getResource(Constant.PDF_FILE_PATH);
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource); // 只可以传pdf格式文件
        return pagePdfDocumentReader.read();
    }

    @GetMapping("/pdf-paragraph")
    public List<Document> readPdfParagraph() {
        logger.info("start read pdf file by paragraph");
        Resource resource = new DefaultResourceLoader().getResource(Constant.PDF_FILE_PATH);
        ParagraphPdfDocumentReader paragraphPdfDocumentReader = new ParagraphPdfDocumentReader(resource); // 有目录的pdf文件
        return paragraphPdfDocumentReader.read();
    }

    @GetMapping("/markdown")
    public List<Document> readMarkdown() {
        logger.info("start read markdown file");
        MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(Constant.MARKDOWN_FILE_PATH); // 只可以传markdown格式文件
        return markdownDocumentReader.read();
    }

    @GetMapping("/html")
    public List<Document> readHtml() {
        logger.info("start read html file");
        Resource resource = new DefaultResourceLoader().getResource(Constant.HTML_FILE_PATH);
        JsoupDocumentReader jsoupDocumentReader = new JsoupDocumentReader(resource); // 只可以传html格式文件
        return jsoupDocumentReader.read();
    }

    @GetMapping("/tika")
    public List<Document> readTika() {
        logger.info("start read file with Tika");
        Resource resource = new DefaultResourceLoader().getResource(Constant.HTML_FILE_PATH);
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource); // 可以传多种文档格式
        return tikaDocumentReader.read();
    }
}
