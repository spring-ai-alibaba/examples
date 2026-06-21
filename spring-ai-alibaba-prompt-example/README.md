# Spring AI Alibaba Prompt Examples

## 模块定位

本模块演示 Prompt 模板和参数化提示词的组织方式。

## 主要内容

- 启动入口：PromptExampleApplication
- Web 入口：RoleController、StuffController
- 模型依赖：DashScope，通常需要配置 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。

## 运行方式

```bash
mvn -f spring-ai-alibaba-prompt-example/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-prompt-example/pom.xml spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
