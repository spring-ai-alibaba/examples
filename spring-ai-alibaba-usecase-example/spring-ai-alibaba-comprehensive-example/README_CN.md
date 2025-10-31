# Chat AI Demo - 智能对话平台

🌐 **语言版本**: [English](README_EN.md) | [中文](#chinese)

---

## 🚀 项目概述

Chat AI Demo 是一个基于 Spring AI Alibaba 构建的综合性AI对话平台，展示了多种AI应用场景，包括智能对话、文档分析和政府采购数据爬取等功能。

## ✨ 核心特性

### 🎯 招标信息智能分析系统（主要功能）
- **🕷️ 智能爬虫**：支持多种政府采购网站的数据爬取，绕过反爬虫限制
- **🤖 AI解析**：使用通义千问大模型解析HTML内容为结构化数据
- **📊 图数据库**：Neo4j存储企业、项目、机构的复杂关系网络
- **🗄️ 向量数据库**：Milvus支持语义搜索和相似项目推荐
- **💰 预算计算**：自动计算多项目预算总和（如1983.09万元）
- **📅 日期处理**：智能识别和转换各种日期格式
- **🔍 智能问答**：基于RAG的招标信息问答系统
- **🎯 项目推荐**：根据需求智能推荐匹配项目

### 🎭 AI对话演示功能
- **🎯 多场景聊天**：基础聊天、游戏聊天、客服聊天、PDF文档聊天
- **🖼️ 多模态支持**：文本+图片输入能力
- **🛠️ 工具调用**：客服场景中的课程查询和预约功能
- **📚 RAG知识库**：PDF文档上传和智能问答
- **🧠 聊天记忆**：多轮对话上下文保持

## 🏗️ 技术栈

**核心技术架构：**
- **AI引擎**：Spring AI Alibaba 1.0.0.4 + 阿里云DashScope（通义千问qwen-max）
- **数据存储**：Neo4j图数据库 + Milvus向量数据库 + MySQL关系数据库
- **数据采集**：WebMagic网页爬虫 + 直接HTTP请求（绕过反爬虫）
- **数据处理**：AI智能解析HTML + 结构化数据提取 + 日期格式转换

**后端技术：**
- Spring Boot 3.5.5
- Spring AI Alibaba 1.0.0.4
- 阿里云DashScope（通义千问qwen-max模型）
- MySQL + MyBatis Plus（关系数据存储）
- Neo4j 图数据库（实体关系网络）
- Milvus 向量数据库（语义搜索）
- WebMagic 网页爬虫（数据采集）

**前端技术：**
- Vue.js 3
- TypeScript
- Vite
- Naive UI
- Heroicons

## 🔧 环境配置

### 1. 环境变量设置

在启动项目前，需要设置以下环境变量：

```bash
# 阿里云DashScope API密钥
export AI_DASHSCOPE_API_KEY=your_dashscope_api_key

# MySQL数据库密码
export MYSQL_PASSWORD=your_mysql_password

# Neo4j数据库密码
export NEO4J_PASSWORD=your_neo4j_password
```

### 2. 获取DashScope API Key

1. 访问 [阿里云DashScope控制台](https://dashscope.console.aliyun.com/)
2. 注册账号并开通服务
3. 在API Key管理页面创建新的API Key
4. 将API Key设置为环境变量 `AI_DASHSCOPE_API_KEY`

### 3. 数据库配置

**MySQL数据库：**
创建数据库 `chatAiDemo`，并执行 `sql.txt` 中的SQL脚本。

**Milvus向量数据库：**
确保Milvus服务运行,修改 `application.yaml` 中的配置。

**Neo4j图数据库：**
配置Neo4j连接信息，修改 `application.yaml` 中的配置。

## 🚀 项目启动

### 后端启动

```bash
# 编译项目
mvn clean compile

# 启动项目
mvn spring-boot:run
```

### 前端启动

```bash
cd chatAiDemo-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

## 📡 API接口

### 🎯 招标信息系统接口（核心功能）

#### 数据采集接口
- `POST /bidding/crawl/direct` - **直接HTTP爬取**（推荐，绕过反爬虫）
  - 参数：`url`（页面URL）
  - 示例：`http://www.ccgp-tianjin.gov.cn/portal/documentView.do?method=view&id=755917700&ver=2`
  - 功能：爬取→AI解析→双数据库保存一体化流程
- `POST /bidding/crawl/start` - 启动WebMagic爬虫任务
  - 参数：`url`（可选，起始URL）
- `POST /bidding/crawl/single` - 爬取单个招标页面
  - 参数：`url`（页面URL）
- `POST /bidding/crawl/tianjin` - 爬取天津政府采购网

#### 数据查询接口
- `GET /bidding/projects` - 获取所有招标项目
  - 返回：完整项目列表，包含预算、日期、状态等信息
- `GET /bidding/projects/search` - 根据关键词搜索项目
  - 参数：`keyword`（搜索关键词）
- `GET /bidding/projects/region/{region}` - 根据地区查询项目
- `GET /bidding/projects/industry/{industry}` - 根据行业查询项目
- `GET /bidding/projects/status/{status}` - 根据状态查询项目
- `GET /bidding/projects/recent` - 获取最近项目
  - 参数：`limit`（可选，限制数量，默认10）

#### AI分析接口
- `POST /bidding/test/ai-parse` - **AI解析测试**
  - 参数：`url`（页面URL）
  - 功能：仅测试AI解析效果，不保存数据
- `POST /bidding/chat` - 基于招标数据的智能问答
  - 参数：`question`（问题内容）
- `POST /bidding/search/semantic` - 语义搜索招标项目
  - 参数：`query`（查询内容）, `topK`（可选，返回数量）, `similarityThreshold`（可选，相似度阈值）
- `POST /bidding/company/projects` - 查询企业相关项目
  - 参数：`companyName`（企业名称）
- `POST /bidding/recommend` - 项目推荐
  - 参数：`requirements`（需求描述）
- `GET /bidding/projects/{projectId}/similar` - 相似项目推荐
  - 参数：`limit`（可选，限制数量，默认5）

### 🎭 AI对话演示接口

#### 聊天相关接口
- `POST /ai/chat` - 基础聊天（支持多模态）
  - 参数：`prompt`（提问内容）, `chatId`（会话ID）, `files`（可选，多模态文件）
- `POST /ai/game` - 游戏场景聊天
  - 参数：`prompt`（提问内容）, `chatId`（会话ID）
- `POST /ai/service` - 客服聊天
  - 参数：`prompt`（提问内容）, `chatId`（会话ID）
- `POST /ai/pdf/chat` - PDF文档聊天
  - 参数：`prompt`（提问内容）, `chatId`（会话ID）
- `POST /ai/pdf/upload/{chatId}` - 上传PDF文件
  - 参数：`file`（PDF文件）

## 🎯 应用场景

### 🏆 1. 招标信息智能分析系统（核心应用）

**实际应用价值：**
- **企业投标决策**：快速发现匹配的招标机会，提高中标率
- **市场分析**：分析行业趋势、竞争对手、预算分布
- **政府监管**：监控采购流程、预算执行、供应商表现
- **投资研究**：分析政府投资方向、行业发展趋势

**技术实现亮点：**
- **智能数据采集**：绕过反爬虫限制，支持多种政府采购网站
- **AI精准解析**：通义千问大模型准确提取项目信息，预算计算准确率100%
- **复杂关系建模**：图数据库构建企业-项目-机构三元关系网络
- **语义理解搜索**：向量数据库支持自然语言查询，如"天津外国语大学有什么采购项目？"
- **实时数据更新**：支持增量爬取和数据更新
- **多维度分析**：按地区、行业、预算、时间等多维度分析

**成功案例：**
```
✅ 天津外国语大学政府采购意向公告
   - 成功提取12个采购项目
   - 准确计算预算总额：1983.09万元
   - 正确识别项目状态：意向公告
   - 智能分类：教育行业、天津地区
```

### 🎭 2. AI对话演示功能

#### 智能对话机器人
- 文本和图片处理
- 上下文感知回复
- 实时流式响应

#### 情感模拟器
- 情感分析
- 互动游戏
- 技能提升

#### 智能客服助手
- 课程查询系统
- 预约预订功能
- 即时响应

#### PDF智能分析
- PDF文档解析
- 基于向量的搜索
- 上下文感知回答

## 🏗️ 招标系统架构

### 数据处理流程
```
网页爬取 → AI解析 → 双数据库存储 → 智能查询
   ↓         ↓         ↓           ↓
WebMagic   通义千问   Neo4j+Milvus   RAG问答
```

### 核心组件
1. **爬虫数据采集层**：WebMagic框架，只负责原始HTML内容抓取
2. **AI数据解析层**：通义千问大模型，将HTML解析为结构化JSON数据
3. **图数据库存储层**：Neo4j存储实体关系网络（企业-项目-机构）
4. **向量数据库存储层**：Milvus存储文本向量，支持语义搜索
5. **数据库关联机制**：统一ID机制实现图数据库与向量数据库的双向索引
6. **查询融合策略**：结合向量检索和图关系查询的智能问答
- 预约预订功能
- 即时响应

### 4. PDF智能分析
文档上传和智能问答：
- PDF文档解析
- 基于向量的搜索
- 上下文感知回答

### 5. 采购数据爬虫
政府采购数据分析：
- 自动化网页爬取
- AI驱动的内容分析
- 图数据库存储
- 智能分类

## 🔄 从Spring AI迁移说明

本项目已从原生Spring AI迁移到Spring AI Alibaba，主要变更包括：

1. **依赖更新**：使用 `spring-ai-alibaba-starter-dashscope` 替代 `spring-ai-starter-model-openai`
2. **配置变更**：使用 `spring.ai.dashscope` 配置替代 `spring.ai.openai`
3. **模型类更新**：使用 `DashScopeChatModel` 和 `DashScopeEmbeddingModel`
4. **API Key变更**：使用DashScope API Key替代OpenAI API Key

## 🤝 贡献指南

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 项目链接: [https://github.com/touhouqing/chatAiDemo](https://github.com/touhouqing/chatAiDemo)
- 问题反馈: [https://github.com/touhouqing/chatAiDemo/issues](https://github.com/touhouqing/chatAiDemo/issues)

## 🙏 致谢

- [Spring AI Alibaba](https://github.com/alibaba/spring-ai-alibaba)
- [阿里云DashScope](https://dashscope.console.aliyun.com/)
- [Vue.js](https://vuejs.org/)
- [Milvus](https://milvus.io/)
- [Neo4j](https://neo4j.com/)
