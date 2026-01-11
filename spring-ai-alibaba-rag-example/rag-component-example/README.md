# Spring Ai Alibaba Rag Component Example
This section will describe how to create example and call rag component.
## 模块说明
This section will describe how to create example and call rag component.。
## 接口文档
### RagComponentController 接口

#### 1. retrievalHybrid 方法

**接口路径：** `GET /rag/component/retrieval/hybrid`

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
GET http://localhost:8080/rag/component/retrieval/hybrid
```

#### 2. retrievalHyde 方法

**接口路径：** `GET /rag/component/retrieval/hyde`

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
GET http://localhost:8080/rag/component/retrieval/hyde
```

#### 3. retrievalHydeWithFilter 方法

**接口路径：** `GET /rag/component/retrieval/hyde/filter`

**功能描述：** 提供 retrievalHydeWithFilter 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/rag/component/retrieval/hyde/filter
```

#### 4. transformHyde 方法

**接口路径：** `GET /rag/component/transform/hyde`

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
GET http://localhost:8080/rag/component/transform/hyde
```

#### 5. retrievalHybridWithFilter 方法

**接口路径：** `GET /rag/component/retrieval/hybrid/filter`

**功能描述：** 提供 retrievalHybridWithFilter 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/rag/component/retrieval/hybrid/filter
```

#### 6. retrievalHybridWithEsQuery 方法

**接口路径：** `GET /rag/component/retrieval/hybrid/esquery`

**功能描述：** 提供 retrievalHybridWithEsQuery 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/rag/component/retrieval/hybrid/esquery
```

#### 7. rerankDocuments 方法

**接口路径：** `GET /rag/component/rerank/documents`

**功能描述：** 提供 rerankDocuments 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/rag/component/rerank/documents
```

#### 8. callHybridAdvisor 方法

**接口路径：** `GET /rag/component/call/hybrid/advisor`

**功能描述：** 提供 callHybridAdvisor 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/rag/component/call/hybrid/advisor
```

#### 9. callMultiQueryRetrieverAdvisor 方法

**接口路径：** `GET /rag/component/call/multiquery/advisor`

**功能描述：** 提供 callMultiQueryRetrieverAdvisor 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/rag/component/call/multiquery/advisor
```
## 技术实现
### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-ai-alibaba-starter-dashscope**: 核心依赖
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-alibaba-rag**: 核心依赖
- **elasticsearch-java**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic
## 测试指导
### 使用 HTTP 文件测试
模块根目录下提供了 **[rag-component-example.http](./rag-component-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# retrievalHybrid 接口测试
curl "http://localhost:8080/rag/component/retrieval/hybrid"
```
## 注意事项
1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-11 00:50:41*
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

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:50:41*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*