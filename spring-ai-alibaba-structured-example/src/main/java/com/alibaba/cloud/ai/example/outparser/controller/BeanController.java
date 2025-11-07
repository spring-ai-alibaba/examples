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

package com.alibaba.cloud.ai.example.outparser.controller;

import com.alibaba.cloud.ai.example.outparser.entity.BeanEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/bean")
public class BeanController {

    private static final Logger log = LoggerFactory.getLogger(BeanController.class);

    private final ChatClient chatClient;
    private final ChatModel chatModel;
    private final BeanOutputConverter<BeanEntity> converter;
    private final String format;

    public BeanController(ChatClient.Builder builder, ChatModel chatModel) {
        this.chatModel = chatModel;

        this.converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<BeanEntity>() {
                }
        );
        this.format = converter.getFormat();
        log.info("format: {}", format);
        this.chatClient = builder
                .build();
    }

    @GetMapping("/chat")
    public String simpleChat(@RequestParam(value = "query", defaultValue = "以影子为作者，写一篇200字左右的有关人工智能诗篇") String query) {
        String result = chatClient.prompt(query)
                .call().content();

        log.info("result: {}", result);
        assert result != null;
        try {
            BeanEntity convert = converter.convert(result);
            log.info("反序列成功，convert: {}", convert);
        } catch (Exception e) {
            log.error("反序列化失败");
        }
        return result;
    }

    @GetMapping("/chat-format")
    public BeanEntity simpleChatFormat(@RequestParam(value = "query", defaultValue = "以影子为作者，写一篇200字左右的有关人工智能诗篇") String query) {
        return chatClient.prompt(query)
                .call().entity(BeanEntity.class);
    }

    @GetMapping("/chat-model-format")
    public String chatModel(@RequestParam(value = "query", defaultValue = "以影子为作者，写一篇200字左右的有关人工智能诗篇") String query) {
        String template = query + "{format}";

        PromptTemplate promptTemplate = PromptTemplate.builder()
            .template(template)
            .variables(Map.of("format", format))
            .renderer(StTemplateRenderer.builder().build())
            .build();

        Prompt prompt = promptTemplate.create();
        String result = chatModel.call(prompt)
                .getResult().getOutput().getText();
        log.info("result: {}", result);
        assert result != null;
        try {
            BeanEntity convert = converter.convert(result);
            log.info("反序列成功，convert: {}", convert);
        } catch (Exception e) {
            log.error("反序列化失败");
        }
        return result;
    }

    /**
     * @return {@link BeanEntity}
     */
    @GetMapping("/play")
    public BeanEntity simpleChat(HttpServletResponse response) {
        Flux<String> flux = this.chatClient.prompt()
                .user(u -> u.text("""
						requirement: 请用大概 120 字，作者为 牧生 ，为计算机的发展历史写一首现代诗;
						format: 以纯文本输出 json，请不要包含任何多余的文字——包括 markdown 格式;
						outputExample: {
							 "title": {title},
							 "author": {author},
							 "date": {date},
							 "content": {content}
						};
						"""))
                .stream()
                .content();

        String result = String.join("\n", Objects.requireNonNull(flux.collectList().block()))
                .replaceAll("\\n", "")
                .replaceAll("\\s+", " ")
                .replaceAll("\"\\s*:", "\":")
                .replaceAll(":\\s*\"", ":\"");

        log.info("LLMs 响应的 json 数据为：{}", result);

        return converter.convert(result);
    }
}
