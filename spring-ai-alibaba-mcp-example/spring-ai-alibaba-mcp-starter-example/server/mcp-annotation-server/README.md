# MCP annotation server

## 模块定位

本模块演示 MCP annotation server，通过注解方式发布时间工具，并在 Streamable 协议下对外暴露。

## 主要内容

- 启动入口：AnnotationServerApplication
- 核心服务：TimeTool

## 运行方式

```bash
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-starter-example/server/mcp-annotation-server/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-starter-example/server/mcp-annotation-server/pom.xml spring-boot:run
```

## 配置要点

- MCP：client/server 示例通常需要先启动 server，再运行对应 client。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
