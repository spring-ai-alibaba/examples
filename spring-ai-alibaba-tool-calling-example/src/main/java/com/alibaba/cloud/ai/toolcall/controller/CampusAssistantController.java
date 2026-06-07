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

import com.alibaba.cloud.ai.toolcall.component.CampusScheduleTools;
import com.alibaba.cloud.ai.toolcall.component.TimeTools;
import com.alibaba.cloud.ai.toolcalling.weather.WeatherService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campus")
public class CampusAssistantController {

    private final ChatClient dashScopeChatClient;

    private final TimeTools timeTools;

    private final CampusScheduleTools campusScheduleTools;

    private final WeatherService weatherService;

    public CampusAssistantController(ChatClient chatClient, TimeTools timeTools,
            CampusScheduleTools campusScheduleTools, WeatherService weatherService) {

        this.dashScopeChatClient = chatClient;
        this.timeTools = timeTools;
        this.campusScheduleTools = campusScheduleTools;
        this.weatherService = weatherService;
    }

    /**
     * Combine annotated method tools and a function callback in one request.
     */
    @GetMapping("/chat-tools")
    public String chatWithCampusTools(@RequestParam(value = "query",
            defaultValue = "请查询上海当前时间和天气，并为我安排一小时的校园跑步计划") String query) {

        return dashScopeChatClient.prompt(query)
                .tools(timeTools, campusScheduleTools)
                .toolCallbacks(FunctionToolCallback.builder("getWeather", weatherService)
                        .description("Use api.weather to get weather information.")
                        .inputType(WeatherService.Request.class)
                        .build())
                .call()
                .content();
    }

}
