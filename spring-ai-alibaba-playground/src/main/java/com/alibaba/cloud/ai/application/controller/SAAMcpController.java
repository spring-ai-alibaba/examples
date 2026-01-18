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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.application.entity.mcp.McpServer;
import com.alibaba.cloud.ai.application.entity.result.Result;
import com.alibaba.cloud.ai.application.entity.tools.ToolCallResp;
import com.alibaba.cloud.ai.application.exception.SAAAppException;
import com.alibaba.cloud.ai.application.mcp.McpServerContainer;
import com.alibaba.cloud.ai.application.service.SAAMcpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author brianxiadong
 */

@RestController
@Tag(name = "MCP APIs")
@RequestMapping("/api/v1")
public class SAAMcpController {

	private final SAAMcpService mcpService;

	public SAAMcpController(SAAMcpService webSearch) {
		this.mcpService = webSearch;
	}

	/**
	 * 内部接口不应该直接被 web 请求！
	 */
	@GetMapping("/inner/mcp")
	@Operation(summary = "DashScope MCP Chat")
	public Result<ToolCallResp> mcpChat(
			@Validated @RequestParam("prompt") String prompt
	) {

		return Result.success(mcpService.chat(prompt));
	}

	@GetMapping("/mcp-list")
	@Operation(summary = "MCP List")
	public Result<List<McpServer>> mcpList() {

		return Result.success(McpServerContainer.getAllServers());
	}

	@PostMapping("/mcp-run")
	@Operation(summary = "MCP Run")
	public Result<ToolCallResp> mcpRun(
			@Validated @RequestParam("id") String id,
			@Validated @RequestParam("prompt") String prompt,
			@RequestParam(value = "envs", required = false) String envs
	) {

		Map<String, String> env = new HashMap<>();
		if (StringUtils.hasText(envs)) {
			for (String entry : envs.split(",")) {
				String[] keyValue = entry.split("=");
				if (keyValue.length == 2) {
					env.put(keyValue[0], keyValue[1]);
				}
			}
		}

		try {
			return Result.success(mcpService.run(id, env, prompt));
		}
		catch (IOException e) {
			throw new SAAAppException(e.getMessage());
		}
	}
}

