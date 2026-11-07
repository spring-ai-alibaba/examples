# Chat AI Demo - 智能对话平台

<div align="center">

![Chat AI Demo](https://img.shields.io/badge/Chat%20AI%20Demo-v1.0.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)
![Spring AI Alibaba](https://img.shields.io/badge/Spring%20AI%20Alibaba-1.0.0.2-orange.svg)
![Vue.js](https://img.shields.io/badge/Vue.js-3.x-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

**一个基于 Spring AI Alibaba 的综合性AI对话平台**

[🌐 English Documentation](README_EN.md) | [📖 中文文档](README_CN.md)

</div>

---

## 📋 项目简介

Chat AI Demo 是一个功能丰富的AI对话平台，集成了多种AI应用场景：

- 🤖 **智能对话** - 支持多模态交互的AI聊天机器人
- 🎮 **情感模拟** - 游戏化的情感交互体验
- 🎧 **智能客服** - 24/7在线客服助手
- 📄 **文档问答** - PDF文档智能分析与问答
- �️ **数据爬虫** - 政府采购数据爬取与AI分析

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

| 功能模块 | 描述 | 技术栈 |
|---------|------|--------|
| 🤖 AI对话 | 多模态智能对话 | Spring AI Alibaba + DashScope |
| 📄 PDF分析 | 文档智能问答 | RAG + Milvus向量数据库 |
| 🕷️ 数据爬虫 | 政府采购数据爬取 | WebMagic + Neo4j |
| 🎮 情感模拟 | 游戏化交互体验 | Vue.js + TypeScript |
| 🎧 智能客服 | 工具调用与预约 | Function Calling |

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

## 离线文件映射编码回归

PDF 会话文件映射 `chat-pdf.properties` 使用 UTF-8 保存和读取，确保中文及 emoji 文件名在重启后保持不变。

在本模块目录执行：

```bash
mvn -Dtest=LocalPdfFileRepositoryTests test
```

该测试在隔离工作目录的子 JVM 中覆盖 ASCII、ISO-8859-1 和 UTF-8 默认字符集，不启动 Spring 应用，也不连接模型或向量数据库。
