# Spring AI Alibaba RAG Pgvector Example

本模块展示了如何使用 Spring AI Alibaba 结合 PostgreSQL + pgvector 扩展实现检索增强生成（RAG）功能。

## 功能概述

RAG（Retrieval Augmented Generation）示例包括两个主要流程：文档导入和检索生成。

### RAG 流程详解

#### 文档导入流程
1. **解析文档**: 读取和解析输入文档
2. **文档分块**: 使用适当的分块大小和分隔符将文档分割成块
3. **向量转换**: 将文本块转换为向量嵌入
4. **存储到向量数据库**: 将文本和向量嵌入存储到向量数据库，包括必要的元数据

#### RAG 检索生成流程
1. **向量检索**: 从向量数据库中使用查询检索相关文档块
2. **重新排序**: 对检索到的文档块进行重新排序，获取与查询的相关性评分
3. **结果生成**: 基于过滤后的文档块生成最终回答

## 数据库配置

### PostgreSQL + pgvector 设置

首先需要在 PostgreSQL 数据库中创建必要的表和索引：

```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE IF NOT EXISTS vector_store (
id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
content text,
metadata json,
embedding vector(1536)
);

CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);
```

### Docker 部署

可以使用提供的 Docker Compose 配置快速搭建 PostgreSQL + pgvector 环境：

详细部署说明请参考：[docker-compose/pgvector/README.md](../../docker-compose/pgvector/README.md)

## 接口文档

### RagController 接口

#### 1. importFileV2 方法

**接口路径：** `POST /ai/rag/importFileV2`

**功能描述：** 导入文件进行 RAG 处理

**请求参数：**
- `file` (必需): 要导入的文件

**使用示例：**
```bash
curl -X POST http://localhost:8080/ai/rag/importFileV2 \
  -F "file=@/path/to/your/file"
```

此端点允许您导入文件进行 RAG 处理。文件将被处理并存储在向量数据库中。

#### 2. search 方法

**接口路径：** `GET /ai/rag/search`

**功能描述：** 在特定文件内容中进行搜索查询

**请求参数：**
- `messages` (必需): 搜索查询内容
- `fileId` (必需): 要搜索的文件 ID

**使用示例：**
```bash
curl -G 'http://localhost:8080/ai/rag/search' \
  --data-urlencode 'messages=what is alibaba?' \
  --data-urlencode 'fileId={fileId}'
```

此端点在特定文件的内容中执行搜索查询。请将 `{fileId}` 替换为实际的文件 ID。

#### 3. deleteFiles 方法

**接口路径：** `DELETE /ai/rag/deleteFiles`

**功能描述：** 从向量数据库中删除特定文件

**请求参数：**
- `fileId` (必需): 要删除的文件 ID

**使用示例：**
```bash
curl -X DELETE 'http://localhost:8080/ai/rag/deleteFiles?fileId={fileId}'
```

此端点允许您从向量数据库中删除特定文件。请将 `{fileId}` 替换为要删除的实际文件 ID。

## 快速开始

### 环境要求
- JDK 17
- PostgreSQL 12+ (已安装 pgvector 扩展)
- Spring Boot 3.x
- DashScope API Key

### 安装步骤

1. **设置 PostgreSQL + pgvector**
   - 安装 PostgreSQL 12 或更高版本
   - 安装 pgvector 扩展
   - 执行上述数据库创建脚本

2. **配置应用**
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/your_database
       username: ${DB_USERNAME}
       password: ${DB_PASSWORD}
     ai:
       dashscope:
         api-key: ${DASHSCOPE_API_KEY}
   ```

3. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

4. **测试功能**
   - 导入文件
   - 执行搜索
   - 删除文件（如需要）

## 技术实现

### 核心组件
- **Spring Boot**: Web应用框架
- **Spring AI Alibaba**: AI功能集成
- **PostgreSQL**: 关系型数据库
- **pgvector**: PostgreSQL 向量扩展
- **DashScope**: 向量嵌入和文本生成服务

### 向量数据库优势
- **ACID 事务**: 确保数据一致性
- **成熟生态**: PostgreSQL 的丰富生态系统
- **高性能**: HNSW 索引提供快速的向量搜索
- **可扩展性**: 支持大规模向量存储

### RAG 工作流程
1. **文件上传**: 接收用户上传的文档
2. **文档处理**: 解析、分块、向量化
3. **向量存储**: 存储到 PostgreSQL 向量表
4. **查询处理**: 用户查询向量化
5. **相似度搜索**: 在向量表中查找相关内容
6. **结果生成**: 基于检索结果生成回答

## 配置要求

### application.yml 配置示例
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spring_ai_rag
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-turbo
      embedding:
        options:
          model: text-embedding-v2
```

### 环境变量
- `DB_USERNAME`: PostgreSQL 数据库用户名
- `DB_PASSWORD`: PostgreSQL 数据库密码
- `DASHSCOPE_API_KEY`: 阿里云DashScope API密钥

### Maven 依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

## 注意事项

1. **pgvector 版本**: 确保使用兼容的 pgvector 版本
2. **向量维度**: 确保向量维度与嵌入模型匹配（1536）
3. **索引类型**: HNSW 索引适合高维向量搜索
4. **内存管理**: 大规模向量数据需要适当调整 PostgreSQL 内存配置
5. **API 密钥安全**: 妥善保管 DashScope API 密钥

## 性能优化

### 数据库优化
- 调整 `shared_buffers` 和 `work_mem` 参数
- 使用适当的连接池配置
- 定期执行 `VACUUM` 和 `ANALYZE`

### 向量搜索优化
- 选择合适的 HNSW 参数（M、ef_construction）
- 根据数据量调整索引策略
- 考虑分片或分区策略

## HTTP 测试文件

可以使用生成的 `rag-pgvector-example.http` 文件在 IDE 中直接测试所有接口。