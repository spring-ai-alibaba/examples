package com.alibaba.cloud.ai.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MCP Gateway 测试控制器
 * <p>
 * 提供以下功能：
 * 1. 列出从 Nacos 发现的所有 MCP 工具
 * 2. 通过 ChatClient 调用聚合的 MCP 工具
 * </p>
 *
 * @author wangx
 */
@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    private final ChatModel chatModel;

    private final ToolCallbackProvider toolCallbackProvider;
    private final static Logger log = LoggerFactory.getLogger(GatewayController.class);

    public GatewayController(ChatModel chatModel, ToolCallbackProvider toolCallbackProvider) {
        this.chatModel = chatModel;
        this.toolCallbackProvider = toolCallbackProvider;
    }

    /**
     * 列出所有从 Nacos 发现并聚合的 MCP 工具
     */
    @GetMapping("/tools")
    public List<Map<String, String>> listTools() {
        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
        return Arrays.stream(toolCallbacks)
                .map(tool -> Map.of(
                        "name", tool.getToolDefinition().name(),
                        "description", tool.getToolDefinition().description()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 使用 AI 模型调用 MCP 工具
     * <p>
     * 示例请求：
     * - /api/gateway/chat?message=现在几点了
     * - /api/gateway/chat?message=杭州的天气怎么样
     * </p>
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        log.info("收到用户消息: {}", message);

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        log.info("AI 响应: {}", response);
        return response;
    }
}
