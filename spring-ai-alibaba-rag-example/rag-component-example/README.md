# Spring Ai Alibaba Rag Component Example 

This section will describe how to create example and call rag component. 

## Quick Start

### 1. add Dependency

Add the following dependencies in the `pom.xml` file of the Spring Boot project:

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-rag</artifactId>
    <version>${latest-version}</version>
</dependency>
```

### 2. Run rag component example
For how to run and test rag component example, please refer to the following instructions:
```
1. start application.
2. retrieval
curl -X GET http://localhost:8080/rag/component/retrieval/hybrid
```

### EndPoint

- `GET /rag/component/retrieval/hybrid` : Test rag component retrieval with hybrid search.
- `GET /rag/component/retrieval/hyde` : Test rag component retrieval with hyde search.
- `GET /rag/component/retrieval/hyde/filter` : Test rag component retrieval with hyde search and filter.
- `GET /rag/component/transform/hyde` : Test rag component transform with hyde, transforms the current query to generate hypothetical document answers.
- `GET /rag/component/retrieval/hybrid/filter` : Test rag component retrieval with hybrid search and filter.
- `GET /rag/component/retrieval/hybrid/esquery` : Test rag component retrieval with hybrid search and es query.
- `GET /rag/component/rerank/documents` : Test rag component rerank with documents, reranks the retrieved documents based on their relevance to the query.
- `GET /rag/component/call/hybrid/advisor` : Test rag component call with hybrid search and advisor, performs a complete RAG operation by combining retrieval and generation using hybrid search and advisor.
  HybridSearchAdvisor includes the following steps, user can freely expand their implementation
  - pre-retrieval: process the input query before retrieval.
    1. Query rewriting
    2. Query compression
    3. Query translation
    4. Query expand
    5. hyde document generation
  - retrieval: retrieve relevant documents using hybrid search.
    6. hybrid search
  - post-retrieval: process the retrieved documents after retrieval.
    7. rerank
  - generation: generate the final response based on the processed documents.
    8. answer generation
- `GET /rag/component/call/multiquery/advisor` : Test rag component call with multi query and advisor, performs a complete RAG operation by combining retrieval and generation using multi query and advisor.
  MultiQueryRetrieverAdvisor includes the following steps,
  - pre-retrieval: process the input query before retrieval.
    1. Query expand
  - generation: generate the final response based on the processed documents.
    2. answer generation

