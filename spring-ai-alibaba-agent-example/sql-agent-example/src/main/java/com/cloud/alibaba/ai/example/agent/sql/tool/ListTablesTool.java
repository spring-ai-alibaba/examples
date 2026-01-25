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

import java.util.List;
import java.util.function.BiFunction;

/**
 * Tool for listing all tables in the database.
 * <p>
 * This tool returns a comma-separated list of all available tables,
 * helping the agent understand the database structure.
 * </p>
 *
 * @author zth9
 * @since 2026-01-22
 */
@Component
public class ListTablesTool implements BiFunction<ListTablesTool.Request, ToolContext, String> {

	private static final Logger logger = LoggerFactory.getLogger(ListTablesTool.class);

	private final JdbcTemplate jdbcTemplate;

	public ListTablesTool(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public String apply(Request request, ToolContext toolContext) {
		logger.info("========== List Tables Tool Start ==========");

		try {
			List<String> tables = jdbcTemplate.queryForList(
					"SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name",
					String.class);

			if (tables.isEmpty()) {
				logger.info("No tables found in the database");
				return "No tables found in the database.";
			}

			String result = String.join(", ", tables);
			logger.info("Found {} tables: {}", tables.size(), result);
			logger.info("========== List Tables Tool End ==========");

			return result;
		}
		catch (Exception e) {
			logger.error("Error listing tables", e);
			return "Error listing tables: " + e.getMessage();
		}
	}

	public ToolCallback toolCallback() {
		return FunctionToolCallback.builder("list_tables", this)
			.description("Lists all available tables in the database. "
					+ "Use this tool first to understand what tables are available before querying. "
					+ "Returns a comma-separated list of table names.")
			.inputType(Request.class)
			.build();
	}

	@JsonClassDescription("Request to list all database tables")
	public record Request(
			@JsonProperty(value = "dummy", required = false)
			@JsonPropertyDescription("Dummy parameter, not used. Just pass an empty string.") String dummy) {
	}

}
