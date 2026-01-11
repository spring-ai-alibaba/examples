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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Libres-coder
 * @since 2025/10/31
 */
public class FinalProcessNode implements AsyncNodeActionWithConfig {

    private static final Logger logger = LoggerFactory.getLogger(FinalProcessNode.class);

    @Override
    public CompletableFuture<Map<String, Object>> apply(OverAllState state, RunnableConfig config) {
        logger.info("FinalProcessNode.apply() executing");
        
        String orderStatus = state.value("order_status", "unknown");
        String operationStatus = state.value("status", "unknown");
        
        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("workflow_status", "completed");
        finalResult.put("order_final_status", orderStatus);
        finalResult.put("operation_final_status", operationStatus);
        finalResult.put("completed_time", System.currentTimeMillis());
        
        String summary = generateSummary(state);
        finalResult.put("summary", summary);
        
        logger.info("Workflow completed, summary: {}", summary);
        
        return CompletableFuture.completedFuture(finalResult);
    }

    private String generateSummary(OverAllState state) {
        StringBuilder summary = new StringBuilder();
        summary.append("工作流执行完成！\n");
        
        if (state.value("order_id").isPresent()) {
            String orderId = state.value("order_id", "");
            Double amount = state.value("order_amount", 0.0);
            String status = state.value("order_status", "");
            summary.append(String.format("- 订单 %s (金额: %.2f) 状态: %s\n", 
                orderId, amount, status));
        }
        
        if (state.value("operation").isPresent()) {
            String operation = state.value("operation", "");
            String status = state.value("status", "");
            summary.append(String.format("- 操作 %s 执行结果: %s\n", 
                operation, status));
        }
        
        return summary.toString();
    }
}
