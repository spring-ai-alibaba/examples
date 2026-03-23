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

import com.cloud.alibaba.ai.example.agent.model.CalendarInfo;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.List;
import java.util.function.BiFunction;

/**
 * A tool for creating calendar events.
 * This class implements BiFunction to process CalendarInfo and ToolContext,
 * validating input data and simulating event creation.
 *
 * @author wangjx
 * @since 2026-02-13
 */
public class CreateCalendarEventTool implements BiFunction<CalendarInfo, ToolContext, String> {
    

    @Override
    public String apply(CalendarInfo calendarInfo, ToolContext toolContext) {
        // Parse parameters
        String title = calendarInfo.getTitle();
        String startTime = calendarInfo.getStartTime();
        String endTime = calendarInfo.getEndTime();
        List<String> attendees = calendarInfo.getAttendees();
        
        // Validate time format (simplified version)
        if (!isValidIsoDateTime(startTime) || !isValidIsoDateTime(endTime)) {
            return "Error: Invalid ISO datetime format";
        }
        
        // Simulate event creation
        return String.format("Event created: %s from %s to %s with %d attendees",
                title, startTime, endTime, attendees==null ? 0:attendees.size());
    }


    private boolean isValidIsoDateTime(String datetime) {
        // Simple validation of ISO 8601 format (should use stricter validation in production)
        return datetime != null && datetime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
    }
    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("create_calendar_event", this)
                .description("create_calendar_event")
                .inputType(CalendarInfo.class)
                .build();
    }
}
