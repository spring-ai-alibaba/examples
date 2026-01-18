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

package com.alibaba.cloud.ai.application.config.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleVectorStoreConfiguration {

	@Value("${spring.ai.alibaba.playground.bailian.enable:false}")
	private Boolean enable;

	@Bean
	CommandLineRunner ingestTermOfServiceToVectorStore(VectorStoreDelegate vectorStoreDelegate) {
		return args -> {
			// 百炼知识库和向量存储初始化
			// 如果未启用百炼知识库，则默认用向量存储服务
			if (!enable) {
				String type = System.getenv("VECTOR_STORE_TYPE");
				VectorStoreInitializer initializer = new VectorStoreInitializer();
				initializer.init(vectorStoreDelegate.getVectorStore(type));
			}
		};
	}

	/**
	 * 提供基于内存的向量存储（SimpleVectorStore）
	 * <p>
	 * 依赖 EmbeddingModel（自动注入，Alibaba 的嵌入模型）
	 * @param embeddingModel
	 * @return
	 */
	@Bean
	public VectorStore simpleVectorStore(
			@Qualifier("dashscopeEmbeddingModel") EmbeddingModel embeddingModel
	) {

		return SimpleVectorStore.builder(embeddingModel).build();
	}

	@Bean
	public VectorStoreDelegate vectorStoreDelegate(
			@Qualifier("simpleVectorStore") VectorStore simpleVectorStore,
			@Qualifier("analyticdbVectorStore") @Autowired(required = false) VectorStore analyticdbVectorStore
	) {

		return new VectorStoreDelegate(simpleVectorStore, analyticdbVectorStore);
	}

}
