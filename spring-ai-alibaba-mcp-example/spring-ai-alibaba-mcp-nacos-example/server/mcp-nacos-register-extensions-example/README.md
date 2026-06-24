# MCP nacos register extensions example

## 模块定位

本模块演示 MCP server 向 Nacos 注册扩展信息，便于客户端或网关通过服务发现接入。

## 主要内容

- 启动入口：NacosRegisterMcpServerApplication
- 工具类/注解工具：TimeService
- 外部依赖：Nacos，默认本地服务地址通常为 `127.0.0.1:8848`。

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-nacos-example/server/mcp-nacos-register-extensions-example/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-nacos-example/server/mcp-nacos-register-extensions-example/pom.xml spring-boot:run
```

## 配置要点

- Nacos：启动本地或远端 Nacos 后再运行注册、发现或网关示例。
- MCP：client/server 示例通常需要先启动 server，再运行对应 client。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
