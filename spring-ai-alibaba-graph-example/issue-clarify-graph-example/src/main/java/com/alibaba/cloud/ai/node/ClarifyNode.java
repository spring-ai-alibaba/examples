package com.alibaba.cloud.ai.node;

import com.alibaba.cloud.ai.domain.Result;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ClarifyNode implements NodeAction {
    private final ChatModel chatModel;
    private final Resource systemResource;
    private final String inputKey;
    private final String outputKey;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String userQuery = state.value(inputKey, "");
        String nowDate = state.value("nowDate", "2025-12-14");
        Result preSlot = state.value(outputKey, Result.empty());

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder().build())
                .defaultSystem(systemResource)
                .build();

        Result slotParams = chatClient.prompt()
                .system(s -> s.params(Map.of(
                        "nowDate", nowDate,
                        "slot_params", preSlot,
                        "user_query", userQuery
                )))
                .call()
                .entity(Result.class);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put(outputKey, slotParams);
        return result;
    }
}
