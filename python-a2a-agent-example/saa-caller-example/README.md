# SAA Call Python A2A Agent Example

## 模块定位

本模块演示 Java 应用调用 Python A2A agent 的跨语言协作链路。

## 主要内容

- 启动入口：CallPythonAgentApplication
- Web 入口：PythonAgentController

## 运行方式

```bash
mvn -f python-a2a-agent-example/saa-caller-example/pom.xml test-compile -DskipTests
mvn -f python-a2a-agent-example/saa-caller-example/pom.xml spring-boot:run
```

## 配置要点

- 常规配置放在 `src/main/resources/application.yml`；没有外部服务时可先执行 `test-compile` 做编译验证。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
