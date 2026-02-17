# sandbox-browser-fullstack

基于 `Spring Boot + Spring AI Alibaba + BrowserSandbox + React(Vite)` 的全栈示例项目。

该模块用于演示：用户通过自然语言下达网页操作任务，后端驱动沙箱浏览器执行，并将执行结果流式返回前端，同时可通过 VNC 预览浏览器实时画面。

## 1. 模块内容

### 1.1 后端模块（Spring Boot）

- 启动入口：`src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/BrowserAgentApplication.java`
- 配置模块：
  - `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/config/SandboxConfiguration.java`
  - `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/config/CorsConfig.java`
  - `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/config/WebSocketConfig.java`
- 控制器：
  - `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/controller/ChatController.java`
  - `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/controller/BrowserController.java`
- 服务层：
  - `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/service/AgentService.java`
  - `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/service/SessionManager.java`
- Agent：`src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/agent/BrowserUseAgent.java`
- 模型对象：
  - `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/model/ChatRequest.java`
  - `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/model/BrowserInfo.java`

### 1.2 前端模块（React + Vite + MUI）

- 入口：
  - `frontend/src/main.tsx`
  - `frontend/src/App.tsx`
- 核心组件：
  - `frontend/src/components/ChatInterface.tsx`
  - `frontend/src/components/MessageList.tsx`
  - `frontend/src/components/MessageInput.tsx`
  - `frontend/src/components/VNCPanel.tsx`
- API 调用：`frontend/src/services/api.ts`
- 主题与多语言：
  - `frontend/src/theme.ts`
  - `frontend/src/i18n.ts`
- 开发代理配置：`frontend/vite.config.ts`

### 1.3 构建与部署文件

- 后端构建：`pom.xml`
- 后端镜像：`Dockerfile`
- 前端镜像：`frontend/Dockerfile`
- Nginx 配置：`frontend/nginx.conf`
- 快速文档：`QUICKSTART.md`

## 2. 实现效果

- 支持自然语言下发浏览器任务（例如访问网站、搜索信息、页面观察）。
- 通过 SSE 实现智能体回复的流式输出，前端逐步显示结果。
- 每个 `sessionId` 独立管理会话，保证会话隔离。
- 支持查看沙箱浏览器 VNC 实时画面，便于观察自动化过程。
- 前端提供中英双语 UI、任务建议词和分栏浏览面板。

## 3. 启动教程

### 3.1 环境要求

- JDK 17+
- Docker（必须运行，用于 Sandbox 容器）
- Node.js 18+
- DashScope API Key（变量名：`AI_DASHSCOPE_API_KEY`）

### 3.2 启动后端

在项目根目录执行：

```bash
export AI_DASHSCOPE_API_KEY=你的Key
mvn spring-boot:run
```

默认地址：`http://localhost:8080`

### 3.3 启动前端

打开新终端执行：

```bash
cd frontend
npm install
npm run dev
```

默认地址：`http://localhost:5173`

### 3.4 使用方式

1. 打开 `http://localhost:5173`
2. 输入任务（如“打开 github.com 并搜索 spring-ai-alibaba”）
3. 观察对话区流式返回
4. 点击“显示浏览器”查看 VNC 实时预览

## 4. 实现原理

### 4.1 初始化阶段

- `SandboxConfiguration` 在 Spring 容器启动时创建并启动 `SandboxService`。
- 同时创建 `ChatModel`（DashScope `qwen-max`）供智能体推理使用。

### 4.2 请求处理阶段

- 前端通过 `EventSource` 调用 `GET /api/chat/stream`。
- `ChatController` 接收请求后创建 `SseEmitter`，异步调用 `AgentService`。

### 4.3 会话与 Agent 阶段

- `SessionManager` 按 `sessionId` 获取或创建 `BrowserUseAgent`。
- `BrowserUseAgent.initialize()` 创建 `BrowserSandbox` 并构建 `ReactAgent`。
- `ReactAgent` 注册 `ToolkitInit.BrowserNavigateTool(browserSandbox)`，执行网页操作。

### 4.4 结果回传阶段

- Agent 输出被切分为文本 chunk，经 SSE 持续推送到前端。
- 前端接收 chunk 后拼接到当前 assistant 消息，实现“边生成边显示”。

### 4.5 可视化阶段

- 前端轮询 `GET /api/browser/info` 获取 `desktopUrl`。
- `VNCPanel` 使用 `iframe` 加载桌面地址，实现浏览器实时可视化。

## 5. 接口清单

- `GET /api/chat/stream`：聊天流式输出（前端主链路）
- `POST /api/chat/stream`：聊天流式输出（备选）
- `GET /api/browser/info`：查询浏览器桌面信息（含 `desktopUrl`）
- `WS /ws/chat`：WebSocket 聊天接口

## 6. 注意事项

- 生产环境请移除 `application.yml` 中 API Key 默认值，改为强制环境变量注入。
- 若前端端口非 `5173`/`3000`，需同步调整后端 CORS 配置。
- 若 Docker 未启动，Sandbox 初始化会失败。
