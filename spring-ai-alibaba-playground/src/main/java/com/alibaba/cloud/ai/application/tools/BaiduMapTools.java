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

package com.alibaba.cloud.ai.application.tools;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ai.chat.model.ToolContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class BaiduMapTools implements BiFunction<BaiduMapTools.BaiduMapToolRequest, ToolContext, BaiduMapTools.BaiduMapToolResponse> {

	private final String ak;

	private final HttpClient httpClient;

	public BaiduMapTools(String ak) {

		this.ak = ak;
		this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();;
	}

	@Override
	public BaiduMapToolResponse apply(BaiduMapTools.BaiduMapToolRequest baiduMapToolRequest, ToolContext toolContext) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			ObjectNode jsonObject = objectMapper.createObjectNode();
			String addressCityCodeResponse = getAddressCityCode(baiduMapToolRequest.input.address);
			JsonNode cityCodeJson = objectMapper.readTree(addressCityCodeResponse);
			JsonNode districtsArray = cityCodeJson.path("districts");

			if (districtsArray.isEmpty()) {
				return new BaiduMapToolResponse(new Response("No districts found in the response."));
			} else {
				for (JsonNode district : districtsArray) {
					String AdCode = district.path("adcode").asText();
					if (!AdCode.isEmpty()) {
						String weather = getWeather(AdCode);
						jsonObject.put("weather", weather);
					}
				}

				String facilityJsonStr = getFacilityInformation(baiduMapToolRequest.input.address, baiduMapToolRequest.input.facilityType);
				JsonNode facilityJson = objectMapper.readTree(facilityJsonStr);
				JsonNode resultsArray = facilityJson.path("results");

				if (!resultsArray.isEmpty()) {
					jsonObject.set("facilityInformation", resultsArray);
				} else {
					jsonObject.put("facilityInformation", "No facility information found.");
				}

				return new BaiduMapToolResponse(new Response(objectMapper.writeValueAsString(jsonObject)));
			}
		} catch (Exception e) {
			return new BaiduMapToolResponse(new Response("Error occurred while processing the request: " + e.getMessage()));
		}
	}

	public String getAddressCityCode(String address) {

		String path = String.format("/api_region_search/v1/?ak=%s&keyword=%s&sub_admin=0&extensions_code=1", ak, address);
		HttpRequest httpRequest = this.createGetRequest(path);
		CompletableFuture<HttpResponse<String>> responseFuture = this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
		HttpResponse<String> response = responseFuture.join();
		if (response.statusCode() != 200) {
			throw new RuntimeException("Failed to get address city code");
		}
		else {
			return response.body();
		}
	}

	public String getWeather(String cityCode) {
		String path = String.format("/weather/v1/?ak=%s&district_id=%s&data_type=%s", ak, cityCode, "all");
		HttpRequest httpRequest = this.createGetRequest(path);
		CompletableFuture<HttpResponse<String>> responseFuture = this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
		HttpResponse<String> response = responseFuture.join();
		if (response.statusCode() != 200) {
			throw new RuntimeException("Failed to get weather information");
		}
		else {
			return response.body();
		}
	}

	public String getFacilityInformation(String address, String facilityType) {
		String path = String.format("/place/v2/search?query=%s&region=%s&output=json&ak=%s", facilityType, address, ak);
		HttpRequest httpRequest = this.createGetRequest(path);
		CompletableFuture<HttpResponse<String>> responseFuture = this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
		HttpResponse<String> response = responseFuture.join();
		if (response.statusCode() != 200) {
			throw new RuntimeException("Failed to get facility information");
		}
		else {
			return response.body();
		}
	}

	private HttpRequest createGetRequest(String path) {
		URI uri = URI.create("https://api.map.baidu.com" + path);
		return HttpRequest.newBuilder().uri(uri).GET().build();
	}

	// 模型的构造参数为：{"Request": {"address": "杭州西溪园区", "facilityType": "科技园区"}}
	public record BaiduMapToolRequest(@JsonProperty("Request") BaiduMapTools.Request input) {
		public BaiduMapToolRequest(BaiduMapTools.Request input) {
			this.input = input;
		}
	}

	public record BaiduMapToolResponse(@JsonProperty("Response") BaiduMapTools.Response output) {
		public BaiduMapToolResponse(BaiduMapTools.Response output) {
			this.output = output;
		}
	}

	@JsonClassDescription("Get the weather conditions for a specified address and facility type.")
	public record Request(String address, String facilityType) {
		public Request(@JsonProperty(required = true, value = "address") @JsonPropertyDescription("The address") String address, @JsonProperty(required = true, value = "facilityType") @JsonPropertyDescription("The type of facility (e.g., bank, airport, restaurant)") String facilityType) {
			this.address = address;
			this.facilityType = facilityType;
		}

		@JsonProperty(
				required = true,
				value = "address"
		)
		@JsonPropertyDescription("The address")
		public String address() {
			return this.address;
		}

		// {"Request": {"address": "杭州西溪园区", "facilityType": ""}}
		// 可能 facilityType 为空
		@JsonProperty(
				required = false,
				value = "facilityType"
		)
		@JsonPropertyDescription("The type of facility (e.g., bank, airport, restaurant)")
		public String facilityType() {
			return this.facilityType;
		}
	}

	public record Response(String message) {
		public Response(String message) {
			this.message = message;
		}

		public String message() {
			return this.message;
		}
	}

}
