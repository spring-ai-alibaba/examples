# MCP streamble client

## 模块定位

本模块演示 Streamable HTTP MCP 客户端，启动后列出服务端工具，并支持 smoke question 自动调用后退出。

## 主要内容

- 启动入口：StreamableClientApplication
- 模型依赖：DashScope，通常需要配置 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-starter-example/client/mcp-streamble-client/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-starter-example/client/mcp-streamble-client/pom.xml spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- MCP：client/server 示例通常需要先启动 server，再运行对应 client。
- 默认连接本地 Streamable WebMVC server；运行 client 前先启动 `server/mcp-streamable-webmvc-server`。
- 设置 `MCP_CLIENT_SMOKE_QUESTION` 后，客户端会在启动时打印工具列表、提问、输出回答并退出，适合自动化 smoke 验证。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
