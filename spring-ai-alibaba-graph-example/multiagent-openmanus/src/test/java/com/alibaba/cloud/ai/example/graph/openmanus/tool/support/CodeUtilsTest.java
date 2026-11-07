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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeUtilsTest {

	@TempDir
	Path directory;

	@ParameterizedTest
	@ValueSource(strings = { "large-stdout", "large-both-success", "large-both-failure", "lines", "empty", "interrupt" })
	void executesWithoutBlocking(String scenario) throws Exception {
		Path log = directory.resolve(scenario + ".log");
		Process probe = new ProcessBuilder(command("probe", scenario, directory.toString()))
			.redirectErrorStream(true).redirectOutput(log.toFile()).start();
		try {
			assertTrue(probe.waitFor(10, TimeUnit.SECONDS), "Command blocked: " + scenario);
			assertEquals(0, probe.exitValue(), () -> readLog(log));
		}
		finally {
			// A regression must not leave the blocked probe or its command running.
			probe.descendants().forEach(ProcessHandle::destroyForcibly);
			probe.destroyForcibly();
			probe.waitFor(5, TimeUnit.SECONDS);
		}
	}

	private static String readLog(Path log) {
		try {
			return Files.readString(log);
		}
		catch (Exception e) {
			return e.toString();
		}
	}

	private static String[] command(String... args) {
		String[] command = new String[4 + args.length];
		command[0] = Path.of(System.getProperty("java.home"), "bin", "java").toString();
		command[1] = "-cp";
		command[2] = System.getProperty("java.class.path");
		command[3] = CodeUtilsTest.class.getName();
		System.arraycopy(args, 0, command, 4, args.length);
		return command;
	}

	public static void main(String[] args) throws Exception {
		String scenario = args[1];
		if (args[0].equals("emit")) {
			if (scenario.equals("interrupt")) {
				Files.writeString(Path.of(args[2], "pid"), Long.toString(ProcessHandle.current().pid()));
				Thread.sleep(60000);
			}
			else if (scenario.equals("lines")) {
				System.out.print("first\n\nlast\n");
				System.err.print("diagnostic\n");
			}
			else if (!scenario.equals("empty")) {
				System.out.print("x".repeat(1024 * 1024));
				if (!scenario.equals("large-stdout")) {
					System.err.print("e".repeat(1024 * 1024));
				}
				if (scenario.equals("large-both-failure")) {
					System.exit(7);
				}
			}
			return;
		}
		if (scenario.equals("interrupt")) {
			AtomicBoolean interrupted = new AtomicBoolean();
			Thread caller = new Thread(() -> {
				assertNull(CodeUtils.executeCommand(command("emit", scenario, args[2])));
				interrupted.set(Thread.currentThread().isInterrupted());
			});
			caller.start();
			Path pidFile = Path.of(args[2], "pid");
			long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
			while (!Files.exists(pidFile) && System.nanoTime() < deadline) {
				Thread.sleep(10);
			}
			assertTrue(Files.exists(pidFile), "Child did not start");
			long pid = Long.parseLong(Files.readString(pidFile));
			caller.interrupt();
			caller.join(2000);
			assertTrue(interrupted.get(), "Interrupt status was not restored");
			ProcessHandle child = ProcessHandle.of(pid).orElse(null);
			if (child != null) {
				child.onExit().get(2, TimeUnit.SECONDS);
			}
			return;
		}
		ExecuteCommandResult result = CodeUtils.executeCommand(command("emit", scenario));
		assertNotNull(result);
		boolean failed = scenario.equals("large-both-failure");
		assertEquals(failed ? 7 : 0, result.getExitCode());
		String expected = switch (scenario) {
			case "lines" -> "first\n\nlast";
			case "empty" -> "";
			default -> (failed ? "e" : "x").repeat(1024 * 1024);
		};
		assertEquals(expected, result.getOutput());
	}

}
