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

import java.util.Locale;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class CampusScheduleTools {

    @Tool(description = "Create a campus activity schedule with preparation, main activity, and wrap-up steps.")
    public String createCampusSchedule(
            @ToolParam(description = "Campus activity or study goal.") String activity,
            @ToolParam(description = "Recommended start time, such as 14:00.") String startTime,
            @ToolParam(description = "Duration in minutes.") int durationMinutes) {

        requireText(activity, "Activity");
        requireText(startTime, "Start time");
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero.");
        }

        int preparationMinutes = Math.min(10, Math.max(5, durationMinutes / 6));
        int wrapUpMinutes = Math.min(10, Math.max(5, durationMinutes / 8));
        int mainActivityMinutes = Math.max(1, durationMinutes - preparationMinutes - wrapUpMinutes);

        return "Campus schedule plan: activity=" + activity + ", startTime=" + startTime
                + ", durationMinutes=" + durationMinutes
                + ". Suggested flow: preparation=" + preparationMinutes + " minutes, mainActivity="
                + mainActivityMinutes + " minutes, wrapUp=" + wrapUpMinutes
                + " minutes. Reminder: check weather and campus safety rules before outdoor activities.";
    }

    @Tool(description = "Estimate whether a campus outdoor activity should continue based on weather and duration.")
    public String estimateCampusActivityRisk(
            @ToolParam(description = "Weather summary returned by a weather tool.") String weatherSummary,
            @ToolParam(description = "Campus activity or study goal.") String activity,
            @ToolParam(description = "Duration in minutes.") int durationMinutes) {

        requireText(weatherSummary, "Weather summary");
        requireText(activity, "Activity");
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero.");
        }

        String normalizedWeather = weatherSummary.toLowerCase(Locale.ROOT);
        String riskLevel = "LOW";
        String suggestion = "Activity can continue as planned.";

        if (containsAny(normalizedWeather, "storm", "thunder", "snow", "hail", "暴雨", "雷", "雪", "冰雹")) {
            riskLevel = "HIGH";
            suggestion = "Move the activity indoors or postpone it.";
        }
        else if (containsAny(normalizedWeather, "rain", "wind", "hot", "cold", "fog", "雨", "风", "高温", "低温", "雾")) {
            riskLevel = "MEDIUM";
            suggestion = "Shorten the activity, prepare protection, and choose a nearby indoor backup place.";
        }

        if (durationMinutes > 120 && !"HIGH".equals(riskLevel)) {
            riskLevel = "MEDIUM";
            suggestion = "Long activity duration detected. Add breaks and hydration reminders.";
        }

        return "Campus activity risk: activity=" + activity + ", durationMinutes=" + durationMinutes
                + ", riskLevel=" + riskLevel + ", suggestion=" + suggestion;
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

}
