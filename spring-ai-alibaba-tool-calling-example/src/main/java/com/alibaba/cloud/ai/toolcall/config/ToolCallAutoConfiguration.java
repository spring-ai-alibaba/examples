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
package com.alibaba.cloud.ai.toolcall.config;

import com.alibaba.cloud.ai.toolcall.component.AddressInformationTools;
import com.alibaba.cloud.ai.toolcall.component.CampusScheduleTools;
import com.alibaba.cloud.ai.toolcall.component.TimeTools;
import com.alibaba.cloud.ai.toolcalling.baidumap.BaiduMapSearchInfoService;
import com.alibaba.cloud.ai.toolcalling.time.GetTimeByZoneIdService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(GetTimeByZoneIdService.class)
public class ToolCallAutoConfiguration {

    @Bean
    public GetTimeByZoneIdService getCurrentTimeByTimeZoneIdService() {
        return new GetTimeByZoneIdService();
    }

    @Bean
    public TimeTools timeTools(GetTimeByZoneIdService service) {
        return new TimeTools(service);
    }

    @Bean
    public CampusScheduleTools campusScheduleTools() {
        return new CampusScheduleTools();
    }

    @Bean
    public AddressInformationTools addressInformationTools(BaiduMapSearchInfoService service) {
        return new AddressInformationTools(service);
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

}
