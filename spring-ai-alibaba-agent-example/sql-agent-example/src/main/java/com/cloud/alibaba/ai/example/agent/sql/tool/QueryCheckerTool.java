/*
 * Copyright 2026-2027 the original author or authors.
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
package com.cloud.alibaba.ai.example.agent.sql.tool;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

/**
 * Tool for checking SQL queries for common mistakes.
 * <p>
 * This tool uses the LLM to validate SQL queries before execution,
 * helping catch syntax errors and common mistakes.
 * </p>
 *
 * @author zth9
 * @since 2026-01-22
 */
@Component
public class QueryCheckerTool implements BiFunction<QueryCheckerTool.Request, ToolContext, String> {

	private static final Logger logger = LoggerFactory.getLogger(QueryCheckerTool.class);

	private final ChatModel chatModel;

	private static final String CHECK_PROMPT_TEMPLATE = """
			You are a SQL query validator. Check the following SQLite query for common mistakes:

			```sql
			%s
			```

			Check for:
			1. Syntax errors
			2. Incorrect column or table names (if context is provided)
			3. Missing quotes around string values
			4. Incorrect JOIN conditions
			5. GROUP BY clause issues
			6. Any potential SQL injection vulnerabilities

			If the query looks correct, respond with exactly:
			"VALID: The query appears to be correct."

			If there are issues, respond with:
			"ISSUES FOUND:" followed by a numbered list of problems and suggested fixes.

			Keep your response concise.
			""";

	public QueryCheckerTool(ChatModel chatModel) {
		this.chatModel = chatModel;
	}

	@Override
	public String apply(Request request, ToolContext toolContext) {
		logger.info("========== Query Checker Tool Start ==========");
		logger.info("Query to check: {}", request.query());

		try {
			String promptText = String.format(CHECK_PROMPT_TEMPLATE, request.query());
			Prompt prompt = new Prompt(promptText);

			String result = chatModel.call(prompt).getResult().getOutput().getText();

			logger.info("Check result: {}", result);
			logger.info("========== Query Checker Tool End ==========");

			return result;
		}
		catch (Exception e) {
			logger.error("Error checking query", e);
			return "Error checking query: " + e.getMessage();
		}
	}

	public ToolCallback toolCallback() {
		return FunctionToolCallback.builder("check_query", this)
			.description("Validates a SQL query for common mistakes before execution. "
					+ "Use this tool to double-check your query before running it with execute_query. "
					+ "The tool will identify syntax errors, potential issues, and suggest fixes.")
			.inputType(Request.class)
			.build();
	}

	@JsonClassDescription("Request to check a SQL query for errors")
	public record Request(@JsonProperty(value = "query", required = true)
	@JsonPropertyDescription("The SQL query to validate for common mistakes") String query) {
	}

}
