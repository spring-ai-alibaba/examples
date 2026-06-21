# Spring AI Alibaba Graph Examples

## 模块定位

这个聚合模块收拢 Spring AI Alibaba Graph 工作流示例，覆盖节点编排、并行流、人工中断、多智能体和可观测性场景。

## 主要内容

- 子模块：workflow-review-classifier、workflow-writing-assistant、multiagent-openmanus、stream-node、mcp-node、human-node、usecase-field-classifier、product-analysis-graph、parallel-node、graph-observability-langfuse 等
- 用途：作为同类示例的 Maven 聚合入口，便于统一编译和按需进入子模块运行。

## 运行方式

```bash
mvn -f spring-ai-alibaba-graph-example/pom.xml test-compile -DskipTests
# 进入需要运行的子模块后再执行 spring-boot:run
```

## 配置要点

- 常规配置放在 `src/main/resources/application.yml`；没有外部服务时可先执行 `test-compile` 做编译验证。

## 验证建议

- 先执行聚合模块 `test-compile`，再进入目标子模块做运行态验证。
- 聚合模块不直接暴露业务接口，运行说明以子模块 README 为准。
