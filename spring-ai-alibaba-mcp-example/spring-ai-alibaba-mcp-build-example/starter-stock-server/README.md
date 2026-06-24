# Spring AI MCP Stock Server

## 模块定位

本模块演示 MCP Stock server，把股票查询能力封装为可被 MCP client 调用的工具。

## 主要内容

- 启动入口：StockServerApplication
- 工具类/注解工具：StockService

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-build-example/starter-stock-server/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-build-example/starter-stock-server/pom.xml spring-boot:run
```

## 配置要点

- MCP：client/server 示例通常需要先启动 server，再运行对应 client。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
