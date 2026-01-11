# Spring-Ai-Alibaba-Usecase-Example 模块

## 模块说明

用户场景化的 Example 项目。

## 接口文档

### GameController 接口

#### 1. chat 方法

**接口路径：** `GET /ai/ai`

**功能描述：** 提供 chat 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/ai
```

#### 2. chat 方法

**接口路径：** `GET /aitext/html;charset=utf-8`

**功能描述：** 提供 chat 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/aitext/html;charset=utf-8
```


### ProcurementTestController 接口

#### 1. testExamplePage 方法

**接口路径：** `GET /procurement/test/example`

**功能描述：** 测试爬取示例页面

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/test/example
```

#### 2. healthCheck 方法

**接口路径：** `GET /procurement/test/health`

**功能描述：** 健康检查

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/test/health
```

#### 3. getSystemInfo 方法

**接口路径：** `GET /procurement/test/info`

**功能描述：** 健康检查

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/test/info
```


### ChatHistoryController 接口

#### 1. getChatIds 方法

**接口路径：** `GET /ai/history/ai/history`

**功能描述：** 提供 getChatIds 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/history/ai/history
```

#### 2. getChatIds 方法

**接口路径：** `GET /ai/history/{type}`

**功能描述：** 提供 getChatIds 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/history/{type}
```

#### 3. getChatHistory 方法

**接口路径：** `GET /ai/history/{type}/{chatId}`

**功能描述：** 提供 getChatHistory 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/history/{type}/{chatId}
```


### ChatController 接口

#### 1. chat 方法

**接口路径：** `GET /ai/ai`

**功能描述：** 提供 chat 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/ai
```

#### 2. chat 方法

**接口路径：** `GET /aitext/html;charset=utf-8`

**功能描述：** 提供 chat 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/aitext/html;charset=utf-8
```


### PdfController 接口

#### 1. chat 方法

**接口路径：** `GET /ai/pdftext/html;charset=utf-8`

**功能描述：** 提供 chat 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/pdftext/html;charset=utf-8
```

#### 2. uploadPdf 方法

**接口路径：** `GET /ai/pdf/upload/{chatId}`

**功能描述：** 文件上传

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/pdf/upload/{chatId}
```

#### 3. download 方法

**接口路径：** `GET /ai/pdf/file/{chatId}`

**功能描述：** 文件下载

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/pdf/file/{chatId}
```


### ProcurementController 接口

#### 1. startCrawling 方法

**接口路径：** `GET /procurement/crawl/start`

**功能描述：** 启动爬虫任务

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/crawl/start
```

#### 2. crawlSinglePage 方法

**接口路径：** `GET /procurement/crawl/single`

**功能描述：** 爬取单个页面

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/crawl/single
```

#### 3. getAllProjects 方法

**接口路径：** `GET /procurement/projects`

**功能描述：** 查询所有采购项目

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/projects
```

#### 4. getProjectById 方法

**接口路径：** `GET /procurement/projects/{id}`

**功能描述：** 根据ID查询采购项目详情

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/projects/{id}
```

#### 5. searchProjects 方法

**接口路径：** `GET /procurement/projects/search`

**功能描述：** 根据关键词搜索项目

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 信息检索
- 知识查询
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/projects/search
```

#### 6. getProjectsByBudget 方法

**接口路径：** `GET /procurement/projects/budget`

**功能描述：** 根据预算范围查询项目

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/projects/budget
```

#### 7. getRecentProjects 方法

**接口路径：** `GET /procurement/projects/recent`

**功能描述：** 查询最近的项目

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/projects/recent
```

#### 8. getAllOrganizations 方法

**接口路径：** `GET /procurement/organizations`

**功能描述：** 查询所有采购单位

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/organizations
```

#### 9. getProjectsByOrganization 方法

**接口路径：** `GET /procurement/organizations/{name}/projects`

**功能描述：** 根据采购单位查询项目

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/organizations/{name}/projects
```

#### 10. analyzeProject 方法

**接口路径：** `GET /procurement/analyze`

**功能描述：** AI分析项目内容

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/analyze
```

#### 11. getTrends 方法

**接口路径：** `GET /procurement/trends`

**功能描述：** 获取采购趋势分析

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/procurement/trends
```


### CustomerServiceController 接口

#### 1. service 方法

**接口路径：** `GET /ai/ai`

**功能描述：** 提供 service 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/ai
```

#### 2. service 方法

**接口路径：** `GET /aitext/html;charset=utf-8`

**功能描述：** 提供 service 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/aitext/html;charset=utf-8
```


### MultiModelChatController 接口

#### 1. streamChat 方法

**接口路径：** `GET /stream/chat`

**功能描述：** Streams responses from two large models simultaneously using Server-Sent Events (SSE).

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/stream/chat
```


### SummaryController 接口

#### 1. summarize 方法

**接口路径：** `GET /text/plain`

**功能描述：** 提供 summarize 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/text/plain
```


### ClassificationController 接口

#### 1. getPromptWithFewShotsHistory 方法

**接口路径：** `GET /classify`

**功能描述：** 提供 getPromptWithFewShotsHistory 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/classify
```


### ClassificationController 接口

#### 1. chat 方法

**接口路径：** `GET /api/classify/chat/field`

**功能描述：** 查询字段的分类分级信息（流式返回）

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/classify/chat/field
```


### TranslateController 接口

#### 1. translateFile 方法

**接口路径：** `GET /api/translate/file`

**功能描述：** 提供 translateFile 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/translate/file
```


### DashScopeTranslateController 接口

#### 1. simpleTranslation 方法

**接口路径：** `GET /api/dashscope/translate/simple`

**功能描述：** 基础翻译服务

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/dashscope/translate/simple
```

#### 2. streamTranslation 方法

**接口路径：** `GET /api/dashscope/translate/stream`

**功能描述：** 流式翻译服务

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/dashscope/translate/stream
```

#### 3. customTranslation 方法

**接口路径：** `GET /api/dashscope/translate/custom`

**功能描述：** 使用自定义配置的翻译服务

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/dashscope/translate/custom
```

#### 4. translateMarkdownFile 方法

**接口路径：** `GET /api/dashscope/translate/markdown-file`

**功能描述：** Markdown文件翻译服务

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/api/dashscope/translate/markdown-file
```


### SQLController 接口

#### 1. sql 方法

**接口路径：** `GET /sql`

**功能描述：** 提供 sql 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/sql
```


## 技术实现

### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic

## 测试指导

### 使用 curl 测试
```bash
# chat 接口测试
curl "http://localhost:8080/ai/ai"
```

```bash
# testExamplePage 接口测试
curl "http://localhost:8080/procurement/test/example"
```

## 注意事项

1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-09 23:30:06*
