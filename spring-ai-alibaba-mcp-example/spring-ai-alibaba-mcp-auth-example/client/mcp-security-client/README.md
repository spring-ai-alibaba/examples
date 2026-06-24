# MCP security client

## 模块定位

本模块演示带安全配置的 MCP 客户端，负责连接受保护的 MCP server，并通过 ChatClient 调用服务端暴露的工具。

## 主要内容

- 启动入口：SecurityClientApplication
- 模型依赖：DashScope，通常需要配置 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- 安全链路：包含 API key 或 OAuth2 相关配置，适合验证受保护 MCP endpoint。

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-auth-example/client/mcp-security-client/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-auth-example/client/mcp-security-client/pom.xml spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- MCP：client/server 示例通常需要先启动 server，再运行对应 client。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
