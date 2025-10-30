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

package com.alibaba.cloud.ai.mcp.server.util;

import com.alibaba.cloud.ai.mcp.server.model.Parameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.github.victools.jsonschema.module.swagger2.Swagger2Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.ai.util.json.schema.SpringAiSchemaModule;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author yingzi
 * @date 2025/4/6:17:32
 */
public class JSONSchemaUtil {

    private static final SchemaGenerator SUBTYPE_SCHEMA_GENERATOR;
    private static final Logger logger = LoggerFactory.getLogger(JSONSchemaUtil.class);

    static {
        Module jacksonModule = new JacksonModule(new JacksonOption[]{JacksonOption.RESPECT_JSONPROPERTY_REQUIRED});
        Module openApiModule = new Swagger2Module();
        Module springAiSchemaModule = new SpringAiSchemaModule(new SpringAiSchemaModule.Option[0]);
        SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder = (new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)).with(jacksonModule).with(openApiModule).with(springAiSchemaModule).with(Option.EXTRA_OPEN_API_FORMAT_VALUES, new Option[0]).with(Option.PLAIN_DEFINITION_KEYS, new Option[0]);
        SchemaGeneratorConfig subtypeSchemaGeneratorConfig = schemaGeneratorConfigBuilder.without(Option.SCHEMA_VERSION_INDICATOR, new Option[0]).build();
        SUBTYPE_SCHEMA_GENERATOR = new SchemaGenerator(subtypeSchemaGeneratorConfig);
    }

    public static String getInputSchema(List<Parameter> parameters) {
        ObjectNode schema = JsonParser.getObjectMapper().createObjectNode();
        schema.put("$schema", SchemaVersion.DRAFT_2020_12.getIdentifier());
        schema.put("type", "object");
        ObjectNode properties = schema.putObject("properties");
        List<String> required = new ArrayList<>();

        for (Parameter parameter : parameters) {
            String parameterName = parameter.parameteNname();
            Type parameterType = null;
            if (parameter.required()) {
                required.add(parameterName);
            }
            try {
                if (parameter.type() != null) {
                    // 示例：获取String类型的Type
                    parameterType = getTypeFromString(parameter.type());
                } else {
                    logger.warn("参数 {} 的 schema 或 format 为空，跳过类型转换", parameterName);
                }
            } catch (ClassNotFoundException e) {
                logger.error("无法将字符串类型转换为Type: {}", parameter.type(), e);
            }

            ObjectNode parameterNode = SUBTYPE_SCHEMA_GENERATOR.generateSchema(parameterType, new Type[0]);
            if (StringUtils.hasText(parameter.description())) {
                parameterNode.put("description", parameter.description());
            }
            properties.set(parameterName, parameterNode);
        }
        ArrayNode requiredArray = schema.putArray("required");
        Objects.requireNonNull(requiredArray);
        required.forEach(requiredArray::add);

        JsonSchemaGenerator.SchemaOption[] schemaOptions = new JsonSchemaGenerator.SchemaOption[0];
        processSchemaOptions(schemaOptions, schema);
        return schema.toPrettyString();
    }

    public static Type getTypeFromString(String typeString) throws ClassNotFoundException {
        return switch (typeString) {
            case "string" -> Class.forName("java.lang.String");
            case "number" -> Class.forName("java.lang.Number");
            case "integer" -> Class.forName("java.lang.Integer");
            case "boolean" -> Class.forName("java.lang.Boolean");
            default -> throw new ClassNotFoundException("Unsupported type: " + typeString);
        };
    }

    private static void processSchemaOptions(JsonSchemaGenerator.SchemaOption[] schemaOptions, ObjectNode schema) {
        if (Stream.of(schemaOptions).noneMatch((option) -> {
            return option == JsonSchemaGenerator.SchemaOption.ALLOW_ADDITIONAL_PROPERTIES_BY_DEFAULT;
        })) {
            schema.put("additionalProperties", false);
        }

        if (Stream.of(schemaOptions).anyMatch((option) -> {
            return option == JsonSchemaGenerator.SchemaOption.UPPER_CASE_TYPE_VALUES;
        })) {
            convertTypeValuesToUpperCase(schema);
        }

    }

    public static void convertTypeValuesToUpperCase(ObjectNode node) {
        if (node.isObject()) {
            node.fields().forEachRemaining((entry) -> {
                JsonNode value = (JsonNode) entry.getValue();
                if (value.isObject()) {
                    convertTypeValuesToUpperCase((ObjectNode) value);
                } else if (value.isArray()) {
                    value.elements().forEachRemaining((element) -> {
                        if (element.isObject() || element.isArray()) {
                            convertTypeValuesToUpperCase((ObjectNode) element);
                        }

                    });
                } else if (value.isTextual() && ((String) entry.getKey()).equals("type")) {
                    String oldValue = node.get("type").asText();
                    node.put("type", oldValue.toUpperCase());
                }

            });
        } else if (node.isArray()) {
            node.elements().forEachRemaining((element) -> {
                if (element.isObject() || element.isArray()) {
                    convertTypeValuesToUpperCase((ObjectNode) element);
                }

            });
        }

    }

}
