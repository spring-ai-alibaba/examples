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
package com.alibaba.cloud.ai.examples.sandbox.simple.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.Function;

/**
 * Weather Tool
 *
 * Simulates weather information queries.
 * In a real application, this would call an actual weather API.
 */
@Component
public class WeatherTool {

    private static final Logger logger = LoggerFactory.getLogger(WeatherTool.class);
    private final Random random = new Random();

    /**
     * Get weather information for a city
     */
    public ToolCallback getWeatherTool() {
        return FunctionToolCallback.builder("getWeather", (Function<WeatherRequest, String>) request -> {
            logger.info("Weather query for city: {}", request.city());
            // Simulate weather data
            String[] conditions = {"Sunny", "Cloudy", "Rainy", "Partly Cloudy"};
            String condition = conditions[random.nextInt(conditions.length)];
            int temperature = 15 + random.nextInt(20); // 15-35°C
            int humidity = 40 + random.nextInt(40); // 40-80%
            return String.format(
                    "Weather in %s: %s, Temperature: %d°C, Humidity: %d%%",
                    request.city(), condition, temperature, humidity
            );
        })
                .description("Get weather information for a city")
                .inputType(WeatherRequest.class)
                .build();
    }

    public record WeatherRequest(String city) {}

}
