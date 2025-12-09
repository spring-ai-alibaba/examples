# Spring-Ai-Alibaba-Playground 模块

## 模块说明

运行如下命令，可以使用 Docker 快速启动 Playground 项目。请访问 [阿里云百炼 API-KEY](https://bailian.console.aliyun.com/?tab=model#/api-key)获得 API-KEY 并设置 `AI_DASHSCOPE_API_KEY=your_api_key`。。

## 接口文档

### SAAToolsController 接口

#### 1. toolCallChat 方法

**接口路径：** `GET /api/v1/tool-call`

**功能描述：** http://127.0.0.1:8080/api/v1/tool-call?prompt="使用百度翻译将隐私计算翻译为英文"

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
GET http://localhost:8080/api/v1/tool-call
```


### SAAVideoController 接口

#### 1. videoQuestionAnswering 方法

**接口路径：** `GET /api/v1/video-qa`

**功能描述：** 视频问答接口

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/video-qa
```

#### 2. genVideo 方法

**接口路径：** `GET /api/v1/video-gen`

**功能描述：** 此处只使用最简单的文生视频接口，更多 example 查阅：

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/video-gen
```


### SAAAudioController 接口

#### 1. audioToText 方法

**接口路径：** `GET /api/v1//audio2text`

**功能描述：** used to convert audio to text output

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1//audio2text
```


### SAAMcpController 接口

#### 1. mcpChat 方法

**接口路径：** `GET /api/v1/inner/mcp`

**功能描述：** 内部接口不应该直接被 web 请求！

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
GET http://localhost:8080/api/v1/inner/mcp
```

#### 2. mcpList 方法

**接口路径：** `GET /api/v1/mcp-list`

**功能描述：** 提供 mcpList 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/mcp-list
```

#### 3. mcpRun 方法

**接口路径：** `GET /api/v1/mcp-run`

**功能描述：** 提供 mcpRun 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/mcp-run
```


### SAAImageController 接口

#### 1. image2text 方法

**接口路径：** `GET /api/v1/image2text`

**功能描述：** Image Recognition

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/image2text
```

#### 2. text2Image 方法

**接口路径：** `GET /api/v1/text2image`

**功能描述：** 提供 text2Image 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/text2image
```


### SAABaseController 接口

#### 1. health 方法

**接口路径：** `GET /api/v1/health`

**功能描述：** 提供 health 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/health
```


### SAAChatController 接口

#### 1. chat 方法

**接口路径：** `GET /api/v1/chat`

**功能描述：** Send the specified parameters to get the model response.

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
GET http://localhost:8080/api/v1/chat
```

#### 2. deepThinkingChat 方法

**接口路径：** `GET /api/v1/deep-thinking/chat`

**功能描述：** 提供 deepThinkingChat 相关功能

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
GET http://localhost:8080/api/v1/deep-thinking/chat
```


### SAASummarizerController 接口

#### 1. summary 方法

**接口路径：** `GET /api/v1/summarizer`

**功能描述：** 提供 summary 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/v1/summarizer
```


### SAAWebSearchController 接口

#### 1. search 方法

**接口路径：** `GET /api/v1/search`

**功能描述：** ModuleRag: 基于模块化rag的iqs在线搜索

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
GET http://localhost:8080/api/v1/search
```


### SAARAGController 接口

#### 1. ragChat 方法

**接口路径：** `GET /api/v1/rag`

**功能描述：** 提供 ragChat 相关功能

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
GET http://localhost:8080/api/v1/rag
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **logback-classic**: 核心依赖
- **logback-core**: 核心依赖
- **spring-boot-starter-web**: 核心依赖
- **spring-boot-starter-webflux**: 核心依赖
- **spring-boot-starter-validation**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 HTTP 文件测试
模块根目录下提供了 **[spring-ai-alibaba-playground.http](./spring-ai-alibaba-playground.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# toolCallChat 接口测试
curl "http://localhost:8080/api/v1/tool-call"
```

```bash
# videoQuestionAnswering 接口测试
curl "http://localhost:8080/api/v1/video-qa"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:29:58*
