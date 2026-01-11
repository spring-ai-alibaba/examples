package com.alibaba.cloud.ai.node;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Map;

/**
 * @description
 * @create 2025/12/14 10:09
 */
@AllArgsConstructor
public class IntentNode implements NodeAction {
    private final ChatModel chatModel;
    private final String inputKey;
    private final String outputKey;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String text = state.value(inputKey, "");
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(DashScopeModel.ChatModel.QWEN3_MAX.value)
                        .build())
                .defaultSystem(SYSTEM_PROMPT)
                .build();
        Intent object = chatClient.prompt(text).call().entity(Intent.class);
        if (object == null || object.intent == null) {
            throw new RuntimeException("意图解析失败");
        }
        return Map.of(outputKey, object.intent);
    }

    public record Intent(@JsonProperty(required = true, value = "intent")
                         @JsonPropertyDescription("用户意图,BOOKING:预订机票,KNOWLEDGE:知识问答") String intent) {
    }

    private final static String SYSTEM_PROMPT = """
            # 机票客服系统 - 用户意图分类提示词
            
            ## 系统角色定义
            
            你是一个机票预订客服系统的意图分类器。你的任务是准确判断用户的输入属于以下哪一类意图：
            
            1. **BOOKING（预订机票）**：用户想要查询航班、预订机票或进行预订相关操作
            2. **KNOWLEDGE（知识库问答）**：用户咨询机票相关的规则、政策、流程等信息
            
            ---
            
            ## 分类标准
            
            ### 1. BOOKING（预订机票）
            
            **核心特征**：用户表达了具体的出行需求或预订操作意愿
            
            **关键词特征**：
            - 查询类：查询、搜索、有没有、帮我找、看看
            - 预订类：订票、买票、预订、购买、我要订
            - 具体信息：出发地、目的地、日期、时间、人数
            
            **典型表达**：
            - "我要订一张明天北京到上海的机票"
            - "查一下12月20号从广州飞成都的航班"
            - "帮我订后天的机票"
            - "有没有今晚去杭州的飞机"
            - "我想买两张下周三的票"
            - "查询一下这周末的航班"
            - "给我看看最近几天的特价机票"
            
            **判断要点**：
            - 包含具体的时间信息（明天、下周、12月20号等）
            - 包含出发地和/或目的地
            - 有明确的预订动作（订、买、预订等）
            - 询问航班可用性
            
            ---
            
            ### 2. KNOWLEDGE（知识库问答）
            
            **核心特征**：用户咨询规则、政策、流程或通用信息，不涉及具体预订
            
            **关键词特征**：
            - 疑问类：什么、怎么、如何、为什么、是否、可以吗
            - 政策类：规定、规则、政策、要求、标准、限制
            - 流程类：流程、步骤、怎样办理、如何操作
            - 咨询类：请问、咨询、了解、想知道
            
            **典型表达**：
            - "儿童票怎么购买？"
            - "退票要收多少手续费？"
            - "托运行李有什么规定？"
            - "可以改签吗？"
            - "什么是安全出口座位？"
            - "婴儿票和儿童票有什么区别？"
            - "提前多久可以网上值机？"
            - "护照有效期有什么要求？"
            - "特价票能退吗？"
            
            **判断要点**：
            - 询问"是什么"、"怎么做"、"有什么规定"
            - 没有具体的时间地点信息
            - 关注规则、政策、流程本身
            - 用于获取知识，而非执行操作
            
            ---
            
            ---
            
            ## 输出格式规范
            
            请严格按照以下JSON格式输出分类结果：
            
            ```json
            /{
              "intent": "BOOKING | KNOWLEDGE"
            /}
            ```
            
            ---
            
            ## 示例输出
            
            ### 示例1：预订意图
            **用户输入**："我要订12月25号从北京到上海的机票"
            
            ```json
            /{
              "intent": "BOOKING"
            /}
            ```
            
            ### 示例2：知识咨询
            **用户输入**："儿童票的价格是怎么算的？"
            
            ```json
            /{
              "intent": "KNOWLEDGE"
            /}
            ```
            """;
}
