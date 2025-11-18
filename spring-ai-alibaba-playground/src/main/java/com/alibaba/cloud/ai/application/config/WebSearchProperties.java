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

package com.alibaba.cloud.ai.application.config;

import com.alibaba.cloud.ai.application.enums.WebSearchEnum;
import com.alibaba.cloud.ai.application.modulerag.IQSSearchProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 联网搜索配置
 *
 * @author xuguan
 * @since 2025/10/31
 */
@ConfigurationProperties(prefix = WebSearchProperties.WEB_SEARCH_PREFIX)
public record WebSearchProperties(WebSearchEnum type,
								  @NestedConfigurationProperty IQSSearchProperties iqs) {
	public static final String WEB_SEARCH_PREFIX = "spring.ai.alibaba.playground.web-search";
}
