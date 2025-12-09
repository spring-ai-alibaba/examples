# Module-Rag 模块

## 模块说明

Spring AI Module RAG: https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#modules。

## 接口文档

### ModuleRAGTranslationController 接口

#### 1. rag 方法

**接口路径：** `GET /module-rag/rag/translation`

**功能描述：** 提供 rag 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/module-rag/rag/translation
```


### ModuleRAGRewriteController 接口

#### 1. rag 方法

**接口路径：** `GET /module-rag/rag/rewrite`

**功能描述：** 提供 rag 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/module-rag/rag/rewrite
```


### ModuleRAGCompressionController 接口

#### 1. rag 方法

**接口路径：** `GET /module-rag/rag/compression/{chatId}`

**功能描述：** 提供 rag 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/module-rag/rag/compression/{chatId}
```


### ModuleRAGMemoryController 接口

#### 1. chatWithDocument 方法

**接口路径：** `GET /module-rag/rag/memory/{chatId}`

**功能描述：** 提供 chatWithDocument 相关功能

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
GET http://localhost:8080/module-rag/rag/memory/{chatId}
```


### ModuleRAGBasicController 接口

#### 1. chatWithDocument 方法

**接口路径：** `GET /module-rag/rag/basic`

**功能描述：** 提供 chatWithDocument 相关功能

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
GET http://localhost:8080/module-rag/rag/basic
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-ai-alibaba-starter-dashscope**: 核心依赖
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-starter-vector-store-elasticsearch**: 核心依赖
- **elasticsearch-java**: 核心依赖
- **elasticsearch-rest-client**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 HTTP 文件测试
模块根目录下提供了 **[module-rag.http](./module-rag.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# rag 接口测试
curl "http://localhost:8080/module-rag/rag/translation"
```

```bash
# rag 接口测试
curl "http://localhost:8080/module-rag/rag/rewrite"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:31:02*
