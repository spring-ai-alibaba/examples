# Spring AI Alibaba OpenAI Chat Examples

## 模块定位

本模块演示 OpenAI ChatModel 和 ChatClient 接入，并保留与 DashScope OpenAI 兼容模式的对照。

## 主要内容

- 启动入口：OpenAiChatModelApplication
- Web 入口：OpenAiChatClientController、OpenAiChatModelController
- 模型依赖：OpenAI 兼容接口，按模块配置 API key 和 base URL。

## 运行方式

```bash
mvn -f spring-ai-alibaba-chat-example/openai-chat/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-chat-example/openai-chat/pom.xml spring-boot:run
```

## 配置要点

- OpenAI 兼容配置：按 `application.yml` 设置 API key、base URL 和模型名。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
