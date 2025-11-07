/*
 * Copyright 2025-2026 the original author or authors.
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
package com.alibaba.cloud.ai.graph.node;

import com.alibaba.cloud.ai.graph.GraphResponse;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.async.AsyncGenerator;
import com.alibaba.cloud.ai.graph.model.NodeStatus;
import com.alibaba.cloud.ai.graph.streaming.FluxConverter;
import com.alibaba.cloud.ai.graph.streaming.StreamingChatGenerator;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author yingzi
 * @since 2025/6/13
 */

public class TranslateNode implements NodeAction {

    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("Given a user query, translate it to {targetLanguage}.\nIf the query is already in {targetLanguage}, return it unchanged.\nIf you don't know the language of the query, return it unchanged.\nDo not add explanations nor any other text.\n\nOriginal query: {query}\n\nTranslated query:\n");

    private final ChatClient chatClient;

    private final String  TARGET_LANGUAGE= "English";

    private final Map<String, NodeStatus> node2Status;

    public static final String NODE_NAME = "translate";


    public TranslateNode(ChatClient.Builder chatClientBuilder, Map<String, NodeStatus> node2Status) {
        this.chatClient = chatClientBuilder.build();
        this.node2Status = node2Status;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) {
        node2Status.put(NODE_NAME, NodeStatus.RUNNING);

        String query = state.value("query", "");
        String targetLanguage = state.value("translate_language", TARGET_LANGUAGE);

        Flux<ChatResponse> chatResponseFlux = this.chatClient.prompt().user((user) -> user.text(DEFAULT_PROMPT_TEMPLATE.getTemplate()).param("targetLanguage", targetLanguage).param("query", query)).stream().chatResponse();

        Flux<GraphResponse<StreamingOutput>> generator = FluxConverter.builder()
                .startingNode("translate_llm_stream")
                .startingState(state)
                .mapResult(response -> {
                    String text = response.getResult().getOutput().getText();
                    node2Status.put(NODE_NAME, NodeStatus.COMPLETED);
                    assert text != null;
                    return Map.of("translate_content", text);
                }).build(chatResponseFlux);

        return Map.of("translate_content", generator);
    }
}
