# Spring AI Alibaba Starter MCP Nacos Examples

## 模块定位

这个聚合模块收拢 MCP client、server、认证、安全、Nacos 注册发现和手动 SDK 示例，便于按传输协议或安全模型选择子模块运行。

## 主要内容

- 子模块：server/mcp-nacos-gateway-example、server/mcp-nacos-register-extensions-example、client/mcp-nacos-distributed-extensions-example
- 用途：作为同类示例的 Maven 聚合入口，便于统一编译和按需进入子模块运行。

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-nacos-example/pom.xml test-compile -DskipTests
# 进入需要运行的子模块后再执行 spring-boot:run
```

## 配置要点

- Nacos：启动本地或远端 Nacos 后再运行注册、发现或网关示例。
- MCP：client/server 示例通常需要先启动 server，再运行对应 client。

## 验证建议

- 先执行聚合模块 `test-compile`，再进入目标子模块做运行态验证。
- 聚合模块不直接暴露业务接口，运行说明以子模块 README 为准。
