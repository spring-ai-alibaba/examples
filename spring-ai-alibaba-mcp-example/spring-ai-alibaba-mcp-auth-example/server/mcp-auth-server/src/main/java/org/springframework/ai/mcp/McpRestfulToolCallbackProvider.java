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

package org.springframework.ai.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.util.Assert;

/**
 * @author yingzi
 * @since 2025/6/28
 */

public class McpRestfulToolCallbackProvider implements ToolCallbackProvider {

    private final ToolCallback[] toolCallbacks;

    private static final Logger logger = LoggerFactory.getLogger(McpRestfulToolCallbackProvider.class);

    public McpRestfulToolCallbackProvider(McpRestfulToolCallback... toolCallbacks) {
        Assert.notNull(toolCallbacks, "toolCallbacks cannot be null");
        this.toolCallbacks = toolCallbacks;
    }

    @Override
    public ToolCallback[] getToolCallbacks() {
        return this.toolCallbacks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private McpRestfulToolCallback[] toolCallbacks;

        private Builder() {
        }

        public Builder toolCallbacks(McpRestfulToolCallback... toolCallbacks) {
            this.toolCallbacks = toolCallbacks;
            return this;
        }

        public McpRestfulToolCallbackProvider build() {
            return new McpRestfulToolCallbackProvider(this.toolCallbacks);
        }
    }
}
