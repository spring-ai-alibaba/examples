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
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Tool for executing SQL queries against the database.
 * <p>
 * This tool executes SELECT queries and returns the results.
 * For safety, it only allows SELECT statements and blocks DML operations.
 * </p>
 *
 * @author zth9
 * @since 2026-01-22
 */
@Component
public class ExecuteQueryTool implements BiFunction<ExecuteQueryTool.Request, ToolContext, String> {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteQueryTool.class);

	private static final Pattern DML_PATTERN = Pattern
		.compile("^\\s*(INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|TRUNCATE|REPLACE)\\s+", Pattern.CASE_INSENSITIVE);

	private final JdbcTemplate jdbcTemplate;

	@Value("${sql-agent.max-results:10}")
	private int maxResults;

	public ExecuteQueryTool(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public String apply(Request request, ToolContext toolContext) {
		logger.info("========== Execute Query Tool Start ==========");
		logger.info("Query: {}", request.query());

		String query = request.query().trim();

		// Security check: block DML statements
		if (DML_PATTERN.matcher(query).find()) {
			String errorMsg = "Error: DML statements (INSERT, UPDATE, DELETE, DROP, etc.) are not allowed. "
					+ "This agent only supports SELECT queries for safety.";
			logger.warn(errorMsg);
			return errorMsg;
		}

		try {
			// Add LIMIT if not present to prevent large result sets
			String limitedQuery = addLimitIfNeeded(query);

			List<Map<String, Object>> results = jdbcTemplate.queryForList(limitedQuery);

			if (results.isEmpty()) {
				logger.info("Query returned no results");
				return "Query executed successfully. No results found.";
			}

			String resultStr = formatResults(results);
			logger.info("Query returned {} rows", results.size());
			logger.info("========== Execute Query Tool End ==========");

			return resultStr;
		}
		catch (Exception e) {
			logger.error("Error executing query", e);
			return "Error executing query: " + e.getMessage()
					+ "\n\nPlease check your query syntax and try again. "
					+ "Use get_schema to verify table and column names.";
		}
	}

	private String addLimitIfNeeded(String query) {
		String lowerQuery = query.toLowerCase();
		if (!lowerQuery.contains(" limit ") && !lowerQuery.contains("\nlimit ")) {
			// Remove trailing semicolon if present
			if (query.endsWith(";")) {
				query = query.substring(0, query.length() - 1);
			}
			return query + " LIMIT " + maxResults;
		}
		return query;
	}

	private String formatResults(List<Map<String, Object>> results) {
		if (results.isEmpty()) {
			return "No results found.";
		}

		StringBuilder sb = new StringBuilder();

		// Format as a readable table
		List<String> columns = results.get(0).keySet().stream().toList();

		// Header
		sb.append(String.join(" | ", columns)).append("\n");
		sb.append("-".repeat(columns.stream().mapToInt(String::length).sum() + (columns.size() - 1) * 3)).append("\n");

		// Rows
		for (Map<String, Object> row : results) {
			String rowStr = columns.stream()
				.map(col -> row.get(col) == null ? "NULL" : String.valueOf(row.get(col)))
				.collect(Collectors.joining(" | "));
			sb.append(rowStr).append("\n");
		}

		sb.append("\n(").append(results.size()).append(" row(s) returned)");

		return sb.toString();
	}

	public ToolCallback toolCallback() {
		return FunctionToolCallback.builder("execute_query", this)
			.description("Executes a SQL SELECT query against the database and returns the results. "
					+ "IMPORTANT: Only SELECT queries are allowed for safety. "
					+ "DML statements (INSERT, UPDATE, DELETE, DROP) will be rejected. "
					+ "Always use check_query to validate your query before execution. "
					+ "Results are limited to " + maxResults + " rows by default.")
			.inputType(Request.class)
			.build();
	}

	@JsonClassDescription("Request to execute a SQL query")
	public record Request(@JsonProperty(value = "query", required = true)
	@JsonPropertyDescription("The SQL SELECT query to execute. Only SELECT statements are allowed.") String query) {
	}

}
