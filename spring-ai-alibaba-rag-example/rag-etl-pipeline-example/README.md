# Rag-Etl-Pipeline-Example 模块

## 模块说明

本模块是 rag-etl-pipeline-example 模块，包含 3 个控制器。

## 接口文档

### TransformerController 接口

#### 1. tokenTextSplitter 方法

**接口路径：** `GET /transformer/token-text-splitter`

**功能描述：** 提供 tokenTextSplitter 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/transformer/token-text-splitter
```

#### 2. contentFormatTransformer 方法

**接口路径：** `GET /transformer/content-format-transformer`

**功能描述：** 提供 contentFormatTransformer 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/transformer/content-format-transformer
```

#### 3. keywordMetadataEnricher 方法

**接口路径：** `GET /transformer/keyword-metadata-enricher`

**功能描述：** 提供 keywordMetadataEnricher 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/transformer/keyword-metadata-enricher
```

#### 4. summaryMetadataEnricher 方法

**接口路径：** `GET /transformer/summary-metadata-enricher`

**功能描述：** 提供 summaryMetadataEnricher 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/transformer/summary-metadata-enricher
```


### WriterController 接口

#### 1. writeFile 方法

**接口路径：** `GET /writer/file`

**功能描述：** 提供 writeFile 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/writer/file
```

#### 2. writeVector 方法

**接口路径：** `GET /writer/vector`

**功能描述：** 提供 writeVector 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/writer/vector
```

#### 3. search 方法

**接口路径：** `GET /writer/search`

**功能描述：** 提供 search 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 信息检索
- 知识查询
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/writer/search
```


### ReaderController 接口

#### 1. readText 方法

**接口路径：** `GET /reader/reader`

**功能描述：** @author yingzi

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/reader/reader
```

#### 2. readText 方法

**接口路径：** `GET /reader/text`

**功能描述：** @author yingzi

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/reader/text
```

#### 3. readJson 方法

**接口路径：** `GET /reader/json`

**功能描述：** 提供 readJson 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/reader/json
```

#### 4. readPdfPage 方法

**接口路径：** `GET /reader/pdf-page`

**功能描述：** 提供 readPdfPage 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/reader/pdf-page
```

#### 5. readPdfParagraph 方法

**接口路径：** `GET /reader/pdf-paragraph`

**功能描述：** 提供 readPdfParagraph 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/reader/pdf-paragraph
```

#### 6. readMarkdown 方法

**接口路径：** `GET /reader/markdown`

**功能描述：** 提供 readMarkdown 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/reader/markdown
```

#### 7. readHtml 方法

**接口路径：** `GET /reader/html`

**功能描述：** 提供 readHtml 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/reader/html
```

#### 8. readTika 方法

**接口路径：** `GET /reader/tika`

**功能描述：** 提供 readTika 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/reader/tika
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-alibaba-starter-dashscope**: 核心依赖
- **spring-ai-commons**: 核心依赖
- **spring-ai-rag**: 核心依赖
- **spring-ai-jsoup-document-reader**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 HTTP 文件测试
模块根目录下提供了 **[rag-etl-pipeline-example.http](./rag-etl-pipeline-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# tokenTextSplitter 接口测试
curl "http://localhost:8080/transformer/token-text-splitter"
```

```bash
# writeFile 接口测试
curl "http://localhost:8080/writer/file"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:31:06*
