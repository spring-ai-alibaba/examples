# RAG openai dashscope pgvector example

## 模块定位

本模块演示 RAG 的一个具体环节，围绕文档处理、向量存储或检索增强生成组织代码。

## 主要内容

- 启动入口：OpenAiDashscopeRagApplication
- Web 入口：KnowledgeBaseController
- 核心服务：KnowledgeBaseService、KnowledgeBaseServiceImpl
- 模型依赖：DashScope，通常需要配置 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- 模型依赖：OpenAI 兼容接口，按模块配置 API key 和 base URL。
- 外部依赖：对应向量数据库或搜索服务需要先启动并完成连接配置。

## 运行方式

```bash
mvn -f spring-ai-alibaba-rag-example/rag-openai-dashscope-pgvector-example/pom.xml test-compile -DskipTests
mvn -f spring-ai-alibaba-rag-example/rag-openai-dashscope-pgvector-example/pom.xml spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- OpenAI 兼容配置：按 `application.yml` 设置 API key、base URL 和模型名。

## 验证建议

- README 中的运行命令用于本地 smoke 验证；需要模型或外部服务的场景，先确认对应环境变量和服务状态。
- 修改代码后至少执行本模块 `test-compile`，涉及接口行为时再补充启动或 curl 验证。
