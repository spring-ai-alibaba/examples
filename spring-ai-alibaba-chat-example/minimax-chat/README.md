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

## 当前请求链路

最新调用链路：

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

## 各层职责

| 层 | 类 | 职责 |
| --- | --- | --- |
| 前端 | `src/main/resources/static/index.html` | 发送用户问题，维护短期聊天历史，渲染 Markdown，展示调试信息。 |
| Controller | `MiniMaxChatClientController` | 接收 HTTP 请求，把对话处理委托给 Agent 层。 |
| Agent | `LearningAgentService` | 编排记忆读取、意图识别、模型调用、工具访问和记忆更新。 |
| Memory | `LearningMemoryService` | 从 JSON 文件读取用户学习记忆，并在每轮对话后写回用户学习阶段、关注主题、最近意图和对话轮次。 |
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
