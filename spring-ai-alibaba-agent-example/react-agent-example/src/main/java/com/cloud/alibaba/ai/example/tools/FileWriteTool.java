package com.cloud.alibaba.ai.example.tools;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.io.FileWriter;
import java.io.IOException;
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

            FileWriter writer = new FileWriter(safePath);
            writer.write(s.content);
            writer.close();
            
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
