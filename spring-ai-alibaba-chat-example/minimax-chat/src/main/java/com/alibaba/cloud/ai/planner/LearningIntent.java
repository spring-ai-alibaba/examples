/*
 * Copyright 2026-2027 the original author or authors.
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

package com.alibaba.cloud.ai.planner;

/**
 * Learning intent identified before asking the model to answer.
 */
public enum LearningIntent {

	/**
	 * 用户主要在询问当前时间、北京时间、UTC 时间等真实时钟信息。
	 */
	TIME_QUERY,

	/**
	 * 用户主要在询问学习路线、下一步学习什么、学习建议或实践路径。
	 */
	LEARNING_ADVICE,

	/**
	 * 用户主要在要求生成今日计划、固定时长学习安排、每日练习或任务拆分。
	 */
	DAILY_PLAN,

	/**
	 * 用户主要在询问 Tool、Skill、Agent、RAG、MCP、Graph 等概念含义或区别。
	 */
	CONCEPT_EXPLAIN,

	/**
	 * 用户问题同时命中多个学习意图，需要模型组合多个工具或技能能力回答。
	 */
	MIXED,

	/**
	 * 用户问题不属于明确的学习工具场景，按普通聊天或一般问答处理。
	 */
	GENERAL_CHAT

}
