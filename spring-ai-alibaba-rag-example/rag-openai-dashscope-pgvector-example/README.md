# MXY RAG Server

基于Spring AI的RAG（检索增强生成）服务器，使用阿里云通义千问模型（OpenAI兼容模式）。

## 特性

- 智能问答：基于知识库的RAG问答
- 多格式支持：PDF、Word、TXT等文档
- 向量存储：PostgreSQL + pgvector
- 流式响应：支持实时对话
- 阿里云模型：通义千问 + text-embedding-v3
- 多模型集成：支持OpenAI和DashScope模型切换

## 技术栈

- Spring Boot 3.4.5 + Spring AI 1.1.0
- Java 17
- PostgreSQL + pgvector
- 阿里云通义千问（OpenAI兼容模式）

## 快速开始

### 1. 环境准备

```bash
# 设置API Key
export AI_DASHSCOPE_API_KEY=your_api_key
# 或者 Windows
set AI_DASHSCOPE_API_KEY=your_api_key

# 确保PostgreSQL已安装pgvector扩展
CREATE EXTENSION IF NOT EXISTS vector;
```

### 2. 配置数据库

修改 `application.yaml` 中的数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/springai
    username: postgres
    password: postgres
```

### 3. 启动应用

```bash
mvn spring-boot:run
```

应用启动在 `http://localhost:8080`

## 接口文档

### KnowledgeBaseController 接口

#### 1. insertTextContent 方法

**接口路径：** `POST /api/v1/knowledge-base/insert-text`

**功能描述：** 将字符串内容插入到向量库中。

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码
- 自动文本分块和向量化

**使用场景：**
- 知识库内容录入
- 实时文本添加
- 数据处理和响应

**示例请求：**
```http
POST /api/v1/knowledge-base/insert-text
Content-Type: application/x-www-form-urlencoded

content=人工智能是计算机科学的一个分支，致力于创建能够执行通常需要人类智能的任务的系统。
```

```bash
curl -X POST http://localhost:8080/api/v1/knowledge-base/insert-text \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "content=人工智能是计算机科学的一个分支，致力于创建能够执行通常需要人类智能的任务的系统。"
```

#### 2. uploadFileByType 方法

**接口路径：** `POST /api/v1/knowledge-base/upload-file`

**功能描述：** 根据文件类型动态选择Reader加载文件到知识库。

**主要特性：**
- 支持多种文件格式：PDF、Word、TXT、Markdown等
- 自动文档解析和分块
- 并行处理提高效率
- 最大文件大小：10MB

**使用场景：**
- 批量文档导入
- 知识库建设
- 文档管理系统

**示例请求：**
```http
POST /api/v1/knowledge-base/upload-file
Content-Type: multipart/form-data; boundary=boundary

--boundary
Content-Disposition: form-data; name="file"; filename="document.pdf"
Content-Type: application/pdf

<./document.pdf
--boundary--
```

```bash
curl -X POST http://localhost:8080/api/v1/knowledge-base/upload-file \
  -F "file=@document.pdf"
```

#### 3. chatWithKnowledge 方法

**接口路径：** `POST /api/v1/knowledge-base/chat`

**功能描述：** 阻塞式LLM对话接口，根据业务类型获取相关知识库数据进行问答。

**主要特性：**
- 基于知识库的智能问答
- 支持上下文对话
- 可配置检索参数
- 返回完整响应

**请求参数：**
- `query`: 用户问题（必填）
- `topK`: 检索文档数量，默认5
- `chatId`: 会话ID，用于多轮对话

**示例请求：**
```http
POST /api/v1/knowledge-base/chat
Content-Type: application/x-www-form-urlencoded

query=什么是人工智能？&topK=5&chatId=session123
```

```bash
curl -X POST http://localhost:8080/api/v1/knowledge-base/chat \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "query=什么是人工智能？&topK=5&chatId=session123"
```

#### 4. chatWithKnowledgeStream 方法

**接口路径：** `POST /api/v1/knowledge-base/chat-stream`

**功能描述：** 流式LLM对话接口，根据业务类型获取相关知识库数据进行问答。

**主要特性：**
- Server-Sent Events (SSE) 流式响应
- 实时逐字输出
- 更好的用户体验
- 支持中断和重连

**示例请求：**
```http
POST /api/v1/knowledge-base/chat-stream
Content-Type: application/x-www-form-urlencoded

query=请详细介绍机器学习的发展历程&topK=5
```

```bash
curl -X POST http://localhost:8080/api/v1/knowledge-base/chat-stream \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "query=请详细介绍机器学习的发展历程&topK=5"
```

#### 5. searchSimilarDocuments 方法

**接口路径：** `GET /api/v1/knowledge-base/search`

**功能描述：** 相似性搜索，返回与查询最相关的文档片段。

**主要特性：**
- 向量相似性搜索
- 返回原始文档片段
- 支持相似度阈值
- 快速检索

**示例请求：**
```http
GET /api/v1/knowledge-base/search?query=深度学习&topK=5&threshold=0.7
```

```bash
curl "http://localhost:8080/api/v1/knowledge-base/search?query=深度学习&topK=5&threshold=0.7"
```

## 模型配置

### Chat模型配置
```yaml
spring:
  ai:
    openai:
      api-key: ${AI_DASHSCOPE_API_KEY}
      base-url: https://dashscope.aliyuncs.com/compatible-mode
      chat:
        model: qwen-plus-latest
        options:
          temperature: 0.7
          max-tokens: 2000
```

### 嵌入模型配置
```yaml
spring:
  ai:
    openai:
      embedding:
        model: text-embedding-v3
        dimensions: 1024
```

### 向量存储配置
```yaml
spring:
  ai:
    vectorstore:
      pgvector:
        dimension: 1024
        distance-type: cosine
        index-type: ivfflat
```

## 部署指南

### Docker部署

1. 创建 `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/rag-openai-dashscope-pgvector-example-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

2. 使用Docker Compose（包含PostgreSQL）:
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - AI_DASHSCOPE_API_KEY=${AI_DASHSCOPE_API_KEY}
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/springai
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      - postgres

  postgres:
    image: pgvector/pgvector:pg16
    environment:
      - POSTGRES_DB=springai
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    ports:
      - "5432:5432"
```

3. 启动服务：
```bash
docker-compose up -d
```

### 生产环境配置

1. JVM参数优化：
```bash
java -Xms2g -Xmx4g \
     -XX:+UseG1GC \
     -XX:+UseStringDeduplication \
     -jar app.jar
```

2. 数据库连接池配置：
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
```

## 性能优化

### 1. 向量检索优化
- 创建合适的索引：
```sql
CREATE INDEX ON document_embedding USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
```

### 2. 缓存配置
```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=5m
```

### 3. 并发处理
```yaml
spring:
  task:
    execution:
      pool:
        core-size: 8
        max-size: 20
        queue-capacity: 100
```

## 测试指导

### 使用 HTTP 文件测试
模块根目录下提供了 **[rag-openai-dashscope-pgvector-example.http](./rag-openai-dashscope-pgvector-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 完整测试流程

1. 插入测试数据：
```bash
curl -X POST http://localhost:8080/api/v1/knowledge-base/insert-text \
  -d "content=Spring AI是一个面向AI工程的应用框架。"
```

2. 上传测试文档：
```bash
curl -X POST http://localhost:8080/api/v1/knowledge-base/upload-file \
  -F "file=@test-document.pdf"
```

3. 测试问答：
```bash
curl -X POST http://localhost:8080/api/v1/knowledge-base/chat \
  -d "query=Spring AI是什么？"
```

4. 测试流式响应：
```bash
curl -X POST http://localhost:8080/api/v1/knowledge-base/chat-stream \
  -d "query=介绍一下Spring AI的特性"
```

## 注意事项

1. **API Key**: 需要有效的阿里云DashScope API Key
2. **PostgreSQL**: 必须安装pgvector扩展（版本0.5.0+）
3. **网络**: 确保能访问阿里云服务（公网或VPC）
4. **文件大小**: 默认限制10MB，可在配置中调整
5. **向量维度**: 固定为1024维，与text-embedding-v3模型匹配
6. **并发控制**: 建议控制并发请求数，避免触发API限流

## 故障排查

### 常见问题

1. **向量连接失败**
   - 检查PostgreSQL是否安装pgvector
   - 验证数据库连接配置
   - 查看向量表是否创建成功

2. **API调用失败**
   - 验证API Key是否有效
   - 检查网络连接
   - 查看API调用配额

3. **文档解析失败**
   - 确认文件格式支持
   - 检查文件是否损坏
   - 查看文件大小限制

### 日志配置
```yaml
logging:
  level:
    com.alibaba.cloud.ai: DEBUG
    org.springframework.ai: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

---

*更新时间: 2025-12-09*
*版本: 1.1.0*