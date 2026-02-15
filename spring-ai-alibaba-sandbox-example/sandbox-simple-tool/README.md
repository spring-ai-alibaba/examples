# Spring AI Alibaba Sandbox Simple Tool Example

这是一个完整的示例，展示如何在 Spring Boot 应用中集成 Spring AI Alibaba 的 Sandbox 功能。

## 功能特性

- **Calculator Tool** - 数学运算工具（加减乘除）
- **Weather Tool** - 天气查询工具（模拟数据）
- **Python Runner** - 在沙箱中执行 Python 代码
- **Shell Runner** - 在沙箱中执行 Shell 命令
- **Browser Navigator** - 浏览器自动化工具

## 快速开始

### 前置要求

- JDK 17+
- Docker（用于运行 Sandbox 容器）
- AI_DASHSCOPE_API_KEY（从[阿里云百炼](https://bailian.console.aliyun.com/)获取）

### 运行应用

1. 启动 Docker（必须）

macOS（Docker Desktop）：
```bash
open -a Docker
docker ps
```

Linux：
```bash
docker ps
```

`docker ps` 能正常输出（即使为空列表）代表 Docker 可用。

2. 设置 API Key（必须）
```bash
export AI_DASHSCOPE_API_KEY=your-api-key
```

3. 启动应用
```bash
./mvnw -q -DskipTests spring-boot:run
```

4. 应用启动后，控制台会显示可用的 API 端点

## 详细教程：快速启动并验证 Sandbox

面向不熟悉 Sandbox 的同学，按下面步骤操作即可完成启动与验证。

### 1) 环境准备（必须）

- **JDK 17+**
- **Docker Desktop / Docker Engine**
- **网络可访问镜像仓库**（首次运行会拉取 sandbox 镜像）
- **DashScope API Key**（Agent 需要先调用大模型，才会触发工具/沙箱）

```bash
export AI_DASHSCOPE_API_KEY=your-real-api-key
```

### 2) Docker Socket 路径确认（macOS 常见问题）

本示例默认使用 `unix:///var/run/docker.sock`。在 macOS 上，这个路径有时不存在（Docker Desktop 的真实 socket 通常在 `${HOME}/.docker/run/docker.sock`）。

用命令确认本机实际 socket：
```bash
ls -l /var/run/docker.sock || true
ls -l "${HOME}/.docker/run/docker.sock" || true
```

如果 `/var/run/docker.sock` 不存在但 `${HOME}/.docker/run/docker.sock` 存在，修改 `src/main/resources/application.yml`：
```yaml
sandbox:
  docker:
    host: "unix://${HOME}/.docker/run/docker.sock"
```

### 3) 启动应用

在 `saa-example/sandbox-simple-tool/` 目录执行：
```bash
./mvnw -q -DskipTests spring-boot:run
```

### 4) 触发一次 Sandbox 工具（Python Runner）

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Use Python to calculate 1+1"}'
```

### 5) 如何判断 Sandbox 启动成功（关键）

成功标志（满足任意一个即可确认 Sandbox 生效）：

- **日志出现镜像拉取/容器创建/健康检查/工具执行成功**，例如：
  - `Created Container: ...`
  - `Container started successfully: ...`
  - `Sandbox service is healthy: http://localhost:XXXXX/fastapi`
  - `Successful execution of tool: run_ipython_cell`
- **Docker 看到新容器**：
```bash
docker ps
```
出现名为 `sandbox_container_*` 的容器即可确认。

### 常见问题排查

1) **401 InvalidApiKey**

日志示例：
```
401 - {"code":"InvalidApiKey","message":"Invalid API-key provided."}
```
处理：确认 `AI_DASHSCOPE_API_KEY` 正确，并重新启动应用。

2) **Docker 连接失败（macOS 常见）**

如果看到类似：
```
Failed to connect to Docker: java.net.SocketException: No such file or directory
```
说明当前配置的 docker socket 路径不存在。可切换为 Docker Desktop 的真实 socket：

`src/main/resources/application.yml`：
```yaml
sandbox:
  docker:
    host: "unix://${HOME}/.docker/run/docker.sock"
```

3) **首次拉取镜像较慢**

日志会出现：
`Pulling image: ...`  
等待拉取完成即可，后续启动会快很多。

4) **启动时出现一次 Docker WARN，但后续能创建容器**

如果看到类似：
`Connecting to Docker at tcp://unix:///var/run/docker.sock:2375 ...` + `Falling back to default Docker configuration`，
通常表示底层库先尝试了一个不兼容的地址，然后回退到默认配置。只要后续能看到容器创建/工具执行成功日志，就可视为正常。

## API 接口

### 健康检查
```bash
curl http://localhost:8080/api/chat/health
```

### 发送消息
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "计算 123 + 456"}'
```

### 执行 Python 代码
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "用 Python 计算斐波那契数列第 10 项"}'
```

### 浏览器导航
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "用浏览器访问 https://www.baidu.com 并返回页面标题"}'
```

### 获取工具列表
```bash
curl http://localhost:8080/api/chat/tools
```

### 重置会话
```bash
curl -X DELETE http://localhost:8080/api/chat/reset
```

## 项目结构

```
saa-example/sandbox-simple-tool/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/alibaba/cloud/ai/examples/sandbox/simple/
    │   │       ├── SimpleSandboxApplication.java
    │   │       ├── config/
    │   │       │   ├── AgentConfiguration.java
    │   │       │   └── SandboxConfiguration.java
    │   │       ├── controller/
    │   │       │   └── ChatController.java
    │   │       ├── model/
    │   │       │   ├── ChatRequest.java
    │   │       │   ├── ChatResponse.java
    │   │       │   └── ToolInfo.java
    │   │       ├── service/
    │   │       │   └── AgentService.java
    │   │       └── tools/
    │   │           ├── CalculatorTool.java
    │   │           └── WeatherTool.java
    │   └── resources/
    │       └── application.yml
    └── test/
```

## 配置说明

### application.yml

```yaml
spring:
  ai:
    dashscope:
      api-key: ${AI_DASHSCOPE_API_KEY:your-api-key-here}
      chat:
        options:
          model: qwen-max
          temperature: 0.7

sandbox:
  pool:
    size: 0  # 沙箱池大小，0 表示按需创建
```

## 技术栈

- Spring Boot 3.2.x
- Spring AI Alibaba 1.1.2.0+
- Spring AI Alibaba Sandbox Tool
- DashScope Chat Model (qwen-max)
- Java 17+

## 注意事项

1. Docker 必须已安装并运行，Sandbox 依赖 Docker
2. API Key 可以通过环境变量 `AI_DASHSCOPE_API_KEY` 或配置文件设置
3. 沙箱容器的首次启动可能需要一些时间来拉取镜像
