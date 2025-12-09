# Spring AI Alibaba HelloWorld 示例

本示例展示如何使用 Spring AI Alibaba 集成阿里云通义千问模型，包括基本对话、流式响应、对话记忆等功能。

## 快速开始

### 环境准备

1. **Java 17+**
2. **Maven 3.6+**
3. **阿里云 DashScope API Key**

### 配置步骤

1. 设置环境变量：
```bash
# Linux/macOS
export AI_DASHSCOPE_API_KEY=your_api_key_here

# Windows
set AI_DASHSCOPE_API_KEY=your_api_key_here
```

2. 或者在 `application.yml` 中配置：
```yaml
spring:
  ai:
    dashscope:
      api-key: your_api_key_here
      chat:
        model: qwen-plus
        options:
          temperature: 0.7
          max-tokens: 2000
```

3. 启动应用：
```bash
mvn spring-boot:run
```

应用将启动在 `http://localhost:8080`

## 接口文档

### HelloworldController 接口

#### 1. simpleChat 方法

**接口路径：** `GET /helloworld/simple/chat`

**功能描述：** ChatClient 简单调用示例，展示基本的AI对话功能。

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码
- 简单的问答交互

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```http
GET http://localhost:8080/helloworld/simple/chat
```

```bash
curl -X GET http://localhost:8080/helloworld/simple/chat
```

**示例响应：**
```json
{
  "response": "Hello! I'm a helpful AI assistant. How can I help you today?",
  "metadata": {
    "model": "qwen-plus",
    "timestamp": "2025-12-09T12:00:00Z"
  }
}
```

#### 2. streamChat 方法

**接口路径：** `GET /helloworld/stream/chat`

**功能描述：** 提供流式对话功能，实时返回AI响应内容。

**主要特性：**
- Server-Sent Events (SSE) 流式响应
- 实时逐字输出
- 更好的用户体验
- 支持中断和重连

**使用场景：**
- 实时对话场景
- 长文本生成
- 交互式应用

**示例请求：**
```http
GET http://localhost:8080/helloworld/stream/chat
```

```bash
curl -N http://localhost:8080/helloworld/stream/chat
```

**示例响应：**
```
data: {"type":"START","message":"Stream started"}

data: {"type":"CHUNK","content":"Hello"}

data: {"type":"CHUNK","content":"! I"}

data: {"type":"CHUNK","content":"'m"}

data: {"type":"CHUNK","content":" an"}

data: {"type":"END","message":"Stream completed"}
```

#### 3. advisorChat 方法

**接口路径：** `GET /helloworld/advisor/chat/{conversationId}`

**功能描述：** ChatClient 使用自定义的 Advisor 实现功能增强，支持对话记忆。

**主要特性：**
- 对话上下文记忆
- 多轮对话支持
- 自定义 Advisor 链
- 会话管理

**使用场景：**
- 多轮对话场景
- 需要上下文的任务
- 智能客服系统

**示例请求：**
```http
GET http://localhost:8080/helloworld/advisor/chat/session123?message=你好，我叫小明
```

```bash
curl -X GET "http://localhost:8080/helloworld/advisor/chat/session123?message=你好，我叫小明"
```

**继续对话：**
```bash
curl -X GET "http://localhost:8080/helloworld/advisor/chat/session123?message=你还记得我的名字吗？"
```

#### 4. newChat 方法

**接口路径：** `GET /helloworld/advisor/newChat`

**功能描述：** ChatClient 新的聊天接口，支持流式输出和自定义 ChatOptions 配置。

**主要特性：**
- 流式响应
- 自定义模型参数
- 灵活的配置选项
- 高级功能展示

**使用场景：**
- 高级对话功能
- 自定义模型参数
- 性能优化场景

**示例请求：**
```http
GET http://localhost:8080/helloworld/advisor/newChat?prompt=请写一首关于春天的诗&temperature=0.9
```

```bash
curl -X GET "http://localhost:8080/helloworld/advisor/newChat?prompt=请写一首关于春天的诗&temperature=0.9"
```

## 技术实现

### 核心组件

- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **DashScope Client**: 阿里云模型客户端
- **ChatClient**: Spring AI 核心对话接口
- **Advisor**: 功能增强组件
- **ChatMemory**: 对话记忆管理

### 依赖配置

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring AI Alibaba Starter -->
    <dependency>
        <groupId>com.alibaba.cloud.ai</groupId>
        <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
        <version>1.1.0</version>
    </dependency>
</dependencies>
```

### 代码实现示例

#### 1. 基础对话实现

```java
@RestController
@RequestMapping("/helloworld")
public class HelloworldController {

    private final ChatClient chatClient;

    public HelloworldController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
            .defaultSystem("You are a helpful AI assistant.")
            .build();
    }

    @GetMapping("/simple/chat")
    public Map<String, Object> simpleChat() {
        String response = chatClient.prompt()
            .user("Hello! Can you introduce yourself?")
            .call()
            .content();

        return Map.of(
            "response", response,
            "timestamp", Instant.now().toString()
        );
    }
}
```

#### 2. 流式响应实现

```java
@GetMapping("/stream/chat")
public Flux<String> streamChat() {
    return chatClient.prompt()
        .user("Please tell me a story")
        .stream()
        .content();
}
```

#### 3. 自定义 Advisor

```java
@Component
public class CustomChatAdvisor implements RequestResponseAdvisor {

    @Override
    public AdvisorResponse adviseResponse(AdvisorRequest request,
                                        AdvisorResponse response) {
        // 添加自定义响应处理
        String content = response.response().getResult().getOutput().getContent();

        // 记录对话日志
        logConversation(request, content);

        return response;
    }

    private void logConversation(AdvisorRequest request, String content) {
        System.out.println("User: " + request.userText());
        System.out.println("AI: " + content);
    }
}
```

#### 4. 对话记忆实现

```java
@Component
public class InMemoryChatMemory implements ChatMemory {

    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    @Override
    public void add(String conversationId, Message message) {
        conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>())
                         .add(message);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> history = conversationHistory.getOrDefault(conversationId, Collections.emptyList());
        int size = history.size();
        return history.subList(Math.max(0, size - lastN), size);
    }
}
```

## 配置详解

### 1. 基础配置

```yaml
spring:
  ai:
    dashscope:
      api-key: ${AI_DASHSCOPE_API_KEY}
      base-url: https://dashscope.aliyuncs.com/api/v1
      chat:
        model: qwen-plus
        options:
          temperature: 0.7
          max-tokens: 2000
          top-p: 0.8
```

### 2. 高级配置

```yaml
spring:
  ai:
    dashscope:
      chat:
        model: qwen-plus
        options:
          # 温度参数：控制输出的随机性 (0-1)
          temperature: 0.7
          # 最大生成 token 数
          max-tokens: 2000
          # 核采样参数 (0-1)
          top-p: 0.8
          # 停止词
          stop: ["\n\n", "###"]
          # 是否启用流式响应
          stream: true
```

### 3. 完整配置示例

```yaml
server:
  port: 8080

spring:
  application:
    name: spring-ai-alibaba-helloworld

  ai:
    dashscope:
      api-key: ${AI_DASHSCOPE_API_KEY}
      chat:
        model: qwen-plus
        options:
          temperature: 0.7
          max-tokens: 2000
          top-p: 0.8

logging:
  level:
    com.alibaba.cloud.ai: DEBUG
    org.springframework.ai: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

## 部署指南

### 1. 本地部署

```bash
# 构建项目
mvn clean package

# 运行应用
java -jar target/spring-ai-alibaba-helloworld-1.0.0.jar
```

### 2. Docker 部署

创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/spring-ai-alibaba-helloworld-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建和运行：

```bash
# 构建镜像
docker build -t spring-ai-helloworld .

# 运行容器
docker run -d \
  -p 8080:8080 \
  -e AI_DASHSCOPE_API_KEY=your_api_key \
  spring-ai-helloworld
```

### 3. Docker Compose 部署

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - AI_DASHSCOPE_API_KEY=${AI_DASHSCOPE_API_KEY}
      - SPRING_PROFILES_ACTIVE=prod
    restart: unless-stopped
```

## 测试

### 1. 使用 HTTP 文件测试

项目根目录下提供了 `spring-ai-alibaba-helloworld.http` 文件，包含所有接口的测试用例：

```http
### 简单对话测试
GET http://localhost:8080/helloworld/simple/chat

### 流式对话测试
GET http://localhost:8080/helloworld/stream/chat

### 带记忆的对话测试
GET http://localhost:8080/helloworld/advisor/chat/session123?message=你好

### 继续对话测试
GET http://localhost:8080/helloworld/advisor/chat/session123?message=我刚才说了什么？
```

### 2. 使用 curl 测试

```bash
# 测试简单对话
curl -X GET http://localhost:8080/helloworld/simple/chat

# 测试流式响应
curl -N http://localhost:8080/helloworld/stream/chat

# 测试带记忆的对话
curl -X GET "http://localhost:8080/helloworld/advisor/chat/test123?message=请记住我喜欢蓝色"

# 验证记忆
curl -X GET "http://localhost:8080/helloworld/advisor/chat/test123?message=我喜欢什么颜色？"
```

### 3. 单元测试

```java
@SpringBootTest
@AutoConfigureTestDatabase
class HelloworldControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ChatClient chatClient;

    @Test
    void testSimpleChat() {
        // 模拟 ChatClient 响应
        when(chatClient.prompt().user(anyString()).call().content())
            .thenReturn("Hello! I'm an AI assistant.");

        ResponseEntity<String> response = restTemplate.getForEntity(
            "/helloworld/simple/chat", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("AI assistant"));
    }
}
```

## 最佳实践

### 1. 错误处理

```java
@ControllerAdvice
public class AIExceptionHandler {

    @ExceptionHandler(ChatModelException.class)
    public ResponseEntity<String> handleChatModelException(ChatModelException e) {
        log.error("AI model error", e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("AI service temporarily unavailable");
    }
}
```

### 2. 性能优化

```java
@Configuration
public class ChatConfig {

    @Bean
    @Primary
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel,
                                              ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
            .defaultSystem("You are a helpful assistant.")
            .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
            .defaultOptions(DashScopeChatOptions.builder()
                .withTemperature(0.7)
                .withMaxTokens(2000)
                .build());
    }
}
```

### 3. 安全考虑

```java
@Component
public class SecurityAdvisor implements RequestResponseAdvisor {

    private static final List<String> BLOCKED_KEYWORDS =
        Arrays.asList("password", "secret", "token");

    @Override
    public AdvisorRequest adviseRequest(AdvisorRequest request) {
        String userInput = request.userText();

        for (String keyword : BLOCKED_KEYWORDS) {
            if (userInput.toLowerCase().contains(keyword)) {
                throw new SecurityException("Input contains sensitive information");
            }
        }

        return request;
    }
}
```

## 故障排查

### 常见问题

1. **API Key 未配置**
   ```
   解决方案：确保 AI_DASHSCOPE_API_KEY 环境变量已设置
   ```

2. **网络连接问题**
   ```
   解决方案：检查网络连接，确保能访问阿里云服务
   ```

3. **模型调用失败**
   ```
   解决方案：检查模型名称和参数配置是否正确
   ```

4. **流式响应中断**
   ```
   解决方案：检查客户端是否正确处理 SSE 格式
   ```

### 日志配置

```yaml
logging:
  level:
    com.alibaba.cloud.ai: DEBUG
    org.springframework.ai: DEBUG
    org.springframework.web: DEBUG
  file:
    name: logs/spring-ai-helloworld.log
    max-size: 10MB
    max-history: 30
```

## 扩展功能

### 1. 多模态支持

```java
@RestController
@RequestMapping("/multimodal")
public class MultimodalController {

    @PostMapping("/analyze")
    public String analyzeImage(@RequestParam("file") MultipartFile file) {
        // 实现图像分析功能
        return chatClient.prompt()
            .user(user -> user.text("Describe this image").media(MimeTypeUtils.IMAGE_JPEG, file.getResource()))
            .call()
            .content();
    }
}
```

### 2. 函数调用

```java
@Component
public class WeatherService {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonClassDescription("Get the current weather in a given location")
    public record WeatherRequest(
        @JsonProperty(required = true, value = "location")
        @JsonPropertyDescription("The city and state, e.g. San Francisco, CA")
        String location) {
    }

    @Function("getCurrentWeather")
    public String getCurrentWeather(WeatherRequest request) {
        // 调用天气 API
        return String.format("The weather in %s is 72°F and sunny.", request.location());
    }
}
```

---

*更新时间: 2025-12-09*
*版本: 1.1.0*