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

package com.alibaba.cloud.ai.example.chat.dashscope.controller;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * 阿里云 DashScope 模型文档：https://help.aliyun.com/zh/model-studio/qwen-api-via-dashscope
 *
 * 调用 url 的区别：
 *      纯文本模型（如qwen-plus）：POST https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation
 *      多模态模型（如qwen3.5-plus或qwen3-vl-plus）POST https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation
 *
 * qwen3.5-plus 需要用 incrementalOutput 参数设为 true。而 incrementalOutput 只有在 stream 下设置才能生效
 *      incremental_output boolean （可选）默认为false（Qwen3-Max、Qwen3-VL、Qwen3 开源版、QwQ 、QVQ模型默认值为 true）
 *      在流式输出模式下是否开启增量输出。推荐您优先设置为true。
 */

@RestController
@RequestMapping("/other-model")
public class OtherModelCallController {

    @GetMapping("/stream")
    public Flux<String> call() {

        return getChatModel().stream("hi");
    }

    private ChatModel getChatModel() {

        return DashScopeChatModel.builder()
                .defaultOptions(DashScopeChatOptions.builder()
                        // 设置此参数为 false 时：报错
                        // [InvalidParameter] <400> InternalError.Algo.InvalidParameter: This model only supports incremental_output set to True. (requestId: xxx)
                        // Tips：SAA 框架在 stream 调用下，此参数默认为 true，call 调用时为 false
                        // .incrementalOutput(false)
                        // 不设置此参数时：报错
                        // [InvalidParameter] url error, please check url！ For details, see: https://help.aliyun.com/zh/model-studio/error-code#error-url (requestId: xxx)
                        .multiModel(true)
                        .model("qwen3.5-plus")
                        .build())
                .dashScopeApi(
                        DashScopeApi.builder()
                                .apiKey("sk-xxx")
                                .build()
                )
                .build();
    }

}
