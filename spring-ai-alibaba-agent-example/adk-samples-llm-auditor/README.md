# Spring AI Alibaba LLM Auditor Example

## 简介

此示例演示了如何使用 Spring AI Alibaba 构建一个 LLM 答案审核系统。该系统通过两个 Agent 的协作，对 AI 生成的答案进行事实核查和修订，确保答案的准确性和可靠性。
参考自 google adk样例   https://github.com/google/adk-samples/tree/main/python/agents/llm-auditor
### 核心功能

- **Critic Agent（审稿 Agent）**：使用联网搜索工具验证答案中的事实声明，识别不准确、有争议或不支持的主张
- **Reviser Agent（修订 Agent）**：根据审稿结果，对答案进行最小化修改，保持原意和风格的同时提高准确性
- **自动引用提取**：从搜索工具响应中自动提取引用信息，格式化为 Markdown 引用格式

## 架构设计

```
用户查询
    ↓
LLM Auditor (SequentialAgent)
    ↓
┌─────────────────┐
│  Critic Agent   │ → 使用 WebSearchTool 验证事实
│  (审稿 Agent)   │ → CriticAgentHook 提取引用
└─────────────────┘
    ↓
┌─────────────────┐
│  Reviser Agent  │ → 根据审稿结果修订答案
│  (修订 Agent)   │ → ReviserAgentHook 清理标记
└─────────────────┘
    ↓
最终输出（包含引用）
```

## 核心组件

### 1. LLMAuditorController

主要的 REST Controller，提供 `/ai/agent` 接口。

**接口路径：** `GET /ai/agent`

**功能描述：** 使用 SequentialAgent 对问题和答案进行评估和修订

**请求参数：**
- `query` (可选，默认值: "中国的首都是哪里?"): 包含问题和答案的查询字符串

**返回格式：** 文本格式，包含 critic_agent_output 和 reviser_agent_output

**示例请求：**
```bash
GET http://localhost:8080/ai/agent?query=问：为什么蓝莓是蓝色的？A：因为蓝莓的表皮上有色素。
```

### 2. CriticAgent（审稿 Agent）

**功能：**
- 识别答案中的所有主张（事实陈述和逻辑论证）
- 使用联网搜索工具验证每个主张的可靠性
- 对每个主张做出裁决：准确、不准确、有争议、不支持、不适用
- 提供详细的验证理由和引用来源

**工具：**
- `WebSearchTool`: Tavily 联网搜索工具，用于查找最新信息和验证事实

**Hook：**
- `CriticAgentHook`: 从工具响应中提取引用信息，格式化为 Markdown 引用

### 3. ReviserAgent（修订 Agent）

**功能：**
- 根据审稿结果，对答案进行最小化修改
- 保持原答案的整体结构、风格和长度
- 修正不准确的主张，平衡有争议的观点，删除不支持的主张

**Hook：**
- `ReviserAgentHook`: 清理答案中的 `---END-OF-EDIT---` 标记

### 4. WebSearchTool（联网搜索工具）

**实现：** 使用 Tavily API 进行联网搜索

**特性：**
- 专为 AI Agent 设计，返回清洗后的文本内容
- 自动生成 AI 摘要
- 提供详细的来源信息（标题、内容、链接）

**配置：**
- 需要配置 `WEB_SEARCH_KEY` 环境变量（Tavily API Key）
- 在 `application.yml` 中配置：`search.tavily.api-key`

**获取 API Key：**
1. 访问 [Tavily 官网](https://tavily.com/)
2. 注册账号并获取 API Key
3. 设置环境变量：`export WEB_SEARCH_KEY=your_tavily_api_key`

## 环境配置

### 必需的环境变量

```bash
# DashScope API Key（必需）
export AI_DASHSCOPE_API_KEY=your_dashscope_api_key

# Tavily API Key（必需，用于联网搜索）
export WEB_SEARCH_KEY=your_tavily_api_key
```

### 配置文件

`src/main/resources/application.yml`:

```yaml
spring:
  ai:
    dashscope:
      api-key: ${AI_DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-plus
      embedding:
        options:
          model: text-embedding-v3

search:
  tavily:
    api-key: ${WEB_SEARCH_KEY}
```

## 运行项目

### 1. 配置环境变量

```bash
export AI_DASHSCOPE_API_KEY=<your-dashscope-api-key>
export WEB_SEARCH_KEY=<your-tavily-api-key>
```

### 2. 构建项目

```bash
mvn clean install
```

### 3. 启动项目

```bash
mvn spring-boot:run
```

## 接口测试

### LLM Auditor 接口

**接口路径：** `GET /ai/agent`

**测试示例：**

```bash
# 使用默认查询
curl "http://localhost:8080/ai/agent"

# 自定义查询
curl -X GET -G --data-urlencode 'query=问：为什么蓝莓是蓝色的？A：因为蓝莓的表皮上有色素。' 'http://localhost:8080/ai/agent'
```

**响应示例：**

```
===============critic_agent_ouput============
critic_agent_output:    
[审稿结果内容，包含验证的主张和裁决]

Reference:

* [蓝莓为什么是蓝色的](https://jandan.net/p/115589): 蓝莓之所以呈现出蓝色，秘密就在于其蜡质覆盖层的结构...
* [藍莓的藍色來自兩個關鍵因素](https://www.threads.com/...): 內部的花青素：讓果肉呈現紫色...
===============end============
===============reviser_agent_output============
reviser_agent_output:    
[修订后的答案内容]
===============end============
```

### 其他评估接口

模块还提供了其他评估器接口，详见 [evaluation.http](./evaluation.http) 文件：

- `GET /ai/evaluation/sa/relevancy` - 相关性评估器
- `GET /ai/evaluation/sa/fact-checking` - 事实性评估器
- `GET /ai/evaluation/saa/answer-relevancy` - 答案相关性评估器
- `GET /ai/evaluation/saa/answer-correctness` - 答案正确性评估器
- `GET /ai/evaluation/saa/answer-faithfulness` - 答案忠实性评估器

## 技术实现

### 核心依赖

- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **spring-ai-alibaba-starter-dashscope**: DashScope 模型支持
- **spring-boot-starter-web**: Web 支持

### 关键技术点

1. **SequentialAgent**: 顺序执行多个 Agent
2. **ReactAgent**: 支持工具调用的 Agent
3. **ModelHook**: Agent 执行前后的钩子函数
4. **FunctionToolCallback**: 函数工具回调
5. **引用提取**: 从工具响应中自动提取和格式化引用信息

### 工作流程

1. **Critic Agent 阶段**：
   - 接收问题和答案
   - 识别答案中的所有主张
   - 调用 WebSearchTool 验证每个主张
   - CriticAgentHook 提取搜索结果的引用
   - 生成包含引用的审稿报告

2. **Reviser Agent 阶段**：
   - 接收问题和答案，以及审稿结果
   - 根据审稿结果修订答案
   - ReviserAgentHook 清理编辑标记
   - 输出最终修订后的答案

## 注意事项

1. **环境变量**: 
   - 必须配置 `AI_DASHSCOPE_API_KEY`（DashScope API Key）
   - 必须配置 `WEB_SEARCH_KEY`（Tavily API Key）

2. **网络连接**: 
   - 需要能够访问阿里云 DashScope 服务
   - 需要能够访问 Tavily API 服务（https://api.tavily.com）

3. **API 限制**: 
   - Tavily API 有免费额度限制，超出后需要付费
   - 建议在生产环境中监控 API 使用量

4. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容

5. **端口配置**: 默认端口 8080，可在 `application.yml` 中修改

## 参考文档

- [Spring AI Model Evaluation 文档](https://docs.spring.io/spring-ai/reference/api/testing.html)
- [Tavily API 文档](https://docs.tavily.com/)
- [Spring AI Alibaba 文档](https://github.com/alibaba/spring-ai-alibaba)

## 项目结构

```
adk-samples-llm-auditor/
├── src/main/java/com/alibaba/cloud/ai/example/evaluation/
│   ├── controller/
│   │   └── LLMAuditorController.java      # 主控制器
│   ├── hook/
│   │   ├── CriticAgentHook.java           # Critic Agent 钩子（提取引用）
│   │   └── ReviserAgentHook.java          # Reviser Agent 钩子（清理标记）
│   └── tool/
│       └── WebSearchTool.java             # Tavily 联网搜索工具
├── src/main/resources/
│   └── application.yml                    # 配置文件
├── evaluation.http                         # HTTP 测试文件
└── README.md                               # 本文档
```

---

*最后更新：2026-01-23*
