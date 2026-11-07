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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class FileWriteToolTests {

    private static final String CONTENT = "Hello, 你好 🌍";

    @TempDir
    Path directory;

    @ParameterizedTest
    @ValueSource(strings = { "US-ASCII", "ISO-8859-1", "UTF-8" })
    void writesUtf8RegardlessOfTheDefaultCharset(String charset) throws Exception {
        Path target = directory.resolve("result.txt");
        Path output = directory.resolve("probe.log");
        Process process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-Dfile.encoding=" + charset, "-cp", System.getProperty("java.class.path"),
                WriterProbe.class.getName(), target.toString())
                .redirectErrorStream(true)
                .redirectOutput(output.toFile())
                .start();
        try {
            assertThat(process.waitFor(15, TimeUnit.SECONDS)).as("writer probe finished").isTrue();
            assertThat(process.exitValue())
                    .as(new String(Files.readAllBytes(output), StandardCharsets.UTF_8))
                    .isZero();
            assertThat(Files.readAllBytes(target)).isEqualTo(CONTENT.getBytes(StandardCharsets.UTF_8));
        }
        finally {
            process.destroyForcibly();
            process.waitFor();
        }
    }

    public static class WriterProbe {

        public static void main(String[] args) {
            System.out.println("Default charset: " + java.nio.charset.Charset.defaultCharset());
            String result = new FileWriteTool().apply(new FileWriteTool.Request(args[0], CONTENT), null);
            if (!result.startsWith("Successfully wrote to file:")) {
                throw new AssertionError(result);
            }
            String readBack = new FileReadTool().apply(new FileReadTool.Request(args[0]), null);
            if (!CONTENT.equals(readBack)) {
                throw new AssertionError("Written content did not round-trip through FileReadTool");
            }
        }
    }
}
