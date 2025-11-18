package com.cloud.alibaba.ai.example.tools;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReadTool implements Tool<FileReadTool.Request, String> {
    @Override
    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("file_read", this)
                .description("Tool for read files. ")
                .inputType(Request.class)
                .build();
    }

    @Override
    public String apply(FileReadTool.Request request, ToolContext toolContext) {
        try {
            return Files.readString(Path.of(request.filePath));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @JsonClassDescription("Request for the FileReadTool")
    public record Request(
            @JsonProperty(value = "file_path", required = true)
            @JsonPropertyDescription("The path of the file to read")
            String filePath
    ) {}
}
