/*
 * Copyright 2024-2026 the original author or authors.
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

package com.alibaba.cloud.ai.application.service;

import com.alibaba.cloud.ai.application.entity.dashscope.ChatResponseDTO;
import com.alibaba.cloud.ai.application.enums.WebSearchEnum;
import reactor.core.publisher.Flux;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * AI 联网搜索服务：提供两种实现
 * 1. 基于 DashScope 的 Web Search；
 * 2. 基于 Spring AI Module RAG 实现。
 */

public interface ISAAWebSearchService {

    WebSearchEnum type();

    Flux<ChatResponseDTO> chat(String prompt);

}
