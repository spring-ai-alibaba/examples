# Spring AI Alibaba Agent 学习版

这个仓库已经按 Agent 与 Skill 学习路线做了瘦身，只保留 MiniMax、Tool Calling、Agent/Skill、Agentic RAG、SQL Agent、Sequential Agent、Graph、MCP 和 Multi-Agent 相关示例。

## 保留模块

| 模块 | 学习重点 |
|---|---|
| `spring-ai-alibaba-chat-example/minimax-chat` | MiniMax `ChatModel`、`ChatClient`、同步与流式调用 |
| `spring-ai-alibaba-tool-calling-example` | `@Tool`、`@ToolParam`、`FunctionToolCallback`、工具调用基础 |
| `spring-ai-alibaba-agent-example/react-agent-example` | `ReactAgent`、工具注册、`MemorySaver`、Human-in-the-loop、拦截器 |
| `spring-ai-alibaba-agent-example/skills-agent-example` | `SkillsInterceptor`、`SKILL.md`、动态 Skill 加载 |
| `spring-ai-alibaba-agent-example/rag-agent-example` | Agentic RAG、知识检索工具、向量检索 |
| `spring-ai-alibaba-agent-example/sql-agent-example` | SQL Agent、数据库工具、安全查询约束 |
| `spring-ai-alibaba-agent-example/adk-samples-llm-auditor` | `SequentialAgent`、审稿/修订 Agent、联网搜索工具 |
| `spring-ai-alibaba-graph-example/react` | Graph 版 ReAct Agent |
| `spring-ai-alibaba-graph-example/mcp-node` | MCP 工具按 Graph 节点分配 |
| `spring-ai-alibaba-graph-example/multiagent-openmanus` | Planning Agent、Supervisor Agent、Executor Agent |
| `spring-ai-alibaba-graph-example/big-tool` | 大量工具场景下的工具检索与筛选 |

## 建议学习顺序

1. 跑通 `minimax-chat`，确认 MiniMax API Key 和基础对话。
2. 学 `tool-calling-example`，理解工具如何暴露给模型。
3. 学 `react-agent-example`，把工具、记忆、审批、拦截器组合成 Agent。
4. 学 `skills-agent-example`，理解 Skill 是给 Agent 的领域流程说明，Tool 才是执行能力。
5. 学 `rag-agent-example`、`sql-agent-example`、`adk-samples-llm-auditor`，掌握典型业务 Agent。
6. 学 `graph/react`、`mcp-node`、`multiagent-openmanus`、`big-tool`，进入工作流和多智能体编排。

## 运行提示

当前环境需要 Java 17+ 和 Maven。示例通常需要配置模型 API Key：

```bash
set MINIMAX_API_KEY=your_minimax_api_key
set AI_DASHSCOPE_API_KEY=your_dashscope_api_key
```

运行某个模块：

```bash
cd spring-ai-alibaba-chat-example/minimax-chat
mvn spring-boot:run
```

如果只使用 MiniMax，可优先从 `minimax-chat` 开始；后续 Agent 示例默认多为 DashScope，可再逐步替换为 MiniMax `ChatModel`。
