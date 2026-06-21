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

package com.alibaba.cloud.ai.mcp.server.tool;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpComplete;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * @author yingzi
 * @since 2025/10/22
 */
@Service
public class TimeTool {

    private static final Logger logger = LoggerFactory.getLogger(TimeTool.class);

    @McpTool(name = "getCityTime", description = "Get the time of a specified city.")
    public String  getCityTimeMethod(@McpToolParam(description = "Time zone id, such as Asia/Shanghai", required = true) String timeZoneId) {
        logger.info("The current time zone is {}", timeZoneId);
        return String.format("The current time zone is %s and the current time is " + "%s", timeZoneId,
                getTimeByZoneId(timeZoneId));
    }

    @McpResource(
            uri = "time://zones",
            name = "time-zones",
            title = "Supported Time Zones",
            description = "Returns sample time zone ids that can be used with getCityTime",
            mimeType = "application/json")
    public ReadResourceResult supportedTimeZones() {
        String json = """
                {
                  "examples": ["Asia/Shanghai", "Asia/Tokyo", "Europe/London", "America/New_York"]
                }
                """;
        return new ReadResourceResult(List.of(new TextResourceContents("time://zones", "application/json", json)));
    }

    @McpPrompt(name = "time-report", title = "Time Report Prompt", description = "Build a prompt for reporting local time")
    public GetPromptResult timeReport(
            @McpArg(name = "timeZoneId", description = "Time zone id, such as Asia/Shanghai", required = true) String timeZoneId) {
        String prompt = """
                Report the current local time for this time zone in one concise sentence:
                %s
                """.formatted(timeZoneId);
        return new GetPromptResult("Time report prompt", List.of(new PromptMessage(Role.USER, new TextContent(prompt))));
    }

    @McpComplete(prompt = "time-report")
    public List<String> timeZoneComplete(String prefix) {
        String normalizedPrefix = prefix == null ? "" : prefix.trim().toLowerCase(Locale.ROOT);
        return ZoneId.getAvailableZoneIds().stream()
                .filter(zoneId -> zoneId.toLowerCase(Locale.ROOT).startsWith(normalizedPrefix))
                .sorted()
                .limit(10)
                .toList();
    }

    private String getTimeByZoneId(String zoneId) {

        // Get the time zone using ZoneId
        ZoneId zid = ZoneId.of(zoneId);

        // Get the current time in this time zone
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);

        // Defining a formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

        // Format ZonedDateTime as a string
        String formattedDateTime = zonedDateTime.format(formatter);

        return formattedDateTime;
    }
}
