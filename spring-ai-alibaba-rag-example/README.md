# Spring AI Alibaba RAG Examples

## 模块定位

这个聚合模块集中展示 RAG、向量库接入、ETL 管道和 Bailian 知识库相关示例，适合按存储或检索策略进入子模块验证。

## 主要内容

- 子模块：bailian-rag-knowledge、bailian-agent、rag-elasticsearch-example、rag-milvus-example、rag-pgvector-example、module-rag、spring-ai-alibaba-vector-databases-example、rag-etl-pipeline-example、rag-openai-dashscope-pgvector-example、rag-elasticsearch-autoconfigure-example 等
- 用途：作为同类示例的 Maven 聚合入口，便于统一编译和按需进入子模块运行。

## 运行方式

```bash
mvn -f spring-ai-alibaba-rag-example/pom.xml test-compile -DskipTests
# 进入需要运行的子模块后再执行 spring-boot:run
```

## 配置要点

- DashScope key：优先使用环境变量 `DASHSCOPE_API_KEY` 或 `AI_DASHSCOPE_API_KEY`。
- OpenAI 兼容配置：按 `application.yml` 设置 API key、base URL 和模型名。

## 验证建议

- 先执行聚合模块 `test-compile`，再进入目标子模块做运行态验证。
- 聚合模块不直接暴露业务接口，运行说明以子模块 README 为准。
