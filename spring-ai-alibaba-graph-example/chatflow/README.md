# SAA ChatFlow Demo

本demo基于 [Spring AI Alibaba Graph (SAA-Graph)](https://github.com/alibaba/spring-ai-alibaba-graph) 实现了一个*
*智能待办事项助手**，具备**多轮对话**、**意图识别**和**任务管理**能力，适合多轮会话场景的设计与落地参考。

------

## 1. 设计思路

- **图驱动对话编排**：以 StateGraph + Node 的方式显式建模每一轮对话的流程与节点；
- **主流程/子图解耦**：主流程负责整体对话逻辑与意图判定，任务类操作交由独立子图处理，互不干扰；
- **动态变量与状态管理**：所有对话变量依赖 OverAllState 进行存取，主流程与子图通过 threadId 隔离上下文，实现变量池与任务池隔离；
- **LLM 功能原子化**：AI 闲聊、意图识别、任务内容润色等均以 LlmNode/QuestionClassifierNode/AssignerNode 形式进行节点化封装，灵活可插拔；
- **Lambda 动态节点**：多轮动态变量刷新通过 lambda 构建节点，彻底避免单例 Node 参数卡死问题，保障多轮能力。

------

## 2. Flow 流程说明

**主流程（主 StateGraph）**

1. **意图识别节点**（QuestionClassifierNode）
    - 自动判别用户输入是“创建待办”还是“普通闲聊”；
2. **分支节点**
    - “创建待办” → 进入子图
    - “闲聊” → LLM 闲聊节点
3. **子图节点**（callSubGraph）
    - 动态解析任务内容（提取冒号后文本），传入子图
    - 子图 threadId 隔离，防止变量串扰
    - 子图输出 merge 回主流程任务池
4. **主流程答复节点**（AnswerNode）
    - 统一拼装返回：“你当前待办有：...”，并带闲聊回复

**子图流程（create-todo-subgraph）**

1. **LLM 润色节点**（LlmNode, lambda 动态创建）
    - 根据传入 `task_content`，让大模型直接输出简明、规范的待办描述
2. **AssignerNode**
    - 把 LLM 结果存入 `created_task`
3. **AnswerNode（可选）**
    - 可用于调试或回复

------

## 3. 实现的功能

- **多轮记忆**：每个 sessionId 独立维护待办池，轮轮追加，跨轮问“我有哪些待办”会自动返回所有累计任务
- **AI 智能分流**：自动识别“创建待办”意图，非待办内容走闲聊分支
- **子图隔离/合并**：主流程与子图变量池完全隔离，任务输出精准合并
- **LLM润色/结构化**：原始输入自动润色为高质量任务内容
- **可插拔**：各节点均为标准 NodeAction，可任意拓展为更多意图/多子图

------

## 4. 重点注意事项

- **lambda 构建动态节点**
  不要全局 new LlmNode/AssignerNode，否则多轮参数会卡死。
- **传递动态变量时，params 用 "null" 占位符**，保证 LlmNode 能从 OverAllState 动态拉取本轮输入。
- **每轮调用子图时 threadId 唯一**（可拼 UUID或者时间戳等），避免子图变量被历史覆盖。
- **只在 NodeAction 里做类型转换/处理，避免全局变量类型污染**

------

## 5. 具体演示示例

服务启动后访问，`http://127.0.0.1:8080/assistant/chat`，带如下参数：

### （一）多轮任务追加

#### 第1轮

```
POST /assistant/chat?sessionId=123&userInput=待办：学习TypeScript
```

**返回：**

```
{
  "reply": "你当前待办有：[学习 TypeScript 相关知识。]\n闲聊回复：",
  "tasks": [
    "学习 TypeScript 相关知识。"
  ]
}
```

#### 第2轮

```
POST /assistant/chat?sessionId=123&userInput=待办：用Ts做一个小demo
```

**返回：**

```
{
  "reply": "你当前待办有：[学习 TypeScript 相关知识。, 使用 TypeScript 创建一个小的演示项目。]\n闲聊回复：",
  "tasks": [
    "学习 TypeScript 相关知识。",
    "使用 TypeScript 创建一个小的演示项目。"
  ]
}
```

#### 第3轮（普通闲聊）

```
POST /assistant/chat?sessionId=123&userInput=简单介绍下Spring Cloud
```

**返回：**

```
{
  "reply": "你当前待办有：[学习 TypeScript 相关知识。, 使用 TypeScript 创建一个小的演示项目。]\n闲聊回复：AssistantMessage [messageType=ASSISTANT, toolCalls=[], textContent=Spring Cloud 是一套基于 Spring Boot 的微服务架构开发工具包，它提供了一系列组件来帮助开发者快速构建和管理分布式系统中的各个服务。它的核心目标是简化分布式系统的开发、配置和协调工作。\n\n---\n\n### 🌐 主要功能（核心组件）：\n\n1. **服务注册与发现（Service Discovery）**\n   - 使用 **Eureka（Netflix）** 或 **Consul**、**Nacos** 等组件实现服务注册与发现。\n   - 微服务启动后自动注册到注册中心，并可以从注册中心发现其他服务。\n\n2. **配置中心（Config Server）**\n   - 使用 **Spring Cloud Config** 统一管理各个服务的配置文件，支持从 Git 或本地仓库加载配置。\n\n3. **API 网关（API Gateway）**\n   - 使用 **Zuul（Netflix）** 或 **Gateway（Spring Cloud Gateway）** 来统一处理请求路由、过滤、权限控制等。\n\n4. **负载均衡（Load Balancing）**\n   - **Ribbon** 或 **LoadBalancer** 提供客户端负载均衡能力，结合服务发现实现服务调用的负载均衡。\n\n5. **服务调用（Feign / OpenFeign）**\n   - 简化服务之间的 HTTP 调用，支持声明式 REST 客户端。\n\n6. **熔断器（Circuit Breaker）**\n   - **Hystrix（已停更）** 或 **Resilience4j** 实现服务熔断、降级和容错，提高系统的健壮性。\n\n7. **分布式链路追踪（Distributed Tracing）**\n   - 使用 **Sleuth + Zipkin** 实现请求链路追踪，便于排查分布式系统中的问题。\n\n8. **消息总线（Message Bus）**\n   - **Spring Cloud Bus** 使用消息队列（如 RabbitMQ、Kafka）实现配置的动态刷新和广播。\n\n---\n\n### 🧱 适用场景：\n\n- 微服务架构下的服务治理\n- 多服务间的通信、配置、监控、安全控制\n- 需要高可用、可扩展、易维护的大型分布式系统\n\n---\n\n### ✅ 优点：\n\n- 与 Spring Boot 深度集成，开发体验一致\n- 生态丰富，功能全面\n- 社区活跃，文档完善\n- 支持多种部署方式（本地、容器、云平台）\n\n---\n\n### 🔁 常见替代方案：\n\n- **阿里巴巴的 Spring Cloud Alibaba**（集成 Nacos、Sentinel、Seata 等）\n- **Kubernetes + Istio**（服务网格方案）\n- **Apache Dubbo + Nacos/ Zookeeper**（更侧重 RPC）\n\n---\n\n### 📦 示例项目结构：\n\n```plaintext\n- config-server（配置中心）\n- eureka-server（注册中心）\n- gateway（API 网关）\n- service-a（业务服务A）\n- service-b（业务服务B）\n- zipkin-server（链路追踪）\n```\n\n---\n\n如果你需要某个组件的详细使用示例或具体场景应用，也可以继续问我 😊, metadata={finishReason=STOP, search_info=, id=6992da7c-044f-93a0-bd93-b3a38e1701ce, role=ASSISTANT, messageType=ASSISTANT, reasoningContent=}]",
  "tasks": [
    "学习 TypeScript 相关知识。",
    "使用 TypeScript 创建一个小的演示项目。"
  ]
}
```

- 可见待办累计，闲聊能力并存

### （二）多用户/多会话隔离

用不同 sessionId (`456` 等) 测试，每个会话的任务池互不影响。

