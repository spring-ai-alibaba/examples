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

package com.alibaba.cloud.ai.example.llm.auditor.hook;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.ModelHook;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : zhengyuchao
 * @date : 2026/1/22
 */
public class CriticAgentHook extends ModelHook {

    private static final Logger log = LoggerFactory.getLogger(CriticAgentHook.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "";
    }

    @Override
    public CompletableFuture<Map<String, Object>> beforeModel(OverAllState state, RunnableConfig config) {
        return CompletableFuture.completedFuture(Map.of());
    }

    @Override
    public CompletableFuture<Map<String, Object>> afterModel(OverAllState state, RunnableConfig config) {
        Optional<Object> messagesOpt = state.value("critic_agent_output");
        
        if (!messagesOpt.isPresent()) {
            return CompletableFuture.completedFuture(Map.of());
        }
        
        if (!(messagesOpt.get() instanceof AssistantMessage)) {
            return CompletableFuture.completedFuture(Map.of());
        }
        
        AssistantMessage message = (AssistantMessage) messagesOpt.get();
        
        // 从 state 的 messages 中提取工具响应中的引用信息
        List<String> references = extractReferencesFromState(state);
        
        if (references.isEmpty()) {
            return CompletableFuture.completedFuture(Map.of());
        }
        
        // 构建引用文本
        StringBuilder referenceText = new StringBuilder("\n\nReference:\n\n");
        for (String ref : references) {
            referenceText.append(ref);
        }
        
        // 将引用追加到消息内容
        String originalText = message.getText();
        String newText = originalText + referenceText.toString();
        
        AssistantMessage newAssistantMessage = AssistantMessage.builder()
                .content(newText)
                .media(message.getMedia())
                .properties(message.getMetadata())
                .toolCalls(message.getToolCalls())
                .build();
        
        return CompletableFuture.completedFuture(Map.of("critic_agent_output", newAssistantMessage));
    }
    
    /**
     * 从 state 的 messages 中提取工具响应中的引用信息
     * 解析 Tavily 搜索工具返回的结果格式
     */
    private List<String> extractReferencesFromState(OverAllState state) {
        List<String> references = new ArrayList<>();
        
        try {
            // 从 state 中获取 messages 数组
            Optional<Object> messagesObj = state.value("messages");
            if (!messagesObj.isPresent()) {
                return references;
            }
            
            List<?> messages;
            if (messagesObj.get() instanceof List) {
                messages = (List<?>) messagesObj.get();
            } else {
                return references;
            }
            
            // 遍历 messages，找到 TOOL 类型的消息
            for (Object msgObj : messages) {
                if (!(msgObj instanceof Map)) {
                    continue;
                }
                
                Map<String, Object> msgMap = (Map<String, Object>) msgObj;
                String messageType = getStringValue(msgMap, "messageType");
                
                // 查找 TOOL 类型的消息
                if ("TOOL".equals(messageType)) {
                    // 获取 responses 数组
                    Object responsesObj = msgMap.get("responses");
                    if (responsesObj == null) {
                        continue;
                    }
                    
                    List<?> responses;
                    if (responsesObj instanceof List) {
                        responses = (List<?>) responsesObj;
                    } else {
                        continue;
                    }
                    
                    // 遍历每个 response，提取 responseData
                    for (Object respObj : responses) {
                        if (!(respObj instanceof Map)) {
                            continue;
                        }
                        
                        Map<String, Object> respMap = (Map<String, Object>) respObj;
                        String responseData = getStringValue(respMap, "responseData");
                        
                        if (responseData != null && !responseData.isEmpty()) {
                            // 解析 Tavily 返回的搜索结果格式
                            List<String> refs = parseTavilySearchResults(responseData);
                            references.addAll(refs);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.warn("Failed to extract references from state messages: {}", e.getMessage());
            log.debug("State content: {}", state, e);
        }
        
        return references;
    }
    
    /**
     * 解析 Tavily 搜索工具返回的结果格式
     * 格式示例：
     * 【AI 摘要】: ...
     * 
     * 【详细来源】:
     * 1. title
     *    内容: content
     *    链接: url
     */
    private List<String> parseTavilySearchResults(String responseData) {
        List<String> references = new ArrayList<>();
        
        try {
            // 移除 JSON 字符串的转义字符（如果存在）
            String cleanData = responseData;
            if (cleanData.startsWith("\"") && cleanData.endsWith("\"")) {
                cleanData = cleanData.substring(1, cleanData.length() - 1);
                // 处理转义的换行符和引号
                cleanData = cleanData.replace("\\n", "\n").replace("\\\"", "\"");
            }
            
            // 查找【详细来源】部分
            int startIndex = cleanData.indexOf("【详细来源】");
            if (startIndex == -1) {
                startIndex = cleanData.indexOf("详细来源");
            }
            
            if (startIndex == -1) {
                // 如果没有找到标记，尝试从整个字符串中提取
                startIndex = 0;
            } else {
                // 跳过标记行
                int newlineIndex = cleanData.indexOf('\n', startIndex);
                if (newlineIndex != -1) {
                    startIndex = newlineIndex + 1;
                }
            }
            
            String sourceSection = cleanData.substring(startIndex);
            
            // 使用正则表达式提取每个搜索结果
            // 匹配格式：数字. title\n   内容: content\n   链接: url
            // 注意：content 可能包含多行，所以使用非贪婪匹配，直到遇到"链接:"
            Pattern pattern = Pattern.compile(
                "(\\d+)\\.\\s+([^\\n]+?)\\n\\s+内容:\\s+([^\\n]+?)\\n\\s+链接:\\s+([^\\n]+)",
                Pattern.MULTILINE | Pattern.DOTALL
            );
            
            Matcher matcher = pattern.matcher(sourceSection);
            
            while (matcher.find()) {
                String title = matcher.group(2).trim();
                String content = matcher.group(3).trim();
                String url = matcher.group(4).trim();
                
                // 清理 content，移除多余的空白和换行
                content = content.replaceAll("\\s+", " ").trim();
                
                // 构建引用格式：[title](url): content
                if (!title.isEmpty() && !url.isEmpty()) {
                    // 如果 content 太长，截取前200个字符
                    if (content.length() > 200) {
                        content = content.substring(0, 200) + "...";
                    }
                    String reference = String.format("* [%s](%s): %s\n", title, url, content);
                    references.add(reference);
                }
            }
            
            // 如果没有匹配到完整格式，尝试更宽松的匹配模式（只有标题和链接）
            if (references.isEmpty()) {
                Pattern simplePattern = Pattern.compile(
                    "(\\d+)\\.\\s+([^\\n]+?)\\n\\s+链接:\\s+([^\\n]+)",
                    Pattern.MULTILINE
                );
                
                Matcher simpleMatcher = simplePattern.matcher(sourceSection);
                while (simpleMatcher.find()) {
                    String title = simpleMatcher.group(2).trim();
                    String url = simpleMatcher.group(3).trim();
                    
                    if (!title.isEmpty() && !url.isEmpty()) {
                        String reference = String.format("* [%s](%s)\n", title, url);
                        references.add(reference);
                    }
                }
            }
            
        } catch (Exception e) {
            log.warn("Failed to parse Tavily search results: {}", e.getMessage());
            log.debug("Response data: {}", responseData, e);
        }
        
        return references;
    }
    
    /**
     * 安全地从 Map 中获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
