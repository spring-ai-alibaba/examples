# Spring AI Alibaba Autoconfigure RAG Elasticsearch Example ###
This section will describe how to create example and use autoconfigure rag component.
## 接口文档
### RagAutoConfigureController 接口

#### 1. retrievalHybrid 方法

**接口路径：** `GET /rag/retrieval/hybrid`

**功能描述：** 提供 retrievalHybrid 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/rag/retrieval/hybrid
```

#### 2. retrievalHyde 方法

**接口路径：** `GET /rag/retrieval/hyde`

**功能描述：** 提供 retrievalHyde 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/rag/retrieval/hyde
```

#### 3. transformHyde 方法

**接口路径：** `GET /rag/transform/hyde`

**功能描述：** 提供 transformHyde 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/rag/transform/hyde
```
## 技术实现
### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-ai-alibaba-starter-dashscope**: 核心依赖
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-alibaba-autoconfigure-rag-elasticsearch**: 核心依赖
- **elasticsearch-java**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic
## 测试指导
### 使用 HTTP 文件测试
模块根目录下提供了 **[rag-elasticsearch-autoconfigure-example.http](./rag-elasticsearch-autoconfigure-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# retrievalHybrid 接口测试
curl "http://localhost:8080/rag/retrieval/hybrid"
```
## 注意事项
1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-11 00:51:02*
## 模块说明
This section will describe how to create example and use autoconfigure rag component.。

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

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:41:32*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:51:02*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*