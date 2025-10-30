# Spring AI MCP + Nacos 示例项目

本项目是一个基于 [spring-ai-alibaba-mcp-nacos](https://github.com/spring-projects/spring-ai-alibaba) 的简单示例，展示如何将 MCP Server 注册到 Nacos 中，并通过注解式工具（Tool）提供服务。

本示例是MCP Server多节点注册在Nacos中，建立稳定性连接，要求版本如下：
1. Nacos版本在3.0.1及以上
2. spring ai alibaba的1.0.0.3-SNAPSHOT以上

## 🧩 主要依赖

```xml
<!-- MCP Nacos 注册 -->
<dependency>
  <groupId>com.alibaba.cloud.ai</groupId>
  <artifactId>spring-ai-alibaba-starter-nacos-mcp-server</artifactId>
  <version>${spring-ai-alibaba.version}</version>
</dependency>
        
<!-- MCP Server (WebMVC) -->
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
  <version>${spring-ai-alibaba.version}</version>
</dependency>
```

---

## 🚀 快速开始

### 1. 启动 Nacos 服务

请确保你已经本地或远程启动了 [Nacos 服务器],要求Nacos版本>3.0.1

### 2. 配置 application.yml

```yaml
spring:
  application:
    name: mcp-nacos-registry-example
  ai:
    mcp:
      server:
        name: webmvc-mcp-server
        version: 1.0.0
        type: SYNC
    dashscope:
      api-key: ${AI_DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-max-latest

    alibaba:
      mcp:
        nacos:
          namespace: 4ad3108b-4d44-43d0-9634-3c1ac4850c8c
          server-addr: 127.0.0.1:8848
          username: nacos
          password: nacos
          register:
            enabled: true
            service-group: mcp-server
            service-name: webflux-mcp-server

server:
  port: 8080

```

### 3. 启动应用

```bash
mvn spring-boot:run
```

或使用 IDE 运行 `SpringAiMcpApplication.java`。

---

## 🔧 工具服务示例

```java
@Service
public class TimeService {

  private static final Logger logger = LoggerFactory.getLogger(TimeService.class);

  @Tool(description = "Get the time of a specified city.")
  public String  getCityTimeMethod(@ToolParam(description = "Time zone id, such as Asia/Shanghai") String timeZoneId) {
    logger.info("The current time zone is {}", timeZoneId);
    return String.format("The current time zone is %s and the current time is " + "%s", timeZoneId,
            getTimeByZoneId(timeZoneId));
  }

  private String getTimeByZoneId(String zoneId) {

    // Get the time zone using ZoneId
    ZoneId zid = ZoneId.of(zoneId);

    // Get the current time in this time zone
    ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);

    // Defining a formatter
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    // Format ZonedDateTime as a string
    String formattedDateTime = zonedDateTime.format(formatter);

    return formattedDateTime;
  }
}
```

该服务将注册为 MCP 工具，发布到 Nacos 并被其他 MCP 客户端识别。

---

## 📡 Nacos 注册效果

- MCP Server 信息注册到：

  ```
  配置中心:
  └── nacos-default-mcp 命名空间
      ├── webmvc-mcp-server-mcp-server.json
      └── webmvc-mcp-server-mcp-tools.json
  ```
  ![img_1.png](img_1.png)
- 服务实例注册到：

  ```
  服务发现:
  └── <service-namespace> 命名空间
      └── <service-group> 组
          └── webmvc-mcp-server-mcp-service
  ```
  ![img.png](img.png)

---

## 🚧 后续开发计划（TODO）

本项目目前为最小可运行示例，仅实现了：

- [x] 注册一个基础 MCP Tool（WeatherService）
- [x] 将 MCP Server 注册到 Nacos 服务中心
- [x] 启用配置中心发布 server/tools 元信息（支持热更新）

后续可扩展方向：

- [ ] 添加多个 Tool 示例（如 LLM 接口、数据库查询工具）
- [ ] 支持动态启用/禁用工具（通过 Nacos 修改 toolsMeta）
- [ ] 引入客户端模拟工具调用流程（基于 SSE 协议）
- [ ] 配置中心同步工具变更后的热刷新能力
- [ ] 支持 WebFlux + Reactive MCP Server 的版本

## 📎 参考资料

- [Spring AI Alibaba 相关代码](https://github.com/alibaba/spring-ai-alibaba/tree/main/spring-ai-alibaba-mcp/spring-ai-alibaba-mcp-nacos)

---
