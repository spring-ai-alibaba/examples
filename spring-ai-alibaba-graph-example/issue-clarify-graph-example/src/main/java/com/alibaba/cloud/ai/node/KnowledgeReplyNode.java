package com.alibaba.cloud.ai.node;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @create 2025/12/14 14:42
 */
@RequiredArgsConstructor
public class KnowledgeReplyNode implements NodeAction {
    private final ChatModel chatModel;
    private final String inputKey;
    private final String outputKey;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String text = state.value(inputKey, "");
        @SuppressWarnings("unchecked")
        List<RagNode.RagDoc> knowledgeList =
                (List<RagNode.RagDoc>) state
                        .value("knowledge_list", List.class)
                        .orElse(Collections.emptyList());

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(DashScopeModel.ChatModel.DEEPSEEK_V3_1.value)
                        .build())
                .defaultSystem(SYSTEM_PROMPT)
                .build();
        String content = chatClient.prompt()
                .system(s -> s.param("knowledge_list", knowledgeList))
                .user(text)
                .call()
                .content();
        return Map.of(outputKey, content);
    }

    private static final String SYSTEM_PROMPT = """
            # 机票知识库问答提示词
            
            ## 角色定义
            
            你是一个专业的机票客服助手，负责根据知识库内容回答用户关于机票预订的问题。
            
            ---
            
            ## 核心任务
            
            基于提供的知识库内容，准确、简洁地回答用户的问题。
            
            ---
            
            ## 回答原则
            
            ### 1. 基于事实
            - **严格依据**知识库提供的内容回答
            - 如果知识库中没有相关信息，明确告知用户
            - 不要编造或猜测信息
            
            ### 2. 简洁清晰
            - 直接回答核心问题
            - 避免冗余信息
            - 使用简短的段落或列表
            - 重要数字和规则要明确
            
            ### 3. 友好专业
            - 使用礼貌、专业的语气
            - 必要时提供具体示例
            - 可以适当补充相关提醒
            
            ## System Context
            <knowledge_base>
            {knowledge_list}
            </knowledge_base>
            """;
}
