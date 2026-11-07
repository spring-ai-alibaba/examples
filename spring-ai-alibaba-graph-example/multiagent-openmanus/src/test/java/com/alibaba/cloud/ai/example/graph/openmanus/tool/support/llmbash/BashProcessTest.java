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
package com.alibaba.cloud.ai.example.graph.openmanus.tool.support.llmbash;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledOnOs({ OS.LINUX, OS.MAC })
class BashProcessTest {

	@TempDir
	Path directory;

	@ParameterizedTest
	@ValueSource(strings = { "stderr", "mixed", "failure", "working-directory" })
	void drainsTerminalOutput(String scenario) throws Exception {
		Path output = directory.resolve("probe.log");
		Process probe = new ProcessBuilder(
				Path.of(System.getProperty("java.home"), "bin", "java").toString(), "-cp",
				System.getProperty("java.class.path"), BashProcessTest.class.getName(), scenario,
				directory.toString()).redirectErrorStream(true).redirectOutput(output.toFile()).start();
		try {
			assertTrue(probe.waitFor(5, TimeUnit.SECONDS), "Bash command blocked: " + scenario);
			assertEquals(0, probe.exitValue(), () -> readLog(output));
		}
		finally {
			probe.descendants().forEach(ProcessHandle::destroyForcibly);
			probe.destroyForcibly();
			probe.waitFor(5, TimeUnit.SECONDS);
		}
	}

	private static String readLog(Path output) {
		try {
			return Files.readString(output);
		}
		catch (Exception e) {
			return e.toString();
		}
	}

	public static void main(String[] args) throws Exception {
		String scenario = args[0];
		if (scenario.equals("working-directory")) {
			List<String> result = BashProcess.executeCommand(List.of("pwd", "printf second"), args[1]);
			assertEquals(List.of(Path.of(args[1]).toRealPath() + "\n", "second\n"), result);
			return;
		}
		// Bash's printf produces enough stderr to fill a pipe, without Python or a model service.
		String command = "printf '%131072s' '' >&2";
		String expected = " ".repeat(131072);
		if (scenario.equals("mixed")) {
			command = "printf 'before\n'; " + command + "; printf 'after\n'";
			expected = "before\n" + expected + "after\n";
		}
		else if (scenario.equals("failure")) {
			command += "; exit 7";
			expected += "\n";
		}
		else {
			expected += "\n";
		}
		assertEquals(List.of(expected), BashProcess.executeCommand(List.of(command), args[1]));
	}

}
