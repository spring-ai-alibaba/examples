package com.cloud.alibaba.ai.example.interceptor;

import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallResponse;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogToolInterceptor extends ToolInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogToolInterceptor.class);

    @Override
    public ToolCallResponse interceptToolCall(ToolCallRequest request, ToolCallHandler handler) {
        log.info("ToolInterceptor: Tool {} is called!", request.getToolName());
        return handler.call(request);
    }

    @Override
    public String getName() {
        return "LogToolInterceptor";
    }
}
