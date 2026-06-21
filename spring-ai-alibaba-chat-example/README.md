# Spring AI Alibaba Chat Examples

## 模块定位

这个聚合模块用于组织同一主题下的多个 Spring AI Alibaba 示例子模块，本身不直接启动业务应用。

## 主要内容

- 子模块：qwq-chat、ollama-chat、openai-chat、zhipuai-chat、deepseek-chat、dashscope-chat、azure-openai-chat、vllm-chat、minimax-chat
- 用途：作为同类示例的 Maven 聚合入口，便于统一编译和按需进入子模块运行。

## 运行方式

```bash
mvn -f spring-ai-alibaba-chat-example/pom.xml test-compile -DskipTests
# 进入需要运行的子模块后再执行 spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- OpenAI 兼容配置：按 `application.yml` 设置 API key、base URL 和模型名。

## 验证建议

- 先执行聚合模块 `test-compile`，再进入目标子模块做运行态验证。
- 聚合模块不直接暴露业务接口，运行说明以子模块 README 为准。
