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

import java.util.Map;
import java.util.Set;

import com.alibaba.cloud.ai.application.entity.result.Result;
import com.alibaba.cloud.ai.application.service.SAABaseService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@Tag(name = "Base APIs")
@RequestMapping("/api/v1")
public class SAABaseController {

	private final SAABaseService baseService;

	public SAABaseController(SAABaseService baseService) {
		this.baseService = baseService;
	}

	@GetMapping("/dashscope/getModels")
	public Result<Set<Map<String, String>>> getDashScopeModels() {

		Set<Map<String, String>> dashScope = baseService.getDashScope();

		if (dashScope.isEmpty()) {
			return Result.failed("No DashScope models found");
		}

		return Result.success(dashScope);
	}

	@GetMapping("/health")
	public Result<String> health() {

		return Result.success("Spring AI Alibaba Playground is running......");
	}

}
