# Spring AI Alibaba Tool Calling Examples

## 模块定位

本模块演示 Spring AI Alibaba Tool Calling 的常见接入方式，包含 book 对齐后的 Search、Python、Time 基础接口，并保留天气、翻译、地址和校园助手等示例。

## 主要内容

- 启动入口：`ToolCallingApplication`
- book 对齐接口：`/basic/tool/search/call`、`/basic/tool/python/call`、`/basic/tool/time/call/function`、`/basic/tool/time/call/method`、`/basic/tool/time/call/auto-config`
- 保留示例接口：`/weather/**`、`/translate/**`、`/address/**`、`/campus/**`、`/time/**`
- 工具实现：Time function callback、Time method tool、Time auto configuration、Aliyun AI Search、Python tool，以及 Weather、Baidu Translate、Address、CampusSchedule 等原有工具
- 调用方式：控制器统一通过 `ToolCallingChatOptions` 和 `ToolCallingAdvisor` 组织工具调用，避免在 prompt 链路中散落注册 callback

## 运行方式

```bash
mvn -f spring-ai-alibaba-tool-calling-example/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-tool-calling-example/pom.xml spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- Aliyun AI Search 默认关闭；需要搜索示例时设置 `ALIYUN_AI_SEARCH_ENABLED=true` 并提供 `ALIYUN_AI_SEARCH`。
- Python tool 默认关闭；需要 Python 示例时设置 `PYTHON_TOOL_ENABLED=true`，本地 Java/GraalVM 环境可能输出 native access warning。
- Baidu Translate、Weather、Address 等外部服务 key 都保留为空默认值，未配置时应优先做编译或本地启动验证。

## 验证建议

- 无密钥环境先验证应用能启动；有 DashScope key 时可调用 Time 示例做 smoke test。
- Search 和 Python 示例需要分别打开对应开关后再访问 `/basic/tool/search/call`、`/basic/tool/python/call`。
- 修改代码后至少执行本模块 `test-compile`；涉及接口行为时再补充启动或 curl 验证。
