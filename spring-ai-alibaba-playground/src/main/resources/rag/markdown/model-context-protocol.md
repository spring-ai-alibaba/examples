---
title: 模型上下文协议（Model Context Protocol）
keywords: [Spring AI, MCP, 模型上下文协议, 智能体应用]
description: "模型上下文协议（Model Context Protocol）介绍"
---

## MCP 简介

[模型上下文协议（即 Model Context Protocol，MCP）](https://modelcontextprotocol.io)是一个开放协议，它规范了应用程序如何向大型语言模型（LLM）提供上下文。MCP 提供了一种统一的方式将 AI 模型连接到不同的数据源和工具，它定义了统一的集成方式。在开发智能体（Agent）的过程中，我们经常需要将将智能体与数据和工具集成，MCP 以标准的方式规范了智能体与数据及工具的集成方式，可以帮助您在 LLM 之上构建智能体（Agent）和复杂的工作流。目前已经有大量的服务接入并提供了 MCP server 实现，当前这个生态正在以非常快的速度不断的丰富中，具体可参见：[MCP Servers](https://github.com/modelcontextprotocol/servers)。

## Spring AI MCP

Spring AI MCP 为模型上下文协议提供 Java 和 Spring 框架集成。它使 Spring AI 应用程序能够通过标准化的接口与不同的数据源和工具进行交互，支持同步和异步通信模式。

![spring-ai-mcp-architecture](/img/blog/mcp-filesystem/spring-ai-mcp-architecture.png)

Spring AI MCP 采用模块化架构，包括以下组件：

- Spring AI 应用程序：使用 Spring AI 框架构建想要通过 MCP 访问数据的生成式 AI 应用程序
- Spring MCP 客户端：MCP 协议的 Spring AI 实现，与服务器保持 1:1 连接
- MCP 服务器：轻量级程序，每个程序都通过标准化的模型上下文协议公开特定的功能
- 本地数据源：MCP 服务器可以安全访问的计算机文件、数据库和服务
- 远程服务：MCP 服务器可以通过互联网（例如，通过 API）连接到的外部系统

## 如何使用

要启用此功能，请将以下依赖项添加到您项目的 Maven`pom.xml`文件中：

```xml
<dependency>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-ai-mcp</artifactId>
    <version>1.0.0</version>
</dependency>
```

或者添加到您的 Gradle`build.gradle`文件中：

```groovy
dependencies {
    implementation 'org.springframework.experimental:spring-ai-mcp:1.0.0'
}
```

Spring AI MCP 目前并没有在 Maven Central Repository 中提供。需要将 `Spring milestone`仓库添加到`pom.xml`中，才可以访问 Spring AI MCP 工件：

```xml
<repositories>
  <repository>
    <id>spring-milestones</id>
    <name>Spring Milestones</name>
    <url>https://repo.spring.io/libs-milestone-local</url>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>
</repositories>
```

要使用 MCP，首先需要创建`McpClient`，它提供了与 MCP server 的同步和异步通信能力。现在我们创建一个 McpClient 来注册 MCP Brave 服务和 ChatClient，从而让 LLM 调用它们：

```java
var stdioParams = ServerParameters.builder("npx")
        .args("-y", "@modelcontextprotocol/server-brave-search")
        .addEnvVar("BRAVE_API_KEY", System.getenv("BRAVE_API_KEY"))
        .build();

var mcpClient = McpClient.using(new StdioClientTransport(stdioParams, McpJsonMapper.getDefault())).sync();

var init = mcpClient.initialize();

var chatClient = chatClientBuilder
        .defaultFunctions(mcpClient.listTools(null)
                .tools()
                .stream()
                .map(tool -> new McpFunctionCallback(mcpClient, tool))
                .toArray(McpFunctionCallback[]::new))
        .build();

String response = chatClient
        .prompt("Does Spring AI supports the Model Context Protocol? Please provide some references.")
        .call().content();
```

在上述代码中，首先通过`npx`命令启动一个独立的进程，运行`@modelcontextprotocol/server-brave-search`服务，并指定 Brave API 密钥。然后创建一个基于 stdio 的传输层，与 MCP server 进行通信。最后初始化与 MCP 服务器的连接。

要使用 McpClient，需要将`McpClient`注入到 Spring AI 的`ChatClient`中，从而让 LLM 调用 MCP server。在 Spring AI 中，可以通过 Function Callbacks 的方式将 MCP 工具转换为 Spring AI 的 Function，从而让 LLM 调用。

最后，通过`ChatClient`与 LLM 进行交互，并使用`McpClient`与 MCP server 进行通信，获取最终的返回结果。
