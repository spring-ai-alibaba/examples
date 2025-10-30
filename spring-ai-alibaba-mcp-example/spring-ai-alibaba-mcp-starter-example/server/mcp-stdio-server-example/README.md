# Spring AI MCP STDIO 服务器示例

这个项目展示了如何使用 Spring AI 的 Model Context Protocol (MCP) 创建一个基于标准输入/输出（STDIO）的服务器应用程序。该服务器提供天气预报和空气质量信息的工具，可以被 MCP 客户端调用。

## 项目概述

本项目是一个基于 Spring Boot 的应用程序，它实现了 MCP 服务器，通过标准输入/输出（STDIO）与客户端通信。这种通信方式使得服务器可以作为子进程被客户端启动和管理，非常适合嵌入式场景。

主要功能：
- 提供天气预报查询工具
- 提供空气质量信息查询工具（模拟数据）
- 通过 STDIO 与客户端进行通信

## 技术栈

- Spring Boot
- Spring AI MCP Server
- Spring Web (用于 HTTP 客户端)
- Model Context Protocol (MCP)

## 项目结构

```
starter-stdio-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/springframework/ai/mcp/sample/server/
│   │   │       ├── McpServerApplication.java  # 应用程序入口
│   │   │       └── OpenMeteoService.java      # 天气服务实现
│   │   └── resources/
│   │       └── application.properties         # 应用配置
│   └── test/
│       └── java/
│           └── org/springframework/ai/mcp/sample/client/
│               └── ClientStdio.java           # 客户端测试类
└── pom.xml                                    # Maven 配置
```

## 核心组件

### McpServerApplication

应用程序的入口点，配置了 Spring Boot 应用并注册了天气工具服务。

### OpenMeteoService

提供天气相关的工具实现：
- `getWeatherForecastByLocation`: 根据经纬度获取天气预报
- `getAirQuality`: 根据经纬度获取空气质量信息（模拟数据）

这些方法使用 `@Tool` 注解标记，使它们可以被 MCP 客户端发现和调用。

## 配置说明

在 `application.properties` 中：

```properties
spring.main.web-application-type=none
spring.main.banner-mode=off
logging.pattern.console=

spring.ai.mcp.server.name=my-weather-server
spring.ai.mcp.server.version=0.0.1
```

注意：
- 必须禁用 web 应用类型
- 必须禁用 banner 和控制台日志，以确保 STDIO 传输正常工作

## 如何使用

### 构建项目

```bash
./mvnw clean install -DskipTests
```

### 客户端示例

参考 `ClientStdio.java` 中的示例代码，了解如何创建客户端并调用服务器提供的工具：

```java
var stdioParams = ServerParameters.builder("java")
        .args("-jar", "target/mcp-stdio-server-exmaple-0.0.1-SNAPSHOT.jar")
        .build();

var transport = new StdioClientTransport(stdioParams, McpJsonMapper.getDefault());
var client = McpClient.sync(transport).build();

client.initialize();

// 列出可用工具
ListToolsResult toolsList = client.listTools();

// 调用天气预报工具
CallToolResult weatherForecastResult = client.callTool(
    new CallToolRequest("getWeatherForecastByLocation", 
    Map.of("latitude", "39.9042", "longitude", "116.4074"))
);

// 调用空气质量工具
CallToolResult airQualityResult = client.callTool(
    new CallToolRequest("getAirQuality", 
    Map.of("latitude", "39.9042", "longitude", "116.4074"))
);

client.closeGracefully();
```

## 注意事项

1. 服务器使用 OpenMeteo 的免费天气 API，无需 API 密钥
2. 空气质量数据为模拟数据，实际应用中应替换为真实 API
3. 使用 STDIO 传输时，必须禁用控制台日志和 banner，否则会干扰通信

## 扩展开发

如需添加新的工具，只需：
1. 创建新的服务类
2. 使用 `@Tool` 注解标记方法
3. 在 `McpServerApplication` 中注册服务

## 许可证

Apache License 2.0 
