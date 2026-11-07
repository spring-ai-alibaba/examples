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

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeUtils {

	private static final Logger log = LoggerFactory.getLogger(CodeUtils.class);

	private static final String CODE_BLOCK_PATTERN = "```(\\w*)\n(.*?)\n```";

	private static final String UNKNOWN = "unknown";

	private static final int DEFAULT_TIMEOUT = 600;

	public static final String WORKING_DIR = Paths.get(System.getProperty("user.dir"), "extensions").toString();

	public static List<Pair<String, String>> extractCode(String text, boolean detectSingleLineCode) {
		List<Pair<String, String>> extracted = new ArrayList<>();
		String content = contentStr(text);
		if (!detectSingleLineCode) {
			Pattern codeBlockPattern = Pattern.compile(CODE_BLOCK_PATTERN, Pattern.DOTALL);
			Matcher matcher = codeBlockPattern.matcher(content);
			while (matcher.find()) {
				String lang = matcher.group(1);
				String code = matcher.group(2);
				extracted.add(Pair.of(lang != null ? lang : "", code));
			}
			if (extracted.isEmpty()) {
				extracted.add(Pair.of(UNKNOWN, content));
			}
		}
		else {
			Pattern codePattern = Pattern.compile("\\{3\\}(\\w+)?\\s*([\\s\\S]*?){3}|([\\^]+)`");
			Matcher matcher = codePattern.matcher(content);
			while (matcher.find()) {
				String lang = matcher.group(1);
				String code = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
				extracted.add(Pair.of(lang != null ? lang.trim() : "", code.trim()));
			}
		}
		return extracted;
	}

	public static String contentStr(Object content) {
		if (content instanceof String) {
			return (String) content;
		}
		String rst = "";
		List<Map<String, Object>> itemList = (List<Map<String, Object>>) content;
		for (Map<String, Object> item : itemList) {
			if (item.get("type").equals("text")) {
				rst += item.get("text");
			}
			else {
				assert item instanceof Map && item.get("type").equals("image_url") : "Wrong content format.";
				rst += "";
			}
		}
		return rst;
	}

	public static CodeExecutionResult executeCode(String code, String lang, String filename, Boolean arm64,
			Map<String, Object> kwargs) {
		log.info("code:{}, lang:{}, filename:{}, arm64:{}, kwargs:{}", code, lang, filename, arm64, kwargs);

		if (code == null && filename == null) {
			String error_msg = "Either code or filename must be provided.";
			log.error(error_msg);
			throw new AssertionError(error_msg);
		}

		String workDir = kwargs.containsKey("work_dir") ? (String) kwargs.get("work_dir") : null;

		// timeout = timeout != null ? timeout : DEFAULT_TIMEOUT;
		// String original_filename = filename;
		// if (WIN32 && Arrays.asList("sh", "shell").contains(lang) && !useDocker) {
		// lang = "ps1";
		// }

		if (filename == null) {
			String code_hash = md5(code);
			filename = String.format("tmp_code_%s.%s", code_hash, lang.startsWith("python") ? "py" : lang);
		}
		if (workDir == null) {
			workDir = WORKING_DIR;
		}
		String filepath = Paths.get(workDir, filename).toString();
		String file_dir = Paths.get(filepath).getParent().toString();
		try {
			Files.createDirectories(Paths.get(file_dir));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		if (code != null) {
			try {
				FileWriter fout = new FileWriter(filepath);
				fout.write(code);
				fout.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		log.info("filepath:{}", filepath);

		ExecuteCommandResult executeCommandResult = null;
		if (lang.equals("python")) {
			List<String> cmds = new ArrayList<>();
			if (arm64 != null) {
				cmds.add("arch");
				cmds.add(arm64 ? "-arm64" : "-x86_64");
			}
			cmds.add("python3");
			cmds.add(filepath);
			executeCommandResult = CodeUtils.executeCommand(cmds.toArray(new String[] {}));
		}
		else if (lang.equals("sh")) {
			String[] cmd = { "sh", filepath, };
			executeCommandResult = CodeUtils.executeCommand(cmd);
		}

		CodeExecutionResult codeExecutionResult = new CodeExecutionResult();
		codeExecutionResult.setExitcode(executeCommandResult.getExitCode());
		codeExecutionResult.setLogs(executeCommandResult.getOutput());
		return codeExecutionResult;
	}

	public static String md5(String input) {
		return DigestUtils.md5Hex(input);
	}

	public static ExecuteCommandResult executeCommand(String... command) {
		Process process = null;
		ExecutorService readers = Executors.newFixedThreadPool(2);
		try {
			process = new ProcessBuilder(command).start();
			Process runningProcess = process;
			// Both pipes must be drained while the command is running, regardless of its exit code.
			Future<String> output = readers.submit(() -> read(runningProcess.getInputStream()));
			Future<String> error = readers.submit(() -> read(runningProcess.getErrorStream()));
			String successResult = output.get();
			String errorResult = error.get();
			int exitCode = process.waitFor();
			log.info("read python error={}", errorResult);
			if (exitCode == 0) {
				log.info("read python success={}", successResult);
			}
			ExecuteCommandResult result = new ExecuteCommandResult();
			result.setExitCode(exitCode);
			result.setOutput(exitCode == 0 ? successResult : errorResult);
			return result;
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("executePythonCode interrupted", e);
			return null;
		}
		catch (Exception e) {
			log.error("executePythonCode error", e);
			return null;
		}
		finally {
			if (process != null) {
				process.destroyForcibly();
				try {
					process.getOutputStream().close();
				}
				catch (IOException e) {
					log.debug("Could not close command input", e);
				}
			}
			readers.shutdownNow();
		}
	}

	private static String read(InputStream stream) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			List<String> lines = new ArrayList<>();
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return String.join("\n", lines);
		}
	}

}
