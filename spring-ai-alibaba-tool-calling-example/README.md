# Spring-Ai-Alibaba-Tool-Calling-Example 模块

## 模块说明

Demonstrate four approaches to ToolCalling with four distinct examples here:。

## 接口文档

### TimeController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /time/chat`

**功能描述：** No Tool

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
GET http://localhost:8080/time/chat
```

#### 2. chatWithTimeFunction 方法

**接口路径：** `GET /time/chat-tool-method`

**功能描述：** 提供 chatWithTimeFunction 相关功能

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
GET http://localhost:8080/time/chat-tool-method
```


### AddressController 接口

#### 1. chat 方法

**接口路径：** `GET /address/chat`

**功能描述：** No Tool

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
GET http://localhost:8080/address/chat
```

#### 2. chatWithBaiduMap 方法

**接口路径：** `GET /address/chat-method-tool-callback`

**功能描述：** Methods as Tools - MethodToolCallback

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
GET http://localhost:8080/address/chat-method-tool-callback
```


### BaiduTranslateController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /translate/chat`

**功能描述：** No Tool

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
GET http://localhost:8080/translate/chat
```

#### 2. chatTranslateFunction 方法

**接口路径：** `GET /translate/chat-tool-function-callback`

**功能描述：** 提供 chatTranslateFunction 相关功能

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
GET http://localhost:8080/translate/chat-tool-function-callback
```


### WeatherController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /weather/chat`

**功能描述：** No Tool

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
GET http://localhost:8080/weather/chat
```

#### 2. chatWithWeatherFunction 方法

**接口路径：** `GET /weather/chat-tool-function-name`

**功能描述：** 提供 chatWithWeatherFunction 相关功能

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
GET http://localhost:8080/weather/chat-tool-function-name
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-alibaba-starter-dashscope**: 核心依赖
- **spring-ai-alibaba-starter-tool-calling-baidutranslate**: 核心依赖
- **spring-ai-alibaba-starter-tool-calling-weather**: 核心依赖
- **spring-ai-alibaba-starter-tool-calling-baidumap**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 HTTP 文件测试
模块根目录下提供了 **[spring-ai-alibaba-tool-calling-example.http](./spring-ai-alibaba-tool-calling-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# simpleChat 接口测试
curl "http://localhost:8080/time/chat"
```

```bash
# chat 接口测试
curl "http://localhost:8080/address/chat"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:29:58*
