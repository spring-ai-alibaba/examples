# Spring AI MCP + Nacos Gateway 示例项目

本项目是一个基于 [spring-ai-alibaba-mcp-gateway-nacos](https://github.com/spring-projects/spring-ai-alibaba) 的简单示例，展示如何构建一个 MCP Gateway 服务，动态代理 Nacos 中注册的 MCP 服务。

本示例是 MCP Gateway 代理 Nacos 中的 MCP 服务，实现服务能力到 AI 工具的转化，要求版本如下：

1. Nacos版本在3.1.0及以上
2. [spring ai extensions](https://github.com/spring-ai-alibaba/spring-ai-extensions)在1.1.0.0-M5版本及以上

## 🧩 主要依赖

```xml
<!-- MCP Gateway Nacos 支持 -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-mcp-gateway-nacos</artifactId>
</dependency>

<!-- MCP Server WebMvc 支持 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
</dependency>
```
