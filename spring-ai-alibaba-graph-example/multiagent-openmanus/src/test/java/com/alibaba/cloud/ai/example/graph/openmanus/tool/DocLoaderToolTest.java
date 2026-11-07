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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.fastjson.JSON;
import com.alibaba.cloud.ai.parser.tika.TikaDocumentParser;
import org.springframework.ai.document.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;
import org.openqa.selenium.chrome.ChromeDriver;

import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.ToolCallback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void stepAgentRegistriesExposeWorkingDocumentCallback(boolean functionCallbacks) throws Exception {
		Path document = directory.resolve("registered.txt");
		String content = "Registered document callback fixture";
		Files.writeString(document, content);
		try (MockedConstruction<ChromeDriver> drivers = mockConstruction(ChromeDriver.class)) {
			List<ToolCallback> callbacks = functionCallbacks ? Builder.getManusAgentFunctionCallbacks()
					: Builder.getManusAgentToolCalls();
			ToolCallback callback = callbacks.stream()
				.filter(tool -> tool.getToolDefinition().name().equals("doc_loader"))
				.findFirst().orElseThrow();
			String arguments = JSON.toJSONString(Map.of("file_type", "txt", "file_path", document.toString()));
			String output = JSON.parseObject(callback.call(arguments)).getString("output");
			assertThat(output).startsWith("Related information: ").contains(content);
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void closesOwnedInputAfterParsing(boolean failParsing) throws Exception {
		Path document = directory.resolve("stream.txt");
		Files.writeString(document, "Stream lifecycle fixture");
		AtomicReference<InputStream> captured = new AtomicReference<>();
		try (MockedConstruction<TikaDocumentParser> parsers = mockConstruction(TikaDocumentParser.class,
				(parser, context) -> when(parser.parse(any(InputStream.class))).thenAnswer(invocation -> {
					captured.set(invocation.getArgument(0));
					if (failParsing) {
						throw new IllegalStateException("parser fixture failure");
					}
					return List.of(new Document("parsed fixture"));
				}))) {
			String result = call(document);
			assertThat(result).startsWith(failParsing ? "Error get Related information: " : "Related information: ");
			try {
				assertThatExceptionOfType(IOException.class).isThrownBy(() -> captured.get().read());
			}
			finally {
				captured.get().close();
			}
		}
	}

	@Test
	void exportedCallbackReadsPdfDocument() throws Exception {
		Path document = directory.resolve("document.pdf");
		String content = "PDF callback regression fixture";
		try (PDDocument pdf = new PDDocument()) {
			PDPage page = new PDPage();
			pdf.addPage(page);
			try (PDPageContentStream stream = new PDPageContentStream(pdf, page)) {
				stream.beginText();
				stream.setFont(PDType1Font.HELVETICA, 12);
				stream.newLineAtOffset(50, 700);
				stream.showText(content);
				stream.endText();
			}
			pdf.save(document.toFile());
		}
		assertCallbackReads(document, content);
	}

	@Test
	void exportedCallbackReadsWordDocument() throws Exception {
		Path document = directory.resolve("document.docx");
		String content = "Word callback regression fixture";
		try (XWPFDocument word = new XWPFDocument(); var output = Files.newOutputStream(document)) {
			word.createParagraph().createRun().setText(content);
			word.write(output);
		}
		assertCallbackReads(document, content);
	}

	private void assertCallbackReads(Path document, String content) {
		try (MockedConstruction<ChromeDriver> drivers = mockConstruction(ChromeDriver.class)) {
			assertThat(call(document)).startsWith("Related information: ").contains(content);
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
		String arguments = JSON.toJSONString(Map.of("file_type", file.getFileName().toString().split("\\.")[1],
				"file_path", file.toString()));
		String response = DocLoaderTool.getFunctionToolCallback().call(arguments);
		return JSON.parseObject(response).getString("output");
	}

}
