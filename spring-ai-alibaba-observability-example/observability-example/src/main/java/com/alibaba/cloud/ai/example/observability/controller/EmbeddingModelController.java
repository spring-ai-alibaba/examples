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

package com.alibaba.cloud.ai.example.observability.controller;

import java.util.List;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/observability/embedding")
public class EmbeddingModelController {

	private final EmbeddingModel embeddingModel;

	public EmbeddingModelController(EmbeddingModel embeddingModel) {
		this.embeddingModel = embeddingModel;
	}

	@GetMapping
	public String embedding() {

		var embeddings = embeddingModel.embed("hello world.");
		return "embedding vector size:" + embeddings.length;
	}

	@GetMapping("/generic")
	public String embeddingGenericOpts() {

		var embeddings = embeddingModel.call(new EmbeddingRequest(
				List.of("hello world."),
				DashScopeEmbeddingOptions.builder().withModel(DashScopeModel.EmbeddingModel.EMBEDDING_V3.getValue()).build())
		).getResult().getOutput();
		return "embedding vector size:" + embeddings.length;
	}

}
