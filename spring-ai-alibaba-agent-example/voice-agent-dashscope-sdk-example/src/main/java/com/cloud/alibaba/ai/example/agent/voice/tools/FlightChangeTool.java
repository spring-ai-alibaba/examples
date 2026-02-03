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
 * Flight Change Tool.
 *
 * @author buvidk
 * @since 2026-02-03
 */
@Component
public class FlightChangeTool implements Tool<FlightChangeTool.Request, String> {

    private static final Logger log = LoggerFactory.getLogger(FlightChangeTool.class);

    @Override
    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("change_flight", this)
                .description("Change flight booking date")
                .inputType(Request.class)
                .build();
    }

    @Override
    public String apply(Request request, ToolContext toolContext) {
        log.info("Changing flight: {} -> {}", request.bookingNumber, request.newDate);
        
        return String.format("Change successful!\nBooking: %s\nNew Flight: CA123\nNew Date: %s 10:00\nChange Fee: $0", 
            request.bookingNumber, request.newDate);
    }

    @JsonClassDescription("Flight change request")
    public record Request(
            @JsonProperty(value = "booking_number", required = true)
            @JsonPropertyDescription("Booking number")
            String bookingNumber,
            
            @JsonProperty(value = "new_date", required = true)
            @JsonPropertyDescription("New date in YYYY-MM-DD format")
            String newDate
    ) {}
}
