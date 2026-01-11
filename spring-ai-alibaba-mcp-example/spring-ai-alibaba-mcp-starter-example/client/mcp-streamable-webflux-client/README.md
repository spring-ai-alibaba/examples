# Spring AI MCP Streamable WebFlux Client

## 项目介绍

Spring AI MCP Streamable WebFlux Client 是一个基于 Spring AI 的 MCP (Model Context Protocol) 流式客户端示例。该客户端展示了如何连接和使用流式 MCP 服务器，实现与 AI 模型的交互式对话，并集成服务器提供的工具功能。

## 主要特性

- **流式通信**: 支持与 MCP 服务器的实时双向通信
- **交互式对话**: 提供命令行交互界面，支持连续对话
- **工具集成**: 自动集成服务器提供的工具（如时间查询）
- **异步处理**: 基于响应式编程模型，支持高并发
- **智能路由**: 支持多服务器连接和智能路由
- **实时响应**: 流式处理，实时显示 AI 响应

## 技术栈

- **Java 17+** - 运行环境
- **Spring Boot 3.4.0** - 应用框架
- **Spring AI 1.0.0** - AI 框架
- **Spring AI Alibaba 1.0.0.3** - 阿里云集成
- **DashScope API** - 阿里云通义千问大模型
- **Model Context Protocol (MCP)** - 工具调用协议
- **Project Reactor** - 响应式编程库
- **Maven** - 构建工具

## 项目结构

```
mcp-streamable-webflux-client/
├── src/
│   ├── main/
│   │   ├── java/com/alibaba/cloud/ai/mcp/client/
│   │   │   └── StreamableWebfluxClientApplication.java  # 主应用类
│   │   └── resources/
│   │       └── application.yml                          # 应用配置
│   └── test/                                            # 测试目录
└── pom.xml                                              # Maven 配置
```

## 核心组件

### StreamableWebfluxClientApplication

客户端主应用类，功能包括：

1. **MCP 客户端初始化**: 自动连接配置的 MCP 服务器
2. **工具集成**: 自动发现和集成服务器工具
3. **交互式界面**: 提供命令行对话界面
4. **流式处理**: 实时处理 AI 响应

## 配置说明

### 客户端配置 (application.yml)

```yaml
server:
  port: 19100  # 客户端应用端口（可选）

spring:
  application:
    name: mcp-streamable-webflux-client

  main:
    web-application-type: none  # 禁用 Web 容器

  ai:
    dashscope:
      api-key: ${AI_DASHSCOPE_API_KEY}  # DashScope API 密钥

    mcp:
      client:
        enabled: true
        name: my-mcp-client
        version: 1.0.0
        request-timeout: 600s  # 请求超时时间
        type: ASYNC  # 异步模式，适合响应式应用

        streamable-http:
          connections:
            server1:
              url: http://localhost:20000  # MCP 服务器地址
              endpoint: /mcp               # MCP 端点路径
```

## 快速开始

### 1. 环境准备

#### 基础环境
- Java 17+
- Maven 3.6+

#### 环境变量
```bash
# 设置 DashScope API 密钥
export AI_DASHSCOPE_API_KEY=your_dashscope_api_key_here
```

### 2. 启动 MCP 服务器

首先启动 MCP Streamable WebFlux 服务器：

```bash
cd ../server/mcp-streamable-webflux-server
mvn spring-boot:run
```

服务器将在 `http://localhost:20000` 启动。

### 3. 启动客户端

```bash
# 进入客户端目录
cd spring-ai-alibaba-mcp-starter-example/client/mcp-streamable-webflux-client

# 编译项目
mvn clean package -DskipTests

# 启动客户端
mvn spring-boot:run
```

### 4. 使用交互式对话

启动后，您将看到命令行提示符：

```
>>> QUESTION:
```

现在可以输入问题与 AI 进行对话：

```
>>> QUESTION: 现在几点了？

>>> ASSISTANT: 我来帮您查询当前时间。根据时区信息，上海现在是 2024-01-15 14:30:25 CST。

>>> QUESTION: 纽约现在是什么时间？

>>> ASSISTANT: 我帮您查询纽约的当前时间。纽约现在是 2024-01-15 01:30:25 EST。

>>> QUESTION: exit  # 退出对话
```

## 使用示例

### 基本时间查询

```
>>> QUESTION: 北京现在是什么时间？
>>> ASSISTANT: [AI 调用时间工具，返回北京当前时间]

>>> QUESTION: 伦敦和东京的时差是多少？
>>> ASSISTANT: [AI 调用工具查询两个城市时间，计算时差]
```

### 复杂查询示例

```
>>> QUESTION: 帮我查一下全球主要金融中心的时间
>>> ASSISTANT: [AI 可能会依次查询纽约、伦敦、东京、香港等地时间]

>>> QUESTION: 如果现在北京时间下午3点，那么纽约是什么时间？
>>> ASSISTANT: [AI 进行时间计算和转换]
```

## 高级配置

### 连接多个 MCP 服务器

在 application.yml 中配置多个服务器：

```yaml
spring:
  ai:
    mcp:
      client:
        streamable-http:
          connections:
            time-server:
              url: http://localhost:20000
              endpoint: /mcp
            weather-server:
              url: http://localhost:20001
              endpoint: /mcp
            stock-server:
              url: http://localhost:20002
              endpoint: /mcp
```

### 客户端优化配置

```yaml
spring:
  ai:
    mcp:
      client:
        request-timeout: 300s        # 请求超时
        connection-timeout: 30s      # 连接超时
        retry-attempts: 3            # 重试次数
        type: ASYNC                  # 异步模式

        streamable-http:
          connections:
            server1:
              url: http://localhost:20000
              endpoint: /mcp
              max-connections: 10    # 最大连接数
              keep-alive: true       # 保持连接
```

## 开发指南

### 自定义客户端逻辑

```java
@SpringBootApplication
public class MyCustomClientApplication {

    @Bean
    public CommandLineRunner customChat(
            ChatClient.Builder chatClientBuilder,
            ToolCallbackProvider tools,
            ConfigurableApplicationContext context) {

        return args -> {
            var chatClient = chatClientBuilder
                    .defaultToolCallbacks(tools.getToolCallbacks())
                    .build();

            // 自定义对话逻辑
            List<String> questions = Arrays.asList(
                "现在北京时间几点？",
                "纽约时间呢？",
                "帮我总结一下时区差异"
            );

            for (String question : questions) {
                System.out.println("\n>>> QUESTION: " + question);
                String response = chatClient.prompt(question).call().content();
                System.out.println("\n>>> ASSISTANT: " + response);
            }

            context.close();
        };
    }
}
```

### 添加记忆功能

```java
@Bean
public CommandLineRunner memoryChat(
        ChatClient.Builder chatClientBuilder,
        ToolCallbackProvider tools) {

    return args -> {
        var chatClient = chatClientBuilder
                .defaultToolCallbacks(tools.getToolCallbacks())
                .defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder()
                            .maxMessages(10)
                            .build()
                    ).build()
                )
                .build();

        // 实现带记忆的对话
        // ...
    };
}
```

## 故障排除

### 常见问题

1. **连接失败**
   ```
   错误: 无法连接到 MCP 服务器
   解决: 检查服务器是否启动，网络是否通畅
   ```

2. **API 密钥问题**
   ```
   错误: 401 Unauthorized
   解决: 检查 AI_DASHSCOPE_API_KEY 环境变量
   ```

3. **工具调用失败**
   ```
   错误: 工具调用超时
   解决: 调整 request-timeout 配置
   ```

### 调试配置

启用详细日志：

```yaml
logging:
  level:
    com.alibaba.cloud.ai.mcp.client: DEBUG
    io.modelcontextprotocol.client: DEBUG
    org.springframework.ai.mcp: DEBUG
```

### 测试连接

使用 curl 测试 MCP 服务器连接：

```bash
# 测试服务器是否可达
curl http://localhost:20000/mcp

# 测试工具列表
curl -X POST http://localhost:20000/mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","method":"tools/list","id":1}'
```

## 性能优化

### 连接池配置

```yaml
spring:
  ai:
    mcp:
      client:
        streamable-http:
          connections:
            server1:
              url: http://localhost:20000
              endpoint: /mcp
              max-connections: 20
              connect-timeout: 10s
              response-timeout: 60s
```

### 响应式优化

```java
@Bean
public WebClient webClient() {
    return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create()
                    .responseTimeout(Duration.ofSeconds(60))
                    .keepAlive(true)
            ))
            .build();
}
```

## 扩展开发

### 添加自定义处理器

```java
@Component
public class CustomToolHandler {

    @EventListener
    public void handleToolCall(ToolCallEvent event) {
        // 处理工具调用事件
        logger.info("Tool called: {}", event.getToolName());
    }

    @EventListener
    public void handleToolResponse(ToolResponseEvent event) {
        // 处理工具响应事件
        logger.info("Tool response: {}", event.getResult());
    }
}
```

### 集成其他 AI 模型

```java
@Bean
public ChatClient customChatClient() {
    return ChatClient.builder(customAiModel())
            .defaultSystem("你是一个智能助手，可以使用时间查询工具。")
            .build();
}
```

## 部署建议

### 生产环境配置

```yaml
spring:
  ai:
    mcp:
      client:
        request-timeout: 120s
        connection-timeout: 30s
        retry-attempts: 5

logging:
  level:
    com.alibaba.cloud.ai: INFO
    io.modelcontextprotocol: WARN
```

### Docker 部署

创建 Dockerfile：

```dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app
COPY target/mcp-streamable-webflux-client-*.jar app.jar

ENV AI_DASHSCOPE_API_KEY=${AI_DASHSCOPE_API_KEY}

ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建和运行：

```bash
docker build -t mcp-streamable-client .
docker run -e AI_DASHSCOPE_API_KEY=your_key mcp-streamable-client
```

## 最佳实践

1. **错误处理**: 实现完善的异常处理和重试机制
2. **资源管理**: 正确关闭连接和释放资源
3. **日志记录**: 记录关键操作和错误信息
4. **配置管理**: 使用环境变量管理敏感配置
5. **性能监控**: 监控连接池使用情况和响应时间
6. **安全考虑**: 验证服务器身份和通信安全

## 相关资源

- [Spring AI Alibaba 官方文档](https://github.com/alibaba/spring-ai-alibaba)
- [Model Context Protocol 规范](https://modelcontextprotocol.io/)
- [DashScope API 文档](https://help.aliyun.com/zh/dashscope/)
- [Spring WebFlux 文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)