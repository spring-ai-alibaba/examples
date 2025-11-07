# Spring AI Alibaba Graph 可观测性 Langfuse 示例

本示例演示了如何将 Spring AI Alibaba Graph 与 Langfuse 集成，实现 AI 图应用的全面可观测性和监控。

## 概述

本项目展示了以下技术的集成：
- **Spring AI Alibaba Graph**: 用于复杂图结构的 AI 处理流程
- **Langfuse**: 用于 AI 可观测性、追踪和分析

## 特性

- 复杂图结构处理（并行边、串行边、子图）
- 实时流式 AI 响应
- 多种节点类型（聊天节点、流式节点、子图节点）
- 完整的 Langfuse 可观测性集成
- OpenTelemetry 追踪和指标收集
- SSE 实时更新支持


## 图结构

```
开始节点 → 并行处理 → 子图处理 → 流式节点 → 汇总节点 → 结束节点
```

## 设置

### 1. Langfuse 配置

#### 选项 A: 使用 Langfuse 云端服务
1. 在 [https://cloud.langfuse.com](https://cloud.langfuse.com) 注册账户
2. 创建新项目
3. 导航到 **Settings** → **API Keys**
4. 生成新的 API 密钥对（公钥和私钥）
5. 将凭据编码为 Base64：
   ```bash
   echo -n "public_key:secret_key" | base64
   ``` 
   ```Windows PowerShell
   [System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes("public_key:secret_key"))
   ```
   
#### 选项 B: 使用自托管 Langfuse
1. 使用 Docker 部署 Langfuse：
   ```bash
   docker compose up -d
   ```
2. 访问 `http://localhost:3000`
3. 创建项目并生成 API 密钥
4. 更新 `application.yml` 中的端点

### 2. 环境变量

设置以下环境变量：

```bash
# 必需：DashScope API 密钥
export AI_DASHSCOPE_API_KEY=your_dashscope_api_key
```

### 3. 应用配置

更新 `src/main/resources/application.yml`：

```yaml
otel:
  exporter:
    otlp:
      endpoint: "https://cloud.langfuse.com/api/public/otel"
      headers:
        Authorization: "Basic YOUR_BASE64_ENCODED_CREDENTIALS"
```

将 `YOUR_BASE64_ENCODED_CREDENTIALS` 替换为您的 Base64 编码的 `public_key:secret_key`。

## 运行应用

### 1. 编译和运行
```bash
mvn clean compile
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动

### 2. 测试端点

#### 同步图执行
```bash
curl "http://localhost:8080/graph/observation/execute?prompt=请分析这段文本：人工智能的发展"
```

**响应示例：**
```json
{
  "success": true,
  "input": "请分析这段文本：人工智能的发展",
  "output": "经过完整图处理的最终结果",
  "logs": "执行日志信息"
}
```

#### 流式图执行
```bash
curl "http://localhost:8080/graph/observation/stream?prompt=请分析这段文本：人工智能的发展&thread_id=demo"
```

## 参考资料

- [Spring AI Alibaba 文档](https://github.com/alibaba/spring-ai-alibaba)
- [Langfuse 文档](https://langfuse.com/docs)
- [OpenTelemetry Java 文档](https://opentelemetry.io/docs/instrumentation/java/)
- [DashScope API 文档](https://help.aliyun.com/zh/dashscope/)

## 许可证

本项目采用 Apache License 2.0 许可证 - 详见 [LICENSE](../../LICENSE) 文件。

## 附：Graph可观测性集成方法
### Step 1. 引入starter依赖
```XML
<dependency>
   <groupId>com.alibaba.cloud.ai</groupId>
   <artifactId>spring-ai-alibaba-starter-graph-observation</artifactId>
   <version>${spring-ai-alibaba.version}</version>
</dependency>
```

### Step 2. 配置application.yml
```yaml
spring:
   ai:
    alibaba:
      graph:
        observation:
          enabled: true
```

### Step 3. 注入observationCompileConfig
```Java
@Bean
public CompiledGraph compiledGraph(StateGraph observabilityGraph, CompileConfig observationCompileConfig) throws GraphStateException {
  return observabilityGraph.compile(observationCompileConfig);
}
```
