/*
 * Copyright 2024-2026 the original author or authors.
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

package com.alibaba.cloud.ai.application.config.handler;

import com.alibaba.cloud.ai.application.entity.result.Result;
import com.alibaba.cloud.ai.application.exception.SAAAIException;
import com.alibaba.cloud.ai.application.exception.SAAAppException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result<?> handleValidationExceptions(
			MethodArgumentNotValidException ex
	) {
		return Result.failed("Invalid params: " + ex.getMessage());
	}

	@ExceptionHandler(SAAAIException.class)
	public Result<?> handleSAAAIExceptions(
			MethodArgumentNotValidException ex
	) {
		return Result.failed("Spring AI Alibaba Exception: " + ex.getMessage());
	}

	@ExceptionHandler(SAAAppException.class)
	public Result<?> handleSAAAppExceptions(
			MethodArgumentNotValidException ex
	) {
		return Result.failed("Spring AI Alibaba Playground Exception: " + ex.getMessage());
	}

}
