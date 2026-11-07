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
package com.alibaba.cloud.ai.example.graph.openmanus.tool;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.openqa.selenium.chrome.ChromeDriver;

import org.springframework.ai.tool.function.FunctionToolCallback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockConstruction;

class DocLoaderToolTest {

	@TempDir
	Path directory;

	@Test
	void createsDocumentCallbackWithoutStartingBrowser() {
		try (MockedConstruction<ChromeDriver> drivers = mockConstruction(ChromeDriver.class)) {
			FunctionToolCallback callback = DocLoaderTool.getFunctionToolCallback();
			assertThat(callback.getToolDefinition().name()).isEqualTo("doc_loader");
			assertThat(drivers.constructed()).isEmpty();
		}
	}

	@Test
	void exportedCallbackReadsLocalDocument() throws Exception {
		Path document = directory.resolve("document.txt");
		String content = "Document callback regression fixture";
		Files.writeString(document, content);
		// Protect the regression run from the old callback's ChromeDriver construction.
		try (MockedConstruction<ChromeDriver> drivers = mockConstruction(ChromeDriver.class)) {
			String response = call(document);
			assertThat(response).startsWith("Related information: ").contains(content);
			assertThat(drivers.constructed()).isEmpty();
		}
	}

	@Test
	void exportedCallbackReportsDocumentReadFailure() {
		try (MockedConstruction<ChromeDriver> drivers = mockConstruction(ChromeDriver.class)) {
			String response = call(directory.resolve("missing.txt"));
			assertThat(response).startsWith("Error get Related information: ").contains("missing.txt");
			assertThat(drivers.constructed()).isEmpty();
		}
	}

	private String call(Path file) {
		String arguments = JSON.toJSONString(Map.of("file_type", "txt", "file_path", file.toString()));
		String response = DocLoaderTool.getFunctionToolCallback().call(arguments);
		return JSON.parseObject(response).getString("output");
	}

}
