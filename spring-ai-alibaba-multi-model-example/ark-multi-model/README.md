# Spring AI Alibaba ARK Multi Model Example

## 模块定位

本模块演示多模型接入，把不同模型平台或不同模型类型放在同一示例中对照使用。

## 主要内容

- 启动入口：ArkMultiModelApplication
- Web 入口：MultiModelController
- 模型依赖：OpenAI 兼容接口，按模块配置 API key 和 base URL。

## 运行方式

```bash
mvn -f spring-ai-alibaba-multi-model-example/ark-multi-model/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-multi-model-example/ark-multi-model/pom.xml spring-boot:run
```

## 配置要点

- OpenAI 兼容配置：按 `application.yml` 设置 API key、base URL 和模型名。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
