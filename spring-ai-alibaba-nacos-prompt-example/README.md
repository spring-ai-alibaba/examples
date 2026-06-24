# Spring AI Alibaba Nacos Examples

## 模块定位

本模块演示 Nacos prompt 或服务发现相关能力。

## 主要内容

- 启动入口：NacosPromptApplication
- Web 入口：PromptController
- 模型依赖：DashScope，通常需要配置 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- 外部依赖：Nacos，默认本地服务地址通常为 `127.0.0.1:8848`。

## 运行方式

```bash
mvn -f spring-ai-alibaba-nacos-prompt-example/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-nacos-prompt-example/pom.xml spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- Nacos：启动本地或远端 Nacos 后再运行注册、发现或网关示例。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
