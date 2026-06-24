# MCP streamable webflux server

## 模块定位

本模块演示 WebFlux Streamable MCP server，通过响应式栈暴露时间工具和 MCP endpoint。

## 主要内容

- 启动入口：StreamableWebfluxServerApplication
- 工具类/注解工具：TimeService

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-starter-example/server/mcp-streamable-webflux-server/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-starter-example/server/mcp-streamable-webflux-server/pom.xml spring-boot:run
```

## 配置要点

- MCP：client/server 示例通常需要先启动 server，再运行对应 client。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
