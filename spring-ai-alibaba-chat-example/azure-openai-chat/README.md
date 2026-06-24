# Spring AI Alibaba Azure OpenAI Chat Examples

## 模块定位

本模块演示 Azure OpenAI ChatModel 和 ChatClient 的基础调用方式。

## 主要内容

- 启动入口：AzureOpenAiChatModelApplication
- Web 入口：AzureOpenAiChatModelController
- 模型依赖：OpenAI 兼容接口，按模块配置 API key 和 base URL。
- 平台依赖：Azure OpenAI，需要配置 endpoint、deployment 和 API key。

## 运行方式

```bash
mvn -f spring-ai-alibaba-chat-example/azure-openai-chat/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-chat-example/azure-openai-chat/pom.xml spring-boot:run
```

## 配置要点

- OpenAI 兼容配置：按 `application.yml` 设置 API key、base URL 和模型名。
- Azure OpenAI：需要 endpoint、deployment name 和 API key。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
