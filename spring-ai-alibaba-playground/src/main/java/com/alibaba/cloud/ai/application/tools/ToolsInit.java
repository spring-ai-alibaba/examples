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

package com.alibaba.cloud.ai.application.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Component
public class ToolsInit {

	@Value("${spring.ai.alibaba.playground.tool-calling.baidu.translate.ak}")
	private String ak;

	@Value("${spring.ai.alibaba.playground.tool-calling.baidu.translate.sk}")
	private String sk;

	@Value("${spring.ai.alibaba.playground.tool-calling.baidu.map.ak}")
	private String mapAK;

	private final RestClient.Builder restClientbuilder;

	private final ResponseErrorHandler responseErrorHandler;

	public ToolsInit(RestClient.Builder restClientbuilder, ResponseErrorHandler responseErrorHandler) {

		this.restClientbuilder = restClientbuilder;
		this.responseErrorHandler = responseErrorHandler;
	}

	public List<ToolCallback> getTools() {

		return List.of(buildBaiduTranslateTools(), buildBaiduMapTools());
	}

	private ToolCallback buildBaiduTranslateTools() {

		return FunctionToolCallback
				.builder(
						"BaiduTranslateService",
						new BaiduTranslateTools(ak, sk, restClientbuilder, responseErrorHandler)
				).description("Baidu translation function for general text translation.")
				.inputSchema(
						"""
								{
									"type": "object",
									"properties": {
										"Request": {
											"type": "object",
											"properties": {
												"q": {
													"type": "string",
													"description": "Content that needs to be translated."
												},
												"from": {
													"type": "string",
													"description": "Source language that needs to be translated."
												},
												"to": {
													"type": "string",
													"description": "Target language to translate into."
												}
											},
											"required": ["q", "from", "to"],
											"description": "Request object to translate text to a target language."
										},
										"Response": {
											"type": "object",
											"properties": {
												"translatedText": {
													"type": "string",
													"description": "The translated text."
												}
											},
											"required": ["translatedText"],
											"description": "Response object for the translation function, containing the translated text."
										}
									},
									"required": ["Request", "Response"]
								}
								"""
				).inputType(BaiduTranslateTools.BaiduTranslateToolRequest.class)
				.toolMetadata(ToolMetadata.builder().returnDirect(false).build())
				.build();
	}

	private ToolCallback buildBaiduMapTools() {

		return FunctionToolCallback.builder(
						"BaiduMapSearchService",
						new BaiduMapTools(mapAK)
				).description("Search for places using Baidu Maps API or "
						+ "Get detail information of a address and facility query with baidu map or "
						+ "Get address information of a place with baidu map or "
						+ "Get detailed information about a specific place with baidu map"
				).inputSchema(
						"""
								{
									"type": "object",
									"properties": {
										"Request": {
											"type": "object",
											"properties": {
												"address": {
													"type": "string",
													"description": "The address."
												},
												"facilityType": {
													"type": "string",
													"description": "The type of facility (e.g., bank, airport, restaurant)."
												}
											},
											"required": ["address", "facilityType"],
											"description": "Request object to get the weather conditions for a specified address and facility type."
										},
										"Response": {
											"type": "object",
											"properties": {
												"weather": {
													"type": "string",
													"description": "Current weather conditions at the specified location."
												},
												"temperature": {
													"type": "number",
													"description": "Current temperature at the specified location."
												}
											},
											"required": ["weather", "temperature"],
											"description": "Response object for the weather conditions request, containing the weather and temperature."
										}
									},
									"required": ["Request", "Response"]
								}
								"""
				).inputType(BaiduMapTools.BaiduMapToolRequest.class)
				.toolMetadata(ToolMetadata.builder().returnDirect(false).build())
				.build();
	}

}
