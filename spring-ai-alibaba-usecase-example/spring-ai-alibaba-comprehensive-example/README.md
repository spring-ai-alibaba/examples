# Chat AI Demo - 智能对话平台

<div align="center">

![Chat AI Demo](https://img.shields.io/badge/Chat%20AI%20Demo-v2.0.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green.svg)
![Spring AI Alibaba](https://img.shields.io/badge/Spring%20AI%20Alibaba-1.0.0.4-orange.svg)
![Neo4j](https://img.shields.io/badge/Neo4j-5.x-red.svg)
![Milvus](https://img.shields.io/badge/Milvus-2.x-purple.svg)
![Vue.js](https://img.shields.io/badge/Vue.js-3.x-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

**一个基于 Spring AI Alibaba 的综合性AI对话平台**

[🌐 English Documentation](README_EN.md) | [📖 中文文档](README_CN.md)

</div>

---

## 📋 项目简介

Chat AI Demo 是一个功能丰富的AI对话平台，集成了多种AI应用场景：

### 🎯 核心功能
- 🕷️ **智能爬虫** - 政府采购网站数据自动采集，绕过反爬虫限制
- 🤖 **AI解析** - 通义千问大模型精准解析HTML为结构化数据
- 📊 **图数据库** - Neo4j构建企业-项目-机构关系网络
- 🗄️ **向量数据库** - Milvus支持语义搜索和智能推荐
- 💰 **预算分析** - 自动计算项目预算总额（如1983.09万元）
- 🔍 **智能问答** - 基于RAG的招标信息问答系统

### 🎭 演示功能
- 🤖 **智能对话** - 支持多模态交互的AI聊天机器人
- 🎮 **情感模拟** - 游戏化的情感交互体验
- 🎧 **智能客服** - 24/7在线客服助手
- 📄 **文档问答** - PDF文档智能分析与问答

## 🚀 快速开始

### 环境要求
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Neo4j 4.x+
- Milvus 2.x+

### 一键启动

```bash
# 1. 克隆项目
git clone https://github.com/touhouqing/chatAiDemo.git
cd chatAiDemo

# 2. 设置环境变量
export AI_DASHSCOPE_API_KEY=your_api_key
export MYSQL_PASSWORD=your_password
export NEO4J_PASSWORD=your_password

# 3. 启动后端
mvn spring-boot:run

# 4. 启动前端
cd chatAiDemo-frontend
npm install && npm run dev
```

## � 详细文档

根据您的语言偏好选择对应的详细文档：

### 🌐 [English Documentation](README_EN.md)
Complete English documentation including:
- Detailed feature descriptions
- API documentation
- Deployment guides
- Contributing guidelines

### � [中文文档](README_CN.md)
完整的中文文档包含：
- 详细功能说明
- API接口文档
- 部署指南
- 贡献指南

## 🎯 核心功能预览

| 功能模块 | 描述 | 技术栈 | 状态 |
|---------|------|--------|------|
| 🕷️ **招标爬虫** | 政府采购数据智能采集 | WebMagic + 直接HTTP + 反爬虫绕过 | ✅ 已完成 |
| 🤖 **AI解析** | HTML内容结构化提取 | 通义千问qwen-max + 智能提示词 | ✅ 已完成 |
| 📊 **图数据库** | 企业项目关系网络 | Neo4j + Spring Data Neo4j | ✅ 已完成 |
| 🗄️ **向量数据库** | 语义搜索和推荐 | Milvus + 向量嵌入 | ✅ 已完成 |
| 💰 **预算计算** | 多项目预算自动汇总 | AI智能计算（如1983.09万元） | ✅ 已完成 |
| 🔍 **智能问答** | 基于RAG的问答系统 | 图数据库 + 向量数据库 + AI | 🚧 开发中 |
| 📄 **PDF分析** | 文档智能问答 | RAG + Milvus向量数据库 | ✅ 已完成 |
| 🎮 **AI对话** | 多模态智能对话演示 | Spring AI Alibaba + DashScope | ✅ 已完成 |

## 📊 项目架构

```
chatAiDemo/
├── src/main/java/           # Spring Boot 后端
│   ├── controller/          # REST API 控制器
│   ├── service/            # 业务逻辑服务
│   ├── config/             # 配置类
│   └── entity/             # 数据实体
├── chatAiDemo-frontend/     # Vue.js 前端
│   ├── src/views/          # 页面组件
│   ├── src/components/     # 通用组件
│   └── src/router/         # 路由配置
└── sql.txt                 # 数据库初始化脚本
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源协议。

---

<div align="center">

**⭐ 如果这个项目对您有帮助，请给个 Star！**

</div>
