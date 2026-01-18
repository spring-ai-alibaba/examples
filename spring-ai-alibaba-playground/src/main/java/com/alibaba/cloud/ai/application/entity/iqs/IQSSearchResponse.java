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

package com.alibaba.cloud.ai.application.entity.iqs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public record IQSSearchResponse(
        @JsonProperty("requestId") String requestId,
        @JsonProperty("pageItems") List<PageItem> pageItems,
        @JsonProperty("sceneItems") List<Object> sceneItems, // Assuming sceneItems can be of any type
        @JsonProperty("searchInformation") SearchInformation searchInformation,
        @JsonProperty("queryContext") QueryContext queryContext,
        @JsonProperty("costCredits") CostCredits costCredits
) {

    public record PageItem(
            @JsonProperty("title") String title,
            @JsonProperty("link") String link,
            @JsonProperty("snippet") String snippet,
            @JsonProperty("publishedTime") String publishedTime,
            @JsonProperty("mainText") String mainText,
            @JsonProperty("markdownText") String markdownText,
            @JsonProperty("images") List<String> images,
            @JsonProperty("hostname") String hostname,
            @JsonProperty("hostLogo") String hostLogo,
            @JsonProperty("summary") String summary,
            @JsonProperty("rerankScore") double rerankScore
    ) {
    }

    public record SearchInformation(
            @JsonProperty("searchTime") int searchTime
    ) {
    }

    public record QueryContext(
            @JsonProperty("engineType") String engineType,
            @JsonProperty("originalQuery") OriginalQuery originalQuery,
            @JsonProperty("rewrite") Rewrite rewrite
    ) {
    }

    public record OriginalQuery(
            @JsonProperty("query") String query,
            @JsonProperty("timeRange") String timeRange
    ) {
    }

    public record Rewrite(
            @JsonProperty("enabled") boolean enabled,
            @JsonProperty("timeRange") Object timeRange // Assuming timeRange can be of any type
    ) {
    }

    public record CostCredits(
            @JsonProperty("search") Search search,
            @JsonProperty("valueAdded") ValueAdded valueAdded
    ) {
    }

    public record Search(
            @JsonProperty("genericTextSearch") int genericTextSearch
    ) {
    }

    public record ValueAdded(
            @JsonProperty("summary") int summary,
            @JsonProperty("advanced") int advanced
    ) {
    }

}
