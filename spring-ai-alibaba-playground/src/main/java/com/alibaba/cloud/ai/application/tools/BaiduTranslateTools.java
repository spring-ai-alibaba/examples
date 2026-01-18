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

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

public class BaiduTranslateTools implements BiFunction<BaiduTranslateTools.BaiduTranslateToolRequest, ToolContext, BaiduTranslateTools.BaiduTranslateToolResponse> {

    private static final Logger logger = LoggerFactory.getLogger(BaiduTranslateTools.class);
    private final String appId;
    private final String secretKey;
    private final RestClient restClient;

    public BaiduTranslateTools(String appId, String secretKey, RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {
        this.appId = appId;
        this.secretKey = secretKey;
        this.restClient = restClientBuilder.baseUrl("https://fanyi-api.baidu.com/api/trans/vip/translate").defaultHeader("Content-Type", new String[]{"application/x-www-form-urlencoded"}).defaultStatusHandler(responseErrorHandler).build();
    }

    private MultiValueMap<String, String> constructRequestBody(Request request, String salt, String sign) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap();
        body.add("q", request.q);
        body.add("from", request.from);
        body.add("to", request.to);
        body.add("appid", this.appId);
        body.add("salt", salt);
        body.add("sign", sign);
        return body;
    }

    private BaiduTranslateToolResponse parseResponse(String responseData) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, String> translations = new HashMap();
            TranslationResponse responseList = (TranslationResponse)mapper.readValue(responseData, TranslationResponse.class);
            String to = responseList.to;
            List<TranslationResult> translationsList = responseList.trans_result;
            if (translationsList != null) {
                for(TranslationResult translation : translationsList) {
                    String translatedText = translation.dst;
                    translations.put(to, translatedText);
                    logger.debug("Translated text to {}: {}", to, translatedText);
                }
            }

            return new BaiduTranslateToolResponse(new Response(translations));
        } catch (Exception var11) {
            try {
                Map<String, String> responseList = (Map)mapper.readValue(responseData, mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
                logger.error("Translation exception, please inquire Baidu translation api documentation to info error_code:{}", responseList);
                return new BaiduTranslateToolResponse(new Response(responseList));
            } catch (Exception var10) {
                logger.error("Failed to parse json due to: {}", var10.getMessage());
                return null;
            }
        }
    }

    @Override
    public BaiduTranslateToolResponse apply(BaiduTranslateToolRequest baiduTranslateToolRequest, ToolContext toolContext) {
        Random random = new Random();
        if (baiduTranslateToolRequest.input != null && StringUtils.hasText(baiduTranslateToolRequest.input.q) && StringUtils.hasText(baiduTranslateToolRequest.input.from) && StringUtils.hasText(baiduTranslateToolRequest.input.to)) {
            String salt = String.valueOf(random.nextInt(100000));
            String sign = DigestUtils.md5DigestAsHex((this.appId + baiduTranslateToolRequest.input.q + salt + this.secretKey).getBytes());
            String url = UriComponentsBuilder.fromHttpUrl("https://fanyi-api.baidu.com/api/trans/vip/translate").toUriString();

            try {
                MultiValueMap<String, String> body = this.constructRequestBody(baiduTranslateToolRequest.input, salt, sign);
                String respData = this.restClient.post().uri(url, new Object[0]).body(body).retrieve().toEntity(String.class).getBody();
                return this.parseResponse(respData);
            } catch (Exception var7) {
                logger.error("Error occurred: {}", var7.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public record BaiduTranslateToolRequest(Request input) {
        @JsonProperty("Request")
        public Request input() {
            return this.input;
        }
    }

    public record BaiduTranslateToolResponse(Response output) {
        @JsonProperty("Response")
        public Response output() {
            return this.output;
        }
    }

    @JsonClassDescription("Request to translate text to a target language")
    public record Request(String q, String from, String to) {
        public Request(@JsonProperty(required = true,value = "q") @JsonPropertyDescription("Content that needs to be translated") String q, @JsonProperty(required = true,value = "from") @JsonPropertyDescription("Source language that needs to be translated") String from, @JsonProperty(required = true,value = "to") @JsonPropertyDescription("Target language to translate into") String to) {
            this.q = q;
            this.from = from;
            this.to = to;
        }

        @JsonProperty(
                required = true,
                value = "q"
        )
        @JsonPropertyDescription("Content that needs to be translated")
        public String q() {
            return this.q;
        }

        @JsonProperty(
                required = true,
                value = "from"
        )
        @JsonPropertyDescription("Source language that needs to be translated")
        public String from() {
            return this.from;
        }

        @JsonProperty(
                required = true,
                value = "to"
        )
        @JsonPropertyDescription("Target language to translate into")
        public String to() {
            return this.to;
        }
    }

    @JsonClassDescription("Response to translate text to a target language")
    public record Response(Map<String, String> translatedTexts) {
    }

    @JsonClassDescription("complete response")
    public record TranslationResponse(String from, String to, List<TranslationResult> trans_result) {
        public TranslationResponse(@JsonProperty(required = true,value = "from") @JsonPropertyDescription("Source language that needs to be translated") String from, @JsonProperty(required = true,value = "to") @JsonPropertyDescription("Target language to translate into") String to, @JsonProperty(required = true,value = "trans_result") @JsonPropertyDescription("part of the response") List<TranslationResult> trans_result) {
            this.from = from;
            this.to = to;
            this.trans_result = trans_result;
        }

        @JsonProperty(
                required = true,
                value = "from"
        )
        @JsonPropertyDescription("Source language that needs to be translated")
        public String from() {
            return this.from;
        }

        @JsonProperty(
                required = true,
                value = "to"
        )
        @JsonPropertyDescription("Target language to translate into")
        public String to() {
            return this.to;
        }

        @JsonProperty(
                required = true,
                value = "trans_result"
        )
        @JsonPropertyDescription("part of the response")
        public List<TranslationResult> trans_result() {
            return this.trans_result;
        }
    }

    @JsonClassDescription("part of the response")
    public record TranslationResult(String src, String dst) {
        public TranslationResult(@JsonProperty(required = true,value = "src") @JsonPropertyDescription("Original Content") String src, @JsonProperty(required = true,value = "dst") @JsonPropertyDescription("Final Result") String dst) {
            this.src = src;
            this.dst = dst;
        }

        @JsonProperty(
                required = true,
                value = "src"
        )
        @JsonPropertyDescription("Original Content")
        public String src() {
            return this.src;
        }

        @JsonProperty(
                required = true,
                value = "dst"
        )
        @JsonPropertyDescription("Final Result")
        public String dst() {
            return this.dst;
        }
    }
}
