# Multiagent-Openmanus 模块

## 模块说明

1. 配置模型 API-KEY：。

## 接口文档

### OpenmanusController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /manus/chat`

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
GET http://localhost:8080/manus/chat
```


### OpenmanusHumanController 接口

#### 1. initGraph 方法

**接口路径：** `GET /manus/human/init`

**功能描述：** 提供 initGraph 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/manus/human/init
```

#### 2. simpleChat 方法

**接口路径：** `GET /manus/human/chat`

**功能描述：** 提供 simpleChat 相关功能

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
GET http://localhost:8080/manus/human/chat
```

#### 3. resume 方法

**接口路径：** `GET /manus/human/resume`

**功能描述：** 提供 resume 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/manus/human/resume
```

#### 4. resumeToNextStep 方法

**接口路径：** `GET /manus/human/resume-to-next-step`

**功能描述：** 提供 resumeToNextStep 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/manus/human/resume-to-next-step
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-boot-dependencies**: 核心依赖
- **spring-ai-bom**: 核心依赖
- **spring-ai-alibaba-bom**: 核心依赖
- **plantuml-mit**: 核心依赖
- **jsoup**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 HTTP 文件测试
模块根目录下提供了 **[multiagent-openmanus.http](./multiagent-openmanus.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# simpleChat 接口测试
curl "http://localhost:8080/manus/chat"
```

```bash
# initGraph 接口测试
curl "http://localhost:8080/manus/human/init"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:30:56*
