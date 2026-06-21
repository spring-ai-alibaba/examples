# Spring AI Alibaba Observability Examples

## 模块定位

这个聚合模块用于组织同一主题下的多个 Spring AI Alibaba 示例子模块，本身不直接启动业务应用。

## 主要内容

- 子模块：observability-example、observability-arms-example、observationhandler-example、observability-langfuse-example
- 用途：作为同类示例的 Maven 聚合入口，便于统一编译和按需进入子模块运行。

## 运行方式

```bash
mvn -f spring-ai-alibaba-observability-example/pom.xml test-compile -DskipTests
# 进入需要运行的子模块后再执行 spring-boot:run
```

## 配置要点

- 常规配置放在 `src/main/resources/application.yml`；没有外部服务时可先执行 `test-compile` 做编译验证。

## 验证建议

- 先执行聚合模块 `test-compile`，再进入目标子模块做运行态验证。
- 聚合模块不直接暴露业务接口，运行说明以子模块 README 为准。
