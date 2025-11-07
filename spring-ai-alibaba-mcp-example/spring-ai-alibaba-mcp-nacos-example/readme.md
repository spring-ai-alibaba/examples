# Spring AI Alibaba MCP Nacos 示例

本项目演示了如何将 Spring AI Alibaba 与模型上下文协议（MCP）和 Nacos 进行集成，实现服务发现和注册。它通过 Nacos 展示了一个完整的 MCP 生态系统，包括服务器端工具注册和客户端工具发现
- 通过nacos实现mcp server服务分布式部署，mcp client负载均衡调用mcp server
- 通过nacos实现存量接口应用，转化为mcp server服务

## 架构概览

本示例包含两个主要组件：

- **服务器（Server）**：提供 AI 工具并向 Nacos 注册自身的 MCP 服务器
- **客户端（Client）**：从 Nacos 发现工具并用于 AI 交互的 MCP 客户端

## 前置条件

- Java 17+
- Maven 3.6+
- nacos版本3+
- 已设置 DASHSCOPE_API_KEY 环境变量

## 目录结构
```angular2html
- client
    - mcp-nacos-distributed-extensions-example      # 基于spring-ai-extensions下的分布式客户端发现示例
    - mcp-nacos-discovery-example                   # 基于spring-ai-alibaba下的分布式客户端发现示例（废弃，待移除）
- server
    - mcp-nacos-register-extensions-example         # 基于spring-ai-extensions下的注册至nacos示例
    - mcp-nacos-register-example                    # 基于spring-ai-alibaba下的注册至nacos示例（废弃，待移除）
    - mcp-nacos-gateway-example                     # 基于spring-ai-alibaba下的注册至nacos并使用nacos作为mcp的网关示例
```

本示例提供了使用 Spring AI Alibaba 和 Nacos 服务发现构建分布式 MCP 应用程序的完整参考。