# Spring AI MCP Streamable WebFlux Server

## 项目介绍

Spring AI MCP Streamable WebFlux Server 是一个基于 Spring WebFlux 的响应式 MCP (Model Context Protocol) 服务器示例。该服务器使用 STREAMABLE 协议，提供时间查询工具，支持异步、流式的 HTTP 通信，展示了如何构建高性能的响应式 MCP 服务器。

## 主要特性

- **响应式架构**: 基于 Spring WebFlux 构建的异步非阻塞服务器
- **STREAMABLE 协议**: 支持流式 HTTP 通信，提供高性能的双向数据传输
- **时间查询工具**: 提供全球城市时间查询功能
- **异步处理**: 专为高并发场景设计的异步请求处理
- **长连接支持**: 支持持久连接和实时数据推送
- **灵活配置**: 支持多种运行模式和协议配置

## 技术栈

- **Java 17+** - 运行环境
- **Spring Boot 3.4.0** - 应用框架
- **Spring WebFlux** - 响应式 Web 框架
- **Spring AI 1.0.0** - AI 框架
- **Spring AI Alibaba 1.0.0.3** - 阿里云集成
- **Model Context Protocol (MCP)** - 工具调用协议
- **Project Reactor** - 响应式编程库
- **Maven** - 构建工具

## 项目结构

```
mcp-streamable-webflux-server/
├── src/
│   ├── main/
│   │   ├── java/com/alibaba/cloud/ai/mcp/server/
│   │   │   ├── StreamableWebfluxServerApplication.java  # 主应用类
│   │   │   └── service/
│   │   │       └── TimeService.java                     # 时间查询服务
│   │   └── resources/
│   │       └── application.yml                          # 应用配置
│   └── test/                                            # 测试目录
└── pom.xml                                              # Maven 配置
```

## 核心组件

### 1. StreamableWebfluxServerApplication

应用程序主入口类，负责：
- Spring Boot 应用配置
- MCP 工具注册
- 响应式服务器初始化

### 2. TimeService

时间查询服务，提供：
- **工具名称**: `getCityTimeMethod`
- **功能**: 获取指定时区城市的当前时间
- **参数**: `timeZoneId` - 时区标识符（如：Asia/Shanghai）
- **返回**: 格式化的时间字符串

支持的时区格式：
- `Asia/Shanghai` - 上海时间
- `America/New_York` - 纽约时间
- `Europe/London` - 伦敦时间
- `Asia/Tokyo` - 东京时间

## 配置说明

### 服务器配置 (application.yml)

```yaml
server:
  port: 20000  # 服务器监听端口

spring:
  application:
    name: mcp-streamable-webflux-server

  ai:
    mcp:
      server:
        name: streamable-mcp-server      # MCP 服务器名称
        protocol: STREAMABLE             # 协议类型：STREAMABLE
        version: 1.0.0                   # 服务器版本
        type: ASYNC                      # 推荐用于响应式应用
        instructions: "This reactive server provides time information tools and resources"
        request-timeout: 20s             # 请求超时时间

        streamable-http:
          mcp-endpoint: /mcp             # MCP 端点路径
          keep-alive-interval: 30s       # 保持连接间隔
          disallow-delete: false         # 是否禁用 DELETE 方法
```

## 快速开始

### 1. 环境准备

确保已安装以下环境：
- Java 17+
- Maven 3.6+

### 2. 构建项目

```bash
# 进入项目目录
cd spring-ai-alibaba-mcp-starter-example/server/mcp-streamable-webflux-server

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
java -jar target/mcp-streamable-webflux-server-*.jar
```

服务器将在 `http://localhost:20000` 启动。

### 4. 验证服务器运行

访问 MCP 端点检查服务器状态：
```bash
curl http://localhost:20000/mcp
```

## API 使用示例

### MCP 客户端配置

在 MCP 客户端中配置此服务器：

```json
{
  "mcpServers": {
    "time-server": {
      "url": "http://localhost:20000/mcp"
    }
  }
}
```

### 工具调用示例

#### JSON-RPC 请求格式
```json
{
  "jsonrpc": "2.0",
  "method": "tools/call",
  "params": {
    "name": "getCityTimeMethod",
    "arguments": {
      "timeZoneId": "Asia/Shanghai"
    }
  },
  "id": 1
}
```

#### 响应示例
```json
{
  "jsonrpc": "2.0",
  "result": {
    "content": [
      {
        "type": "text",
        "text": "The current time zone is Asia/Shanghai and the current time is 2024-01-15 14:30:25 CST"
      }
    ]
  },
  "id": 1
}
```

## 客户端集成

### Spring AI 集成

在 Spring AI 客户端中集成此服务器：

```yaml
spring:
  ai:
    mcp:
      client:
        type: ASYNC
        streamable-http:
          connections:
            time-server:
              url: http://localhost:20000
              endpoint: /mcp
```

### 程序化集成

```java
// 创建流式传输
var transport = new WebFluxSseClientTransport(
    WebClient.builder()
        .baseUrl("http://localhost:20000")
        .build(),
    McpJsonMapper.getDefault()
);

// 创建 MCP 客户端
var mcpClient = McpClient.sync(transport).build();
var init = mcpClient.initialize();

// 调用时间工具
var result = mcpClient.callTool(new CallToolRequest(
    "getCityTimeMethod",
    Map.of("timeZoneId", "Asia/Shanghai")
));
```

## 支持的时区列表

常用的时区标识符：

| 时区 | 标识符 | 示例 |
|------|--------|------|
| 上海 | Asia/Shanghai | 2024-01-15 14:30:25 CST |
| 东京 | Asia/Tokyo | 2024-01-15 15:30:25 JST |
| 纽约 | America/New_York | 2024-01-15 02:30:25 EST |
| 伦敦 | Europe/London | 2024-01-15 06:30:25 GMT |
| 巴黎 | Europe/Paris | 2024-01-15 07:30:25 CET |
| 悉尼 | Australia/Sydney | 2024-01-15 17:30:25 AEDT |

## 性能特性

### 响应式优势

1. **非阻塞 I/O**: 所有 I/O 操作都是非阻塞的
2. **背压处理**: 自动处理请求流量控制
3. **资源效率**: 更少的线程占用更多连接
4. **弹性扩展**: 自动扩缩容能力

### 流式通信

- **长连接**: 支持 HTTP 持久连接
- **实时推送**: 支持服务器主动推送数据
- **双向通信**: 客户端和服务器双向数据传输
- **断线重连**: 自动重连机制

## 监控和调试

### 启用调试日志

在 application.yml 中添加：

```yaml
logging:
  level:
    com.alibaba.cloud.ai.mcp.server: DEBUG
    io.modelcontextprotocol: DEBUG
    org.springframework.web.reactive: DEBUG
```

### 健康检查端点

服务器提供以下端点用于监控：

- `/actuator/health` - 应用健康状态
- `/actuator/info` - 应用信息
- `/mcp` - MCP 协议端点

## 扩展开发

### 添加新工具

1. 创建新的服务类：

```java
@Service
public class MyNewService {

    @Tool(description = "新工具描述")
    public String myNewTool(@ToolParam(description = "参数描述") String param) {
        // 实现工具逻辑
        return "处理结果: " + param;
    }
}
```

2. 在主应用类中注册：

```java
@Bean
public ToolCallbackProvider myNewTools(MyNewService myNewService) {
    return MethodToolCallbackProvider.builder()
            .toolObjects(myNewService)
            .build();
}
```

### 添加资源支持

```java
@Service
public class MyResourceService {

    @Resource(description = "资源描述")
    public String myResource() {
        // 返回资源内容
        return "资源数据";
    }
}
```

## 部署建议

### 生产环境配置

```yaml
spring:
  ai:
    mcp:
      server:
        request-timeout: 60s            # 增加超时时间
        streamable-http:
          keep-alive-interval: 60s       # 增加保活间隔
          max-connections: 1000          # 最大连接数

server:
  port: 20000
  netty:
    connection-timeout: 30s
    keep-alive: true
```

### Docker 部署

创建 Dockerfile：

```dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app
COPY target/mcp-streamable-webflux-server-*.jar app.jar

EXPOSE 20000

ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建和运行：

```bash
docker build -t mcp-streamable-server .
docker run -p 20000:20000 mcp-streamable-server
```

## 故障排除

### 常见问题

1. **端口冲突**
   ```bash
   # 检查端口占用
   lsof -i :20000
   # 更换端口或停止占用进程
   ```

2. **连接超时**
   - 检查网络连接
   - 调整 request-timeout 配置
   - 查看服务器日志

3. **工具调用失败**
   - 验证工具参数格式
   - 检查时区标识符是否正确
   - 查看详细错误日志

### 调试技巧

1. **启用详细日志**
2. **使用网络抓包工具**（如 Wireshark）
3. **检查 MCP 协议兼容性**
4. **验证 JSON 格式正确性**

## 相关资源

- [Spring AI Alibaba 官方文档](https://github.com/alibaba/spring-ai-alibaba)
- [Model Context Protocol 规范](https://modelcontextprotocol.io/)
- [Spring WebFlux 文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Project Reactor 文档](https://projectreactor.io/docs)