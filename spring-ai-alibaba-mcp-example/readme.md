# Spring AI Alibaba MCP Examples

> Model Context Protocol (MCP) 综合示例集合
>
> Spring AI Alibaba Version: 1.0.0.4 | Spring AI Version: 1.1.0-M4

本目录包含了完整的 MCP (Model Context Protocol) 示例，涵盖了从基础到高级的各种使用场景和实现模式。

## 📚 模块概览

### 1. [spring-ai-alibaba-mcp-starter-example](./spring-ai-alibaba-mcp-starter-example) - 快速入门示例

**适用场景**: MCP 学习入门、快速原型开发、完整功能演示

**核心特性**:
- 🔄 **多传输协议支持**: WebFlux、STDIO、Streamable HTTP
- 📝 **注解驱动开发**: `@Tool`、`@ToolParam`、`@McpTool` 注解支持
- ⚡ **响应式编程**: WebFlux 异步非阻塞实现
- 🌐 **真实服务集成**: 天气预报、空气质量、股票查询等实际 API
- 🎯 **完整客户端/服务端**: 多种客户端模式和服务端实现

**学习目标**:
- 理解 MCP 协议的基础概念和架构
- 掌握 Spring Boot 自动配置的 MCP 集成
- 学习注解式的工具定义和参数验证
- 了解不同传输协议的特点和使用场景

**快速开始**:
```bash
# 启动注解式服务端
cd server/mcp-annotation-server
mvn spring-boot:run

# 启动对应客户端
cd client/mcp-annotation-client
mvn spring-boot:run
```

---

### 2. [spring-ai-alibaba-mcp-manual-example](./spring-ai-alibaba-mcp-manual-example) - 第三方集成示例

**适用场景**: 集成现有 MCP 服务、文件系统操作、数据库应用

**核心特性**:
- 🔗 **第三方 MCP 服务集成**: 连接 GitHub 官方 MCP 服务器
- 📁 **文件系统操作**: 文件读写、目录管理的 MCP 实现
- 🗄️ **数据库集成**: SQLite 聊天机器人和查询示例
- 🔧 **手动配置**: 无自动配置的原始 MCP 客户端设置
- 🌐 **多协议支持**: STDIO 和 Web 集成

**学习目标**:
- 学习与外部 MCP 服务的集成方法
- 掌握手动 MCP 客户端配置和设置
- 了解文件系统操作的 MCP 服务器实现
- 实践数据库与 MCP 的集成模式

**特色功能**:
- GitHub MCP 集成（需要个人访问令牌）
- 文件服务器实现文件系统操作
- SQLite 数据库聊天机器人
- Node.js MCP 服务器的 STDIO 集成

---

### 3. [spring-ai-alibaba-mcp-build-example](./spring-ai-alibaba-mcp-build-example) - 自定义构建示例

**适用场景**: 深入理解 MCP 协议、自定义 MCP 服务器、最小化依赖

**核心特性**:
- 🏗️ **自定义 MCP 服务器**: 从零构建不依赖 Spring Boot Starters
- 🔧 **手动服务实现**: 直接 MCP 协议实现
- 📦 **最小依赖**: 仅使用核心 MCP 库
- 🎯 **协议底层理解**: 深入理解 MCP 协议机制

**学习目标**:
- 理解 MCP 协议的基础机制
- 学习手动工具注册和回调机制
- 掌握无自动配置的 MCP 服务器构建
- 了解 MCP 服务器生命周期管理

**技术亮点**:
- StockServerApplication 手动工具提供者设置
- WeatherService 财经数据查询实现
- 不同传输模式的客户端测试示例

---

### 4. [spring-ai-alibaba-mcp-nacos-example](./spring-ai-alibaba-mcp-nacos-example) - 服务注册发现示例

**适用场景**: 微服务架构、分布式 MCP 部署、动态服务管理

**核心特性**:
- 🔍 **服务注册发现**: MCP 服务器通过 Nacos 注册和发现
- ⚙️ **配置中心**: 使用 Nacos 进行动态配置管理
- 🌐 **多节点支持**: 高可用 MCP 服务器集群
- 🔄 **热配置更新**: 通过 Nacos 动态更新工具配置

**学习目标**:
- 掌握 MCP 服务器的 Nacos 注册配置
- 实现动态服务发现机制
- 使用配置中心进行 MCP 工具管理
- 构建高可用 MCP 服务器集群
- 实现热配置更新和工具元数据管理

**架构特点**:
- MCP Nacos 注册发现机制
- 网关集成实现 MCP 服务器路由
- 客户端服务发现实现
- 配置中心集成工具元数据管理

---

### 5. [spring-ai-alibaba-mcp-auth-example](./spring-ai-alibaba-mcp-auth-example) - 认证授权示例

**适用场景**: 企业级应用、安全集成、身份验证

**核心特性**:
- 🔐 **请求头传播**: 认证信息从客户端流向 MCP 服务器再到后端服务
- 🏢 **企业集成**: 与需要认证的 RESTful 服务集成
- 🆔 **身份管理**: 客户端身份验证和授权
- 🛡️ **安全模式**: 安全 MCP 服务器通信模式演示

**学习目标**:
- 实现 MCP 服务器环境中的身份认证
- 掌握安全上下文在 MCP 协议中的传播
- 集成 MCP 服务器与企业认证系统
- 管理 MCP 通信中的客户端身份
- 保护 MCP 工具访问和调用

**安全特性**:
- 认证客户端头部配置
- 认证 MCP 服务器实现
- 带认证的 RESTful 服务集成
- 安全集成的 Web 服务器
- 请求头部管理和传播

---

### 6. [spring-ai-alibaba-mcp-config-example](./spring-ai-alibaba-mcp-config-example) - 配置管理示例

**适用场景**: 复杂配置管理、多源配置、动态服务路由

**核心特性**:
- 📊 **多源配置**: 支持文件、数据库、Nacos 配置源
- 🎯 **配置优先级**: 可配置的多源发现顺序
- 🔄 **动态服务管理**: 运行时服务配置管理
- 🌐 **HTTP 配置 API**: MCP 服务配置的 REST API 管理
- 🗄️ **数据库驱动**: MySQL MCP 服务存储

**学习目标**:
- 实现多源 MCP 服务配置
- 通过不同存储后端管理 MCP 服务元数据
- 创建 MCP 服务的配置管理 API
- 理解配置优先级和回退机制
- 数据库驱动的 MCP 服务发现
- 动态服务生命周期管理

**配置特性**:
- 支持多源配置路由器
- MCP 服务器信息的数据库模式
- 基于文件的配置管理
- Nacos 配置管理集成
- 配置管理的 HTTP API 端点
- 可配置优先级的服务发现

---

## 🚀 快速开始指南

### 环境要求

- **Java 17+**
- **Maven 3.6+**
- **DASHSCOPE_API_KEY** 环境变量（用于 AI 模型调用）
- **可选依赖**: Docker（用于数据库等服务）

### 基础使用流程

1. **设置环境变量**:
   ```bash
   export DASHSCOPE_API_KEY=your_api_key_here
   ```

2. **选择示例模块**:
   ```bash
   # 初学者推荐
   cd spring-ai-alibaba-mcp-starter-example

   # 企业集成推荐
   cd spring-ai-alibaba-mcp-nacos-example
   ```

3. **启动服务端**:
   ```bash
   # 例如启动注解式服务端
   cd server/mcp-annotation-server
   mvn spring-boot:run
   ```

4. **启动客户端**:
   ```bash
   # 例如启动对应客户端
   cd client/mcp-annotation-client
   mvn spring-boot:run
   ```

### 构建命令

```bash
# 构建整个 MCP 模块
mvn clean package -DskipTests

# 构建特定示例
mvn clean package -pl spring-ai-alibaba-mcp-starter-example -DskipTests

# 运行测试
mvn test
```

---

## 📖 MCP 核心概念

### 传输协议对比

| 协议类型 | 特点 | 适用场景 | 示例位置 |
|---------|------|----------|----------|
| **WebFlux** | 响应式、SSE、异步非阻塞 | 高并发、实时通信 | starter-example/server/mcp-webflux-server |
| **STDIO** | 标准输入输出、Node.js 兼容 | 命令行工具、进程间通信 | manual-example, starter-example/server/mcp-stdio-server |
| **Streamable HTTP** | 流式 HTTP、双向通信 | 复杂交互、流式数据 | starter-example/server/mcp-streamable-* |

### 集成模式对比

| 模式 | 复杂度 | 灵活性 | 适用场景 | 示例模块 |
|------|--------|--------|----------|----------|
| **注解驱动** | 低 | 中 | 快速开发、标准场景 | starter-example |
| **手动构建** | 高 | 高 | 自定义需求、深度控制 | build-example |
| **第三方集成** | 中 | 高 | 现有服务集成 | manual-example |

### 企业级特性

| 特性 | 解决问题 | 实现模块 | 相关技术 |
|------|----------|----------|----------|
| **服务发现** | MCP 服务器动态发现 | nacos-example | Nacos, 服务注册 |
| **认证授权** | 企业安全集成 | auth-example | JWT, 请求头传播 |
| **配置管理** | 多源配置、动态更新 | config-example | MySQL, Nacos, 文件配置 |

---

## 🛠️ 开发最佳实践

### 1. 项目结构建议

```
your-mcp-project/
├── server/          # MCP 服务器实现
│   ├── annotation/  # 注解式实现
│   ├── manual/      # 手动实现
│   └── streaming/   # 流式实现
├── client/          # MCP 客户端实现
├── common/          # 公共组件
└── config/          # 配置文件
```

### 2. 工具定义模式

**注解式（推荐）**:
```java
@Service
public class WeatherService {

    @Tool(name = "getWeather", description = "获取天气信息")
    public String getWeather(
        @ToolParam(description = "城市名称", required = true) String city,
        @ToolParam(description = "温度单位", required = false) String unit
    ) {
        // 实现逻辑
        return weatherInfo;
    }
}
```

**手动注册**:
```java
@Bean
public ToolCallbackProvider weatherTools(WeatherService service) {
    return MethodToolCallbackProvider.builder()
            .toolObjects(service)
            .build();
}
```

### 3. 配置管理

**服务端配置**:
```yaml
spring:
  ai:
    mcp:
      server:
        name: weather-mcp-server
        protocol: STATELESS
        type: ASYNC
        capabilities:
          tool: true
          resource: true
```

**客户端配置**:
```yaml
spring:
  ai:
    mcp:
      client:
        enabled: true
        type: SYNC
        sse:
          connections:
            weather-server:
              url: http://localhost:8080
```

---

## 🔍 故障排除

### 常见问题

1. **连接超时**
   - 检查服务端是否已启动
   - 确认端口配置正确
   - 查看防火墙设置

2. **工具调用失败**
   - 验证 `DASHSCOPE_API_KEY` 环境变量
   - 检查工具参数定义
   - 查看服务端日志

3. **配置不生效**
   - 确认配置文件路径
   - 检查 profile 激活状态
   - 验证配置格式

### 调试配置

启用详细日志:
```yaml
logging:
  level:
    io:
      modelcontextprotocol:
        client: DEBUG
        server: DEBUG
        spec: DEBUG
```

---

## 📚 学习路径建议

### 初学者路径

1. **starter-example** → 理解基础概念和注解模式
2. **manual-example** → 学习第三方集成和手动配置
3. **build-example** → 深入理解协议机制

### 进阶开发者路径

1. **nacos-example** → 掌握微服务集成
2. **auth-example** → 学习安全集成模式
3. **config-example** → 掌握复杂配置管理

### 企业级应用路径

1. 完整学习所有模块
2. 根据具体需求选择合适模式
3. 参考最佳实践进行定制开发

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request 来完善这些示例！

- 🐛 **Bug 报告**: 请提供详细的复现步骤
- 💡 **功能建议**: 欢迎提出新的示例场景
- 📝 **文档改进**: 帮助完善使用说明和最佳实践

---

## 📞 技术支持

- **GitHub Issues**: [spring-ai-alibaba-examples](https://github.com/springaialibaba/spring-ai-alibaba-examples)
- **官方网站**: [https://java2ai.com](https://java2ai.com)
- **Spring AI Alibaba**: [GitHub Repository](https://github.com/alibaba/spring-ai-alibaba)

---

*本文档持续更新中，最新版本请查看 GitHub 仓库。*
