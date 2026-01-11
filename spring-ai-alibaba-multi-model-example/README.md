# Spring-Ai-Alibaba-Multi-Model-Example 模块

## 模块说明

本模块演示 Spring AI Alibaba 的AI对话功能。

## 接口文档

### OpenAiChatClientController 接口

#### 1. analyzeImageByUrl 方法

**接口路径：** `GET /openai/client/image/analyze/url`

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
GET http://localhost:8080/openai/client/image/analyze/url
```

#### 2. analyzeImageByUpload 方法

**接口路径：** `GET /openai/client/image/analyze/upload`

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
GET http://localhost:8080/openai/client/image/analyze/upload
```


### MultiModelController 接口

#### 1. image 方法

**接口路径：** `GET /dashscope/multi/image`

**功能描述：** 提供 image 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/dashscope/multi/image
```

#### 2. video 方法

**接口路径：** `GET /dashscope/multi/video`

**功能描述：** 提供 video 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/dashscope/multi/video
```

#### 3. audio 方法

**接口路径：** `GET /dashscope/multi/audio`

**功能描述：** 提供 audio 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/dashscope/multi/audio
```

#### 4. imagesBinary 方法

**接口路径：** `GET /dashscope/multi/image/bin`

**功能描述：** 提供 imagesBinary 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/dashscope/multi/image/bin
```

#### 5. streamImage 方法

**接口路径：** `GET /dashscope/multi/stream/image`

**功能描述：** 提供 streamImage 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/dashscope/multi/stream/image
```


### MultiModelController 接口

#### 1. image 方法

**接口路径：** `GET /api/image`

**功能描述：** 提供 image 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/image
```

#### 2. streamImage 方法

**接口路径：** `GET /api/stream/image`

**功能描述：** 提供 streamImage 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/stream/image
```

#### 3. video 方法

**接口路径：** `GET /api/video`

**功能描述：** 提供 video 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/video
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
# analyzeImageByUrl 接口测试
curl "http://localhost:8080/openai/client/image/analyze/url"
```

```bash
# image 接口测试
curl "http://localhost:8080/dashscope/multi/image"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:30:02*
