# Nacos MCP Gateway 示例

本示例演示如何使用 **Nacos MCP Gateway** 聚合多个 MCP Server 的工具，并统一对外暴露。

## 架构说明

```
┌─────────────────┐     ┌─────────────────────────────────┐     ┌───────────────────────┐
│   MCP Client    │────▶│      Nacos MCP Gateway          │────▶│   Nacos Registry      │
│   (AI 模型)      │     │      (本示例: 19000)            │     │   (localhost:8848)    │
└─────────────────┘     └─────────────────────────────────┘     └───────────────────────┘
                                      │                                    │
                                      │ 发现并聚合工具                       │ 注册
                                      ▼                                    │
                        ┌─────────────────────────────────┐               │
                        │                                 │               │
              ┌─────────┴─────────┐         ┌─────────────┴─────────┐     │
              ▼                   ▼         ▼                       ▼     │
    ┌──────────────────┐  ┌──────────────────────────┐                   │
    │ nacos-mcp-server │  │ nacos-mcp-server         │◀──────────────────┘
    │ -sse (10018)     │  │ -streamable (10032)      │
    │ [TimeTool]       │  │ [WeatherTool]            │
    └──────────────────┘  └──────────────────────────┘
```

## 核心功能

1. **工具自动发现**：从 Nacos 注册中心自动发现配置的 MCP Server
2. **工具聚合**：将多个 MCP Server 的工具聚合到统一的工具列表
3. **动态更新**：每 30 秒轮询 Nacos，自动感知工具变更
4. **多协议支持**：支持 HTTP、HTTPS、MCP-SSE、MCP-Streamable 协议
5. **统一暴露**：网关本身作为 MCP Server 对外暴露聚合后的工具

## 前置条件

1. **启动 Nacos**
   ```bash
   # 确保 Nacos 运行在 localhost:8848
   # 默认账号: nacos / nacos
   ```

2. **启动 MCP Server**
   ```bash
   # 启动 02-nacos-mcp-server-sse (端口 10018)
   # 启动 02-nacos-mcp-server-streamable (端口 10032)
   ```

3. **配置 DashScope API Key**
   ```bash
   # 设置环境变量
   export DASHSCOPE_API_KEY=your_api_key

   # 或在 .env 文件中配置
   DASHSCOPE_API_KEY=your_api_key
   ```

## 配置说明

```yaml
spring:
  ai:
    alibaba:
      mcp:
        gateway:
          enabled: true           # 启用 MCP Gateway
          registry: nacos         # 使用 Nacos 作为注册中心
          nacos:
            serviceNames:         # 要发现和聚合的 MCP Server 服务名
              - nacos-mcp-server-sse
              - nacos-mcp-server-streamable
        nacos:
          server-addr: localhost:8848
          namespace: public
          username: nacos
          password: nacos
```

## 启动应用

```bash
cd mcp-nacos-gateway-example
mvn spring-boot:run
```

应用启动后监听端口：**19000**

## API 接口

### 1. 查看聚合的工具列表

```bash
GET http://localhost:19000/api/gateway/tools
```

响应示例：
```json
[
  {
    "name": "getCurrentTime",
    "description": "获取当前时间"
  },
  {
    "name": "getWeather",
    "description": "获取天气信息"
  }
]
```

### 2. 通过 AI 调用工具

```bash
# 调用时间工具
GET http://localhost:19000/api/gateway/chat?message=现在几点了

# 调用天气工具
GET http://localhost:19000/api/gateway/chat?message=杭州的天气怎么样
```

### 3. MCP SSE 端点

```bash
# MCP Client 可通过 SSE 协议连接
GET http://localhost:19000/sse
POST http://localhost:19000/mcp/messages
```

## 工作流程

1. **启动阶段**
   - 网关连接 Nacos 注册中心
   - 根据 `serviceNames` 配置获取 MCP Server 列表
   - 从每个 MCP Server 获取工具定义 (`McpServerDetailInfo`)
   - 构建 `NacosMcpGatewayToolCallback` 并注册到工具列表

2. **运行阶段**
   - 每 30 秒轮询 Nacos，检测服务变更
   - 增量更新工具（新增、删除、修改）
   - 接收 MCP Client 请求，路由到对应的后端 MCP Server

3. **请求处理**
   - HTTP/HTTPS 协议：直接通过 WebClient 转发
   - MCP-SSE/Streamable 协议：通过 MCP Client 调用
## 关键依赖

| 依赖                                      | 说明                     |
|-----------------------------------------|------------------------|
| `spring-ai-starter-mcp-server-webflux`  | 网关作为 MCP Server 对外暴露   |
| `spring-ai-alibaba-starter-mcp-gateway` | Nacos MCP Gateway 核心功能 |
| `spring-ai-alibaba-starter-dashscope`   | DashScope 模型调用         |