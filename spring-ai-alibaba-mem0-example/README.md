# Spring AI Alibaba Chat Memory Example

本示例演示如何使用 Mem0 Memory 聊天记忆功能。

> 包含此依赖的 Spring AI Alibaba 版本尚未发布，如果需要体验此 Demo，需要本地 install。如果不体验可正常跳过，不影响其他 example 启动。

## Spring AI Alibaba Mem0 Memory 实现

1. spring ai 提供了基于内存的 InMemory 实现； 
2. Spring AI Alibaba 提供了基于 Redis 和 JDBC 的 ChatMemory 实现。
    
    - MySQL
    - PostgreSQL
    - Oracle
    - SQLite
    - SqlServer


## Example 演示

下面以 Redis 和 SQLite JDBC 为例。

> 使用 [Docker Compose 启动 Mem0 服务](../docker-compose/mem0/README.md)。
> 配置IDEA环境变量, AI_DASHSCOPE_API_KEY=sk-xxx;AI_DEEPSEEK_API_KEY=sk-xxx

在体验示例之前，确保Docker已经启动容器


在一轮问答中，您应该得看到这样的回复：
参考[chat-memory.http](../spring-ai-alibaba-mem0-example/mem0-memory.http)

```shell
### 聊天记忆
GET http://localhost:8080/advisor/memory/mem0/call?message=你好，我是万能的喵，我爱玩三角洲行动&user_id=miao
### 获取记忆
GET http://localhost:8080/advisor/memory/mem0/messages?query=我的爱好是什么&user_id=miao

### 测试
GET http://localhost:8080/advisor/memory/mem0/test
```
