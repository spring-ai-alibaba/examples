/*
 * Copyright 2024-2026 the original author or authors.
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
package com.alibaba.cloud.ai.examples.sandbox.simple.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Calculator Tool
 *
 * Provides basic mathematical operations as tools for the Agent.
 */
@Component
public class CalculatorTool {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorTool.class);

    /**
     * Addition tool
     */
    public ToolCallback addTool() {
        return FunctionToolCallback.builder("add", (Function<AddRequest, Double>) request -> {
            logger.info("Calculator: {} + {}", request.a(), request.b());
            return request.a() + request.b();
        })
                .description("Add two numbers")
                .inputType(AddRequest.class)
                .build();
    }

    /**
     * Subtraction tool
     */
    public ToolCallback subtractTool() {
        return FunctionToolCallback.builder("subtract", (Function<SubtractRequest, Double>) request -> {
            logger.info("Calculator: {} - {}", request.a(), request.b());
            return request.a() - request.b();
        })
                .description("Subtract two numbers")
                .inputType(SubtractRequest.class)
                .build();
    }

    /**
     * Multiplication tool
     */
    public ToolCallback multiplyTool() {
        return FunctionToolCallback.builder("multiply", (Function<MultiplyRequest, Double>) request -> {
            logger.info("Calculator: {} × {}", request.a(), request.b());
            return request.a() * request.b();
        })
                .description("Multiply two numbers")
                .inputType(MultiplyRequest.class)
                .build();
    }

    /**
     * Division tool
     */
    public ToolCallback divideTool() {
        return FunctionToolCallback.builder("divide", (Function<DivideRequest, Double>) request -> {
            logger.info("Calculator: {} ÷ {}", request.a(), request.b());
            if (request.b() == 0) {
                throw new IllegalArgumentException("Cannot divide by zero");
            }
            return request.a() / request.b();
        })
                .description("Divide two numbers")
                .inputType(DivideRequest.class)
                .build();
    }

    // Request classes
    public record AddRequest(double a, double b) {}
    public record SubtractRequest(double a, double b) {}
    public record MultiplyRequest(double a, double b) {}
    public record DivideRequest(double a, double b) {}

}
