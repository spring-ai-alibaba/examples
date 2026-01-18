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

package com.alibaba.cloud.ai.application.controller;

import com.alibaba.cloud.ai.application.entity.result.Result;
import com.alibaba.cloud.ai.application.entity.tools.ToolCallResp;
import com.alibaba.cloud.ai.application.service.SAAToolsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@Tag(name = "Tool Calling APIs")
@RequestMapping("/api/v1")
public class SAAToolsController {

	private final SAAToolsService functionService;

	public SAAToolsController(SAAToolsService functionService) {
		this.functionService = functionService;
    }

	/**
	 * http://127.0.0.1:8080/api/v1/tool-call?prompt="使用百度翻译将隐私计算翻译为英文"
	 * 触发百度翻译：使用百度翻译将隐私计算翻译为英文
	 * 触发百度地图：使用百度地图查找杭州市的银行 ATM 机信息 or 使用百度地图查找杭州的信息
	 */
	@GetMapping("/tool-call")
	@Operation(summary = "DashScope ToolCall Chat")
	public Result<ToolCallResp> toolCallChat(
			@Validated @RequestParam("prompt") String prompt
	) {

		return Result.success(functionService.chat(prompt));
	}

}
