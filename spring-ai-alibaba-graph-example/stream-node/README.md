# stream node

## 模块定位

本模块演示流式节点输出，适合验证 Graph 执行过程中的增量响应。

## 主要内容

- 启动入口：StreamNodeApplication
- Web 入口：GraphStreamController
- 模型依赖：DashScope，通常需要配置 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。

## 运行方式

```bash
mvn -f spring-ai-alibaba-graph-example/stream-node/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-graph-example/stream-node/pom.xml spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
