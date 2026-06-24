# MultiModelChat

## 模块定位

本模块演示聊天模型接入，重点验证对应模型平台的 ChatModel 与 ChatClient 用法。

## 主要内容

- 启动入口：MultiModelChatApplication
- Web 入口：MultiModelChatController
- 模型依赖：DashScope，通常需要配置 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。

## 运行方式

```bash
mvn -f spring-ai-alibaba-usecase-example/spring-ai-alibaba-scene-example/multi-model-chat/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-usecase-example/spring-ai-alibaba-scene-example/multi-model-chat/pom.xml spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
