# Spring AI Alibaba Autoconfigure RAG Elasticsearch Example ###

This section will describe how to create example and use autoconfigure rag component. 

## Quick Start

### 1. add Dependency

Add the following dependencies in the `pom.xml` file of the Spring Boot project:

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-autoconfigure-rag-elasticsearch</artifactId>
    <version>${latest-version}</version>
</dependency>
```

### 2. rag configuration

Add RAG related configurations in the `application.properties` or `application.yml` file:

#### Basic Configuration

```yaml
spring:
  # spring elasticsearch configuration
  elasticsearch:
    uris: http://localhost:9200
    username: test
    password: test  
  ai:
    # dashscope configuration
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-plus-2025-04-28
      embedding:
        options:
          model: text-embedding-v1
    # vector store configuration
    vectorstore:
      elasticsearch:
        initialize-schema: true
        index-name: rag_index_name
        similarity: cosine
        dimensions: 1536
    # rag configuration
    alibaba:
      rag:
        elasticsearch:
          enabled: true
```

#### Full Configuration
```yaml
spring:
  # spring elasticsearch configuration
  elasticsearch:
    uris: http://localhost:9200
    username: test
    password: test
  ai:
    # dashscope configuration
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-plus-2025-04-28
      embedding:
        options:
          model: text-embedding-v1
    # vector store configuration
    vectorstore:
      elasticsearch:
        initialize-schema: true
        index-name: rag_index_name
        similarity: cosine
        dimensions: 1536
    # rag configuration
    alibaba:
      rag:
        elasticsearch:
          # Default value: true
          enabled: true
          # Default value: false
          use-rrf: false
          # knn recall parameter configuration
          recall:
            # Similarity threshold, default value 0.0, accepts all
            similarity-threshold: 0.8
            # The number of neighbors, default value 50
            neighbors-num: 50
            # The number of candidates, default value 100
            candidate-num: 100
          # rrf parameter configuration
          rrf:
            # k value, default value 60
            rank-constant: 60
            # window size, default value 50
            rank-window-size: 50
          # support bm25, knn , hybrid, default value hybrid
          retriever-type: hybrid
          # return topK documents, default value 50
          top-k: 50
          # hybrid mode knn weight, default value 1
          knn-bias: 1
          # hybrid mode bm25 weight, default value 1
          bm25-bias: 1
```

### 3. Use the RAG component

In your Spring Boot application, directly use the automatically configured RAG component

- HybridElasticsearchRetriever: Combining BM25 and KNN vector search, it supports Reciprocal Rank Fusion (RRF) sorting
- HyDeRetriever: Enhances retrieval performance by generating hypothetical documents
- HyDeTransformer: Generates hypothetical documents for input queries
