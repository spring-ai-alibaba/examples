# Spring AI MCP + Nacos 示例项目
本项目配套[mcp-nacos-register-extensions-examlple](https://github.com/spring-ai-alibaba/examples/tree/main/spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-nacos-example/server/mcp-nacos-register-extensions-example)模块一起使用。实现MCP Server服务的分布式调用

本示例是MCP Server多实例节点注册在Nacos后，MCP Client分布式连接多节点，负载均衡触发工具请求，要求版本如下：
1. Nacos版本在3.1.0及以上
2. [spring ai extensions](https://github.com/spring-ai-alibaba/spring-ai-extensions)在1.1.0.0-M4版本及以上

## mcp-nacos-register-extensions-examlple模块前提概述
借助该模块，可将MCP Server服务注册至Nacos中，支持SSE、Streamable、Stateless三种协议类型，可以同时部署多个节点

确保已有MCP Server服务实例已经注册至Nacos中，才能走接下来的分布式连接流程


## 主要依赖
```xml
        <dependency>
            <groupId>com.alibaba.cloud.ai</groupId>
            <artifactId>spring-ai-alibaba-starter-mcp-distributed</artifactId>
            <version>${spring-ai-alibaba-extensions.version}</version>
        </dependency>
```

## 配置application.yml文件
```yml
spring:
    alibaba:
      mcp:
        nacos:
          client:
            enabled: true
            streamable:
              connections:
                server1:
                  service-name: webflux-mcp-server
                  version: 1.0.0
            configs:
              server1:
                namespace: 4ad3108b-4d44-43d0-9634-3c1ac4850c8c
                server-addr: 127.0.0.1:8848
                username: nacos
                password: nacos
```

1. 支持同时配置sse、streamable、stateless三种协议类型的分布式连接
2. 支持配置不同命名空间下的MCP Server服务
3. MCP Server服务的实例节点数动态增加、删除 -> MCP Client的分布式连接会动态感知，增加、删除对应的连接实例，无需重启MCP Client服务