/*
 * Copyright 2026 the original author or authors.
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
package com.touhouqing.chatAiDemo.repository;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;

import static org.assertj.core.api.Assertions.assertThat;

class LocalPdfFileRepositoryTests {

    private static final String FILENAME = "你好-🌍.pdf";

    @TempDir
    Path directory;

    @ParameterizedTest
    @ValueSource(strings = { "US-ASCII", "ISO-8859-1", "UTF-8" })
    void persistsUnicodeFilenamesAsUtf8(String charset) throws Exception {
        Path output = directory.resolve("probe.log");
        Process process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-Dfile.encoding=" + charset, "-cp", System.getProperty("java.class.path"),
                PersistenceProbe.class.getName())
                .directory(directory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(output.toFile())
                .start();
        try {
            assertThat(process.waitFor(15, TimeUnit.SECONDS)).as("persistence probe finished").isTrue();
            assertThat(process.exitValue())
                    .as(new String(Files.readAllBytes(output), StandardCharsets.UTF_8))
                    .isZero();
            Properties persisted = new Properties();
            try (var reader = Files.newBufferedReader(directory.resolve("chat-pdf.properties"), StandardCharsets.UTF_8)) {
                persisted.load(reader);
            }
            assertThat(persisted.getProperty("chat")).isEqualTo(FILENAME);
        }
        finally {
            process.destroyForcibly();
            process.waitFor();
        }
    }

    public static class PersistenceProbe {

        public static void main(String[] args) throws Exception {
            VectorStore store = (VectorStore) Proxy.newProxyInstance(VectorStore.class.getClassLoader(),
                    new Class<?>[] { VectorStore.class }, (proxy, method, arguments) -> {
                        throw new AssertionError("Unexpected vector store call: " + method.getName());
                    });
            LocalPdfFileRepository original = new LocalPdfFileRepository(store);
            boolean saved = original.save("chat", new ByteArrayResource(new byte[] { 0 }) {
                @Override
                public String getFilename() {
                    return FILENAME;
                }
            });
            if (!saved) {
                throw new AssertionError("PDF fixture was not saved");
            }
            invoke(original, "persistent");

            LocalPdfFileRepository reopened = new LocalPdfFileRepository(store);
            invoke(reopened, "init");
            if (!FILENAME.equals(reopened.getFile("chat").getFilename())) {
                throw new AssertionError("PDF filename did not survive persistence and reload");
            }
        }

        private static void invoke(LocalPdfFileRepository repository, String name) throws Exception {
            Method method = LocalPdfFileRepository.class.getDeclaredMethod(name);
            method.setAccessible(true);
            method.invoke(repository);
        }
    }
}
