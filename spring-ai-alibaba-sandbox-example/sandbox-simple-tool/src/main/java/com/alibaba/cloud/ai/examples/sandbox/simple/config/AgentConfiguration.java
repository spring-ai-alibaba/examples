/*
 * Copyright 2024-2026 the original author or authors.
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
package com.alibaba.cloud.ai.examples.sandbox.simple.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.examples.sandbox.simple.tools.CalculatorTool;
import com.alibaba.cloud.ai.examples.sandbox.simple.tools.WeatherTool;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.sandbox.ToolkitInit;
import io.agentscope.runtime.sandbox.box.BaseSandbox;
import io.agentscope.runtime.sandbox.box.BrowserSandbox;
import io.agentscope.runtime.sandbox.manager.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent Configuration
 *
 * Configures the ChatModel and creates ReactAgent instances with integrated tools.
 */
@Configuration
public class AgentConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AgentConfiguration.class);

    @Autowired
    private Environment environment;

    @Value("${spring.ai.dashscope.chat.options.model:qwen-max}")
    private String modelName;

    /**
     * Configure DashScope ChatModel
     */
    @Bean
    public ChatModel chatModel() {
        String apiKey = resolveApiKey();
        logger.info("Initializing DashScope ChatModel with model: {}", modelName);
        logger.info("DashScope API key fingerprint: {}", maskApiKey(apiKey));
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException(
                    "DASHSCOPE API Key is not configured. " +
                            "Please set spring.ai.dashscope.api-key (or spring.ai.dashscope.apiKey) " +
                            "in application.yml, or AI_DASHSCOPE_API_KEY environment variable."
            );
        }

        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();

        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(modelName)
                        .build())
                .build();
    }

    private String resolveApiKey() {
        String key = environment.getProperty("spring.ai.dashscope.api-key");
        if (isNotBlank(key)) {
            return key.trim();
        }
        key = environment.getProperty("spring.ai.dashscope.apiKey");
        if (isNotBlank(key)) {
            return key.trim();
        }
        key = environment.getProperty("AI_DASHSCOPE_API_KEY");
        if (isNotBlank(key)) {
            return key.trim();
        }
        return null;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String maskApiKey(String key) {
        if (key == null) {
            return "null";
        }
        String trimmed = key.trim();
        if (trimmed.isEmpty()) {
            return "empty";
        }
        int len = trimmed.length();
        if (len <= 8) {
            return "len=" + len + ", value=****";
        }
        return "len=" + len + ", value=" + trimmed.substring(0, 4) + "..." + trimmed.substring(len - 4);
    }

    /**
     * Create Calculator Tool
     */
    @Bean
    public CalculatorTool calculatorTool() {
        return new CalculatorTool();
    }

    /**
     * Create Weather Tool
     */
    @Bean
    public WeatherTool weatherTool() {
        return new WeatherTool();
    }

    /**
     * Create ReactAgent instance with all tools
     *
     * Using prototype scope to create a new agent instance for each request,
     * avoiding shared state issues.
     */
    @Bean
    @Scope("prototype")
    public ReactAgent createReactAgent(
            ChatModel chatModel,
            SandboxService sandboxService,
            CalculatorTool calculatorTool,
            WeatherTool weatherTool) {

        logger.debug("Creating new ReactAgent instance");

        // Collect all tools
        List<ToolCallback> tools = new ArrayList<>();

        // Add custom tools
        tools.add(calculatorTool.addTool());
        tools.add(calculatorTool.subtractTool());
        tools.add(calculatorTool.multiplyTool());
        tools.add(calculatorTool.divideTool());
        tools.add(weatherTool.getWeatherTool());

        // Add sandbox tools if sandbox service is available
        if (sandboxService != null) {
            try {
                // Create BaseSandbox for Python/Shell execution
                BaseSandbox baseSandbox = new BaseSandbox(
                        sandboxService,
                        "default-user",
                        "session-" + System.currentTimeMillis()
                );

                tools.add(ToolkitInit.RunPythonCodeTool(baseSandbox));
                tools.add(ToolkitInit.RunShellCommandTool(baseSandbox));
                logger.debug("Added BaseSandbox tools (Python, Shell)");

                // Create BrowserSandbox for browser automation
                BrowserSandbox browserSandbox = new BrowserSandbox(
                        sandboxService,
                        "default-user",
                        "session-" + System.currentTimeMillis()
                );

                tools.add(ToolkitInit.BrowserNavigateTool(browserSandbox));

                // Log browser desktop URL for debugging
                String desktopUrl = browserSandbox.getDesktopUrl();
                if (desktopUrl != null && !desktopUrl.isEmpty()) {
                    logger.info("Browser Desktop URL: {}", desktopUrl);
                }

                logger.debug("Added BrowserSandbox tools");
            } catch (Exception e) {
                logger.warn("Failed to initialize sandbox tools: {}", e.getMessage());
                // Continue without sandbox tools
            }
        }

        // Build ReactAgent
        return ReactAgent.builder()
                .name("SmartAssistant")
                .model(chatModel)
                .description("An intelligent assistant that can perform calculations, " +
                        "query weather, execute Python code, run shell commands, " +
                        "and automate browser operations.")
                .instruction("""
                        You are a helpful and intelligent assistant named SmartAssistant.
                        You have access to multiple tools:
                        1. Calculator tools - for mathematical operations
                        2. Weather tool - for querying weather information
                        3. Python runner - for executing Python code in a sandbox
                        4. Shell runner - for executing shell commands in a sandbox
                        5. Browser navigator - for web browsing and data extraction
                        When the user asks a question:
                        - Analyze the request carefully
                        - Choose the most appropriate tool(s) to use
                        - Execute the tools and interpret the results
                        - Provide a clear and helpful response
                        Always be professional, accurate, and helpful.
                        """)
                .tools(tools)
                .build();
    }

}
