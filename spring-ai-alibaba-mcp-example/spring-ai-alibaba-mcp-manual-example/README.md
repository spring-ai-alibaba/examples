# Spring AI Alibaba MCP Manual Example

## 项目介绍

Spring AI Alibaba MCP Manual Example 演示了如何使用 Spring AI 接入第三方的 MCP (Model Context Protocol) Server。本示例展示了通过直接使用 MCP SDK 来集成各种外部 MCP 服务，包括 GitHub 操作、文件系统访问和 SQLite 数据库操作。

## 项目结构

```
spring-ai-alibaba-mcp-manual-example/
├── ai-mcp-fileserver/                    # 文件系统 MCP 客户端示例
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/alibaba/cloud/ai/mcp/samples/filesystem/
│   │   │   │   └── Application.java                    # 文件系统操作应用
│   │   │   └── resources/
│   │   │       └── application.properties               # 应用配置
│   └── pom.xml
├── ai-mcp-github/                        # GitHub MCP 客户端示例
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/alibaba/cloud/ai/mcp/samples/github/
│   │   │   │   └── GithubMcpApplication.java            # GitHub 操作应用
│   │   │   └── resources/
│   │   │       ├── application.yml                      # 应用配置
│   │   │       └── mcp-servers-config.json              # MCP 服务器配置
│   └── pom.xml
├── sqlite/
│   ├── ai-mcp-sqlite/                 # SQLite MCP 客户端（预定义问题）
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/alibaba/cloud/ai/mcp/samples/sqlite/
│   │   │   │   │   └── Application.java                 # SQLite 数据库操作应用
│   │   │   │   └── resources/
│   │   │   │       └── application.properties            # 应用配置
│   │   └── pom.xml
│   └── ai-mcp-sqlite-chatbot/        # SQLite MCP 聊天机器人（交互式）
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/org/springframework/ai/mcp/samples/sqlite/
│       │   │   │   └── Application.java                 # 交互式 SQLite 聊天应用
│       │   │   └── resources/
│       │   │       └── application.properties            # 应用配置
│       └── pom.xml
└── pom.xml
```

## 功能特性

### 1. 文件系统操作 (`ai-mcp-fileserver`)
- 使用 `@modelcontextprotocol/server-filesystem` MCP 服务器
- 支持文件读取、写入、目录操作
- 预定义问题演示文件内容分析和总结

### 2. GitHub 集成 (`ai-mcp-github`)
- 使用 `@modelcontextprotocol/server-github` MCP 服务器
- 支持仓库创建、Issue 管理、代码操作
- 集成 GitHub Personal Access Token 认证

### 3. SQLite 数据库操作 (`sqlite/`)
- 使用 `mcp-server-sqlite` MCP 服务器
- **ai-mcp-sqlite**: 预定义问题演示数据库查询和分析
- **ai-mcp-sqlite-chatbot**: 交互式聊天界面进行数据库操作

## 技术栈

- **Java 17+** - 运行环境
- **Spring Boot 3.4.0** - 应用框架
- **Spring AI 1.0.0** - AI 框架
- **Spring AI Alibaba 1.0.0.3** - 阿里云集成
- **Model Context Protocol (MCP)** - 工具调用协议
- **Node.js/NPX** - MCP 服务器运行环境
- **SQLite** - 嵌入式数据库
- **Maven** - 构建工具

## 环境要求

### 基础环境
- **Java 17+**
- **Maven 3.6+**
- **Node.js 18+** (用于运行 MCP 服务器)
- **uvx** (用于 Python MCP 服务器，SQLite 示例需要)

### 环境变量
```bash
# DashScope API Key (阿里云通义千问)
export AI_DASHSCOPE_API_KEY=your_api_key_here

# GitHub Personal Access Token (GitHub 示例需要)
export GITHUB_PERSONAL_ACCESS_TOKEN=your_github_token_here
```

## 快速开始

### 1. 安装依赖

#### 安装 Node.js (如果尚未安装)
```bash
# 使用 nvm 安装 Node.js 18+
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 18
nvm use 18
```

#### 安装 uvx (用于 SQLite 示例)
```bash
# 安装 uvx (Python 包管理器)
pip install uvx
```

### 2. 配置环境变量

```bash
# 设置 DashScope API Key
export AI_DASHSCOPE_API_KEY=your_dashscope_api_key

# 设置 GitHub Token (仅 GitHub 示例需要)
export GITHUB_PERSONAL_ACCESS_TOKEN=your_github_personal_access_token
```

### 3. 运行示例

#### 文件系统操作示例
```bash
cd ai-mcp-fileserver
mvn spring-boot:run
```

**演示功能**:
- 读取文件内容
- 分析和总结文件
- 创建 Markdown 格式的摘要文件

#### GitHub 操作示例
```bash
cd ai-mcp-github
mvn spring-boot:run
```

**演示功能**:
- 创建私有仓库
- 自动化 GitHub 操作

#### SQLite 数据库示例

**预定义问题示例**:
```bash
cd sqlite/ai-mcp-sqlite
mvn spring-boot:run
```

**交互式聊天机器人**:
```bash
cd sqlite/ai-mcp-sqlite-chatbot
mvn spring-boot:run
```

**演示功能**:
- 查询产品信息
- 分析价格分布
- 数据库表设计
- 交互式数据操作

## 详细配置

### GitHub MCP 配置

编辑 `ai-mcp-github/src/main/resources/mcp-servers-config.json`:

```json
{
    "mcpServers": {
        "github": {
            "command": "cmd",
            "args": [
                "/c",
                "npx",
                "-y",
                "@modelcontextprotocol/server-github"
            ],
            "env": {
                "GITHUB_PERSONAL_ACCESS_TOKEN": "${GITHUB_PERSONAL_ACCESS_TOKEN}"
            }
        }
    }
}
```

**Windows 系统配置**:
```json
{
    "mcpServers": {
        "github": {
            "command": "cmd",
            "args": ["/c", "npx", "-y", "@modelcontextprotocol/server-github"],
            "env": {
                "GITHUB_PERSONAL_ACCESS_TOKEN": "your_token_here"
            }
        }
    }
}
```

**Linux/Mac 系统配置**:
```json
{
    "mcpServers": {
        "github": {
            "command": "npx",
            "args": ["-y", "@modelcontextprotocol/server-github"],
            "env": {
                "GITHUB_PERSONAL_ACCESS_TOKEN": "${GITHUB_PERSONAL_ACCESS_TOKEN}"
            }
        }
    }
}
```

### 应用配置

#### GitHub 示例配置 (`application.yml`)
```yaml
spring:
    ai:
        dashscope:
            api-key: ${AI_DASHSCOPE_API_KEY}
        mcp:
            client:
                stdio:
                    servers-configuration: classpath:/mcp-servers-config.json
```

#### 其他示例配置 (`application.properties`)
```properties
# DashScope API Key
spring.ai.dashscope.api-key=${AI_DASHSCOPE_API_KEY}

# 禁用横幅显示（用于 STDIO 通信）
spring.main.banner-mode=off
```

## 核心实现

### 1. MCP 客户端创建

```java
@Bean(destroyMethod = "close")
public McpSyncClient mcpClient() {
    // 创建服务器参数
    var stdioParams = ServerParameters.builder("npx")
            .args("-y", "@modelcontextprotocol/server-filesystem", getDbPath())
            .build();

    // 创建同步 MCP 客户端
    var mcpClient = McpClient.sync(
        new StdioClientTransport(stdioParams, McpJsonMapper.getDefault()))
        .requestTimeout(Duration.ofSeconds(10))
        .build();

    // 初始化客户端
    var init = mcpClient.initialize();
    System.out.println("MCP Initialized: " + init);

    return mcpClient;
}
```

### 2. 工具集成到 ChatClient

```java
@Bean
public CommandLineRunner predefinedQuestions(
        ChatClient.Builder chatClientBuilder,
        McpSyncClient mcpClient,
        ConfigurableApplicationContext context) {

    return args -> {
        // 创建 ChatClient 并集成 MCP 工具
        var chatClient = chatClientBuilder
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpClient))
                .build();

        // 执行预定义问题
        String question = "分析文件内容并生成摘要";
        System.out.println("QUESTION: " + question);
        System.out.println("ASSISTANT: " + chatClient.prompt(question).call().content());

        context.close();
    };
}
```

### 3. 交互式聊天实现

```java
@Bean
public CommandLineRunner interactiveChat(
        ChatClient.Builder chatClientBuilder,
        List<McpSyncClient> mcpClients,
        ConfigurableApplicationContext context) {

    return args -> {
        var chatClient = chatClientBuilder
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpClients))
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(
                    MessageWindowChatMemory.builder().build()).build())
                .build();

        var scanner = new Scanner(System.in);
        System.out.println("\nStarting interactive chat. Type 'exit' to quit.");

        try {
            while (true) {
                System.out.print("\nUSER: ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("exit")) break;

                System.out.print("ASSISTANT: ");
                System.out.println(chatClient.prompt(input).call().content());
            }
        } finally {
            scanner.close();
            context.close();
        }
    };
}
```

## 使用场景

### 1. 文件系统操作
- **文档分析**: 自动分析文档内容并生成摘要
- **代码审查**: 读取代码文件并提供改进建议
- **配置管理**: 批量处理配置文件

### 2. GitHub 集成
- **仓库管理**: 自动创建和管理仓库
- **CI/CD 集成**: 与开发流程集成
- **项目管理**: 自动化 Issue 和 PR 管理

### 3. 数据库操作
- **数据分析**: 智能查询和分析数据
- **报表生成**: 自动生成数据报表
- **数据库设计**: 辅助数据库表结构设计

## 故障排除

### 常见问题

1. **MCP 服务器启动失败**
   ```bash
   # 检查 Node.js 版本
   node --version

   # 手动测试 MCP 服务器
   npx -y @modelcontextprotocol/server-filesystem --help
   ```

2. **GitHub 认证失败**
   ```bash
   # 验证 GitHub Token
   curl -H "Authorization: token your_token" https://api.github.com/user
   ```

3. **SQLite 数据库连接问题**
   ```bash
   # 检查 uvx 安装
   uvx --version

   # 手动测试 SQLite MCP 服务器
   uvx mcp-server-sqlite --help
   ```

4. **DashScope API 调用失败**
   - 验证 API Key 是否正确设置
   - 检查网络连接
   - 确认 API 配额充足

### 调试配置

启用详细日志：

```yaml
logging:
  level:
    io.modelcontextprotocol: DEBUG
    org.springframework.ai.mcp: DEBUG
    com.alibaba.cloud.ai: DEBUG
```

## 扩展开发

### 添加新的 MCP 服务器

1. **添加 MCP 服务器配置**

```java
@Bean(destroyMethod = "close")
public McpSyncClient customMcpClient() {
    var stdioParams = ServerParameters.builder("npx")
            .args("-y", "@your-org/custom-server")
            .env("API_KEY", "your_api_key")
            .build();

    return McpClient.sync(new StdioClientTransport(stdioParams, McpJsonMapper.getDefault()))
            .requestTimeout(Duration.ofSeconds(10))
            .build();
}
```

2. **注册到 ChatClient**

```java
@Bean
public CommandLineRunner customClient(
        ChatClient.Builder chatClientBuilder,
        List<McpSyncClient> mcpClients) {

    return args -> {
        var chatClient = chatClientBuilder
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpClients))
                .build();

        // 使用自定义工具
        String response = chatClient.prompt("使用自定义工具执行任务").call().content();
        System.out.println(response);
    };
}
```

## 最佳实践

1. **错误处理**: 始终处理 MCP 连接和工具调用异常
2. **资源管理**: 正确关闭 MCP 客户端以释放资源
3. **配置管理**: 使用环境变量管理敏感信息
4. **超时设置**: 为 MCP 操作设置合理的超时时间
5. **日志记录**: 启用详细日志便于调试和监控

## 相关资源

- [Model Context Protocol 官方文档](https://modelcontextprotocol.io/)
- [Spring AI Alibaba 官方仓库](https://github.com/alibaba/spring-ai-alibaba)
- [MCP 服务器列表](https://github.com/modelcontextprotocol/servers)
- [DashScope API 文档](https://help.aliyun.com/zh/dashscope/)