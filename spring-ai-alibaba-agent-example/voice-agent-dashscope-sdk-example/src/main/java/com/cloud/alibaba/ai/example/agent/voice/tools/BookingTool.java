/*
 * Copyright 2025-2026 the original author or authors.
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
package com.cloud.alibaba.ai.example.agent.voice.tools;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Component;

/**
 * Booking Tool.
 *
 * @author buvidk
 * @since 2026-02-03
 */
@Component
public class BookingTool implements Tool<BookingTool.Request, String> {

    private static final Logger log = LoggerFactory.getLogger(BookingTool.class);

    @Override
    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("booking_details", this)
                .description("Query flight booking details")
                .inputType(Request.class)
                .build();
    }

    @Override
    public String apply(Request request, ToolContext toolContext) {
        log.info("Checking booking: {}", request.bookingNumber);
        
        if ("12345".equals(request.bookingNumber)) {
            return """
                Booking: 12345
                Flight: CA123 (Shanghai SHA -> Beijing PEK)
                Date: 2024-05-01 10:00
                Status: Ticketed
                Passenger: John Doe
                """;
        } else if ("ERROR".equals(request.bookingNumber)) {
             return "Booking not found. Please verify the booking number.";
        } else {
             return "Found temporary booking " + request.bookingNumber + ", status pending.";
        }
    }

    @JsonClassDescription("Booking query request")
    public record Request(
            @JsonProperty(value = "booking_number", required = true)
            @JsonPropertyDescription("Booking number, e.g., 12345")
            String bookingNumber
    ) {}
}
