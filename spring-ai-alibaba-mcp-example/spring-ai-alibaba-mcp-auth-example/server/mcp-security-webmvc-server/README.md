# MCP security webmvc server

## 模块定位

本模块演示 WebMVC MCP server 的安全接入方式，提供 API Key 和 OAuth2 Resource Server 两类配置入口。

## 主要内容

- 启动入口：SecurityWebmvcServerApplication
- 核心服务：SecureCityWeatherService
- 安全链路：包含 API key 或 OAuth2 相关配置，适合验证受保护 MCP endpoint。

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-auth-example/server/mcp-security-webmvc-server/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-auth-example/server/mcp-security-webmvc-server/pom.xml spring-boot:run
```

## 配置要点

- MCP：client/server 示例通常需要先启动 server，再运行对应 client。
- API key profile 适合本地快速验证；OAuth2 profile 需要先启动授权服务并配置 issuer、jwk 或 token 校验参数。
- 示例工具为 `SecureCityWeatherService`，用于确认受保护的 MCP endpoint 能正确注册并限制访问。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
