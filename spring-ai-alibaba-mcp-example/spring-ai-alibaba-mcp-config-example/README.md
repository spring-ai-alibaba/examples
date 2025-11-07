# Spring AI Alibaba MCP Config Example

## 项目介绍

Spring AI Alibaba MCP Config Example 是一个演示读取 MCP 服务配置的示例项目。

引入了多源支持架构，允许同时使用多种服务发现方式，提供更灵活和可靠的服务发现机制。

## 版本要求

1. Nacos: 3.0.1+
2. Spring AI Alibaba: 1.0.0.4-SNAPSHOT+

## 功能特性

- 从配置文件（application.yml）中读取
- 从数据库（如 MySQL）中读取
- 从 Nacos 配置中心读取

## 快速开始

### 服务发现顺序

配置项`discovery-order`决定服务发现的顺序。举例：

```yml
    alibaba:
      mcp:
        router:
          enabled: true
          discovery-order:
            - file
            - database
            - nacos
```

### 从配置文件中读取

在 application.yml 中配置服务列表，举例：

```yml
spring.ai.alibaba.mcp.router:
          enabled: true  # 启用MCP路由
          services:  # 服务列表
            - name: weather-service  # 服务名称
              description: "天气查询服务"
              protocol: "http"
              version: "1.0.0"
              endpoint: "http://localhost:8080/weather"  # 服务端点
              enabled: true  # 是否启用该服务
              tags:  # 服务标签
                - "weather"
                - "api"
```
发送 HTTP GET 请求，从 application.yml 配置文件中读取所有/特定 MCP 服务配置信息。

> 注：示例 HTTP 请求，参见 [configRequests.http](src/main/resources/configRequests.http)。

### 从数据库中读取

在 application.yml 中添加数据库（如 MySQL）配置，举例：

```yml
spring.ai.alibaba.mcp.router:
   enabled: true  # 启用MCP路由
   database:
      enabled: true
      url: jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=UTC
      username: root
      password: root
      driverClassName: com.mysql.cj.jdbc.Driver
      tableName: mcp_server_info
```

创建 MySQL 数据库表并添加示例记录，举例：

```sql
-- 创建mcp_server_info表
CREATE TABLE IF NOT EXISTS mcp_server_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE COMMENT '服务名称',
    description TEXT COMMENT '服务描述',
    protocol VARCHAR(50) COMMENT '服务协议',
    version VARCHAR(50) COMMENT '服务版本',
    endpoint VARCHAR(255) COMMENT '服务访问端点',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    tags VARCHAR(255) COMMENT '标签，逗号分隔',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 添加示例记录
INSERT INTO mcp_server_info (name, description, protocol, version, endpoint, enabled, tags)
VALUES
('dashscope-chat', '阿里云通义千问大模型服务', 'http', 'v1', 'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/num-tokens', TRUE, 'chat,llm,aliyun'),
('openai-embedding', 'OpenAI Embedding服务', 'http', 'v1', 'https://api.openai.com/v1/embeddings', TRUE, 'embedding,openai'),
('custom-service-a', '自定义服务A', 'grpc', 'v1.0', 'grpc://localhost:9090', TRUE, 'custom,test');
```

发送 HTTP GET 请求，从 MySQL 数据库读取 MCP 服务配置信息。

### 从 Nacos 配置中心读取

在 application.yml 中添加 Nacos 配置，举例：

```yml
spring.ai.alibaba.mcp.nacos:
  server-addr: localhost:8848
  namespace: public
```

1. 启动 Nacos 服务

2. 注册 MCP 服务

3. 发送 HTTP GET 请求
