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
package com.cloud.alibaba.ai.example.agent.voice.config;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.cloud.alibaba.ai.example.agent.voice.tools.BookingTool;
import com.cloud.alibaba.ai.example.agent.voice.tools.FlightChangeTool;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Voice Agent Configuration
 *
 * @author buvidk
 * @since 2026-02-03
 */
@Configuration
public class VoiceAgentConfiguration {

    private final ChatModel chatModel;
    private final BookingTool bookingTool;
    private final FlightChangeTool flightChangeTool;

    public VoiceAgentConfiguration(ChatModel chatModel, 
                                 BookingTool bookingTool, 
                                 FlightChangeTool flightChangeTool) {
        this.chatModel = chatModel;
        this.bookingTool = bookingTool;
        this.flightChangeTool = flightChangeTool;
    }

    @Bean
    public ReactAgent voiceReactAgent() throws GraphStateException {
        return ReactAgent.builder()
            .name("voice-assistant")
            .description("""
                你是一个专业的航空公司语音助手。
                
                你的能力：
                1. 查询航班预订详情
                2. 更改航班日期
                
                重要输出规则：
                - 只用纯文本，不要用 Markdown、列表符号或表情符号
                - 保持回复简短，最多2-3句话
                - 用自然口语化的中文回复，像电话交流一样
                - 如果用户没有提供预订号，请礼貌地询问
                """)
            .model(chatModel)
            .saver(new MemorySaver())
            .tools(bookingTool.toolCallback(), flightChangeTool.toolCallback())
            .build();
    }
}
