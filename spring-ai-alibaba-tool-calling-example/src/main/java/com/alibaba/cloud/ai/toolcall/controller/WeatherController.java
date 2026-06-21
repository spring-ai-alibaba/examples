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
package com.alibaba.cloud.ai.toolcall.controller;

import com.alibaba.cloud.ai.toolcalling.weather.WeatherService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final ChatClient dashScopeChatClient;

    private final ToolCallingAdvisor toolCallingAdvisor;

    private final WeatherService weatherService;

    public WeatherController(ChatClient chatClient, ToolCallingAdvisor toolCallingAdvisor, WeatherService weatherService) {

        this.dashScopeChatClient = chatClient;
        this.toolCallingAdvisor = toolCallingAdvisor;
        this.weatherService = weatherService;
    }

    /**
     * No Tool
     */
    @GetMapping("/chat")
    public String simpleChat(@RequestParam(value = "query", defaultValue = "请告诉我北京1天以后的天气") String query) {

        return dashScopeChatClient.prompt(query).call().content();
    }

    /**
     * Function as Tools - FunctionCallBack
     */
    @GetMapping("/chat-tool-function-name")
    public String chatWithWeatherFunction(@RequestParam(value = "query", defaultValue = "请告诉我北京1天以后的天气") String query) {

        ToolCallback weatherToolCallback = FunctionToolCallback.builder("getWeather", weatherService)
                .description("Use api.weather to get weather information.")
                .inputType(WeatherService.Request.class)
                .build();

        return dashScopeChatClient.prompt(query)
                .options(ToolCallingChatOptions.builder().toolCallbacks(List.of(weatherToolCallback)))
                .advisors(toolCallingAdvisor)
                .call()
                .content();
    }

}
