# Spring AI Alibaba RAG Elasticsearch Example

本模块展示了如何使用 Spring AI Alibaba 结合 Elasticsearch 实现检索增强生成（RAG）功能。

## 功能概述

RAG（Retrieval Augmented Generation）示例包括两个主要流程：文档导入和检索生成。

### Local RAG 流程

#### 文档导入流程
1. **解析文档**: 读取和解析输入文档
2. **文档分块**: 使用适当的分块大小和分隔符将文档分割成块
3. **向量转换**: 将文本块转换为向量嵌入
4. **存储到向量数据库**: 将文本和向量嵌入存储到向量数据库，包括必要的元数据

#### RAG 检索生成流程
1. **向量检索**: 从向量数据库中使用查询检索相关文档块
2. **重新排序**: 对检索到的文档块进行重新排序，获取与查询的相关性评分
3. **结果生成**: 基于过滤后的文档块生成最终回答

### Cloud RAG 流程

#### 文档导入流程
1. **云端处理**: 在云端解析文档并使用适当的分块大小和分隔符分割文档
2. **云端向量存储**: 将文档块添加到云端向量数据库（文档块在云端转换为向量嵌入）

#### RAG 检索生成流程
1. **向量检索**: 从向量数据库中使用查询检索相关文档块
2. **结果生成**: 基于过滤后的文档块生成最终回答

## 接口文档

### LocalRagController 接口

#### 1. importDocument 方法

**接口路径：** `GET /ai/rag/importDocument`

**功能描述：** 导入文档到本地 Elasticsearch 向量存储

**主要特性：**
- 基于 Spring Boot REST API 实现
- 支持 UTF-8 编码
- 自动文档分块和向量化

**使用示例：**
```bash
curl -X GET http://127.0.0.1:8080/ai/rag/importDocument
```

#### 2. rag 方法

**接口路径：** `GET /ai/rag`

**功能描述：** 基于本地 Elasticsearch 的 RAG 问答

**请求参数：**
- `message` (必需): 用户查询消息

**使用示例：**
```bash
curl -G 'http://127.0.0.1:8080/ai/rag' --data-urlencode 'message=如何快速开始spring ai alibaba'
```

### CloudRagController 接口

#### 1. cloudImportDocument 方法

**接口路径：** `GET /ai/cloud/rag/importDocument`

**功能描述：** 导入文档到云端向量存储

**使用示例：**
```bash
curl -X GET http://127.0.0.1:8080/ai/cloud/rag/importDocument
```

#### 2. cloudRag 方法

**接口路径：** `GET /ai/cloud/rag`

**功能描述：** 基于云端向量存储的 RAG 问答

**请求参数：**
- `message` (必需): 用户查询消息

**使用示例：**
```bash
curl -G 'http://127.0.0.1:8080/ai/cloud/rag' --data-urlencode 'message=如何快速开始spring ai alibaba'
```

## 快速开始

### 环境要求
- JDK 17
- Elasticsearch 8.x
- Spring Boot 3.x
- DashScope API Key

### 运行步骤

#### Local RAG 示例
```bash
1. 启动应用
2. 导入文档
curl -X GET http://127.0.0.1:8080/ai/rag/importDocument

3. 检索和生成
curl -G 'http://127.0.0.1:8080/ai/rag' --data-urlencode 'message=如何快速开始spring ai alibaba'
```

#### Cloud RAG 示例
```bash
1. 启动应用
2. 导入文档
curl -X GET http://127.0.0.1:8080/ai/cloud/rag/importDocument

3. 检索和生成
curl -G 'http://127.0.0.1:8080/ai/cloud/rag' --data-urlencode 'message=如何快速开始spring ai alibaba'
```

## 技术实现

### 核心组件
- **Spring Boot**: Web应用框架
- **Spring AI Alibaba**: AI功能集成
- **Elasticsearch**: 向量存储和搜索
- **DashScope**: 向量嵌入和文本生成服务

### RAG流程详解

#### 文档处理
- **文档解析**: 支持多种文档格式
- **智能分块**: 基于语义和结构的智能分割
- **向量化**: 使用高维向量表示文本语义

#### 检索增强
- **相似度搜索**: 基于余弦相似度的向量检索
- **相关性评分**: 对检索结果进行精确评分
- **上下文整合**: 将检索内容与原始查询结合

## 配置要求

### application.yml 配置示例
```yaml
spring:
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
    elasticsearch:
      uris: http://localhost:9200
      username: ${ELASTICSEARCH_USERNAME:}
      password: ${ELASTICSEARCH_PASSWORD:}
```

### Elasticsearch 配置
- 版本：8.x 或更高版本
- 需要安装向量搜索插件（如需要）
- 确保有足够的存储空间

### 环境变量
- `DASHSCOPE_API_KEY`: 阿里云DashScope API密钥
- `ELASTICSEARCH_USERNAME`: Elasticsearch 用户名（可选）
- `ELASTICSEARCH_PASSWORD`: Elasticsearch 密码（可选）

## 注意事项

1. **Elasticsearch 版本兼容性**: 确保使用支持向量搜索的版本
2. **API 密钥安全**: 妥善保管 DashScope API 密钥
3. **内存配置**: Elasticsearch 需要足够的内存来处理向量操作
4. **网络连接**: 确保能够访问 DashScope API 服务

## HTTP 测试文件

可以使用生成的 `rag-elasticsearch-example.http` 文件在 IDE 中直接测试所有接口。