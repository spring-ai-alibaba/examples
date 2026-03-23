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

package com.cloud.alibaba.ai.example.agent.controller;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.cloud.alibaba.ai.example.agent.HITLHelper;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REST controller for managing personal assistant functionalities.
 * This controller handles requests related to the supervisor agent,
 * including streaming responses and human-in-the-loop interventions.
 *
 * @author wangjx
 * @since 2026-02-13
 */
@RestController
public class PersonalAssistantController {

    private static final Map<String, List<InterruptionMetadata.ToolFeedback>> TOOL_FEEDBACK_MAP = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier("supervisorAgent")
    private ReactAgent supervisorAgent;


    /**
     * Handles GET requests to the supervisor agent endpoint.
     * Supports both regular streaming and human-in-the-loop interventions.
     *
     * @param query    the user's query string
     * @param threadId the session thread identifier
     * @param nodeId   the node identifier for human intervention
     * @return a Flux stream of responses from the supervisor agent
     * @throws GraphRunnerException if there's an error during graph execution
     */
    @GetMapping(value = "/react/agent/supervisorAgent", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux supervisorAgentTest(String query, String threadId, String nodeId) throws GraphRunnerException {
        RunnableConfig config;
        if (nodeId != null && TOOL_FEEDBACK_MAP.containsKey(nodeId)) {
            System.out.println("人工介入开始...");
            // Human intervention using checkpoint mechanism.
            // You must provide a thread ID to associate execution with a session thread,
            // so that conversations can be paused and resumed (required for human review).
            InterruptionMetadata metadata = InterruptionMetadata.builder().toolFeedbacks(TOOL_FEEDBACK_MAP.get(nodeId)).build();
            InterruptionMetadata approvalMetadata = HITLHelper.approveAll(metadata);
            // Resume execution using approval decision
            config = RunnableConfig.builder()
                    .threadId(threadId) // Same thread ID
                    .addHumanFeedback(approvalMetadata)
                    .build();
            TOOL_FEEDBACK_MAP.remove(nodeId);
            return supervisorAgent.stream(query, config)
                    .doOnNext(this::println);
        } else {

            config = RunnableConfig.builder()
                    .threadId(threadId)
                    .build();
        }
        return supervisorAgent.stream(query, config)
                .doOnNext(this::println);

    }

    private void println(NodeOutput nodeOutput) {
        if (nodeOutput instanceof StreamingOutput streamingOutput) {
            String node = streamingOutput.node();
            Message message = streamingOutput.message();
            if (message == null) {
                return;
            }
            if ("_AGENT_MODEL_".equals(node)) {
                System.out.print(message.getText());
            }
            if ("_AGENT_TOOL_".equals(node)) {
                ToolResponseMessage responseMessage = (ToolResponseMessage) message;
                List<ToolResponseMessage.ToolResponse> responses = responseMessage.getResponses();
                System.out.println("================================= Tool Message =================================\n");
                for (ToolResponseMessage.ToolResponse respons : responses) {
                    String string = respons.responseData();
                    System.out.println("id: " + respons.id());
                    System.out.println("name: " + respons.name());
                    System.out.println("responseData: " + string);

                }
            }
        } else if (nodeOutput instanceof InterruptionMetadata interruptionMetadata) {
            System.out.println("检测到中断，需要人工审批");
            List<InterruptionMetadata.ToolFeedback> toolFeedbacks =
                    interruptionMetadata.toolFeedbacks();

            for (InterruptionMetadata.ToolFeedback feedback : toolFeedbacks) {
                System.out.println("工具: " + feedback.getName());
                System.out.println("参数: " + feedback.getArguments());
                System.out.println("描述: " + feedback.getDescription());
            }
            String node = interruptionMetadata.node();
            System.out.println("检测到中断,等待人工介入... node: " + node);
            TOOL_FEEDBACK_MAP.put(node, toolFeedbacks);
        }
    }
}
