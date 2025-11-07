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

package com.touhouqing.chatAiDemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcurementAIService {
    
    private final ChatClient chatClient;
    
    /**
     * 分析采购数据
     */
    public String analyzeProcurementData(String title, String content, String tableData) {
        try {
            String prompt = buildAnalysisPrompt(title, content, tableData);
            
            String analysis = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            
            log.info("AI分析完成，标题: {}", title);
            return analysis;
            
        } catch (Exception e) {
            log.error("AI分析失败", e);
            return "AI分析失败: " + e.getMessage();
        }
    }
    
    /**
     * 分析采购项目类别
     */
    public String categorizeProject(String title, String description) {
        try {
            String prompt = String.format("""
                请分析以下采购项目的类别：
                
                项目标题：%s
                项目描述：%s
                
                请从以下类别中选择最合适的一个：
                1. 工程建设
                2. 货物采购
                3. 服务采购
                4. 医疗设备
                5. IT设备
                6. 办公用品
                7. 其他
                
                只返回类别名称，不要其他内容。
                """, title, description);
            
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim();
                    
        } catch (Exception e) {
            log.error("项目分类失败", e);
            return "其他";
        }
    }
    
    /**
     * 提取关键信息
     */
    public String extractKeyInfo(String content) {
        try {
            String prompt = String.format("""
                请从以下采购公告中提取关键信息：
                
                %s
                
                请提取以下信息（如果有的话）：
                1. 项目名称
                2. 预算金额
                3. 采购时间
                4. 采购单位
                5. 项目描述
                6. 特殊要求
                
                请以JSON格式返回结果。
                """, content);
            
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
                    
        } catch (Exception e) {
            log.error("信息提取失败", e);
            return "{}";
        }
    }
    
    /**
     * 分析采购趋势
     */
    public String analyzeTrends(String projectData) {
        try {
            String prompt = String.format("""
                请分析以下采购项目数据的趋势：
                
                %s
                
                请从以下角度进行分析：
                1. 预算规模趋势
                2. 项目类型分布
                3. 采购单位特点
                4. 时间分布特征
                5. 政策导向分析
                
                请提供简洁的分析报告。
                """, projectData);
            
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
                    
        } catch (Exception e) {
            log.error("趋势分析失败", e);
            return "趋势分析失败";
        }
    }
    
    /**
     * 构建分析提示词
     */
    private String buildAnalysisPrompt(String title, String content, String tableData) {
        return String.format("""
            请分析以下政府采购公告的内容：
            
            标题：%s
            
            正文内容：
            %s
            
            表格数据：
            %s
            
            请从以下几个方面进行分析：
            1. 项目性质和类型
            2. 预算规模和资金来源
            3. 采购单位的特点
            4. 项目的重要性和影响
            5. 可能的供应商要求
            6. 项目实施的复杂度
            7. 政策导向和社会意义
            
            请提供结构化的分析结果，重点关注项目的关键特征和商业价值。
            """, title, content, tableData);
    }
}
