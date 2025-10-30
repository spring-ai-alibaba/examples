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

package com.alibaba.cloud.ai.example.opensearch;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author 北极星
 */
@RestController
public class OpensearchController {

    @Autowired
    VectorStore vectorStore;

    @GetMapping("/load/documents")
    public void load(String sourceFile) {
        JsonReader jsonReader = new JsonReader(new FileSystemResource(sourceFile),
                "price", "name", "shortDescription", "description", "tags");
        List<Document> documents = jsonReader.get();
        this.vectorStore.add(documents);
    }

    @GetMapping("/delete/documents")
    public void deleteDocuments(){
        // Create test documents with different metadata
        Document bgDocument = new Document("The World is Big",
                Map.of("country", "Bulgaria"));
        Document nlDocument = new Document("The World is Big",
                Map.of("country", "Netherlands"));

        // Add documents to the store
        vectorStore.add(List.of(bgDocument, nlDocument));

        // Delete documents from Bulgaria using filter expression
        Filter.Expression filterExpression = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("country"),
                new Filter.Value("Bulgaria")
        );
        vectorStore.delete(filterExpression);
    }
}
