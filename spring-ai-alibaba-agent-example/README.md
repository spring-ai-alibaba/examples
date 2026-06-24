# Spring AI Alibaba agent Examples

## 模块定位

这个聚合模块组织 Agent、A2A、RAG Agent、SQL Agent、语音 Agent 和 ADK 示例，是智能体能力的入口层。

## 主要内容

- 子模块：playground-flight-booking、react-agent-example、rag-agent-example、sql-agent-example、adk-samples-llm-auditor、voice-agent-dashscope-sdk-example、voice-agent-example
- 用途：作为同类示例的 Maven 聚合入口，便于统一编译和按需进入子模块运行。

## 运行方式

```bash
mvn -f spring-ai-alibaba-agent-example/pom.xml test-compile -DskipTests
# 进入需要运行的子模块后再执行 spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。

## 验证建议

- 先执行聚合模块 `test-compile`，再进入目标子模块做运行态验证。
- 聚合模块不直接暴露业务接口，运行说明以子模块 README 为准。
