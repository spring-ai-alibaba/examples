# Spring-Ai-Alibaba-Chat-Example 模块

## 模块说明

本模块演示 Spring AI Alibaba 的AI对话, AI对话, AI对话, AI对话, AI对话, AI对话, AI对话, AI对话, AI对话, AI对话, AI对话, AI对话, AI对话功能。

## 接口文档

### DeepSeekChatModelController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /model/simple/chat`

**功能描述：** 最简单的使用方式，没有任何 LLMs 参数注入。

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
GET http://localhost:8080/model/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /model/stream/chat`

**功能描述：** Stream 流式调用。可以使大模型的输出信息实现打字机效果。

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
GET http://localhost:8080/model/stream/chat
```


### DeepSeekChatClientController 接口

#### 1. customOptions 方法

**接口路径：** `GET /client/ai/customOptions`

**功能描述：** 使用自定义参数调用DeepSeek模型

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/client/ai/customOptions
```

#### 2. chat 方法

**接口路径：** `GET /client/ai/generate`

**功能描述：** 执行默认提示语的 AI 生成请求

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
GET http://localhost:8080/client/ai/generate
```

#### 3. stream 方法

**接口路径：** `GET /client/ai/stream`

**功能描述：** 流式生成接口 - 支持实时获取生成过程的分块响应

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/client/ai/stream
```


### AzureOpenAiChatModelController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /azure/openaichat-model/simple/chat`

**功能描述：** 最简单的使用方式，没有任何 LLMs 参数注入。

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
GET http://localhost:8080/azure/openaichat-model/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /azure/openaichat-model/stream/chat`

**功能描述：** Stream 流式调用。可以使大模型的输出信息实现打字机效果。

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
GET http://localhost:8080/azure/openaichat-model/stream/chat
```

#### 3. clientChat 方法

**接口路径：** `GET /azure/openaichat-client/chat`

**功能描述：** 提供 clientChat 相关功能

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
GET http://localhost:8080/azure/openaichat-client/chat
```

#### 4. clientStreamChat 方法

**接口路径：** `GET /azure/openaiclient/stream/chat`

**功能描述：** 提供 clientStreamChat 相关功能

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
GET http://localhost:8080/azure/openaiclient/stream/chat
```


### MoonshotClientController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /client/simple/chat`

**功能描述：** Moonshot的 简单调用

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
GET http://localhost:8080/client/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /client/stream/chat`

**功能描述：** Moonshot的 流式调用

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
GET http://localhost:8080/client/stream/chat
```


### MoonshotModelController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /moonshot/chat-model/simple/chat`

**功能描述：** ChatModel 简单调用

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
GET http://localhost:8080/moonshot/chat-model/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /moonshot/chat-model/stream/chat`

**功能描述：** ChatModel 简单调用

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
GET http://localhost:8080/moonshot/chat-model/stream/chat
```

#### 3. customChat 方法

**接口路径：** `GET /moonshot/chat-model/custom/chat`

**功能描述：** 使用编程方式自定义 LLMs ChatOptions 参数， {@link org.springframework.ai.moonshot.MoonshotChatOptions}

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
GET http://localhost:8080/moonshot/chat-model/custom/chat
```


### DashScopeChatClientController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /client/simple/chat`

**功能描述：** ChatClient 简单调用

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
GET http://localhost:8080/client/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /client/stream/chat`

**功能描述：** 提供 streamChat 相关功能

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
GET http://localhost:8080/client/stream/chat
```

#### 3. analyzeImageByUrl 方法

**接口路径：** `GET /client/image/analyze/url`

**功能描述：** 图片分析接口 - 通过 URL

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/client/image/analyze/url
```

#### 4. analyzeImageByUpload 方法

**接口路径：** `GET /client/image/analyze/upload`

**功能描述：** 图片分析接口 - 通过文件上传

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/client/image/analyze/upload
```


### DashScopeChatModelController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /model/simple/chat`

**功能描述：** 最简单的使用方式，没有任何 LLMs 参数注入。

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
GET http://localhost:8080/model/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /model/stream/chat`

**功能描述：** Stream 流式调用。可以使大模型的输出信息实现打字机效果。

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
GET http://localhost:8080/model/stream/chat
```

#### 3. customChat 方法

**接口路径：** `GET /model/custom/chat`

**功能描述：** 使用编程方式自定义 LLMs ChatOptions 参数， {@link com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions}

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
GET http://localhost:8080/model/custom/chat
```

#### 4. dashScopeWebSearch 方法

**接口路径：** `GET /model/dashscope/web-search`

**功能描述：** DashScope 联网搜索功能演示

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
GET http://localhost:8080/model/dashscope/web-search
```

#### 5. customHttpHeaders 方法

**接口路径：** `GET /model/custom/http-headers`

**功能描述：** DashScope 自定义请求头演示

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/model/custom/http-headers
```


### ZhiPuAiChatClientController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /zhipuai/chat-client/simple/chat`

**功能描述：** ChatClient 简单调用

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
GET http://localhost:8080/zhipuai/chat-client/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /zhipuai/chat-client/stream/chat`

**功能描述：** 提供 streamChat 相关功能

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
GET http://localhost:8080/zhipuai/chat-client/stream/chat
```


### ZhiPuAiChatModelController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /zhipuai/chat-model/simple/chat`

**功能描述：** 最简单的使用方式，没有任何 LLMs 参数注入。

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
GET http://localhost:8080/zhipuai/chat-model/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /zhipuai/chat-model/stream/chat`

**功能描述：** Stream 流式调用。可以使大模型的输出信息实现打字机效果。

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
GET http://localhost:8080/zhipuai/chat-model/stream/chat
```

#### 3. customChat 方法

**接口路径：** `GET /zhipuai/chat-model/custom/chat`

**功能描述：** 使用编程方式自定义 LLMs ChatOptions 参数， {@link org.springframework.ai.zhipuai.ZhiPuAiChatOptions}

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
GET http://localhost:8080/zhipuai/chat-model/custom/chat
```


### VllmChatModelController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /vllm/chat-model/simple/chat`

**功能描述：** 最简单的使用方式，没有任何 LLMs 参数注入。

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
GET http://localhost:8080/vllm/chat-model/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /vllm/chat-model/stream/chat`

**功能描述：** Stream 流式调用。可以使大模型的输出信息实现打字机效果。

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
GET http://localhost:8080/vllm/chat-model/stream/chat
```

#### 3. customChat 方法

**接口路径：** `GET /vllm/chat-model/custom/chat`

**功能描述：** 使用编程方式自定义 LLMs ChatOptions 参数， {@link org.springframework.ai.openai.OpenAiChatOptions}

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
GET http://localhost:8080/vllm/chat-model/custom/chat
```


### OpenAiChatClientController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /openai/chat-client/simple/chat`

**功能描述：** ChatClient 简单调用

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
GET http://localhost:8080/openai/chat-client/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /openai/chat-client/stream/chat`

**功能描述：** 提供 streamChat 相关功能

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
GET http://localhost:8080/openai/chat-client/stream/chat
```

#### 3. simpleChat 方法

**接口路径：** `GET /openai/chat-client/stream/response`

**功能描述：** ChatClient 流式响应

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
GET http://localhost:8080/openai/chat-client/stream/response
```


### OpenAiChatModelController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /openai/chat-model/simple/chat`

**功能描述：** 最简单的使用方式，没有任何 LLMs 参数注入。

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
GET http://localhost:8080/openai/chat-model/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /openai/chat-model/stream/chat`

**功能描述：** Stream 流式调用。可以使大模型的输出信息实现打字机效果。

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
GET http://localhost:8080/openai/chat-model/stream/chat
```

#### 3. customChat 方法

**接口路径：** `GET /openai/chat-model/custom/chat`

**功能描述：** 使用编程方式自定义 LLMs ChatOptions 参数， {@link org.springframework.ai.openai.OpenAiChatOptions}

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
GET http://localhost:8080/openai/chat-model/custom/chat
```

#### 4. jsonChat 方法

**接口路径：** `GET /openai/chat-model/custom/chat/json-mode`

**功能描述：** JSON mode：通过设置 response_format 参数为 JSON 类型，使大模型返回标准的 JSON 格式数据。

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
GET http://localhost:8080/openai/chat-model/custom/chat/json-mode
```


### QWQChatClientController 接口

#### 1. streamChat 方法

**接口路径：** `GET /client/stream/chat`

**功能描述：** temperature、

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
GET http://localhost:8080/client/stream/chat
```


### OllamaChatModelController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /model/simple/chat`

**功能描述：** 最简单的使用方式，没有任何 LLMs 参数注入。

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
GET http://localhost:8080/model/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /model/stream/chat`

**功能描述：** Stream 流式调用。可以使大模型的输出信息实现打字机效果。

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
GET http://localhost:8080/model/stream/chat
```

#### 3. customChat 方法

**接口路径：** `GET /model/custom/chat`

**功能描述：** 使用编程方式自定义 LLMs ChatOptions 参数， {@link OllamaChatOptions}。

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
GET http://localhost:8080/model/custom/chat
```


### OllamaChatClientController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /client/simple/chat`

**功能描述：** ChatClient 简单调用

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
GET http://localhost:8080/client/simple/chat
```

#### 2. streamChat 方法

**接口路径：** `GET /client/stream/chat`

**功能描述：** 提供 streamChat 相关功能

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
GET http://localhost:8080/client/stream/chat
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 curl 测试
```bash
# simpleChat 接口测试
curl "http://localhost:8080/model/simple/chat"
```

```bash
# customOptions 接口测试
curl "http://localhost:8080/client/ai/customOptions"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:29:55*
