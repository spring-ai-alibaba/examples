# Spring-Ai-Alibaba-Nl2Sql-Example 模块

## 模块说明

本模块演示 Spring AI Alibaba 的AI对话, 向量数据库, 向量数据库功能。

## 接口文档

### Nl2sqlForGraphController 接口

#### 1. search 方法

**接口路径：** `GET /nl2sql/search`

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
GET http://localhost:8080/nl2sql/search
```


### AnalyticNl2SqlController 接口

#### 1. nl2Sql 方法

**接口路径：** `GET /chat`

**功能描述：** 提供 nl2Sql 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/chat
```


### HomeController 接口

#### 1. index 方法

**接口路径：** `GET /`

**功能描述：** 提供 index 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/
```

#### 2. index 方法

**接口路径：** `GET /`

**功能描述：** 提供 index 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/
```


### SimpleChatController 接口

#### 1. simpleNl2Sql 方法

**接口路径：** `GET /simpleChat`

**功能描述：** 提供 simpleNl2Sql 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/simpleChat
```


### SimpleVectorManagementController 接口

#### 1. addEvidence 方法

**接口路径：** `GET /simple/simple`

**功能描述：** 提供 addEvidence 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/simple/simple
```

#### 2. addEvidence 方法

**接口路径：** `GET /simple/add/evidence`

**功能描述：** 提供 addEvidence 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/simple/add/evidence
```

#### 3. search 方法

**接口路径：** `GET /simple/search`

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
GET http://localhost:8080/simple/search
```

#### 4. deleteDocuments 方法

**接口路径：** `GET /simple/delete`

**功能描述：** 提供 deleteDocuments 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/simple/delete
```

#### 5. schema 方法

**接口路径：** `GET /simple/init/schema`

**功能描述：** 提供 schema 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/simple/init/schema
```


### AnalyticDbVectorManagementController 接口

#### 1. addEvidence 方法

**接口路径：** `GET /add/evidence`

**功能描述：** 提供 addEvidence 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/add/evidence
```

#### 2. search 方法

**接口路径：** `GET /search`

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
GET http://localhost:8080/search
```

#### 3. deleteDocuments 方法

**接口路径：** `GET /delete`

**功能描述：** 提供 deleteDocuments 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/delete
```

#### 4. schema 方法

**接口路径：** `GET /init/schema`

**功能描述：** 提供 schema 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/init/schema
```


### HomeController 接口

#### 1. index 方法

**接口路径：** `GET /`

**功能描述：** 提供 index 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/
```

#### 2. index 方法

**接口路径：** `GET /`

**功能描述：** 提供 index 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/
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
# search 接口测试
curl "http://localhost:8080/nl2sql/search"
```

```bash
# nl2Sql 接口测试
curl "http://localhost:8080/chat"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:30:02*
