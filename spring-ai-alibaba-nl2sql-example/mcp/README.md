# MCP

## 模块定位

本模块演示 Spring AI MCP 的客户端或服务端能力，按当前目录对应的 transport、注册或安全模型运行。

## 主要内容

- 启动入口：Application
- 工具类/注解工具：McpService
- 模型依赖：OpenAI 兼容接口，按模块配置 API key 和 base URL。

## 运行方式

```bash
mvn -f spring-ai-alibaba-nl2sql-example/mcp/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-nl2sql-example/mcp/pom.xml spring-boot:run
```

## 配置要点

- OpenAI 兼容配置：按 `application.yml` 设置 API key、base URL 和模型名。
- MCP：client/server 示例通常需要先启动 server，再运行对应 client。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
