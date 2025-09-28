# Chat AI Demo - Intelligent Conversation Platform

🌐 **Language**: [English](#english) | [中文](README_CN.md#chinese)

---

## 🚀 Project Overview

Chat AI Demo is a comprehensive AI-powered chat platform built with Spring AI Alibaba, showcasing multiple AI application scenarios including intelligent conversations, document analysis, and government procurement data crawling.

## ✨ Key Features

### 🎯 Intelligent Procurement Analysis System (Core Features)
- **🕷️ Smart Crawling**: Multi-site government procurement data crawling with anti-bot bypass
- **🤖 AI Parsing**: Qwen large model parsing HTML content into structured data
- **📊 Graph Database**: Neo4j storing complex enterprise-project-organization networks
- **🗄️ Vector Database**: Milvus supporting semantic search and similar project recommendations
- **💰 Budget Calculation**: Automatic calculation of multi-project budget totals (e.g., 19.83 million yuan)
- **📅 Date Processing**: Intelligent recognition and conversion of various date formats
- **🔍 Intelligent Q&A**: RAG-based procurement information Q&A system
- **🎯 Project Recommendation**: Smart matching and recommendation based on requirements

### 🎭 AI Conversation Demo Features
- **🎯 Multi-Scenario Chat**: Basic chat, game chat, customer service, PDF document chat
- **🖼️ Multimodal Support**: Text + image input capabilities
- **🛠️ Function Calling**: Course query and booking functions in customer service scenarios
- **📚 RAG Knowledge Base**: PDF document upload and intelligent Q&A
- **🧠 Chat Memory**: Multi-turn conversation context preservation

## 🏗️ Tech Stack

**Backend:**
- Spring Boot 3.5.5
- Spring AI Alibaba 1.0.0.4
- Alibaba Cloud DashScope (Qwen)
- MySQL + MyBatis Plus
- Neo4j Graph Database
- Milvus Vector Database
- WebMagic Web Crawler

**Frontend:**
- Vue.js 3
- TypeScript
- Vite
- Naive UI
- Heroicons

## 🔧 Environment Setup

### 1. Environment Variables

Set the following environment variables before starting the project:

```bash
# Alibaba Cloud DashScope API Key
export AI_DASHSCOPE_API_KEY=your_dashscope_api_key

# MySQL Database Password
export MYSQL_PASSWORD=your_mysql_password

# Neo4j Database Password
export NEO4J_PASSWORD=your_neo4j_password
```

### 2. Get DashScope API Key

1. Visit [Alibaba Cloud DashScope Console](https://dashscope.console.aliyun.com/)
2. Register and activate the service
3. Create a new API Key in the API Key management page
4. Set the API Key as environment variable `AI_DASHSCOPE_API_KEY`

### 3. Database Configuration

**MySQL Database:**
Create database `chatAiDemo` and execute SQL scripts in `sql.txt`.

**Milvus Vector Database:**
Ensure Milvus service is running , and modify configuration in `application.yaml`.

**Neo4j Graph Database:**
Configure Neo4j connection information, or modify configuration in `application.yaml`.

## 🚀 Getting Started

### Backend

```bash
# Compile project
mvn clean compile

# Start application
mvn spring-boot:run
```

### Frontend

```bash
cd chatAiDemo-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

## 📡 API Endpoints

### Chat APIs
- `POST /ai/chat` - Basic chat with multimodal support
  - Parameters: `prompt` (question), `chatId` (session ID), `files` (optional, multimodal files)
- `POST /ai/game` - Game scenario chat
  - Parameters: `prompt` (question), `chatId` (session ID)
- `POST /ai/service` - Customer service chat
  - Parameters: `prompt` (question), `chatId` (session ID)
- `POST /ai/pdf/chat` - PDF document chat
  - Parameters: `prompt` (question), `chatId` (session ID)
- `POST /ai/pdf/upload/{chatId}` - Upload PDF files
  - Parameters: `file` (PDF file)

### Procurement APIs
- `POST /procurement/crawl/start` - Start web crawling
  - Parameters: `url` (optional, starting URL)
- `POST /procurement/crawl/single` - Crawl single page
  - Parameters: `url` (page URL)
- `GET /procurement/projects` - Get all projects
- `GET /procurement/projects/search` - Search projects
  - Parameters: `keyword` (search keyword)
- `POST /procurement/analyze` - AI analysis
  - Parameters: `content` (content to analyze)

## 🎯 Application Scenarios

### 1. AI Chat Hub
Intelligent conversation with multimodal input support. Features include:
- Text and image processing
- Context-aware responses
- Real-time streaming responses

### 2. Game Simulator
Emotional interaction and communication skill improvement:
- Emotion analysis
- Interactive gameplay
- Skill enhancement

### 3. Customer Service Assistant
24/7 professional consultation service:
- Course inquiry system
- Appointment booking
- Instant responses

### 4. PDF Analysis
Document upload and intelligent Q&A:
- PDF document parsing
- Vector-based search
- Context-aware answers

### 5. Procurement Data Crawler
Government procurement data analysis:
- Automated web crawling
- AI-powered content analysis
- Graph database storage
- Intelligent categorization

## 🔄 Migration from Spring AI

This project has been migrated from native Spring AI to Spring AI Alibaba. Key changes include:

1. **Dependency Update**: Using `spring-ai-alibaba-starter-dashscope` instead of `spring-ai-starter-model-openai`
2. **Configuration Change**: Using `spring.ai.dashscope` configuration instead of `spring.ai.openai`
3. **Model Classes Update**: Using `DashScopeChatModel` and `DashScopeEmbeddingModel`
4. **API Key Change**: Using DashScope API Key instead of OpenAI API Key

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

- Project Link: [https://github.com/touhouqing/chatAiDemo](https://github.com/touhouqing/chatAiDemo)
- Issues: [https://github.com/touhouqing/chatAiDemo/issues](https://github.com/touhouqing/chatAiDemo/issues)

## 🙏 Acknowledgments

- [Spring AI Alibaba](https://github.com/alibaba/spring-ai-alibaba)
- [Alibaba Cloud DashScope](https://dashscope.console.aliyun.com/)
- [Vue.js](https://vuejs.org/)
- [Milvus](https://milvus.io/)
- [Neo4j](https://neo4j.com/)
