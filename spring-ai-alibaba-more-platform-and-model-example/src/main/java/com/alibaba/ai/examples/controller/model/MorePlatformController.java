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

package com.alibaba.ai.examples.controller.model;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("no-platform")
public class MorePlatformController {

    private final ChatModel dashScopeChatModel;

    private final ChatModel ollamaChatModel;
    private final ChatModel openAIChatModel;

    /**
    * @Description: @Qualifier注解是用来指定注入的bean的名字，value值需要到源码中查看，如不对应会报错：NoSuchBeanDefinitionException
     * 如openAiChatModel的路径见：org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.java
    */
    public MorePlatformController(
            @Qualifier("dashScopeChatModel") ChatModel dashScopeChatModel,
            @Qualifier("ollamaChatModel") ChatModel OllamaChatModel,
            @Qualifier("openAiChatModel") ChatModel openAIChatModel
    ) {
        this.dashScopeChatModel = dashScopeChatModel;
        this.ollamaChatModel = OllamaChatModel;
        this.openAIChatModel = openAIChatModel;
    }

    @GetMapping("/{platform}/{prompt}")
    public String chat(
            @PathVariable("platform") String model,
            @PathVariable("prompt") String prompt
    ) {

        System.out.println("===============================================");
        System.out.println("DashScope Model：" + dashScopeChatModel.toString());
        System.out.println("Ollama Model：" + ollamaChatModel.toString());
        System.out.println("OpenAI Model：" + openAIChatModel.toString());
        System.out.println("===============================================");

        if ("dashscope".equals(model)) {
            return dashScopeChatModel.call(new Prompt(prompt))
                    .getResult().getOutput().getText();
        }

        if ("ollama".equals(model)) {
            return ollamaChatModel.call(new Prompt(prompt))
                    .getResult().getOutput().getText();
        }

        if ("openAI".equals(model)) {
            return openAIChatModel.call(new Prompt(prompt))
                    .getResult().getOutput().getText();
        }
        return "Error ...";
    }

}
