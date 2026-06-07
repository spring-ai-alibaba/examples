# sandbox-browser-fullstack

基于 `Spring Boot + Spring AI Alibaba + BrowserSandbox + React(Vite)` 的全栈示例。

这个模块演示的是：用户用自然语言下达网页任务，后端驱动沙箱浏览器执行操作，前端实时看到文本回复和浏览器画面。

## 1. 你能复现出的效果

启动后，你可以在页面里直接输入：
- `打开 github.com 并搜索 spring-ai-alibaba`
- `访问 baidu.com 并总结首页模块`

然后会看到：
- AI 回复按流式逐步返回（不是一次性返回）
- 同时可打开 VNC 面板，实时看见沙箱浏览器在操作页面
- 每个 `sessionId` 独立，前端刷新前会话上下文可持续

## 2. 架构（先有全局图）

```text
React + Vite (5173)
   |  HTTP(SSE)/WS
   v
Spring Boot (8080)
   |  AgentService + SessionManager
   v
BrowserUseAgent (ReactAgent + BrowserNavigateTool)
   |
BrowserSandbox (Docker)
   |
VNC desktopUrl -> 前端 iframe 实时预览
```

主链路（最重要）：
1. 前端发起 `GET /api/chat/stream`（SSE）
2. `ChatController` 调用 `AgentService`
3. `SessionManager` 按 `sessionId` 取/建 `BrowserUseAgent`
4. Agent 调用 `BrowserNavigateTool` 操作 BrowserSandbox
5. 后端把 chunk 持续推给前端，前端边收边渲染
6. 前端轮询 `GET /api/browser/info` 获取 `desktopUrl`，在 `VNCPanel` 内展示

## 3. 技术栈（按模块）

后端：
- Java 17
- Spring Boot (`web`, `websocket`)
- Spring AI Alibaba
  - `spring-ai-alibaba-agent-framework`
  - `spring-ai-alibaba-sandbox-tool`
  - `spring-ai-alibaba-starter-dashscope`
- 模型：DashScope `qwen-max`
- 浏览器运行时：`BrowserSandbox`（依赖 Docker）

前端：
- React 18 + TypeScript
- Vite 5
- MUI 7
- Axios
- UUID（生成 `sessionId`）

## 4. 启动教程（新人可直接照抄）

### 4.1 环境准备

- JDK 17+
- Node.js 18+
- Docker（必须已启动）
- DashScope API Key（变量名：`AI_DASHSCOPE_API_KEY`）

可先自检：

```bash
java -version
node -v
npm -v
docker ps
```

### 4.2 启动后端

在当前模块根目录执行：

```bash
export AI_DASHSCOPE_API_KEY=你的Key
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

### 4.3 启动前端

新开一个终端：

```bash
cd frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`

### 4.4 开始体验

1. 打开 `http://localhost:5173`
2. 输入任务并发送
3. 观察左侧聊天区流式输出
4. 点击 “Show Browser / 显示浏览器”，观察右侧 VNC 实时画面

## 5. 重点知识点（理解这个模块的关键）

1. `SSE 流式输出`
- 后端使用 `SseEmitter`
- 前端使用 `EventSource`
- 你会看到消息按 chunk 增量显示

2. `会话隔离`
- 前端启动时生成 `sessionId`
- 后端 `SessionManager` 按 `sessionId` 管理独立 Agent/浏览器上下文

3. `Agent + Tool 调用`
- `BrowserUseAgent` 内部是 `ReactAgent`
- 注册 `ToolkitInit.BrowserNavigateTool(browserSandbox)` 后，LLM 才能“真的操作网页”

4. `BrowserSandbox 与 Docker`
- 浏览器不是本地直接开，而是在沙箱容器里跑
- Docker 没启动时，Sandbox 初始化会失败

5. `VNC 可视化`
- 后端返回 `desktopUrl`
- 前端 `iframe` 加载该地址，实现浏览器操作可视化

6. `前后端联调关键点`
- 前端 Vite 通过 `/api` 代理到 `http://localhost:8080`
- 后端 CORS 默认允许 `http://localhost:5173` 和 `http://localhost:3000`

## 6. 代码阅读顺序（推荐新手）

先后端主链路，再看前端展示：

1. `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/controller/ChatController.java`
2. `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/service/AgentService.java`
3. `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/service/SessionManager.java`
4. `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/agent/BrowserUseAgent.java`
5. `src/main/java/com/alibaba/cloud/ai/examples/sandbox/browser/config/SandboxConfiguration.java`
6. `frontend/src/services/api.ts`
7. `frontend/src/components/ChatInterface.tsx`
8. `frontend/src/components/VNCPanel.tsx`

## 7. 常见问题（最短排障路径）

1. 启动时报 Sandbox 相关错误
- 先确认 `docker ps` 正常

2. 前端无流式返回
- 看后端是否已启动在 `8080`
- 看前端是否走 `/api/chat/stream`

3. 看不到浏览器画面
- 先发起一次聊天，等待 sandbox 初始化
- 再查看 `/api/browser/info?sessionId=xxx` 是否返回 `desktopUrl`

4. API Key 问题
- 优先用环境变量 `AI_DASHSCOPE_API_KEY`
- 建议不要在 `application.yml` 中保留真实 Key

---

如果你只想快速验证：按“4. 启动教程”执行，发送一条 `打开 github.com 并搜索 spring-ai-alibaba`，并打开 VNC 面板即可完成复现。
