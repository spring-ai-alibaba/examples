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

package com.alibaba.cloud.ai.example.rag.knowledge.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@AutoConfiguration
public class BailianAutoconfiguration {

	/**
	 * 百炼调用时需要配置 DashScope API，对 dashScopeApi 强依赖。
	 * @return
	 */
	@Bean
	public DashScopeApi dashScopeApi() {
		
		return DashScopeApi.builder().apiKey(System.getenv("${AI_DASHSCOPE_API_KET}")).build();
	}

}
