# MCP oauth2 authorization server

## 模块定位

本模块提供本地 OAuth2 Authorization Server 示例，用于给受保护的 MCP server 和安全客户端提供授权链路。

## 主要内容

- 启动入口：OAuth2AuthorizationServerApplication
- 安全链路：提供 OAuth2 授权服务，供受保护 MCP server/client 联调。

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-auth-example/server/mcp-oauth2-authorization-server/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-auth-example/server/mcp-oauth2-authorization-server/pom.xml spring-boot:run
```

## 配置要点

- MCP：client/server 示例通常需要先启动 server，再运行对应 client。
- OAuth2：本模块用于本地授权联调，生产凭据需要替换为真实配置。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
