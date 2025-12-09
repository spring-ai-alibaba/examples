# CLAUDE.md

此文件为 Claude Code (claude.ai/code) 在处理此代码库时提供指导。

## 项目概览

这是一个全面的 Spring AI Alibaba 示例代码库，包含大量示例模块，展示了从基础到高级的 Spring AI 和 Spring AI Alibaba 使用模式。该项目被组织为一个多模块 Maven 项目，涵盖以下示例：

- 聊天模型（DashScope、OpenAI、Azure OpenAI、DeepSeek、Moonshot、QWQ、智谱AI、Ollama、VLLM）
- RAG（检索增强生成）与各种向量数据库
- AI 智能体和工作流
- MCP（模型上下文协议）示例
- 多模态应用（图像、音频、视频）
- 工具调用和函数执行
- 可观察性和监控
- 结构化输出和评估

## 架构

### 模块结构
- **父 POM**：根 `pom.xml` 管理 Spring AI (1.1.0)、Spring Boot (3.5.7) 和 Spring AI Alibaba (1.1.0.0-M5) 的版本
- **Java 版本**：需要 Java 17
- **模块组织**：每个主要功能区域都有自己的子目录，包含多个示例
- **应用程序结构**：大多数模块遵循 Spring Boot 约定，主应用程序类位于 `src/main/java`

### 关键模块分类
- `spring-ai-alibaba-chat-example/`：各种聊天模型实现
- `spring-ai-alibaba-rag-example/`：使用不同向量数据库（PGVector、Milvus、Elasticsearch 等）的 RAG 模式
- `spring-ai-alibaba-agent-example/`：AI 智能体模式和示例
- `spring-ai-alibaba-mcp-example/`：MCP 服务器/客户端实现
- `spring-ai-alibaba-graph-example/`：工作流和基于图的 AI 编排
- `spring-ai-alibaba-observability-example/`：监控和跟踪示例

## 开发命令

### 构建和测试
```bash
# 构建整个项目（跳过测试）
make build
# 或者
mvn -B package --file pom.xml -DskipTests=true

# 运行测试
make test
# 或者
mvn test

# 构建特定模块
cd <模块目录>
mvn clean package
```

### 代码质量
```bash
# 格式化代码
make format-fix
# 或者
mvn spring-javaformat:apply

# 检查代码格式
make format-check
# 或者
mvn spring-javaformat:validate

# 运行 checkstyle
make checkstyle-check
# 或者
mvn clean compile -Dcheckstyle.skip=false checkstyle:checkstyle
```

### 运行应用程序
每个示例模块都可以独立运行：
```bash
cd <模块目录>
mvn spring-boot:run

# 或在构建后运行 JAR
java -jar target/<模块名>-<版本>.jar
```

## 关键配置模式

### API 密钥和配置
大多数示例需要在 `application.yml` 或 `application.properties` 中配置 API 密钥：
- 阿里模型需要 DashScope API 密钥
- OpenAI 模型需要 OpenAI API 密钥
- 向量存储的数据库连接
- RAG 示例的 Redis/Milvus/PGVector 连接

### 常见依赖
示例通常包括：
- `spring-boot-starter-web` 用于 Web 应用程序
- `spring-ai-alibaba-starter-dashscope` 用于阿里 AI 模型
- 向量存储启动器（Redis、Milvus、PGVector 等）
- Spring AI 核心依赖

## 开发注意事项

### 先决条件
- Java 17+
- Maven 3.6+
- 适当的 API 密钥和外部服务（Redis、带 PGVector 的 PostgreSQL、Milvus 等）

### 运行示例
1. 在特定模块的 `application.yml` 中配置所需的 API 密钥
2. 启动任何必需的外部服务（Redis、PostgreSQL 等）
3. 导航到示例模块目录
4. 使用 `mvn spring-boot:run` 运行

### Docker 支持
许多示例都有 `docker-compose.yml` 文件，用于所需服务：
- 带 PGVector 扩展的 PostgreSQL
- Redis
- Milvus 向量数据库
- Elasticsearch

请查看各个模块目录中的 Docker 设置说明。

### 测试
- 单元测试使用 Spring Boot Test 框架
- 集成测试可能需要外部服务
- 如果服务不可用，某些测试可能会被跳过

## 项目资源

- **主要代码库**：https://github.com/alibaba/spring-ai-alibaba
- **网站**：https://java2ai.com
- **文档**：每个模块都包含详细的 README.md 文件
- **示例概览**：查看 playground 应用程序以获取全面的示例