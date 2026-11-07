# React-Agent-Example 模块

## 模块说明

本模块是 react-agent-example 模块，包含 1 个控制器。

## 接口文档

### AgentController 接口

#### 1. feedback 方法

**接口路径：** `GET /feedback`

**功能描述：** 提供 feedback 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/feedback
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-boot-starter**: 核心依赖
- **spring-boot-starter-test**: 核心依赖
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-alibaba-agent-framework**: 核心依赖
- **spring-ai-alibaba-starter-dashscope**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 离线文件工具回归

`file_write` 使用 UTF-8 写入文件，与 `file_read` 的读取编码一致，不受 JVM 默认字符集影响。

在本模块目录执行：

```bash
mvn -Dtest=FileWriteToolTests test
```

测试在独立 JVM 中覆盖 ASCII、ISO-8859-1 和 UTF-8 默认字符集，检查中文及 emoji 的文件内容和读写往返。该测试不启动 Spring 应用，也不调用模型服务。

### 使用 HTTP 文件测试
模块根目录下提供了 **[react-agent-example.http](./react-agent-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# feedback 接口测试
curl "http://localhost:8080/feedback"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:30:59*
