/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.application.entity.iqs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UnifiedSearch统一搜索请求参数
 * <a href="https://help.aliyun.com/document_detail/2883041.html">UnifiedSearch - 统一搜索API</a>
 *
 * @author xuguan
 */
public record IQSSearchRequest(
        @JsonProperty("query") String query,
        @JsonProperty("timeRange") String timeRange,
        @JsonProperty("category") String category,
        @JsonProperty("engineType") String engineType,
        @JsonProperty("location") String location,
        @JsonProperty("contents") Contents contents
) {
    public record Contents(
            @JsonProperty("mainText") Boolean mainText,
            @JsonProperty("markdownText") Boolean markdownText,
            @JsonProperty("richMainBody") Boolean richMainBody,
            @JsonProperty("summary") Boolean summary,
            @JsonProperty("rerankScore") Boolean rerankScore
    ) {
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Boolean mainText;
            private Boolean markdownText;
            private Boolean richMainBody;
            private Boolean summary;
            private Boolean rerankScore;

            public Builder mainText(Boolean mainText) {
                this.mainText = mainText;
                return this;
            }

            public Builder markdownText(Boolean markdownText) {
                this.markdownText = markdownText;
                return this;
            }

            public Builder richMainBody(Boolean richMainBody) {
                this.richMainBody = richMainBody;
                return this;
            }

            public Builder summary(Boolean summary) {
                this.summary = summary;
                return this;
            }

            public Builder rerankScore(Boolean rerankScore) {
                this.rerankScore = rerankScore;
                return this;
            }

            public Contents build() {
                return new Contents(mainText, markdownText, richMainBody, summary, rerankScore);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String query;
        private String timeRange;
        private String category;
        private String engineType;
        private String location;
        private Contents contents;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder timeRange(String timeRange) {
            this.timeRange = timeRange;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder engineType(String engineType) {
            this.engineType = engineType;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder contents(Contents contents) {
            this.contents = contents;
            return this;
        }

        public IQSSearchRequest build() {
            return new IQSSearchRequest(query, timeRange, category, engineType, location, contents);
        }
    }
}



