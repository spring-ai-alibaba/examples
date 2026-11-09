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

import java.io.File;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression tests for {@link CodeUtils#executeCommand(String...)}.
 *
 * The child process is this JVM itself, so the test does not depend on python, bash or
 * any other external tool being installed on the build machine.
 */
class CodeUtilsTest {

	/**
	 * Larger than a typical OS pipe buffer (64 KiB). With the previous implementation the
	 * child blocked writing stdout while the parent read stderr to EOF, so the call never
	 * returned.
	 */
	private static final int LARGE_OUTPUT_SIZE = 1024 * 1024;

	@Test
	@Timeout(60)
	void executeCommandReturnsLargeStdoutWithoutBlocking() {
		ExecuteCommandResult result = CodeUtils.executeCommand(javaBinary(), "-cp", childClasspath(),
				LargeStdoutMain.class.getName());

		assertThat(result).isNotNull();
		assertThat(result.getExitCode()).isZero();
		assertThat(result.getOutput()).hasSize(LARGE_OUTPUT_SIZE);
	}

	@Test
	@Timeout(60)
	void executeCommandReturnsStderrWhenChildFails() {
		ExecuteCommandResult result = CodeUtils.executeCommand(javaBinary(), "-cp", childClasspath(),
				FailingMain.class.getName());

		assertThat(result).isNotNull();
		assertThat(result.getExitCode()).isEqualTo(3);
		assertThat(result.getOutput()).contains("boom");
	}

	private static String childClasspath() {
		// Only the two class directories: passing the full surefire classpath exceeds the
		// Windows command line limit (CreateProcess error=206).
		return locationOf(CodeUtilsTest.class) + File.pathSeparator + locationOf(CodeUtils.class);
	}

	private static String locationOf(Class<?> type) {
		try {
			return Paths.get(type.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
		}
		catch (Exception ex) {
			throw new IllegalStateException("cannot locate classes for " + type.getName(), ex);
		}
	}

	private static String javaBinary() {
		return ProcessHandle.current()
			.info()
			.command()
			.orElseThrow(() -> new IllegalStateException("cannot locate the current java binary"));
	}

	/** Writes more than a pipe buffer to stdout, then exits normally. */
	public static class LargeStdoutMain {

		public static void main(String[] args) {
			System.out.print("x".repeat(LARGE_OUTPUT_SIZE));
			System.out.flush();
		}

	}

	/** Writes to stderr and exits non-zero. */
	public static class FailingMain {

		public static void main(String[] args) {
			System.err.println("boom");
			System.exit(3);
		}

	}

}
