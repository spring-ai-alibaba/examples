/*
 * Copyright 2024-2025 the original author or authors.
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
package com.alibaba.cloud.ai.toolcall.component;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class CampusScheduleTools {

    @Tool(description = "Create a concise campus activity schedule after considering the user's request.")
    public String createCampusSchedule(
            @ToolParam(description = "Campus activity or study goal.") String activity,
            @ToolParam(description = "Recommended start time, such as 14:00.") String startTime,
            @ToolParam(description = "Duration in minutes.") int durationMinutes) {

        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero.");
        }

        return "Campus schedule: activity=" + activity + ", startTime=" + startTime
                + ", durationMinutes=" + durationMinutes + ".";
    }

}
