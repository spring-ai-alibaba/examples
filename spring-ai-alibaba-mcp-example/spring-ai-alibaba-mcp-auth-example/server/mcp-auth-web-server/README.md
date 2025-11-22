# Spring AI MCP Auth Web Server

## 项目介绍

Spring AI MCP Auth Web Server 是一个实现了身份认证机制的 MCP (Model Context Protocol) 服务器示例。该项目展示了如何在 MCP 服务器中添加基于 HTTP 过滤器的认证机制，确保只有经过身份验证的客户端才能访问 MCP 服务。

## 主要特性

- **身份认证**: 基于 HTTP 请求头的身份验证机制
- **过滤器保护**: 使用 Spring WebFlux 过滤器实现请求拦截
- **灵活配置**: 支持自定义认证规则和 token 配置
- **响应式架构**: 基于 Spring WebFlux 的异步非阻塞处理
- **详细日志**: 提供完整的认证过程日志记录
- **安全拒绝**: 对未认证请求返回 401 Unauthorized

## 技术栈

- **Java 17+** - 运行环境
- **Spring Boot 3.4.0** - 应用框架
- **Spring WebFlux** - 响应式 Web 框架
- **Spring AI 1.0.0** - AI 框架
- **Model Context Protocol (MCP)** - 工具调用协议
- **Project Reactor** - 响应式编程库
- **Maven** - 构建工具

## 项目结构

```
mcp-auth-web-server/
├── src/
│   ├── main/
│   │   ├── java/com/alibaba/cloud/ai/mcp/server/
│   │   │   ├── AuthWebServerApplication.java    # 主应用类
│   │   │   ├── service/
│   │   │   │   └── TimeService.java            # 时间查询服务
│   │   │   └── filter/
│   │   │       └── McpServerFilter.java        # MCP 认证过滤器
│   │   └── resources/
│   │       └── application.yml                  # 应用配置
│   └── test/                                    # 测试目录
└── pom.xml                                      # Maven 配置
```

## 核心组件

### 1. AuthWebServerApplication

服务器主应用类，负责：
- Spring Boot 应用配置
- MCP 服务器初始化
- 工具注册和管理

### 2. McpServerFilter

认证过滤器，实现：
- HTTP 请求头检查
- Token 验证逻辑
- 未认证请求拒绝
- 详细的认证日志记录

### 3. TimeService

时间查询服务，提供：
- 全球城市时间查询功能
- 基于 `@Tool` 注解的工具定义

## 认证机制

### Token 验证规则

服务器在 `McpServerFilter` 中实现了以下认证规则：

```java
private static final String TOKEN_HEADER = "token-1";
private static final String TOKEN_VALUE = "yingzi-1";
```

- **认证头**: `token-1`
- **期望值**: `yingzi-1`
- **验证方式**: 请求头字符串匹配
- **失败处理**: 返回 401 Unauthorized

### 认证流程

1. **请求拦截**: 所有到达 `/mcp` 端点的请求都会被过滤器拦截
2. **头信息检查**: 检查请求中是否包含 `token-1` 头
3. **Token 验证**: 验证 token 值是否为 `yingzi-1`
4. **处理决策**:
   - 验证通过：继续处理请求
   - 验证失败：返回 401 状态码并拒绝访问

## 配置说明

### 服务器配置 (application.yml)

```yaml
server:
  port: 20000  # 服务器监听端口

spring:
  application:
    name: mcp-auth-web-server

  ai:
    mcp:
      server:
        name: streamable-mcp-server     # MCP 服务器名称
        protocol: STREAMABLE            # 协议类型
        version: 1.0.0                  # 服务器版本
        type: ASYNC                     # 异步模式
        instructions: "This reactive server provides time information tools and resources"
        request-timeout: 20s            # 请求超时时间

        streamable-http:
          mcp-endpoint: /mcp            # MCP 端点路径
          keep-alive-interval: 30s      # 保持连接间隔
          disallow-delete: false        # 是否禁用 DELETE 方法
```

## 快速开始

### 1. 环境准备

确保已安装以下环境：
- Java 17+
- Maven 3.6+

### 2. 构建项目

```bash
# 进入项目目录
cd spring-ai-alibaba-mcp-auth-example/server/mcp-auth-web-server

# 编译项目
mvn clean package -DskipTests
```

### 3. 启动服务器

#### 方式一：使用 Maven 启动
```bash
mvn spring-boot:run
```

#### 方式二：使用 JAR 包启动
```bash
java -jar target/mcp-auth-web-server-*.jar
```

服务器将在 `http://localhost:20000` 启动，并启用认证过滤器。

### 4. 验证认证机制

#### 测试有效认证
```bash
# 使用正确的 token
curl -H "token-1: yingzi-1" http://localhost:20000/mcp
```

#### 测试无效认证
```bash
# 使用错误的 token
curl -H "token-1: wrong-token" http://localhost:20000/mcp

# 无 token 请求
curl http://localhost:20000/mcp
```

### 5. 启动认证客户端

启动配套的认证客户端进行完整测试：

```bash
cd ../client/mcp-auth-client
mvn spring-boot:run
```

## 认证实现详解

### 1. 过滤器实现

```java
@Component
public class McpServerFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();

        // 打印所有请求头信息（用于调试）
        for (String headerName : headers.keySet()) {
            logger.info("Header {}: {}", headerName, headers.getFirst(headerName));
        }

        String token = headers.getFirst(TOKEN_HEADER);

        if (TOKEN_VALUE.equals(token)) {
            // 认证通过，继续处理请求
            logger.info("preHandle: 验证通过");
            return chain.filter(exchange);
        } else {
            // 认证失败，返回 401
            logger.warn("Token验证失败");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
```

### 2. 日志记录

认证过程中的详细日志：

```
# 成功认证日志
INFO  --- McpServerFilter: Header token-1: yingzi-1
INFO  --- McpServerFilter: preHandle: 验证通过
INFO  --- McpServerFilter: preHandle: 请求的URL: http://localhost:20000/mcp
INFO  --- McpServerFilter: preHandle: 请求的TOKEN: yingzi-1

# 失败认证日志
WARN  --- McpServerFilter: Token验证失败: 请求的URL: http://localhost:20000/mcp, 提供的TOKEN: wrong-token
WARN  --- McpServerFilter: 要求的token为：yingzi-1
```

## 开发指南

### 自定义认证规则

#### 1. 修改 Token 配置

在 `McpServerFilter.java` 中修改认证规则：

```java
@Component
public class McpServerFilter implements WebFilter {

    // 自定义认证头和值
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_VALUE = "your-secret-token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authHeader = headers.getFirst(TOKEN_HEADER);

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            String token = authHeader.substring(TOKEN_PREFIX.length());
            if (TOKEN_VALUE.equals(token)) {
                return chain.filter(exchange);
            }
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
```

#### 2. 多 Token 支持

```java
@Component
public class McpServerFilter implements WebFilter {

    private static final Set<String> VALID_TOKENS = Set.of(
        "token-1-value",
        "token-2-value",
        "token-3-value"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst("Authorization");

        if (token != null && VALID_TOKENS.contains(token)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
```

#### 3. 配置文件驱动的认证

从配置文件读取认证规则：

```yaml
# application.yml
auth:
  tokens:
    - header: "token-1"
      value: "yingzi-1"
    - header: "Authorization"
      value: "Bearer jwt-token"
```

```java
@Component
public class McpServerFilter implements WebFilter {

    @Value("${auth.tokens}")
    private List<Map<String, String>> authTokens;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();

        for (Map<String, String> tokenConfig : authTokens) {
            String header = tokenConfig.get("header");
            String expectedValue = tokenConfig.get("value");
            String actualValue = headers.getFirst(header);

            if (expectedValue.equals(actualValue)) {
                return chain.filter(exchange);
            }
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
```

### 高级认证场景

#### 1. JWT Token 验证

```java
@Component
public class McpServerFilter implements WebFilter {

    private final JwtTokenValidator jwtValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authHeader = headers.getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            return jwtValidator.validate(jwt)
                .flatMap(isValid -> {
                    if (isValid) {
                        return chain.filter(exchange);
                    } else {
                        return unauthorized(exchange);
                    }
                })
                .onErrorResume(e -> unauthorized(exchange));
        }

        return unauthorized(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
```

#### 2. API Key 认证

```java
@Component
public class McpServerFilter implements WebFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private final Set<String> validApiKeys;

    public McpServerFilter() {
        // 从环境变量或配置中心加载有效 API Key
        this.validApiKeys = Set.of(
            System.getenv("API_KEY_1"),
            System.getenv("API_KEY_2")
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String apiKey = headers.getFirst(API_KEY_HEADER);

        if (apiKey != null && validApiKeys.contains(apiKey)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
```

#### 3. IP 白名单认证

```java
@Component
public class McpServerFilter implements WebFilter {

    private static final Set<String> ALLOWED_IPS = Set.of(
        "127.0.0.1",
        "192.168.1.0/24",  // 支持网段
        "10.0.0.0/8"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

        if (isIpAllowed(clientIp)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    private boolean isIpAllowed(String ip) {
        return ALLOWED_IPS.contains(ip) ||
               ALLOWED_IPS.stream().anyMatch(allowed -> isIpInSubnet(ip, allowed));
    }
}
```

### 过滤器链配置

```java
@Configuration
public class FilterConfig {

    @Bean
    public WebFilter mcpAuthFilter() {
        return new McpServerFilter();
    }

    @Bean
    public WebFilter corsFilter() {
        return (exchange, chain) -> {
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "*");
            headers.add("Access-Control-Max-Age", "3600");

            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }

            return chain.filter(exchange);
        };
    }
}
```

## 安全建议

### 1. 生产环境配置

```yaml
spring:
  profiles:
    active: prod

auth:
  # 从环境变量读取敏感配置
  jwt-secret: ${JWT_SECRET}
  api-keys: ${API_KEYS}

logging:
  level:
    com.alibaba.cloud.ai.mcp.server.filter: INFO
    # 避免在生产环境中打印敏感信息
    root: WARN
```

### 2. 安全最佳实践

- **Token 管理**: 定期轮换认证 token
- **HTTPS**: 在生产环境中使用 HTTPS
- **速率限制**: 实施请求速率限制
- **日志审计**: 记录所有认证尝试
- **最小权限**: 为不同客户端分配最小必要权限

### 3. 监控和告警

```java
@Component
public class AuthMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter authSuccessCounter;
    private final Counter authFailureCounter;

    public AuthMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.authSuccessCounter = Counter.builder("auth.success")
            .description("Successful authentications")
            .register(meterRegistry);
        this.authFailureCounter = Counter.builder("auth.failure")
            .description("Failed authentications")
            .register(meterRegistry);
    }

    public void recordSuccess() {
        authSuccessCounter.increment();
    }

    public void recordFailure() {
        authFailureCounter.increment();
    }
}
```

## 故障排除

### 常见问题

1. **认证失败 (401 Unauthorized)**
   ```
   问题: 所有请求都被拒绝
   解决:
   - 检查请求头是否正确设置
   - 验证 token 值是否匹配
   - 查看服务器日志确认验证逻辑
   ```

2. **过滤器不生效**
   ```
   问题: 请求没有被过滤器拦截
   解决:
   - 确认 @Component 注解存在
   - 检查过滤器注册顺序
   - 验证请求路径是否匹配
   ```

3. **性能问题**
   ```
   问题: 认证导致响应变慢
   解决:
   - 优化认证逻辑
   - 添加缓存机制
   - 使用异步验证
   ```

### 调试配置

启用详细日志：

```yaml
logging:
  level:
    com.alibaba.cloud.ai.mcp.server.filter: DEBUG
    org.springframework.web.server: DEBUG
    reactor.netty.http.client: DEBUG
```

### 测试工具

#### 使用 curl 测试

```bash
# 测试各种认证场景
#!/bin/bash

echo "测试正确认证"
curl -v -H "token-1: yingzi-1" http://localhost:20000/mcp

echo -e "\n\n测试错误认证"
curl -v -H "token-1: wrong-token" http://localhost:20000/mcp

echo -e "\n\n测试无认证"
curl -v http://localhost:20000/mcp
```

#### 使用 Postman 测试

创建 Postman 集合测试不同认证场景：
- 正确 token 测试
- 错误 token 测试
- 无 token 测试
- 性能压力测试

## 扩展开发

### 1. 认证服务抽象

```java
public interface AuthenticationService {
    Mono<Boolean> authenticate(HttpHeaders headers);
    Mono<String> getAuthenticatedUser(HttpHeaders headers);
}
```

### 2. 插件化认证

```java
@Component
public class PluginAuthFilter implements WebFilter {

    private final List<AuthenticationPlugin> plugins;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Flux.fromIterable(plugins)
            .flatMap(plugin -> plugin.authenticate(exchange.getRequest().getHeaders()))
            .filter(Boolean::booleanValue)
            .hasElements()
            .flatMap(hasValidAuth -> {
                if (hasValidAuth) {
                    return chain.filter(exchange);
                } else {
                    return unauthorized(exchange);
                }
            });
    }
}
```

## 相关资源

- [Spring AI Alibaba 官方文档](https://github.com/alibaba/spring-ai-alibaba)
- [Model Context Protocol 规范](https://modelcontextprotocol.io/)
- [Spring WebFlux 文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Spring Security 参考指南](https://docs.spring.io/spring-security/reference/)
- [JWT 规范 (RFC 7519)](https://tools.ietf.org/html/rfc7519)
- [HTTP 认证最佳实践](https://tools.ietf.org/html/rfc7235)