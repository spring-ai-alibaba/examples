# Spring AI Alibaba MiniMax Chat 示例

本模块是一个基于 Spring AI Alibaba 和 MiniMax-M2.7 的聊天示例。

当前它被用作学习 Chat、Tool Calling、Skill、Planner、Agent 和 Memory 开发的渐进式示例。

## 当前功能

- 多轮聊天页面
- Markdown 回答渲染
- MiniMax-M2.7 模型接入
- Tool Calling 调试信息展示
- Skill 业务能力层
- 轻量 Planner 意图识别
- 轻量 Agent 编排层
- JSON 文件持久化 Learning Memory
- 多用户 Learning Memory
- Memory 查看和清空管理
- 本地文档 Simple RAG 检索
- 轻量 Graph 工作流节点
- 流式模式 SSE 调试事件

## 当前请求链路

最新调用链路：

```text
前端问题
 -> 前端携带 userId
 -> Controller
 -> LearningAgentService
 -> LearningGraphService 生成 Graph 节点
 -> LearningMemoryService 按 userId 从 JSON 文件读取记忆
 -> Planner 判断意图
 -> MiniMax + Tools
 -> searchLearningDocs Tool 按需检索本地文档
 -> LearningMemoryService 按 userId 更新记忆并写回 JSON 文件
 -> 前端展示回答 + Graph节点 + Agent步骤 + Tool调用 + 当前用户Memory信息
```

流式模式会额外通过 SSE 事件分阶段返回：

```text
debug -> message -> done
```

## 各层职责

| 层 | 类 | 职责 |
| --- | --- | --- |
| 前端 | `src/main/resources/static/index.html` | 发送用户 ID 和用户问题，维护短期聊天历史，渲染 Markdown，展示调试信息。 |
| Controller | `MiniMaxChatClientController` | 接收 HTTP 请求，把对话处理委托给 Agent 层。 |
| Agent | `LearningAgentService` | 编排记忆读取、意图识别、模型调用、工具访问和记忆更新。 |
| Graph | `LearningGraphService` | 把 Agent 执行流程表达成可展示的轻量工作流节点。 |
| Memory | `LearningMemoryService` | 按 userId 从 JSON 文件读取用户学习记忆，并在每轮对话后写回该用户的学习阶段、关注主题、最近意图和对话轮次。 |
| RAG | `LearningRagService` | 基于关键词检索当前 minimax-chat 的 README 和关键源码，为模型回答当前项目实现细节提供本地资料。 |
| Planner | `LearningIntentPlanner` | 把当前用户请求识别成具体学习意图。 |
| Tool | `MiniMaxLearningTools` | 暴露可以被大模型调用的工具入口。 |
| Skill | `LearningSkillService` | 承载学习建议、计划生成、概念解释、当前时间等真实业务逻辑。 |
| Model | MiniMax-M2.7 | 生成最终回答，并根据需要决定是否调用工具。 |

## 演进记录

### 1. Chat

初始目标：让 MiniMax 聊天先跑通，支持简单前端页面和自定义输入内容。

```text
前端问题
 -> Controller
 -> MiniMax
 -> 前端展示回答
```

### 2. Tool Calling

新增可以被模型调用的工具，用于获取当前时间和生成 Spring AI Alibaba 学习建议。

```text
前端问题
 -> Controller
 -> MiniMax
 -> MiniMax 按需调用 Tools
 -> 前端展示回答 + Tool调用
```

### 3. Skill 层

把真实学习业务逻辑从 Tool 方法中拆出来，放入 `LearningSkillService`。

```text
前端问题
 -> Controller
 -> MiniMax
 -> Tools
 -> LearningSkillService
 -> 前端展示回答 + Tool调用
```

### 4. Planner

新增 `LearningIntentPlanner`，用于识别用户是在询问时间、学习建议、每日计划、概念解释、混合意图还是普通聊天。

```text
前端问题
 -> Controller
 -> Planner 判断意图
 -> MiniMax + Tools
 -> 前端展示回答 + Planner意图 + Tool调用
```

### 5. 轻量 Agent

新增 `LearningAgentService` 和 `LearningAgentResult`。

Controller 不再自己组装完整 prompt，而是把对话处理交给 Agent 层。Agent 层负责记录执行步骤，例如接收问题、读取记忆、规划意图、选择策略、调用模型和处理工具结果。

```text
前端问题
 -> Controller
 -> LearningAgentService
 -> Planner 判断意图
 -> MiniMax + Tools
 -> 前端展示回答 + Agent步骤 + Tool调用
```

### 6. Learning Memory

新增 `LearningMemory` 和 `LearningMemoryService`。

Agent 在调用模型前读取用户学习记忆，在回答生成后更新记忆。

```text
前端问题
 -> Controller
 -> LearningAgentService
 -> LearningMemoryService 读取记忆
 -> Planner 判断意图
 -> MiniMax + Tools
 -> LearningMemoryService 更新记忆
 -> 前端展示回答 + Agent步骤 + Tool调用 + Memory信息
```

### 7. Memory 持久化

把第 6 阶段的进程内 Memory 升级为 JSON 文件持久化 Memory。

记忆文件位置：

```text
memory/learning-memory.json
```

当前示例在 `application.yml` 中固定配置为：

```yaml
minimax:
  memory:
    file: spring-ai-alibaba-chat-example/minimax-chat/memory/learning-memory.json
```

这样即使从项目根目录启动应用，也会写入 `minimax-chat` 模块下的记忆文件，而不会误写到项目根目录的 `memory/learning-memory.json`。

应用启动时，`LearningMemoryService` 会从该文件读取历史学习记忆；每次对话结束后，会把更新后的记忆写回该文件。这样即使 Spring Boot 应用重启，用户学习阶段、关注主题、上次意图和对话轮次也不会丢失。

```text
前端问题
 -> Controller
 -> LearningAgentService
 -> LearningMemoryService 从 JSON 文件读取记忆
 -> Planner 判断意图
 -> MiniMax + Tools
 -> LearningMemoryService 更新记忆并写回 JSON 文件
 -> 前端展示回答 + Agent步骤 + Tool调用 + Memory信息
```

### 8. 多用户 Memory

把第 7 阶段的单用户持久化 Memory 升级为多用户 Memory。

前端新增用户 ID 输入框，请求体新增 `userId` 字段。Controller 会把 `userId` 传给 `LearningAgentService`，Agent 再使用真实 `userId` 调用 `LearningMemoryService.read(userId)` 和 `LearningMemoryService.update(userId, ...)`。

JSON 文件结构保持为按用户 ID 分组：

```json
{
  "default-user": {},
  "user-a": {},
  "user-b": {}
}
```

升级后的链路：

```text
前端问题
 -> 前端携带 userId
 -> Controller
 -> LearningAgentService
 -> LearningMemoryService 按 userId 从 JSON 文件读取记忆
 -> Planner 判断意图
 -> MiniMax + Tools
 -> LearningMemoryService 按 userId 更新记忆并写回 JSON 文件
 -> 前端展示回答 + Agent步骤 + Tool调用 + 当前用户Memory信息
```

### 9. Memory 管理能力

新增长期 Memory 的查看和清空能力，用于区分短期上下文和长期记忆。

后端接口：

```text
GET /minimax/chat-client/memory?userId=user-a
DELETE /minimax/chat-client/memory?userId=user-a
```

前端新增按钮：

- `查看记忆`：读取当前用户的长期 Memory。
- `清空记忆`：清空当前用户的长期 Memory，并同步清空当前浏览器中的短期上下文。
- `清空`：只清空当前用户的短期上下文，不影响 JSON 文件中的长期 Memory。

查看 Memory 链路：

```text
用户点击查看记忆
 -> Controller
 -> LearningMemoryService.read(userId)
 -> 前端展示当前用户 Memory
```

清空 Memory 链路：

```text
用户点击清空记忆
 -> Controller
 -> LearningMemoryService.clear(userId)
 -> JSON 文件写回
 -> 前端提示已清空该用户长期记忆
```

### 10. Simple RAG

新增本地文档检索能力，用于让 Agent 回答当前 `minimax-chat` 项目的 README、源码结构和调用链问题。

新增类：

- `LearningDocument`
- `LearningRagService`

新增 Tool：

```text
searchLearningDocs(query, limit)
```

当前 Simple RAG 不接向量数据库，先用关键词检索本地文档和关键源码，覆盖以下资料：

- `README.md`
- `MiniMaxChatClientController`
- `LearningAgentService`
- `MiniMaxLearningTools`
- `LearningSkillService`
- `LearningIntentPlanner`
- `LearningMemoryService`

调用链：

```text
前端问题
 -> Controller
 -> LearningAgentService
 -> LearningMemoryService 按 userId 读取记忆
 -> Planner 判断意图
 -> MiniMax
 -> searchLearningDocs Tool
 -> LearningRagService 检索本地文档
 -> MiniMax 基于检索结果生成回答
 -> 前端展示回答 + Tool调用 + Memory信息
```

这一阶段的目标不是构建完整知识库，而是先理解 RAG 在 Agent 链路中的位置：模型在回答当前项目相关问题前，先通过工具检索本地资料，再基于资料组织回答。

### 11. 轻量 Graph 工作流

新增轻量 Graph 层，用于把当前 Agent 编排过程表达成节点流程。

新增类：

- `LearningGraphStep`
- `LearningGraphResult`
- `LearningGraphService`

当前阶段不改变真实执行逻辑，只把 Agent 流程图谱化，方便观察和学习：

```text
Receive
 -> Memory Read
 -> Planner
 -> Strategy
 -> Model Call
 -> Tool Execute
 -> Memory Write
 -> Response
```

请求链路：

```text
前端问题
 -> Controller
 -> LearningAgentService
 -> LearningGraphService 生成 Graph 节点
 -> Memory Read
 -> Planner
 -> MiniMax + Tools/RAG
 -> Memory Write
 -> 前端展示回答 + Graph节点 + Tool调用 + Memory信息
```

这一阶段的目标是理解 Graph 在 Agent 中的位置：Agent 负责编排任务，Graph 负责把编排过程表达成节点流程。后续可以再接入正式的 Spring AI Alibaba Graph/工作流框架。

### 12. 流式调试增强

把流式接口 `/conversation/stream` 从单纯文本流升级为多事件 SSE。

事件格式：

```text
event: debug
data: {"intent":"MIXED","graphSteps":[...],"agentSteps":[...],"memoryBefore":...}

event: message
data: {"content":"模型回答片段"}

event: done
data: {"memoryAfter":...,"toolCalls":[...],"agentSteps":[...]}
```

流式模式现在可以边输出回答，边展示调试信息：

- `debug`：回答开始前返回 Planner 意图、Graph 节点、Agent 初始步骤和调用前 Memory。
- `message`：持续追加模型回答片段。
- `done`：回答结束后返回 Tool 调用、调用后 Memory 和最终 Agent 步骤。

同步模式和流式模式的定位：

```text
同步模式：一次性返回完整结果，适合接口验证。
流式模式：边输出边展示调试信息，适合真实聊天体验。
```

## 建议测试用例

打开：

```text
http://localhost:8080/index.html
```

测试：

```text
我是初学者，想学习 Agent，给我一个 30 分钟计划。
```

继续追问：

```text
基于我刚才的学习方向，下一步应该学什么？
```

预期调试结果：

- `intent` 会显示 Planner 识别出的意图。
- `agentSteps` 会包含 `MEMORY_READ` 和 `MEMORY_WRITE`。
- `toolCalls` 会显示模型本轮调用了哪些工具。
- `memoryBefore` 和 `memoryAfter` 会显示学习记忆的变化。
- 重启应用后再次提问，`memoryBefore` 应该能读取到上次保存在 `memory/learning-memory.json` 中的学习记忆。
- 切换不同用户 ID 后，不同用户的关注主题和对话轮次应该互不影响。
- 点击 `查看记忆` 可以展示当前用户的长期 Memory。
- 点击 `清空记忆` 后，该用户在 JSON 文件中的长期 Memory 会重置。
- 点击 `清空` 只清空短期上下文，不会重置 JSON 文件中的长期 Memory。
- 询问当前项目 README、源码结构或调用链时，`toolCalls` 中应出现 `searchLearningDocs`。
- 同步模式下，调试区应展示 `Graph 节点`，包含 Receive、Memory Read、Planner、Model Call 等节点。
- 流式模式下，回答应逐步输出，并在调试区展示 Graph、Tool 调用和 Memory 变化。

多用户测试：

```text
用户 ID：user-a
问题：我是初学者，想学习 Agent。
```

```text
用户 ID：user-b
问题：我是进阶开发者，想学习 RAG。
```

预期 `memory/learning-memory.json` 中会出现 `user-a` 和 `user-b` 两份独立记忆。

Simple RAG 测试：

```text
根据当前 minimax-chat 项目，解释 Tool、Skill、Agent、Memory 的调用关系。
```

```text
查看当前项目 README，说明这个项目已经演进到哪一步。
```

预期前端调试区会显示 `searchLearningDocs` 工具调用，并返回本地文档检索摘要。

## 接口测试矩阵

推荐使用模块根目录下的 HTTP 用例文件进行回归测试：

```text
minimax-chat.http
minimax-tool-calling.http
```

### 全量回归用例

| 编号 | 测试目标 | HTTP 用例 | 请求接口 | 预期结果 |
| --- | --- | --- | --- | --- |
| 01 | 验证基础 ChatModel 同步调用 | `minimax-chat.http` | `GET /minimax/chat-model/simple/chat` | 返回普通文本回答 |
| 02 | 验证基础 ChatModel 流式调用 | `minimax-chat.http` | `GET /minimax/chat-model/stream/chat` | 返回 `text/event-stream` 文本流 |
| 03 | 验证自定义 ChatOptions | `minimax-chat.http` | `GET /minimax/chat-model/custom/chat` | 使用 Controller 内自定义模型参数返回回答 |
| 04 | 验证 ChatClient 同步调用 | `minimax-chat.http` | `GET /minimax/chat-client/simple/chat` | 返回普通文本回答 |
| 05 | 验证 ChatClient 流式调用 | `minimax-chat.http` | `GET /minimax/chat-client/stream/chat` | 返回基础流式文本 |
| 06 | 验证 Tool Calling 时间工具 | `minimax-chat.http` | `POST /minimax/chat-client/conversation/chat` | `toolCalls` 中出现 `getCurrentTime` |
| 07 | 验证 Skill 学习计划 | `minimax-chat.http` | `POST /minimax/chat-client/conversation/chat` | `toolCalls` 中出现 `generateDailyPlan` |
| 08 | 验证多轮上下文 | `minimax-chat.http` | `POST /minimax/chat-client/conversation/chat` | 回答能结合请求体中的 `history` |
| 09 | 验证 Simple RAG | `minimax-chat.http` | `POST /minimax/chat-client/conversation/chat` | `toolCalls` 中出现 `searchLearningDocs` |
| 10 | 验证 Graph 调试节点 | `minimax-chat.http` | `POST /minimax/chat-client/conversation/chat` | 响应中包含 `graphSteps` |
| 11 | 验证流式 SSE 调试 | `minimax-chat.http` | `POST /minimax/chat-client/conversation/stream` | 依次返回 `debug`、`message`、`done` 事件 |
| 12 | 查看用户 Memory | `minimax-chat.http` | `GET /minimax/chat-client/memory` | 返回指定 `userId` 的长期 Memory |
| 13 | 清空用户 Memory | `minimax-chat.http` | `DELETE /minimax/chat-client/memory` | 指定 `userId` 的 Memory 被重置并写回 JSON |
| 14 | 写入 user-a Memory | `minimax-chat.http` | `POST /minimax/chat-client/conversation/chat` | `user-a` 关注主题更新为 Agent |
| 15 | 写入 user-b Memory | `minimax-chat.http` | `POST /minimax/chat-client/conversation/chat` | `user-b` 关注主题更新为 RAG |
| 16 | 验证多用户隔离 | `minimax-chat.http` | `GET /minimax/chat-client/memory` | `user-a` 和 `user-b` 记忆互不影响 |

### 专项能力用例

| 能力 | HTTP 用例 | 示例问题 | 重点观察 |
| --- | --- | --- | --- |
| 时间 Tool | `minimax-tool-calling.http` | `现在北京时间几点？` | `toolCalls.name = getCurrentTime` |
| 学习建议 Skill | `minimax-tool-calling.http` | `下一步应该怎么学习 Agent？` | `toolCalls.name = generateLearningAdvice` |
| 今日计划 Skill | `minimax-tool-calling.http` | `给我今天 30 分钟学习计划` | `toolCalls.name = generateDailyPlan` |
| 概念解释 Skill | `minimax-tool-calling.http` | `解释 Tool、Skill、Agent、Graph 的区别` | `toolCalls.name = explainConcept` |
| 本地文档 RAG | `minimax-tool-calling.http` | `根据当前项目 README 和源码说明调用关系` | `toolCalls.name = searchLearningDocs` |
| Graph 节点 | `minimax-tool-calling.http` | `当前 Agent 的 Graph 节点有哪些？` | 响应中包含 `graphSteps` |
| 流式调试 | `minimax-tool-calling.http` | `请流式解释 Tool、RAG 和 Graph 的关系` | SSE 中包含 `debug/message/done` |

### 回归测试顺序

建议每次较大改动后按以下顺序测试：

```text
1. 基础模型接口：01-05
2. Agent 同步链路：06-10
3. 流式 SSE 链路：11
4. Memory 管理：12-13
5. 多用户隔离：14-16
6. Tool/RAG/Graph 专项：minimax-tool-calling.http
```

### 关键断言

同步接口 `/conversation/chat` 的响应应包含：

```json
{
  "content": "...",
  "intent": "MIXED",
  "memoryBefore": {},
  "memoryAfter": {},
  "graphSteps": [],
  "agentSteps": [],
  "toolCalls": []
}
```

流式接口 `/conversation/stream` 应包含：

```text
event:debug
event:message
event:done
```

Memory 持久化文件应按用户 ID 分组：

```json
{
  "user-a": {},
  "user-b": {}
}
```
