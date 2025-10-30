# mcp-nacos-discovery-example

本示例是MCP Server多节点注册在Nacos中，建立稳定性连接，要求版本如下：
1. Nacos: 3.0.1+
2. spring ai alibaba: 1.0.0.3-SNAPSHOT+

## 详细步骤

1. MCP Server多节点注册在Nacos中
2. MCP Client建立1-N连接
3. 根据sync、async模式提供clients自动注入

```java
    @Autowired
    private List<LoadbalancedMcpSyncClient> loadbalancedMcpSyncClients;
```
or
```java
    @Autowired
    private List<LoadbalancedMcpAsyncClient> loadbalancedMcpAsyncClients;
```
4. 提供ToolCallbackProvider
```java
@Qualifier("loadbalancedSyncMcpToolCallbacks") ToolCallbackProvider tools
```
or
```java
@Qualifier("loadbalancedMcpAsyncToolCallbacks") ToolCallbackProvider tools

```

## application.yml配置

```yaml
spring:
  application:
    name: mcp-client-webflux
  ai:
    openai:
      api-key: ${AI_DASHSCOPE_API_KEY}
      base-url: https://dashscope.aliyuncs.com/compatible-mode
      chat:
        options:
          model: qwen-max
    alibaba:
      mcp:
        nacos:
          namespace: 4ad3108b-4d44-43d0-9634-3c1ac4850c8c
          server-addr: 127.0.0.1:8848
          username: nacos
          password: nacos
          client:
            enabled: true
            sse:
              connections:
                server1:
                  service-name: webflux-mcp-server
                  version: 1.0.0
    mcp:
      client:
        enabled: true
        name: mcp-client-webflux
        version: 0.0.1
        initialized: true
        request-timeout: 600s
        type: sync
        toolcallback:
          enabled: true
        root-change-notification: true
```
