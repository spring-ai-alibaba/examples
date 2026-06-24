/*
 * Copyright 2026-2027 the original author or authors.
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
package com.alibaba.cloud.ai.example.langgraph.custom.rag.node;

import com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.KnowledgeTool;
import com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.request.KnowledgeRequest;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : txuw
 * @date : 2026/1/16
 */
public class GenerateQueryNode implements NodeAction {

    private static final Logger logger = LoggerFactory.getLogger(GenerateQueryNode.class);

    private final ChatClient chatClient;

    private final ToolCallback knowledgeToolCallback;

    public final static String NAME = "GenerateQueryNode";

    public GenerateQueryNode(ChatClient.Builder chatClientBuilder, KnowledgeTool knowledgeTool) {

        this.knowledgeToolCallback = FunctionToolCallback.builder("get_alibaba_knowledge", knowledgeTool)
                .description("用于查询spring ai alibaba教程知识库")
                .inputType(KnowledgeRequest.class)
                .build();
        this.chatClient = chatClientBuilder.build().mutate()
                .defaultTools(knowledgeToolCallback)
                .build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String query = state.value("query", "");

        logger.info("node :" + NAME + " 发起请求:" + query);

        // 关闭 ToolCallingAdvisor 自动执行，如果调用工具，则让 AI 只产出 call 参数。
        ChatClient.CallResponseSpec callResponseSpec = chatClient.prompt(query)
                .advisors(AdvisorParams.toolCallingAdvisorAutoRegister(false))
                .options(ToolCallingChatOptions.builder())
                .call();
        ChatResponse response = callResponseSpec.chatResponse();
        Generation result = response.getResult();
        String id = result.getMetadata().getOrDefault("requestId", "");
        String content = result.getOutput().getText();

        // Q:为什么需要换上下文，这样的话不如直接调工具吧？
        // A:因为如果是常规聊天，就不会去调用工具，直接输出内容了
        if (result.getOutput().hasToolCalls()) {
            AssistantMessage.ToolCall toolCall = result.getOutput().getToolCalls().get(0);
            content = knowledgeToolCallback.call(toolCall.arguments());
        }

        logger.info("node :" + NAME + " id: " + id + " 返回值: " + content);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", content);

        return resultMap;
    }
}
