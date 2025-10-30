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

package com.alibaba.cloud.ai.example.rag.init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.DenseVectorProperty;
import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.ObjectProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TextProperty;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.autoconfigure.ElasticsearchVectorStoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Component
public class VectorDBInit {

	private static final Logger logger = LoggerFactory.getLogger(VectorDBInit.class);

	private final VectorStore vectorStore;

	@Value("classpath:documents/story-1.md")
	Resource file1;

	@Value("classpath:documents/story-2.md")
	Resource file2;

	private final ElasticsearchClient elasticsearchClient;

	private final ElasticsearchVectorStoreProperties options;

	private static final String textField = "content";

	private static final String vectorField = "embedding";

	public VectorDBInit(
			VectorStore vectorStore,
			ElasticsearchClient elasticsearchClient,
			ElasticsearchVectorStoreProperties options) {

		this.vectorStore = vectorStore;
		this.elasticsearchClient = elasticsearchClient;
		this.options = options;
	}

	@PostConstruct
	void run() {

		logger.info("Loading *.md files as Documents");

		var markdownReader1 = new MarkdownDocumentReader(file1, MarkdownDocumentReaderConfig.builder()
				.withAdditionalMetadata("location", "North Pole")
				.build());
		List<Document> documents = new ArrayList<>(markdownReader1.get());

		var markdownReader2 = new MarkdownDocumentReader(file2, MarkdownDocumentReaderConfig.builder()
				.withAdditionalMetadata("location", "Italy")
				.build());
		documents.addAll(markdownReader2.get());

		logger.info("Creating and storing Embeddings from Documents");

		createIndexIfNotExists();
		vectorStore.add(new TokenTextSplitter().split(documents));
	}

	private void createIndexIfNotExists() {
		try {
			String indexName = options.getIndexName();
			Integer dimsLength = options.getDimensions();

			if (!StringUtils.hasLength(indexName)) {
				throw new IllegalArgumentException("Elastic search index name must be provided");
			}

			boolean exists = elasticsearchClient.indices().exists(idx -> idx.index(indexName)).value();
			if (exists) {
				logger.debug("Index {} already exists. Skipping creation.", indexName);
				return;
			}

			String similarityAlgo = options.getSimilarity().name();
			IndexSettings indexSettings = IndexSettings
					.of(settings -> settings.numberOfShards(String.valueOf(1)).numberOfReplicas(String.valueOf(1)));

			// Maybe using json directly?
			Map<String, Property> properties = new HashMap<>();
			properties.put(vectorField, Property.of(property -> property.denseVector(
					DenseVectorProperty.of(dense -> dense.index(true).dims(dimsLength).similarity(similarityAlgo)))));
			properties.put(textField, Property.of(property -> property.text(TextProperty.of(t -> t))));

			Map<String, Property> metadata = new HashMap<>();
			metadata.put("ref_doc_id", Property.of(property -> property.keyword(KeywordProperty.of(k -> k))));

			properties.put("metadata",
					Property.of(property -> property.object(ObjectProperty.of(op -> op.properties(metadata)))));

			CreateIndexResponse indexResponse = elasticsearchClient.indices()
					.create(createIndexBuilder -> createIndexBuilder.index(indexName)
							.settings(indexSettings)
							.mappings(TypeMapping.of(mappings -> mappings.properties(properties))));

			if (!indexResponse.acknowledged()) {
				throw new RuntimeException("failed to create index");
			}

			logger.info("create elasticsearch index {} successfully", indexName);
		}
		catch (IOException e) {
			logger.error("failed to create index", e);
			throw new RuntimeException(e);
		}
	}

}
