# Spring AI Alibaba MCP Build Example

## 项目介绍

Spring AI Alibaba MCP Build Example 演示如何使用 Java 开发一个 MCP (Model Context Protocol) Server。本示例包含一个股票查询服务器，展示了如何使用 Spring AI Alibaba 的 MCP 构建功能来创建自定义的工具服务。

## 项目结构

```
spring-ai-alibaba-mcp-build-example/
├── starter-stock-server/           # 股票查询 MCP 服务器
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/alibaba/cloud/ai/example/stock/
│   │   │   │   ├── StockServerApplication.java  # 主应用类
│   │   │   │   └── service/
│   │   │   │       └── StockService.java        # 股票查询服务
│   │   │   └── resources/
│   │   │       └── application.yml              # 应用配置
│   │   └── test/
│   │       └── java/com/alibaba/spring/ai/example/stock/client/
│   │           └── ClientStdio.java            # STDIO 测试客户端
│   └── pom.xml
└── pom.xml
```

## 功能特性

### 股票查询工具 (`StockService`)
- **工具名称**: `getStockInfo`
- **功能**: 获取指定股票代码的实时信息
- **数据源**: 东方财富网 API
- **返回数据**:
  - 当前价格 (Current Price)
  - 最高价 (High Price)
  - 最低价 (Low Price)
  - 开盘价 (Open Price)
  - 成交量 (Volume)
  - 成交额 (Amount)

## 技术栈

- **Java 17+** - 运行环境
- **Spring Boot 3.4.0** - 应用框架
- **Spring AI 1.0.0** - AI 框架
- **Spring AI Alibaba 1.0.0.3** - 阿里云集成
- **Maven** - 构建工具
- **Model Context Protocol (MCP)** - 工具调用协议

## 快速开始

### 1. 环境准备

确保已安装以下环境：
- Java 17+
- Maven 3.6+

### 2. 构建项目

```bash
# 进入项目目录
cd spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-build-example

# 构建整个模块
mvn clean package -DskipTests

# 构建特定的股票服务器
mvn clean package -pl starter-stock-server -DskipTests
```

### 3. 运行股票查询服务器

```bash
# 进入股票服务器目录
cd starter-stock-server

# 运行服务器（STDIO 模式）
mvn spring-boot:run
```

### 4. 测试股票查询功能

使用提供的测试客户端进行测试：

```bash
# 运行测试客户端
cd src/test/java/com/alibaba/spring/ai/example/stock/client/
java ClientStdio
```

或者在运行服务器后，可以输入以下 JSON-RPC 请求来测试：

```json
{
  "jsonrpc": "2.0",
  "method": "tools/call",
  "params": {
    "name": "getStockInfo",
    "arguments": {
      "stockCode": "000001"
    }
  },
  "id": 1
}
```

## API 使用说明

### 股票代码格式

股票代码必须是 6 位数字：
- 沪市股票：以 6 开头（如：600000）
- 深市股票：以 0 或 3 开头（如：000001, 300001）

### 返回数据格式

```json
{
  "code": "000001",
  "name": "平安银行",
  "currentPrice": 12.50,
  "highPrice": 12.80,
  "lowPrice": 12.30,
  "openPrice": 12.60,
  "volume": 1500.0,
  "amount": 1.875
}
```

**字段说明**：
- `code`: 股票代码
- `name`: 股票名称
- `currentPrice`: 当前价格（元）
- `highPrice`: 最高价（元）
- `lowPrice`: 最低价（元）
- `openPrice`: 开盘价（元）
- `volume`: 成交量（万手）
- `amount`: 成交额（亿元）

## 核心实现

### 1. 工具定义

使用 `@Tool` 注解定义 MCP 工具：

```java
@Tool(name = "getStockInfo", description = "Get real-time stock information for the specified stock code")
public StockInfo getStockInfo(String stockCode) {
    // 实现逻辑
}
```

### 2. 工具注册

通过 `MethodToolCallbackProvider` 注册工具：

```java
@Bean
public ToolCallbackProvider stockTools(StockService stockService) {
    return MethodToolCallbackProvider.builder().toolObjects(stockService).build();
}
```

### 3. 服务器配置

配置 MCP 服务器的基本信息：

```yaml
spring:
  ai:
    mcp:
      server:
        name: my-stock-server
        version: 0.0.1
```

## 开发指南

### 添加新的工具

1. 创建新的服务类：

```java
@Service
public class YourService {

    @Tool(name = "yourTool", description = "Your tool description")
    public YourResult yourMethod(@McpToolParam String param) {
        // 实现逻辑
    }
}
```

2. 在 `StockServerApplication` 中注册工具：

```java
@Bean
public ToolCallbackProvider yourTools(YourService yourService) {
    return MethodToolCallbackProvider.builder().toolObjects(yourService).build();
}
```

### 错误处理

服务实现了完善的错误处理：
- 参数验证：股票代码格式检查
- 网络异常：API 调用失败处理
- 数据解析：JSON 解析异常处理
- 业务异常：未找到股票信息处理

### 日志配置

启用详细的调试日志：

```yaml
logging:
  level:
    com.alibaba.cloud.ai.example.stock: DEBUG
    io.modelcontextprotocol: DEBUG
```

## 注意事项

1. **STDIO 模式配置**：必须在配置中禁用 web 应用和横幅显示
2. **API 限制**：东方财富网 API 有调用频率限制，请合理使用
3. **网络依赖**：需要能够访问外部 API (push2.eastmoney.com)
4. **股票代码格式**：必须使用正确的 6 位数字股票代码

## 故障排除

### 常见问题

1. **服务器启动失败**
   - 检查 Java 版本是否为 17+
   - 确认 Maven 依赖是否正确安装

2. **股票查询失败**
   - 检查网络连接
   - 验证股票代码格式
   - 查看日志输出获取详细错误信息

3. **STDIO 通信问题**
   - 确保配置中设置了 `web-application-type: none`
   - 检查 `banner-mode: off` 配置

## 扩展示例

基于此示例，您可以：
- 添加更多的金融数据工具（如期货、汇率）
- 集成其他数据源（如雅虎财经、新浪财经）
- 实现数据缓存机制
- 添加批量查询功能
- 支持更多数据格式（CSV、XML）

## 相关文档

- [Spring AI Alibaba 官方文档](https://github.com/alibaba/spring-ai-alibaba)
- [Model Context Protocol 规范](https://modelcontextprotocol.io/)
- [Spring AI 工具集成指南](https://docs.spring.io/spring-ai/reference/)