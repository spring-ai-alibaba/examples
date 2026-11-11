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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

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

	/**
	 * The pipe readers must not run on the shared common pool: when unrelated work occupies
	 * every worker, a queued reader never starts and the call fails even though the child
	 * exited normally.
	 */
	@Test
	@Timeout(60)
	void executeCommandIsNotStarvedByTheCommonPool() throws Exception {
		ForkJoinPool pool = ForkJoinPool.commonPool();
		int workers = Math.max(1, pool.getParallelism());
		CountDownLatch started = new CountDownLatch(workers);
		CountDownLatch release = new CountDownLatch(1);
		List<Future<?>> blockers = new ArrayList<>();
		try {
			for (int i = 0; i < workers; i++) {
				blockers.add(pool.submit(() -> {
					started.countDown();
					release.await();
					return null;
				}));
			}
			assertThat(started.await(10, TimeUnit.SECONDS)).as("common pool workers started").isTrue();

			ExecuteCommandResult result = CodeUtils.executeCommand(javaBinary(), "-cp", childClasspath(),
					SmallStdoutMain.class.getName());

			assertThat(result).isNotNull();
			assertThat(result.getExitCode()).isZero();
			assertThat(result.getOutput()).contains("small output");
		}
		finally {
			release.countDown();
			for (Future<?> blocker : blockers) {
				blocker.get(10, TimeUnit.SECONDS);
			}
		}
	}

	/**
	 * A command that starts a worker of its own must not leave the worker behind when the
	 * timeout fires: the worker keeps the pipes open, so the drain threads never reach EOF.
	 */
	@Test
	@Timeout(60)
	void timeoutKillsTheWholeProcessTree(@TempDir Path tempDir) throws Exception {
		Path pidFile = tempDir.resolve("grandchild.pid");

		ExecuteCommandResult result = CodeUtils.executeCommand(8, javaBinary(), "-cp", childClasspath(),
				SpawnerMain.class.getName(), pidFile.toString(), childClasspath());

		assertThat(result).isNotNull();
		assertThat(result.getExitCode()).isEqualTo(-1);
		assertThat(result.getOutput()).contains("timed out");

		long pid = awaitPid(pidFile);
		try {
			assertThat(awaitExit(pid, Duration.ofSeconds(15)))
				.as("the grandchild (%s) outlived the timeout", pid)
				.isTrue();
		}
		finally {
			ProcessHandle.of(pid).ifPresent(ProcessHandle::destroyForcibly);
		}
	}

	private static long awaitPid(Path pidFile) throws Exception {
		long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
		while (System.nanoTime() < deadline) {
			if (Files.exists(pidFile)) {
				String text = Files.readString(pidFile).trim();
				if (!text.isEmpty()) {
					return Long.parseLong(text);
				}
			}
			Thread.sleep(20);
		}
		throw new AssertionError("the child never wrote the grandchild pid to " + pidFile);
	}

	private static boolean awaitExit(long pid, Duration timeout) throws InterruptedException {
		long deadline = System.nanoTime() + timeout.toNanos();
		while (System.nanoTime() < deadline) {
			if (!ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)) {
				return true;
			}
			Thread.sleep(50);
		}
		return false;
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

	/** Writes a small amount of output, so no pipe ever fills up. */
	public static class SmallStdoutMain {

		public static void main(String[] args) {
			System.out.println("small output");
		}

	}

	/** Starts a grandchild process, records its pid and then waits to be killed. */
	public static class SpawnerMain {

		public static void main(String[] args) throws Exception {
			Path pidFile = Paths.get(args[0]);
			Process grandchild = new ProcessBuilder(javaBinary(), "-cp", args[1], SleeperMain.class.getName())
				.start();
			Files.writeString(pidFile, Long.toString(grandchild.pid()));
			Thread.sleep(120_000);
		}

	}

	/** A worker that holds the inherited pipes open until it is killed. */
	public static class SleeperMain {

		public static void main(String[] args) throws Exception {
			Thread.sleep(120_000);
		}

	}

}
