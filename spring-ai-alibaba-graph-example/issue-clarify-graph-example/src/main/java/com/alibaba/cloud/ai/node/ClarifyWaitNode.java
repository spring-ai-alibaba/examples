package com.alibaba.cloud.ai.node;

import com.alibaba.cloud.ai.domain.Result;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.action.AsyncNodeActionWithConfig;
import com.alibaba.cloud.ai.graph.action.InterruptableAction;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ClarifyWaitNode implements AsyncNodeActionWithConfig, InterruptableAction {
    private final String inputKey;

    @Override
    public CompletableFuture<Map<String, Object>> apply(OverAllState state, RunnableConfig config) {
        config.context().remove(RunnableConfig.STATE_UPDATE_METADATA_KEY);
        return CompletableFuture.completedFuture(Collections.emptyMap());
    }

    @Override
    public Optional<InterruptionMetadata> interrupt(String nodeId, OverAllState state, RunnableConfig config) {
        // 获取是否是恢复会话
        boolean isResume = (boolean) config.context().getOrDefault(RunnableConfig.STATE_UPDATE_METADATA_KEY, false);
        Result slotParams = state.value(inputKey, Result.class).orElse(Result.empty());

        if (StringUtils.hasText(slotParams.reply())) {
            state.input(Map.of("reply", slotParams.reply()));
        }
        if (isResume || "2".equals(slotParams.status())) {
            return Optional.empty();
        }
        return Optional.of(InterruptionMetadata.builder(nodeId, state)
                .addMetadata("reply", slotParams.reply())
                .build());
    }
}
