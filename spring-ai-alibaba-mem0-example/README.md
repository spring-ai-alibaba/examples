# Spring AI Alibaba Mem0 Examples

## 模块定位

本模块提供一个 Spring AI Alibaba 示例，用于展示当前目录对应的模型、工具或业务集成方式。

## 主要内容

- 启动入口：Mem0MemoryApplication
- Web 入口：Mem0MemoryController

## 运行方式

```bash
mvn -f spring-ai-alibaba-mem0-example/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-mem0-example/pom.xml spring-boot:run
```

## 配置要点

- 常规配置放在 `src/main/resources/application.yml`；没有外部服务时可先执行 `test-compile` 做编译验证。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
