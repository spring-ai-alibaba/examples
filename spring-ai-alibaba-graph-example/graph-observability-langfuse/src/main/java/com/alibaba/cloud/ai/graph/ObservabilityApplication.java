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

package com.alibaba.cloud.ai.graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Observability Langfuse Example Application
 * 
 * This application demonstrates graph structure with various node types and edge types:
 * Start → Parallel Nodes → Parallel Nodes → SubGraph Node → Streaming Node → Summary → End
 * 
 * Features:
 * - Parallel edges: ParallelNode1 and ParallelNode2 execute simultaneously
 * - Serial edges: Strict execution order
 * - SubGraph node: Contains internal serial processing flow
 * - Streaming node: Real-time streaming AI responses
 * - Each node uses ChatClient for AI processing
 * 
 * @author sixiyida
 */
@SpringBootApplication
public class ObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityApplication.class, args);
    }
} 
