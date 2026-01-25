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
package com.cloud.alibaba.ai.example.agent.sql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SQL Agent Example Application
 * <p>
 * This application demonstrates a SQL Agent that can answer questions about databases
 * using natural language. The agent uses a ReAct pattern to:
 * <ul>
 *   <li>List available database tables</li>
 *   <li>Get table schemas</li>
 *   <li>Generate and validate SQL queries</li>
 *   <li>Execute queries and synthesize answers</li>
 * </ul>
 * </p>
 *
 * @author zth9
 * @since 2026-01-22
 */
@SpringBootApplication
public class SqlAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqlAgentApplication.class, args);
	}

}
