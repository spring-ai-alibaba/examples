# Spring-Ai-Alibaba-Prompt-Example 模块

## 模块说明

此示例将演示如何使用 Spring AI 提供的 Prompt 模板配置功能。。

## 接口文档

### StuffController 接口

#### 1. completion 方法

**接口路径：** `GET /prompt/ai/stuff`

**功能描述：** 演示使用特定的 prompt 上下文信息以增强大模型的回答。

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/prompt/ai/stuff
```


### RoleController 接口

#### 1. generate 方法

**接口路径：** `GET /example/ai/roles`

**功能描述：** 加载 System prompt tmpl.

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/example/ai/roles
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-ai-alibaba-starter-dashscope**: 核心依赖
- **spring-boot-starter-web**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 HTTP 文件测试
模块根目录下提供了 **[spring-ai-alibaba-prompt-example.http](./spring-ai-alibaba-prompt-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# completion 接口测试
curl "http://localhost:8080/prompt/ai/stuff"
```

```bash
# generate 接口测试
curl "http://localhost:8080/example/ai/roles"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:29:55*
