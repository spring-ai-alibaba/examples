# Rag-Openai-Dashscope-Pgvector-Example 模块

## 模块说明

基于Spring AI的RAG（检索增强生成）服务器，使用阿里云通义千问模型（OpenAI兼容模式）。。

## 接口文档

### KnowledgeBaseController 接口

#### 1. insertTextContent 方法

**接口路径：** `GET /api/v1/knowledge-base/insert-text`

**功能描述：** 将字符串内容插入到向量库中。

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/knowledge-base/insert-text
```

#### 2. uploadFileByType 方法

**接口路径：** `GET /api/v1/knowledge-base/upload-file`

**功能描述：** 根据文件类型动态选择Reader加载文件到知识库。

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/knowledge-base/upload-file
```

#### 3. chatWithKnowledge 方法

**接口路径：** `GET /api/v1/knowledge-base/chat`

**功能描述：** 阻塞式LLM对话接口，根据业务类型获取相关知识库数据进行问答。

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/knowledge-base/chat
```

#### 4. chatWithKnowledgeStream 方法

**接口路径：** `GET /api/v1/knowledge-base/chat-stream`

**功能描述：** 流式LLM对话接口，根据业务类型获取相关知识库数据进行问答。

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/knowledge-base/chat-stream
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-ai-bom**: 核心依赖
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-starter-model-openai**: 核心依赖
- **spring-ai-pgvector-store**: 核心依赖
- **spring-ai-autoconfigure-vector-store-pgvector**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 HTTP 文件测试
模块根目录下提供了 **[rag-openai-dashscope-pgvector-example.http](./rag-openai-dashscope-pgvector-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# insertTextContent 接口测试
curl "http://localhost:8080/api/v1/knowledge-base/insert-text"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:31:02*
