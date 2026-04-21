# Spring AI Alibaba Examples

> Spring AI Alibaba Repo: https://github.com/alibaba/spring-ai-alibaba
>
> Spring AI Alibaba Website:  https://java2ai.com
>
> Spring AI Alibaba Website Repo: https://github.com/springaialibaba/spring-ai-alibaba-website

[English](./README-en.md) | 中文

## 介绍

此仓库中包含许多 Example 模块项目来介绍 Spring AI 和 Spring AI Alibaba 从基础到高级的各种用法和 AI 项目的最佳实践。

更详细的介绍请参阅每个子项目中的 README.md 和 [Spring AI Alibaba 官网](https://java2ai.com)。

## 参与建设

欢迎任何形式的代码贡献。

## Quickstart Matrix (Community Contribution)

| module | purpose | command | required services | env vars | env template | entry |
|---|---|---|---|---|---|---|
| spring-ai-alibaba-helloworld | basic chat and advisor examples | `mvn -pl spring-ai-alibaba-helloworld spring-boot:run` | none | `AI_DASHSCOPE_API_KEY` | — | [README](./spring-ai-alibaba-helloworld/README.md) |
| spring-ai-alibaba-chat-example/dashscope-chat | DashScope chat basics | `mvn -pl spring-ai-alibaba-chat-example/dashscope-chat spring-boot:run` | none | `AI_DASHSCOPE_API_KEY` | — | [README](./spring-ai-alibaba-chat-example/dashscope-chat/README.md) |
| spring-ai-alibaba-image-example/dashscope-image | DashScope image generation | `mvn -pl spring-ai-alibaba-image-example/dashscope-image spring-boot:run` | none | `AI_DASHSCOPE_API_KEY` | — | [README](./spring-ai-alibaba-image-example/dashscope-image/README.md) |
| spring-ai-alibaba-mcp-example | MCP demo | `mvn -pl spring-ai-alibaba-mcp-example spring-boot:run` | none/local mcp tool | model api key | [`.env.example`](./spring-ai-alibaba-mcp-example/.env.example) | [README](./spring-ai-alibaba-mcp-example/README.md) |
| spring-ai-alibaba-rag-example | RAG demo | `mvn -pl spring-ai-alibaba-rag-example spring-boot:run` | vector db (optional by profile) | model api key, embedding model | [`.env.example`](./spring-ai-alibaba-rag-example/.env.example) | [README](./spring-ai-alibaba-rag-example/README.md) |
| spring-ai-alibaba-tool-calling-example | tool calling | `mvn -pl spring-ai-alibaba-tool-calling-example spring-boot:run` | none | model api key, map api key | [`.env.example`](./spring-ai-alibaba-tool-calling-example/.env.example) | [README](./spring-ai-alibaba-tool-calling-example/README.md) |

## 常用配置键速查

| 配置键 | 常见模块 | 说明 |
|---|---|---|
| `AI_DASHSCOPE_API_KEY` | `spring-ai-alibaba-helloworld`、DashScope chat/image、tool calling、evaluation、很多 graph/rag 示例 | DashScope 兼容模型最常见的 API Key |
| `OPENAI_API_KEY` | `spring-ai-alibaba-chat-example/openai-chat`、`spring-ai-alibaba-chat-example/vllm-chat` | OpenAI 兼容接口示例常用 |
| `AI_OPENAI_API_KEY` | `spring-ai-alibaba-image-example/openai-image` | OpenAI 图片生成示例使用 |
| `AI_DEEPSEEK_API_KEY` | `spring-ai-alibaba-chat-example/deepseek-chat`、`spring-ai-alibaba-mem0-example` | DeepSeek 相关示例使用 |
| `MINIMAX_API_KEY` | `spring-ai-alibaba-chat-example/minimax-chat` | MiniMax 模型示例使用 |
| `ZHIPUAI_API_KEY` | `spring-ai-alibaba-chat-example/zhipuai-chat` | 智谱模型示例使用 |
| `BAIDU_MAP_API_KEY` | `spring-ai-alibaba-tool-calling-example` | 地图工具调用示例需要 |

## 常见启动问题 / Troubleshooting

- `AI_DASHSCOPE_API_KEY` 未设置 / Missing `AI_DASHSCOPE_API_KEY`: 先确认环境变量已在当前 shell 或 IDE 中生效，再重新启动示例。
- 端口被占用 / Port already in use: 检查对应模块 `application.yml` 中的 `server.port`，释放端口或改端口后重试。
- 本地依赖未启动 / Required local services not running: RAG、MCP、向量库或 Docker 相关示例通常需要先启动对应的中间件或容器。
- 模块里暂时没有 `.env.example` / No `.env.example` in a module yet: 优先查看该模块 README 和 `src/main/resources/application.yml`，确认真实的变量名和依赖服务。
