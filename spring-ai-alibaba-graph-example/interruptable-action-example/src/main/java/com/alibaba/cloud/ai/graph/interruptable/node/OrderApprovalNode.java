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
package com.alibaba.cloud.ai.graph.interruptable.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.action.AsyncNodeActionWithConfig;
import com.alibaba.cloud.ai.graph.action.InterruptableAction;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Libres-coder
 * @since 2025/10/31
 */
public class OrderApprovalNode implements AsyncNodeActionWithConfig, InterruptableAction {

    private static final Logger logger = LoggerFactory.getLogger(OrderApprovalNode.class);
    
    private static final double APPROVAL_THRESHOLD = 10000.0;

    @Override
    public Optional<InterruptionMetadata> interrupt(String nodeId, OverAllState state, RunnableConfig config) {
        logger.info("OrderApprovalNode.interrupt() called, nodeId: {}", nodeId);
        
        Optional<Object> feedback = config.metadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY);
        if (feedback.isPresent()) {
            Map<String, Object> feedbackData = (Map<String, Object>) feedback.get();
            Boolean approved = (Boolean) feedbackData.get("approved");
            logger.info("Human feedback received, approved: {}", approved);
            return Optional.empty();
        }
        
        Double orderAmount = state.value("order_amount", 0.0);
        String orderId = state.value("order_id", "UNKNOWN");
        
        if (orderAmount > APPROVAL_THRESHOLD) {
            logger.info("Order amount {} exceeds threshold {}, interrupting for approval", orderAmount, APPROVAL_THRESHOLD);
            
            InterruptionMetadata interruptionMetadata = InterruptionMetadata.builder(nodeId, state)
                .addMetadata("reason", "订单金额超过 " + APPROVAL_THRESHOLD + " 元，需要人工审批")
                .addMetadata("order_id", orderId)
                .addMetadata("order_amount", orderAmount)
                .addMetadata("threshold", APPROVAL_THRESHOLD)
                .addMetadata("interrupt_time", System.currentTimeMillis())
                .build();
            
            return Optional.of(interruptionMetadata);
        }
        
        logger.info("Order amount within limit, continuing");
        return Optional.empty();
    }

    @Override
    public CompletableFuture<Map<String, Object>> apply(OverAllState state, RunnableConfig config) {
        logger.info("OrderApprovalNode.apply() executing");
        
        String orderId = state.value("order_id", "UNKNOWN");
        Double orderAmount = state.value("order_amount", 0.0);
        boolean wasApproved = state.value("approved", false);
        
        Map<String, Object> result = new HashMap<>();
        
        if (orderAmount > APPROVAL_THRESHOLD) {
            if (wasApproved) {
                logger.info("Order {} approved, processing", orderId);
                result.put("order_status", "approved_and_processing");
                result.put("message", String.format("订单 %s 已获批准，正在处理中...", orderId));
            } else {
                logger.info("Order {} rejected", orderId);
                result.put("order_status", "rejected");
                result.put("message", String.format("订单 %s 审批被拒绝", orderId));
            }
        } else {
            logger.info("Order {} processing directly", orderId);
            result.put("order_status", "processing");
            result.put("message", String.format("订单 %s 正在处理中...", orderId));
        }
        
        result.put("processed_time", System.currentTimeMillis());
        
        return CompletableFuture.completedFuture(result);
    }
}

