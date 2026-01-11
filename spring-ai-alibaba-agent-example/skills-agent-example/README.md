# Skills Agent Web 应用

基于 Spring AI Alibaba 的 Web 版 AI Agent，支持浏览器访问和 REST API 调用。

## 特点

- ✅ Web 界面，支持浏览器访问
- ✅ REST API，支持程序调用
- ✅ 会话管理，支持多用户
- ✅ 完美支持中文
- ✅ 集成 Skills 系统
- ✅ 实时对话

## 快速开始

### 1. 设置 API Key（可选）

```bash
set DASHSCOPE_API_KEY=your_api_key_here
```

如果不设置，会使用 application.yml 中的默认配置。

### 2. 启动应用

```bash
mvn spring-boot:run
```

或者先构建再运行：

```bash
mvn clean package -DskipTests
java -jar target/skills-agent-example-0.0.1-SNAPSHOT.jar
```

### 3. 访问应用

打开浏览器访问：

```
http://localhost:8080
```

## API 文档

### 1. 发送消息

**POST** `/api/chat/message`

请求体：
```json
{
  "message": "你好，介绍一下你自己",
  "sessionId": "user123"
}
```

响应：
```json
{
  "success": true,
  "reply": "你好！我是一个 AI 助手...",
  "sessionId": "user123"
}
```

### 2. 重置会话

**POST** `/api/chat/reset`

请求体：
```json
{
  "sessionId": "user123"
}
```

响应：
```json
{
  "success": true,
  "message": "会话已重置",
  "sessionId": "user123"
}
```

### 3. 查看会话列表

**GET** `/api/chat/sessions`

响应：
```json
{
  "success": true,
  "sessionCount": 2,
  "sessions": ["user123", "user456"]
}
```

### 4. 健康检查

**GET** `/api/chat/health`

响应：
```json
{
  "status": "ok",
  "service": "Skills Agent API"
}
```

## 使用示例

### cURL 调用

```bash
# 发送消息
curl -X POST http://localhost:8080/api/chat/message \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"你好\",\"sessionId\":\"test\"}"

# 重置会话
curl -X POST http://localhost:8080/api/chat/reset \
  -H "Content-Type: application/json" \
  -d "{\"sessionId\":\"test\"}"
```

### Python 调用

```python
import requests

# 发送消息
response = requests.post('http://localhost:8080/api/chat/message', json={
    'message': '帮我搜索关于机器学习的论文',
    'sessionId': 'python-client'
})
print(response.json()['reply'])

# 重置会话
requests.post('http://localhost:8080/api/chat/reset', json={
    'sessionId': 'python-client'
})
```

### JavaScript 调用

```javascript
// 发送消息
const response = await fetch('http://localhost:8080/api/chat/message', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        message: '你好',
        sessionId: 'js-client'
    })
});
const data = await response.json();
console.log(data.reply);
```

## 配置

### 修改端口

编辑 `src/main/resources/application.yml`：

```yaml
server:
  port: 9090  # 改成你想要的端口
```

### 修改模型

```yaml
spring:
  ai:
    dashscope:
      chat:
        options:
          model: qwen-max  # 可选: qwen-plus, qwen-turbo, qwen-max
```

### 跨域配置

如果需要从其他域名访问，Controller 已经配置了 `@CrossOrigin(origins = "*")`。

生产环境建议修改为具体域名：

```java
@CrossOrigin(origins = "https://yourdomain.com")
```

## Skills 功能

应用已集成以下 Skills：

1. **arxiv-search** - 搜索 arXiv 论文库
2. **skill-creator** - 创建新的 skill
3. **web-research** - 进行网络研究

Agent 会根据用户问题自动调用相应的 Skill。

## 部署

### Docker 部署

创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-slim
COPY target/skills-agent-example-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

构建并运行：

```bash
docker build -t skills-agent .
docker run -p 8080:8080 -e DASHSCOPE_API_KEY=your_key skills-agent
```

### 云服务器部署

```bash
# 构建
mvn clean package -DskipTests

# 上传 jar 到服务器
scp target/skills-agent-example-0.0.1-SNAPSHOT.jar user@server:/app/

# 在服务器上运行
nohup java -jar /app/skills-agent-example-0.0.1-SNAPSHOT.jar > /app/logs/app.log 2>&1 &
```

## 故障排查

### 端口被占用

修改 `application.yml` 中的端口号，或者使用命令行参数：

```bash
java -jar target/skills-agent-example-0.0.1-SNAPSHOT.jar --server.port=9090
```

### API 调用失败

1. 检查 API Key 是否正确
2. 检查网络连接
3. 查看日志：`tail -f logs/spring.log`

### 会话过多导致内存问题

可以添加会话清理机制，或者使用 Redis 存储会话。

## 性能优化

1. **启用缓存** - 缓存常见问题的回答
2. **异步处理** - 使用 `@Async` 处理长时间请求
3. **连接池** - 配置 HTTP 客户端连接池
4. **限流** - 添加 API 限流保护

## 安全建议

1. 添加认证机制（JWT、OAuth2）
2. 限制请求频率
3. 验证输入内容
4. 使用 HTTPS
5. 不要在前端暴露 API Key
