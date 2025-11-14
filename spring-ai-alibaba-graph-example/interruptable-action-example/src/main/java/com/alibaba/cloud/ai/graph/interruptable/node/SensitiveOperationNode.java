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

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author Libres-coder
 * @since 2025/10/31
 */
public class SensitiveOperationNode implements AsyncNodeActionWithConfig, InterruptableAction {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveOperationNode.class);
    
    private static final Set<String> SENSITIVE_OPERATIONS = Set.of(
        "delete_user",
        "delete_database",
        "modify_system_config",
        "send_mass_email",
        "export_sensitive_data"
    );

    @Override
    public Optional<InterruptionMetadata> interrupt(String nodeId, OverAllState state, RunnableConfig config) {
        logger.info("SensitiveOperationNode.interrupt() called, nodeId: {}", nodeId);
        
        Optional<Object> feedback = config.metadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY);
        if (feedback.isPresent()) {
            logger.info("Human feedback received, continuing");
            return Optional.empty();
        }
        
        String operation = state.value("operation", "unknown");
        Map<String, Object> operationParams = state.value("operation_params", new HashMap<>());
        
        if (SENSITIVE_OPERATIONS.contains(operation)) {
            logger.info("Sensitive operation detected: {}, interrupting for confirmation", operation);
            
            String confirmMessage = buildConfirmMessage(operation, operationParams);
            
            InterruptionMetadata interruptionMetadata = InterruptionMetadata.builder(nodeId, state)
                .addMetadata("reason", "敏感操作需要人工确认")
                .addMetadata("operation", operation)
                .addMetadata("operation_params", operationParams)
                .addMetadata("confirm_message", confirmMessage)
                .addMetadata("interrupt_time", System.currentTimeMillis())
                .addMetadata("risk_level", getRiskLevel(operation))
                .build();
            
            return Optional.of(interruptionMetadata);
        }
        
        logger.info("Normal operation, continuing");
        return Optional.empty();
    }

    @Override
    public CompletableFuture<Map<String, Object>> apply(OverAllState state, RunnableConfig config) {
        logger.info("SensitiveOperationNode.apply() executing");
        
        String operation = state.value("operation", "unknown");
        Map<String, Object> operationParams = state.value("operation_params", new HashMap<>());
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String operationResult = performOperation(operation, operationParams);
            
            result.put("status", "success");
            result.put("operation", operation);
            result.put("result", operationResult);
            result.put("executed_time", System.currentTimeMillis());
            
            logger.info("Operation executed successfully");
            
        } catch (Exception e) {
            logger.error("Operation failed: {}", e.getMessage());
            result.put("status", "failed");
            result.put("error", e.getMessage());
        }
        
        return CompletableFuture.completedFuture(result);
    }

    private String buildConfirmMessage(String operation, Map<String, Object> params) {
        StringBuilder message = new StringBuilder();
        message.append("警告：您即将执行敏感操作！\n");
        message.append("操作类型: ").append(getOperationDescription(operation)).append("\n");
        
        if (!params.isEmpty()) {
            message.append("操作参数:\n");
            params.forEach((key, value) -> 
                message.append("  - ").append(key).append(": ").append(value).append("\n")
            );
        }
        
        message.append("\n请仔细确认后再继续！");
        return message.toString();
    }

    private String getRiskLevel(String operation) {
        return switch (operation) {
            case "delete_database" -> "CRITICAL";
            case "delete_user", "modify_system_config" -> "HIGH";
            case "send_mass_email", "export_sensitive_data" -> "MEDIUM";
            default -> "LOW";
        };
    }

    private String getOperationDescription(String operation) {
        return switch (operation) {
            case "delete_user" -> "删除用户";
            case "delete_database" -> "删除数据库";
            case "modify_system_config" -> "修改系统配置";
            case "send_mass_email" -> "发送群发邮件";
            case "export_sensitive_data" -> "导出敏感数据";
            default -> operation;
        };
    }

    private String performOperation(String operation, Map<String, Object> params) {
        return String.format("操作 '%s' 执行成功，参数: %s", operation, params);
    }
}
