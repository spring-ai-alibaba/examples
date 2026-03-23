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
package com.cloud.alibaba.ai.example.agent.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Utility class for handling date and time operations.
 * Provides functionality to retrieve the current date and time.
 *
 * @author wangjx
 * @since 2026-02-13
 */
public class DateTimeTools implements BiFunction<Map<String, Object>, ToolContext, String> {


    @Override
    public String apply(Map<String, Object> map, ToolContext toolContext) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH：mm：ss"));
    }

    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("get_current_date_time", this)
                .description("get_current_date_time")
                .inputType(Map.class)
                .build();
    }
}
