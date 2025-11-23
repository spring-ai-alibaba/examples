# Spring AI Alibaba MCP Nacos 示例

本项目演示了如何将 Spring AI Alibaba 与模型上下文协议（MCP）和 Nacos 进行集成，实现服务发现和注册。它通过 Nacos 展示了一个完整的 MCP 生态系统，包括服务器端工具注册和客户端工具发现
- 注册：通过Nacos实现，MCP服务注册至Nacos中
- 分布式：通过nacos实现mcp server服务分布式部署，mcp client负载均衡调用mcp server
- 网关：通过nacos实现存量restful接口应用，转化为mcp server服务
- 路由：通过nacos实现mcp client调用工具路由控制（待补充）

依赖：[spring ai extensions](https://github.com/spring-ai-alibaba/spring-ai-extensions)在1.1.0.0-M4版本及以上

## 前置条件

- Java 17+
- Maven 3.6+
- nacos版本3.1.0+
- 已设置 DASHSCOPE_API_KEY 环境变量

## 目录结构
```angular2html
- client
    - mcp-nacos-distributed-extensions-example      # 基于spring-ai-extensions下的分布式客户端发现示例
- server
    - mcp-nacos-register-extensions-example         # 基于spring-ai-extensions下的注册至nacos示例
    - mcp-nacos-gateway-example                     # 基于spring-ai-alibaba下的注册至nacos并使用nacos作为mcp的网关示例
```

本示例提供了使用 Spring AI Alibaba 和 Nacos 服务发现构建分布式 MCP 应用程序的完整参考。