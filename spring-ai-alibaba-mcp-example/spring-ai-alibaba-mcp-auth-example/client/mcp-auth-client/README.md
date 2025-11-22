# Spring AI MCP Auth Client

## 项目介绍

Spring AI MCP Auth Client 是一个实现了身份认证机制的 MCP (Model Context Protocol) 客户端示例。该项目展示了如何在 MCP 客户端中添加自定义请求头，实现与需要认证的 MCP 服务器的安全连接。

## 主要特性

- **身份认证**: 支持通过自定义 HTTP 请求头进行身份验证
- **可配置认证**: 支持多个认证 token 的灵活配置
- **流式通信**: 支持与 MCP 服务器的实时双向通信
- **交互式对话**: 提供命令行交互界面，支持连续对话
- **异步处理**: 基于响应式编程模型，支持高并发
- **自动集成**: 自动集成服务器提供的工具功能

## 技术栈

- **Java 17+** - 运行环境
- **Spring Boot 3.4.0** - 应用框架
- **Spring AI 1.0.0** - AI 框架
- **Spring AI Alibaba 1.0.0.3** - 阿里云集成
- **DashScope API** - 阿里云通义千问大模型
- **Model Context Protocol (MCP)** - 工具调用协议
- **Maven** - 构建工具

## 项目结构

```
mcp-auth-client/
├── src/
│   ├── main/
│   │   ├── java/com/alibaba/cloud/ai/mcp/client/
│   │   │   ├── AuthClientApplication.java                # 主应用类
│   │   │   └── config/
│   │   │       ├── HeaderSyncHttpRequestCustomizer.java  # 请求头定制器
│   │   │       └── HttpClientConfig.java                 # HTTP 客户端配置
│   │   └── resources/
│   │       └── application.yml                           # 应用配置
│   └── test/                                             # 测试目录
└── pom.xml                                               # Maven 配置
```

## 核心组件

### 1. AuthClientApplication

客户端主应用类，负责：
- MCP 客户端初始化
- 工具集成和发现
- 交互式对话界面
- 流式响应处理

### 2. HeaderSyncHttpRequestCustomizer

请求头定制器，实现：
- 自定义 HTTP 请求头的添加
- 认证 token 的自动注入
- 支持多个认证头配置

### 3. HttpClientConfig

HTTP 客户端配置类，负责：
- 配置认证请求头
- 注册请求头定制器
- 提供灵活的认证配置

## 认证机制

### Token 配置

在 `HttpClientConfig.java` 中配置认证 token：

```java
@Bean
public McpSyncHttpClientRequestCustomizer mcpAsyncHttpClientRequestCustomizer() {
    Map<String, String> headers = new HashMap<>();
    headers.put("token-1", "yingzi-1");
    headers.put("token-2", "yingzi-2");

    return new HeaderSyncHttpRequestCustomizer(headers);
}
```

### 请求头自动注入

客户端会自动在所有 MCP 请求中添加以下请求头：
- `token-1: yingzi-1`
- `token-2: yingzi-2`

## 配置说明

### 客户端配置 (application.yml)

```yaml
server:
  port: 19100  # 客户端应用端口

spring:
  application:
    name: mcp-auth-client

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

### 2. 启动认证服务器

首先启动 MCP Auth Web Server：

```bash
cd ../server/mcp-auth-web-server
mvn spring-boot:run
```

服务器将在 `http://localhost:20000` 启动，并启用认证过滤器。

### 3. 启动认证客户端

```bash
# 进入客户端目录
cd spring-ai-alibaba-mcp-auth-example/client/mcp-auth-client

# 编译项目
mvn clean package -DskipTests

# 启动客户端
mvn spring-boot:run
```

### 4. 验证认证流程

启动客户端后，观察服务器日志：

```
# 服务器端会显示如下日志
Header token-1: yingzi-1
Header token-2: yingzi-2
preHandle: 验证通过
preHandle: 请求的URL: http://localhost:20000/mcp
preHandle: 请求的TOKEN: yingzi-1
```

### 5. 使用交互式对话

```
>>> QUESTION: 北京时间现在几点了？

>>> ASSISTANT: 我来帮您查询当前时间。根据时区信息，北京现在是 2024-01-15 14:30:25 CST。

>>> QUESTION: exit  # 退出对话
```

## 认证流程详解

### 1. 客户端认证流程

1. **初始化阶段**:
   - 创建 `HeaderSyncHttpRequestCustomizer` 实例
   - 配置认证 token 到请求头映射

2. **连接建立**:
   - 客户端发起连接请求
   - `HeaderSyncHttpRequestCustomizer` 自动添加认证头
   - 发送包含认证信息的 HTTP 请求

3. **认证验证**:
   - 服务器端过滤器验证请求头中的 token
   - 验证通过后建立连接
   - 验证失败返回 401 Unauthorized

### 2. 请求头定制机制

```java
@Override
public void customize(HttpRequest.Builder builder, String method, URI endpoint, String body, McpTransportContext context) {
    // 自动添加所有配置的认证头
    headers.forEach(builder::header);
}
```

## 开发指南

### 自定义认证配置

#### 1. 修改认证 Token

在 `HttpClientConfig.java` 中修改 token 配置：

```java
@Bean
public McpSyncHttpClientRequestCustomizer mcpAsyncHttpClientRequestCustomizer() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer your-jwt-token");
    headers.put("X-API-Key", "your-api-key");
    headers.put("Client-Id", "your-client-id");

    return new HeaderSyncHttpRequestCustomizer(headers);
}
```

#### 2. 动态 Token 配置

支持从环境变量或配置文件读取 token：

```java
@Value("${auth.token1:default-token-1}")
private String token1;

@Value("${auth.token2:default-token-2}")
private String token2;

@Bean
public McpSyncHttpClientRequestCustomizer mcpAsyncHttpClientRequestCustomizer() {
    Map<String, String> headers = new HashMap<>();
    headers.put("token-1", token1);
    headers.put("token-2", token2);

    return new HeaderSyncHttpRequestCustomizer(headers);
}
```

#### 3. 添加环境变量配置

在 `application.yml` 中添加：

```yaml
auth:
  token1: ${AUTH_TOKEN_1:yingzi-1}
  token2: ${AUTH_TOKEN_2:yingzi-2}
```

### 高级认证场景

#### 1. JWT Token 认证

```java
@Bean
public McpSyncHttpClientRequestCustomizer jwtAuthCustomizer() {
    Map<String, String> headers = new HashMap<>();

    // 从环境变量获取 JWT
    String jwt = System.getenv("JWT_TOKEN");
    if (jwt != null) {
        headers.put("Authorization", "Bearer " + jwt);
    }

    return new HeaderSyncHttpRequestCustomizer(headers);
}
```

#### 2. API Key 认证

```java
@Bean
public McpSyncHttpClientRequestCustomizer apiKeyAuthCustomizer() {
    Map<String, String> headers = new HashMap<>();

    // 从配置文件获取 API Key
    String apiKey = environment.getProperty("api.key");
    if (apiKey != null) {
        headers.put("X-API-Key", apiKey);
    }

    return new HeaderSyncHttpRequestCustomizer(headers);
}
```

#### 3. 多环境认证配置

```java
@Configuration
public class MultiEnvAuthConfig {

    @Bean
    @Profile("dev")
    public McpSyncHttpClientRequestCustomizer devAuthCustomizer() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token-1", "dev-token-1");
        return new HeaderSyncHttpRequestCustomizer(headers);
    }

    @Bean
    @Profile("prod")
    public McpSyncHttpClientRequestCustomizer prodAuthCustomizer() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token-1", System.getenv("PROD_TOKEN_1"));
        headers.put("token-2", System.getenv("PROD_TOKEN_2"));
        return new HeaderSyncHttpRequestCustomizer(headers);
    }
}
```

## 故障排除

### 常见问题

1. **认证失败 (401 Unauthorized)**
   ```
   错误: 服务器返回 401 Unauthorized
   解决:
   - 检查 token 配置是否正确
   - 验证服务器端期望的 token 值
   - 查看服务器端日志确认 token 验证逻辑
   ```

2. **请求头未添加**
   ```
   错误: 服务器端收不到认证请求头
   解决:
   - 确认 HeaderSyncHttpRequestCustomizer 已正确注册
   - 检查 HttpClientConfig 类的 @Bean 注解
   - 查看客户端日志确认请求头定制器是否生效
   ```

3. **连接超时**
   ```
   错误: 连接 MCP 服务器超时
   解决:
   - 检查服务器是否正常启动
   - 验证网络连接是否通畅
   - 调整 request-timeout 配置
   ```

### 调试配置

启用详细日志以调试认证流程：

```yaml
logging:
  level:
    com.alibaba.cloud.ai.mcp.client: DEBUG
    com.alibaba.cloud.ai.mcp.client.config: DEBUG
    io.modelcontextprotocol.client: DEBUG
    org.springframework.ai.mcp: DEBUG
```

### 测试认证

使用 curl 测试认证机制：

```bash
# 测试正确 token
curl -H "token-1: yingzi-1" http://localhost:20000/mcp

# 测试错误 token
curl -H "token-1: wrong-token" http://localhost:20000/mcp

# 测试无 token
curl http://localhost:20000/mcp
```

## 安全建议

### 1. Token 管理

- **环境变量**: 使用环境变量存储敏感 token
- **配置加密**: 对配置文件中的敏感信息进行加密
- **Token 轮换**: 定期更新认证 token
- **最小权限**: 为不同客户端分配最小必要权限

### 2. 生产环境配置

```yaml
# 生产环境配置示例
spring:
  profiles:
    active: prod

auth:
  # 从环境变量读取 token
  token1: ${AUTH_TOKEN_1}
  token2: ${AUTH_TOKEN_2}

logging:
  level:
    com.alibaba.cloud.ai.mcp.client: INFO
    # 避免在生产环境中打印敏感信息
    io.modelcontextprotocol.client: WARN
```

### 3. 监控和审计

- **认证日志**: 记录所有认证尝试（成功和失败）
- **异常监控**: 监控认证失败的频率和模式
- **性能监控**: 监控认证流程的性能影响

## 扩展开发

### 1. 自定义认证策略

```java
public class CustomAuthStrategy implements McpSyncHttpClientRequestCustomizer {

    private final AuthenticationService authService;

    @Override
    public void customize(HttpRequest.Builder builder, String method, URI endpoint, String body, McpTransportContext context) {
        // 实现自定义认证逻辑
        String token = authService.getValidToken();
        builder.header("Authorization", "Bearer " + token);
    }
}
```

### 2. 认证状态管理

```java
@Component
public class AuthManager {

    private volatile String currentToken;
    private final ScheduledExecutorService scheduler;

    @PostConstruct
    public void init() {
        // 定期刷新 token
        scheduler.scheduleAtFixedRate(this::refreshToken, 0, 1, TimeUnit.HOURS);
    }

    public String getCurrentToken() {
        return currentToken;
    }

    private void refreshToken() {
        // 实现 token 刷新逻辑
        this.currentToken = fetchNewToken();
    }
}
```

## 相关资源

- [Spring AI Alibaba 官方文档](https://github.com/alibaba/spring-ai-alibaba)
- [Model Context Protocol 规范](https://modelcontextprotocol.io/)
- [DashScope API 文档](https://help.aliyun.com/zh/dashscope/)
- [Spring Security 参考指南](https://docs.spring.io/spring-security/reference/)
- [HTTP 认证最佳实践](https://tools.ietf.org/html/rfc7235)