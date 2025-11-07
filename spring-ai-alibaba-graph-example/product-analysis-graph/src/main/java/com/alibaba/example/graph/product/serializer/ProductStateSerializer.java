/*
 * Copyright 2025 the original author or authors.
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

package com.alibaba.example.graph.product.serializer;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.serializer.plain_text.PlainTextStateSerializer;
import com.alibaba.cloud.ai.graph.state.AgentStateFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

// Custom StateSerializer for Product object serialization with type information
public class ProductStateSerializer extends PlainTextStateSerializer {

    private final ObjectMapper mapper;

    public ProductStateSerializer(AgentStateFactory<OverAllState> stateFactory) {
        super(stateFactory);
        this.mapper = new ObjectMapper();
        // Enable default typing to handle custom objects like Product
        this.mapper.activateDefaultTyping(
                this.mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        // Exclude null values from serialization
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void writeData(Map<String, Object> data, ObjectOutput out) throws IOException {
        String json = mapper.writeValueAsString(data);
        out.writeUTF(json);
    }

    @Override
    public Map<String, Object> readData(ObjectInput in) throws IOException {
        String json = in.readUTF();
        return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public OverAllState cloneObject(OverAllState state) throws IOException {
        String json = mapper.writeValueAsString(state.data());
        Map<String, Object> rawMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        return stateFactory().apply(rawMap);
    }
}
