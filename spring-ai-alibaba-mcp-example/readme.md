# Spring AI Alibaba MCP Examples

## 模块定位

这个聚合模块收拢 MCP client、server、认证、安全、Nacos 注册发现和手动 SDK 示例，便于按传输协议或安全模型选择子模块运行。

## 主要内容

- 子模块：spring-ai-alibaba-mcp-manual-example、spring-ai-alibaba-mcp-starter-example、spring-ai-alibaba-mcp-build-example、spring-ai-alibaba-mcp-nacos-example、spring-ai-alibaba-mcp-auth-example、spring-ai-alibaba-mcp-config-example
- starter 示例：覆盖 STDIO、WebFlux、Streamable WebMVC/WebFlux、annotation client/server
- manual/build 示例：保留手动 SDK、GitHub、filesystem、SQLite 和 stock server 等 examples 原有内容
- nacos/config 示例：验证 Nacos 注册发现、gateway 转发和多 MCP 配置读取
- auth/security 示例：包含 header token、API key、OAuth2 Resource Server 和本地 OAuth2 Authorization Server

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/pom.xml test-compile -DskipTests
# 进入需要运行的子模块后再执行 spring-boot:run
```

## 配置要点

- MCP：client/server 示例通常需要先启动 server，再运行对应 client。
- Streamable server 使用 `/mcp` endpoint；client 示例支持启动时打印可用工具。
- Nacos 示例需要本地或远端 Nacos 服务；security/OAuth2 示例需要匹配的 server、client 和 token 配置。

## 验证建议

- 先执行聚合模块 `test-compile`，再进入目标子模块做运行态验证。
- 聚合模块不直接暴露业务接口，运行说明以子模块 README 为准。
