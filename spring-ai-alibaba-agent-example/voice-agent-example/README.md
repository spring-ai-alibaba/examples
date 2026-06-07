# Voice Agent Example

基于 Spring AI Alibaba 的语音对话 Agent 示例，实现 Push-to-Talk 模式的 Audio → STT → Agent → TTS → Audio 全链路。

## 功能

- **Push-to-Talk 语音交互**：WebSocket 双向通信，推送录音、流式播放回复
- **打断控制**：AI 播放中随时点击打断
- **Reactive Pipeline**：STT → Agent → TTS 基于 Reactor 的流式编排
- **工具调用**：示例为航班预订助手（查询/改签）

## 快速开始

### 1. 配置 API Key

```bash
# Linux/Mac
export AI_DASHSCOPE_API_KEY=sk-xxx

# Windows PowerShell
$env:AI_DASHSCOPE_API_KEY="sk-xxx"
```

### 2. 启动

```bash
mvn spring-boot:run
```

### 3. 使用

浏览器打开 `http://localhost:8081`，点击 🎤 录音 → 再次点击发送 → AI 语音回复。

## 架构

```
Browser ──PCM──► VoiceWebSocketHandler ──► VoiceAgentPipeline
   ◄──PCM+JSON──┘                          │
                                    ┌──────┴────────────────────────┐
                                    │ STT (paraformer-realtime-v2)  │
                                    │      ↓                        │
                                    │ ReactAgent + Tools            │
                                    │      ↓                        │
                                    │ TTS (cosyvoice-v1)            │
                                    └───────────────────────────────┘
```

## 项目结构

```
voice-agent-example/src/main/java/.../voice/
├── config/
│   ├── VoiceAgentConfiguration.java  # ReactAgent + Tools 组装
│   └── WebSocketConfig.java          # WebSocket 端点注册
├── controller/
│   ├── PageController.java           # 前端路由
│   └── VoiceWebSocketHandler.java    # WebSocket 消息处理
├── service/
│   ├── VoiceAgentPipeline.java       # 核心管道 STT→Agent→TTS
│   └── VoiceAgentService.java        # ReactAgent 调用封装
├── event/                            # Pipeline 事件（sealed interface）
└── tools/                            # 航班查询/改签工具
```

## 技术栈

| 组件 | 实现                             |
|------|--------------------------------|
| 框架 | Spring Boot 3.x                |
| AI 集成 | Spring AI Alibaba (ReactAgent) |
| 流式编排 | Project Reactor                |
| 实时通信 | WebSocket (PCM 16kHz)          |

## 注意事项

- 端口 `8081`，可在 `application.yml` 修改
- 浏览器麦克风需 HTTPS 或 localhost
