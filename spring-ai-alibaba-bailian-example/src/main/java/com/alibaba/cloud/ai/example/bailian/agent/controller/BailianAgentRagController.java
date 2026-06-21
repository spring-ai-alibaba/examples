/*
* Copyright 2024 the original author or authors.
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

package com.alibaba.cloud.ai.example.bailian.agent.controller;

import java.util.List;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo controller for integrating with Alibaba Cloud RAG service published on Bailian platform.
 */
@RestController
@RequestMapping("/ai")
public class BailianAgentRagController {

	private static final Logger logger = LoggerFactory.getLogger(BailianAgentRagController.class);

	private DashScopeAgent agent;

	@Value("${spring.ai.dashscope.agent.app-id}")
	private String appId;

	public BailianAgentRagController(DashScopeAgentApi dashscopeAgentApi) {
		this.agent = new DashScopeAgent(dashscopeAgentApi, DashScopeAgentOptions.builder().build());
	}

	@GetMapping("/bailian/agent/call")
	public String call(@RequestParam(value = "message",
			defaultValue = "如何使用SDK快速调用阿里云百炼的应用?") String message) {
		ChatResponse response = agent.call(new Prompt(message, DashScopeAgentOptions.builder().appId(appId).build()));
		if (response == null || response.getResult() == null) {
			logger.error("chat response is null");
			return "chat response is null";
		}

		AssistantMessage app_output = response.getResult().getOutput();
		String content = app_output.getText();

		DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput output = (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput) app_output.getMetadata().get("output");
		List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference> docReferences = output.docReferences();
		List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts> thoughts = output.thoughts();

		logger.info("content:\n{}\n\n", content);

		if (docReferences != null && !docReferences.isEmpty()) {
			for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference docReference : docReferences) {
				logger.info("{}\n\n", docReference);
			}
		}

		if (thoughts != null && !thoughts.isEmpty()) {
			for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts thought : thoughts) {
				logger.info("{}\n\n", thought);
			}
		}

		return content;
	}

}
