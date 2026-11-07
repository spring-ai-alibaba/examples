/*
 * Copyright 2026 the original author or authors.
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
package com.cloud.alibaba.ai.example.tools;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FileWriteTool implements Tool<FileWriteTool.Request, String> {

    @Override
    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("file_write", this)
                .description("Tool for write files")
                .inputType(Request.class)
                .build();
    }

    @Override
    public String apply(FileWriteTool.Request s, ToolContext toolContext) {
        try {
            String safePath = Paths.get(System.getProperty("user.dir"))
                    .resolve(s.filePath)
                    .normalize()
                    .toString();

            try (FileWriter writer = new FileWriter(safePath, StandardCharsets.UTF_8)) {
                writer.write(s.content);
            }
            
            return "Successfully wrote to file: " + s.filePath;
        } catch (IOException e) {
            return "Error writing to file: " + e.getMessage();
        }
    }

    @JsonClassDescription("Request for writing a file")
    public record Request(
            @JsonProperty(value = "file_path", required = true)
            @JsonPropertyDescription("The path of the file to write")
            String filePath,
            @JsonProperty(value = "content", required = true)
            @JsonPropertyDescription("The content to write to the file")
            String content
    ) {

    }
}
