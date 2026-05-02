# 官方 Agent Framework 接入说明

本阶段在 `minimax-chat` 中新增了一个并行的官方 Agent 调用链路，用于学习和对比 Spring AI Alibaba 官方 `ReactAgent`。

## 新增内容

### 1. Maven 依赖

`minimax-chat/pom.xml` 新增：

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-agent-framework</artifactId>
</dependency>
```

该依赖由根项目 BOM 管理版本，避免使用 `1.1.2.0-SNAPSHOT`。

### 2. official 包

新增包：

```text
com.alibaba.cloud.ai.official
```

包含：

```text
OfficialLearningAgentConfiguration
OfficialLearningAgentService
OfficialLearningAgentResult
OfficialLearningToolCallbacks
```

职责划分：

```text
OfficialLearningToolCallbacks
 -> 把现有 MiniMaxLearningTools 包装成官方 ToolCallback

OfficialLearningAgentConfiguration
 -> 构建官方 ReactAgent

OfficialLearningAgentService
 -> 读取 Memory
 -> Planner 识别意图
 -> 调用官方 ReactAgent
 -> 收集 Tool 调用信息
 -> 更新 Memory

OfficialLearningAgentResult
 -> 返回 content、intent、memoryBefore、memoryAfter、agentSteps、toolCalls、rawState
```

### 3. 新增接口

```text
POST /minimax/chat-client/official-agent/chat
```

请求体：

```json
{
  "userId": "user-a",
  "message": "请使用官方 ReactAgent 告诉我现在北京时间，并给我一个 30 分钟 Agent 学习计划。",
  "history": []
}
```

响应重点字段：

```json
{
  "content": "...",
  "intent": "MIXED",
  "memoryBefore": {},
  "memoryAfter": {},
  "agentSteps": [],
  "toolCalls": [],
  "rawState": {}
}
```

## 当前两条链路的区别

### 原有学习链路

```text
前端
 -> Controller
 -> LearningAgentService
 -> LearningMemoryService
 -> LearningIntentPlanner
 -> MiniMax ChatClient + @Tool
 -> LearningGraphService 调试步骤
 -> Memory 更新
 -> 前端调试区展示
```

特点：

- 调试信息最完整
- 前端已完全适配
- Graph 目前是轻量调试图，不是官方 StateGraph

### 官方 Agent 链路

```text
HTTP 测试请求
 -> Controller
 -> OfficialLearningAgentService
 -> LearningMemoryService
 -> LearningIntentPlanner
 -> 官方 ReactAgent
 -> 官方 ToolCallback
 -> MiniMaxLearningTools
 -> Memory 更新
 -> JSON 响应
```

特点：

- 已开始使用 Spring AI Alibaba 官方 Agent Framework
- Tool 入口从 `@Tool` 对象适配为官方 `ToolCallback`
- 当前先提供独立接口，不影响原有前端主链路

## 如何测试

推荐使用：

```text
minimax-official-agent.http
```

测试顺序：

```text
1. 执行 01，验证官方 ReactAgent 可以调用时间工具和学习计划工具。
2. 执行 02，验证官方 ReactAgent 可以调用 searchLearningDocs 检索当前项目资料。
3. 执行 03，对比原有 LearningAgent 链路，观察两套返回结构差异。
```

关键观察点：

```text
toolCalls 不为空：说明官方 ReactAgent 成功触发了工具。
memoryAfter.round 增加：说明官方链路也写入了长期 Memory。
rawState 不为空：说明拿到了官方 Graph/Agent 执行后的状态数据。
```

## 下一步建议

下一阶段可以把当前轻量 `LearningGraphService` 升级为真正的官方 `StateGraph`：

```text
Memory Read 节点
 -> Planner 节点
 -> ReactAgent 节点
 -> Memory Write 节点
 -> Response 节点
```

这样项目就会从“官方 ReactAgent 接入”继续演进到“官方 Graph 编排接入”。
