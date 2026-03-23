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

import com.cloud.alibaba.ai.example.agent.model.AvailableTimeInfo;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.List;
import java.util.function.BiFunction;

/**
 * A tool that retrieves available time slots for scheduling meetings.
 * This tool implements the BiFunction interface to process input parameters
 * and return formatted time slot information.
 *
 * @author wangjx
 * @since 2026-02-13
 */
public class AvailableTimeSlotsTool implements BiFunction<AvailableTimeInfo, ToolContext, String> {
    

    @Override
    public String apply(AvailableTimeInfo args, ToolContext toolContext) {
        // Parse input parameters
        String date =args.getDate();

        // Validate date format (simplified version)
        if (!isValidIsoDate(date)) {
            return "Error: Invalid ISO date format";
        }
        
        // Simulate querying available time slots
        List<String> timeSlots = List.of("09:00", "14:00", "16:00");
        return String.format("Available time slots for %s: %s", date, String.join(", ", timeSlots));
    }


    private boolean isValidIsoDate(String date) {
        // Simple validation of ISO 8601 date format
        return date != null && date.matches("\\d{4}-\\d{2}-\\d{2}");
    }


    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("get_available_time_slots", this)
                .description("get_available_time_slots")
                .inputType(AvailableTimeInfo.class)
                .build();
    }
}
