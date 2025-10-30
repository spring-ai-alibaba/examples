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

package com.alibaba.cloud.ai.mcp.server.model;

/**
 * @author yingzi
 * @date 2025/4/6:15:59
 */
public record Parameter(String parameteNname,
                        String description,
                        boolean required,
                        String type
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String parameteNname;
        private String description;
        private boolean required;
        private String type;

        public Builder parameteNname(String parameteNname) {
            this.parameteNname = parameteNname;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Parameter build() {
            return new Parameter(parameteNname, description, required, type);
        }

    }

}
