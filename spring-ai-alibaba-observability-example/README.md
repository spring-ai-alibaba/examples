# Spring-Ai-Alibaba-Observability-Example 模块

## 模块说明

> 以下最佳实践基于 Spring AI 1.0.0 和 Spring AI Alibaba 1.0.0.2 版本。。

## 接口文档

### EmbeddingModelController 接口

#### 1. embeddingGenericOpts 方法

**接口路径：** `GET /observability/embedding/generic`

**功能描述：** 提供 embeddingGenericOpts 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/observability/embedding/generic
```


### ImageModelController 接口

#### 1. image 方法

**接口路径：** `GET /observability/image/generate`

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
GET http://localhost:8080/observability/image/generate
```


### ImageModelController 接口

#### 1. image 方法

**接口路径：** `GET /observability/image/generate`

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
GET http://localhost:8080/observability/image/generate
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-boot-starter-web**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 curl 测试
```bash
# embeddingGenericOpts 接口测试
curl "http://localhost:8080/observability/embedding/generic"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:30:03*
