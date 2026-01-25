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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Tool for getting the schema of specified tables.
 * <p>
 * This tool returns the CREATE TABLE statement and sample data for the specified tables,
 * helping the agent understand the table structure before writing queries.
 * </p>
 *
 * @author zth9
 * @since 2026-01-22
 */
@Component
public class GetSchemaTool implements BiFunction<GetSchemaTool.Request, ToolContext, String> {

	private static final Logger logger = LoggerFactory.getLogger(GetSchemaTool.class);

	private static final int SAMPLE_ROWS = 3;

	private final JdbcTemplate jdbcTemplate;

	public GetSchemaTool(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public String apply(Request request, ToolContext toolContext) {
		logger.info("========== Get Schema Tool Start ==========");
		logger.info("Tables requested: {}", request.tables());

		try {
			List<String> tableNames = Arrays.stream(request.tables().split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.toList();

			if (tableNames.isEmpty()) {
				return "No table names provided. Please specify table names separated by commas.";
			}

			StringBuilder result = new StringBuilder();

			for (String tableName : tableNames) {
				result.append(getTableSchema(tableName));
				result.append("\n\n");
			}

			logger.info("========== Get Schema Tool End ==========");
			return result.toString().trim();
		}
		catch (Exception e) {
			logger.error("Error getting schema", e);
			return "Error getting schema: " + e.getMessage();
		}
	}

	private String getTableSchema(String tableName) {
		StringBuilder sb = new StringBuilder();

		try {
			// Get CREATE TABLE statement
			String createSql = jdbcTemplate.queryForObject(
					"SELECT sql FROM sqlite_master WHERE type='table' AND name=?", String.class, tableName);

			sb.append(createSql).append("\n\n");

			// Get sample rows
			List<Map<String, Object>> sampleRows = jdbcTemplate
				.queryForList("SELECT * FROM " + sanitizeTableName(tableName) + " LIMIT " + SAMPLE_ROWS);

			if (!sampleRows.isEmpty()) {
				sb.append("/*\n");
				sb.append(SAMPLE_ROWS).append(" rows from ").append(tableName).append(" table:\n");

				// Header
				String header = String.join("\t", sampleRows.get(0).keySet());
				sb.append(header).append("\n");

				// Data rows
				for (Map<String, Object> row : sampleRows) {
					String rowStr = row.values()
						.stream()
						.map(v -> v == null ? "NULL" : String.valueOf(v))
						.collect(Collectors.joining("\t"));
					sb.append(rowStr).append("\n");
				}
				sb.append("*/");
			}
		}
		catch (Exception e) {
			sb.append("Error getting schema for table '")
				.append(tableName)
				.append("': ")
				.append(e.getMessage())
				.append("\n");
			sb.append("Make sure the table exists by calling list_tables first.");
		}

		return sb.toString();
	}

	private String sanitizeTableName(String tableName) {
		// Basic SQL injection prevention - only allow alphanumeric and underscore
		if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
			throw new IllegalArgumentException("Invalid table name: " + tableName);
		}
		return tableName;
	}

	public ToolCallback toolCallback() {
		return FunctionToolCallback.builder("get_schema", this)
			.description("Gets the schema (CREATE TABLE statement) and sample rows for the specified tables. "
					+ "Use this tool to understand the structure and columns of tables before writing queries. "
					+ "Input should be a comma-separated list of table names. "
					+ "Example: 'users, orders, products'")
			.inputType(Request.class)
			.build();
	}

	@JsonClassDescription("Request to get schema for specified tables")
	public record Request(@JsonProperty(value = "tables", required = true)
	@JsonPropertyDescription("Comma-separated list of table names to get schema for. "
			+ "Example: 'users, orders, products'") String tables) {
	}

}
