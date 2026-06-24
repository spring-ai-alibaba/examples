# Spring AI Alibaba MCP auth example

## 模块定位

这个聚合模块收拢 MCP client、server、认证、安全、Nacos 注册发现和手动 SDK 示例，便于按传输协议或安全模型选择子模块运行。

## 主要内容

- 子模块：client/mcp-auth-client、client/mcp-security-client、server/mcp-auth-web-server、server/mcp-security-webmvc-server、server/mcp-oauth2-authorization-server
- `mcp-auth-client` / `mcp-auth-web-server`：保留 examples 原有 header token 认证链路
- `mcp-security-client` / `mcp-security-webmvc-server`：补齐 book 风格 security client 和受保护 WebMVC server
- `mcp-oauth2-authorization-server`：提供本地 OAuth2 授权服务，支持与 security server/client 联调

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-auth-example/pom.xml test-compile -DskipTests
# 进入需要运行的子模块后再执行 spring-boot:run
```

## 配置要点

- MCP：client/server 示例通常需要先启动 server，再运行对应 client。
- header token 示例使用请求头转发完成认证；security 示例可按 profile 切换 API key 或 OAuth2 Resource Server。
- OAuth2 完整链路需要 authorization server、受保护 MCP server 和 client 同时按匹配配置启动。

## 验证建议

- 先执行聚合模块 `test-compile`，再进入目标子模块做运行态验证。
- 聚合模块不直接暴露业务接口，运行说明以子模块 README 为准。
