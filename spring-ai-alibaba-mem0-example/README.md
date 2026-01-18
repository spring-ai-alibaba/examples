# Spring AI Alibaba Chat Memory Example
本示例演示如何使用 Mem0 Memory 聊天记忆功能。

## 接口文档
### Mem0MemoryController 接口

#### 1. call 方法

**接口路径：** `GET /advisor/memory/mem0/call`

**功能描述：** 提供 call 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/advisor/memory/mem0/call
```

#### 2. messages 方法

**接口路径：** `GET /advisor/memory/mem0/messages`

**功能描述：** 提供 messages 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/advisor/memory/mem0/messages
```

#### 3. test 方法

**接口路径：** `GET /advisor/memory/mem0/test`

**功能描述：** 提供 test 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/advisor/memory/mem0/test
```
## 技术实现
### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-ai-alibaba-starter-memory-mem0**: 核心依赖
- **spring-boot-starter-web**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic
## 测试指导
### 使用 HTTP 文件测试
模块根目录下提供了 **[spring-ai-alibaba-mem0-example.http](./spring-ai-alibaba-mem0-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# call 接口测试
curl "http://localhost:8080/advisor/memory/mem0/call"
```
## 注意事项
1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-11 00:51:02*
## 模块说明
本示例演示如何使用 Mem0 Memory 聊天记忆功能。。

## Spring AI Alibaba Mem0 Memory 实现
1. spring ai 提供了基于内存的 InMemory 实现； 
2. Spring AI Alibaba 提供了基于 Redis 和 JDBC 的 ChatMemory 实现。
    
    - MySQL
    - PostgreSQL
    - Oracle
    - SQLite
    - SqlServer

## Example 演示
下面以 Redis 和 SQLite JDBC 为例。

> 使用 [Docker Compose 启动 Mem0 服务](../docker-compose/mem0/README.md)。
> 配置IDEA环境变量, AI_DASHSCOPE_API_KEY=sk-xxx;AI_DEEPSEEK_API_KEY=sk-xxx

在体验示例之前，确保Docker已经启动容器


在一轮问答中，您应该得看到这样的回复：
参考[chat-memory.http](../spring-ai-alibaba-mem0-example/mem0-memory.http)

```shell

### 聊天记忆
GET http://localhost:8080/advisor/memory/mem0/call?message=你好，我是万能的喵，我爱玩三角洲行动&user_id=miao

### 获取记忆
GET http://localhost:8080/advisor/memory/mem0/messages?query=我的爱好是什么&user_id=miao

### 测试
GET http://localhost:8080/advisor/memory/mem0/test
```

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:41:32*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:51:02*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*