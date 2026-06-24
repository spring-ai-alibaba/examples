# Interruptable Action Example

## 模块定位

本模块演示 Spring AI Alibaba Graph 的一个具体工作流，用节点和边组织模型、工具和业务逻辑。

## 主要内容

- 启动入口：InterruptableActionApplication
- Web 入口：InterruptableController

## 运行方式

```bash
mvn -f spring-ai-alibaba-graph-example/interruptable-action-example/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-graph-example/interruptable-action-example/pom.xml spring-boot:run
```

## 配置要点

- 常规配置放在 `src/main/resources/application.yml`；没有外部服务时可先执行 `test-compile` 做编译验证。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
