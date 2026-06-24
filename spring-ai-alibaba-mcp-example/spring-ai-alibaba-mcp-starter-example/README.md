# Spring AI Alibaba Starter MCP Examples

## 模块定位

这个聚合模块收拢 MCP client、server、认证、安全、Nacos 注册发现和手动 SDK 示例，便于按传输协议或安全模型选择子模块运行。

## 主要内容

- 子模块：client/mcp-stdio-client-example、client/mcp-webflux-client-example、client/mcp-sdk-streamable-client-example、client/mcp-streamble-client、client/mcp-streamable-webflux-client、client/mcp-annotation-client、server/mcp-webflux-server-example、server/mcp-stdio-server-example、server/mcp-streamable-webmvc-server、server/mcp-streamable-webflux-server 等
- client 侧：演示 STDIO、WebFlux、SDK Streamable、Streamable WebMVC/WebFlux 和 annotation client 的连接方式
- server 侧：演示 STDIO、WebFlux、Streamable WebMVC/WebFlux 和 annotation server 的工具暴露方式
- book 对齐点：server 使用 `MethodToolCallbackProvider.builder().toolObjects(...)` 注册工具，Streamable 协议统一暴露 `/mcp`

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-starter-example/pom.xml test-compile -DskipTests
# 进入需要运行的子模块后再执行 spring-boot:run
```

## 配置要点

- MCP：client/server 示例通常需要先启动 server，再运行对应 client。
- WebMVC Streamable server 默认可作为本地 smoke server；client 可通过 `MCP_CLIENT_SMOKE_QUESTION` 自动提问并退出。
- annotation 示例使用 `org.springframework.ai.mcp.annotation.*`，用于验证注解扫描和回调处理。

## 验证建议

- 先执行聚合模块 `test-compile`，再进入目标子模块做运行态验证。
- 聚合模块不直接暴露业务接口，运行说明以子模块 README 为准。
