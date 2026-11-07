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
package com.alibaba.cloud.ai.example.graph.openmanus.tool.support;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.cloud.ai.example.graph.openmanus.tool.FileSaver;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilePersistenceTest {

	private static final String CONTENT = "Hello, 你好 🌍";

	private static final String SCRIPT = "# " + CONTENT + "\nprintf done\n";

	@TempDir
	Path directory;

	@ParameterizedTest
	@ValueSource(strings = { "US-ASCII", "ISO-8859-1", "UTF-8" })
	void savesAndAppendsUtf8(String charset) throws Exception {
		probe("save", charset);
	}

	@EnabledOnOs({ OS.LINUX, OS.MAC })
	@ParameterizedTest
	@ValueSource(strings = { "US-ASCII", "ISO-8859-1", "UTF-8" })
	void writesExecutableScriptsAsUtf8(String charset) throws Exception {
		probe("script", charset);
	}

	private void probe(String mode, String charset) throws Exception {
		Path output = directory.resolve("probe.log");
		Process process = new ProcessBuilder(
				Path.of(System.getProperty("java.home"), "bin", "java").toString(),
				"-Dfile.encoding=" + charset, "-cp", System.getProperty("java.class.path"),
				FilePersistenceTest.class.getName(), mode, directory.toString(), charset)
			.redirectErrorStream(true).redirectOutput(output.toFile()).start();
		try {
			assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Persistence probe blocked");
			assertEquals(0, process.exitValue(), () -> readLog(output));
		}
		finally {
			process.descendants().forEach(ProcessHandle::destroyForcibly);
			process.destroyForcibly();
			process.waitFor(5, TimeUnit.SECONDS);
		}
	}

	private static String readLog(Path path) {
		try {
			return Files.readString(path);
		}
		catch (Exception e) {
			return e.toString();
		}
	}

	public static void main(String[] args) throws Exception {
		assertEquals(Charset.forName(args[2]), Charset.defaultCharset());
		Path directory = Path.of(args[1]);
		if (args[0].equals("save")) {
			Path target = directory.resolve("nested/result.txt");
			FileSaver saver = new FileSaver();
			String input = JSON.toJSONString(Map.of("content", CONTENT, "file_path", target.toString()));
			for (int i = 0; i < 2; i++) {
				assertTrue(saver.run(input).getOutput().startsWith("Content successfully saved"));
			}
			assertArrayEquals((CONTENT + CONTENT).getBytes(StandardCharsets.UTF_8), Files.readAllBytes(target));
		}
		else {
			// The shell only executes ASCII; the generated source must retain its Unicode comment.
			CodeExecutionResult result = CodeUtils.executeCode(SCRIPT, "sh", "source.sh", null,
					Map.of("work_dir", directory.toString()));
			assertEquals(0, result.getExitcode());
			assertArrayEquals(SCRIPT.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(directory.resolve("source.sh")));
		}
	}

}
