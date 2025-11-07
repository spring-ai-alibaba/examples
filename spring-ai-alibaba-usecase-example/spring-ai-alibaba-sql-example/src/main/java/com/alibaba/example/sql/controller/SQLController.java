/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.example.sql.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.alibaba.example.sql.SQLGenerationException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SQLController {

	@Value("classpath:/schema.sql")
	private Resource ddlResource;

	@Value("classpath:/sql-prompt-template.st")
	private Resource sqlPromptTemplateResource;

	private final ChatClient chatClient;
	private final JdbcTemplate jdbcTemplate;

	public SQLController(
			ChatClient.Builder aiClientBuilder,
			JdbcTemplate jdbcTemplate
	) {

		this.chatClient = aiClientBuilder.build();
		this.jdbcTemplate = jdbcTemplate;
	}

	@PostMapping(path = "/sql")
	public Answer sql(@RequestBody SqlRequest sqlRequest) throws IOException {

		String schema = ddlResource.getContentAsString(Charset.defaultCharset());

		String query = chatClient.prompt()
				.user(userSpec -> userSpec
						.text(sqlPromptTemplateResource)
						.param("question", sqlRequest.question())
						.param("ddl", schema)
				)
				.call()
				.content();

		assert query != null;
		if (query.toLowerCase().startsWith("select")) {

			return new Answer(
					query,
					jdbcTemplate.queryForList(query)
			);
		}

		throw new SQLGenerationException(query);
	}

	public record SqlRequest(String question) { }

	public record Answer(String sqlQuery, List<Map<String, Object>> results) { }

}
