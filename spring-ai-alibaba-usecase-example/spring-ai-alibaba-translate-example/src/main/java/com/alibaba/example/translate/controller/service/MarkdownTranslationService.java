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

package com.alibaba.example.translate.controller.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * Description: 文件翻译
 * Author: yhong
 * @since 1.0.0-M2
 */
@Service
public class MarkdownTranslationService {

    private final DashScopeChatModel dashScopeChatModel;

    @Value("classpath:/prompts/markdown-translation-prompt.st")
    private Resource markdownPromptResource;

    public MarkdownTranslationService(DashScopeChatModel dashScopeChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
    }

    public String translateMarkdownFile(String filePath,
                                        String sourceLanguage,
                                        String targetLanguage) throws IOException {
        // 1. 读取原始Markdown文件
        String originalContent = Files.readString(Paths.get(filePath));

        // 2. 加载Prompt模板
        PromptTemplate promptTemplate = new PromptTemplate(markdownPromptResource);
        Map<String, Object> params = Map.of(
                "sourceLanguage", sourceLanguage,
                "targetLanguage", targetLanguage,
                "markdownContent", originalContent
        );

        // 3. 构建翻译Prompt
        Prompt prompt = new Prompt(
                String.valueOf(promptTemplate.create(params)),
                buildTranslationOptions()
        );

        // 4. 调用大模型翻译
        String translatedContent = dashScopeChatModel.call(prompt)
                .getResult().getOutput().getText();

        // 5. 保存文件到本地（桌面）
        return saveTranslatedFile(filePath, translatedContent);
    }

    private DashScopeChatOptions buildTranslationOptions() {
        return DashScopeChatOptions.builder()
                .withModel(DashScopeModel.ChatModel.QWEN_PLUS.getValue())
                .withTopP(0.7)
                .withTopK(50)
                .withTemperature(0.3)
                .build();
    }

    private String saveTranslatedFile(String originalPath, String content) throws IOException {
        // 1. 输出目录存在（~/Desktop/translated）
        Path outputDir = Paths.get(System.getProperty("user.home"), "Desktop", "translated");
        Files.createDirectories(outputDir);

        // 2. 构建新文件名
        String originalFilename = Paths.get(originalPath).getFileName().toString();
        String newFilename = originalFilename.replaceAll("\\.md$", "") + "_zh.md"; // 确保去除原.md后缀
        Path outputPath = outputDir.resolve(newFilename);

        // 3. 写入内容（覆盖已存在文件）
        Files.writeString(
                outputPath,
                content,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );

        return outputPath.toString();
    }
}
