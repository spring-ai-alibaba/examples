# Voice Agent Dashscope SDK Example

本示例是一个基于 **Spring AI Alibaba** 构建的实时语音对话 Agent。它演示了如何利用 WebSocket 实现全双工语音交互，让你能够与 AI 进行自然的语音对话。

## ✨ 功能特性

- **实时语音对话**: WebSocket 实现低延迟音频流传输
- **打断功能**: AI 播放时可随时点击打断
- **流式处理**: STT → Agent → TTS 全流程流式响应
- **工具调用**: 集成 Function Calling，示例为航班预订助手

## 🚀 快速开始

### 1. 配置环境

**Mac/Linux:**
```bash
export DASHSCOPE_API_KEY=sk-xxx
```

**Windows (PowerShell):**
```powershell
$env:DASHSCOPE_API_KEY="sk-xxx"
```

### 2. 启动应用

```bash
mvn spring-boot:run
```

### 3. 开始体验

1. 打开浏览器：`http://localhost:8081`
2. 点击 **🎤 麦克风** 开始录音
3. 说话后再次点击结束录音
4. 等待 AI 语音回复（播放时可点击打断）

## 🏗️ 架构设计

### 核心流程

```
┌─────────┐    PCM Audio     ┌───────────────────┐
│         │ ───────────────► │                   │
│ Browser │                  │ VoiceWebSocket    │
│         │ ◄─────────────── │ Handler           │
└─────────┘   PCM + Events   └─────────┬─────────┘
                                       │
                                       ▼
                        ┌──────────────────────────┐
                        │   VoiceAgentPipeline     │
                        │                          │
                        │  ┌────────────────────┐  │
                        │  │ DashScope STT      │  │
                        │  │ (语音 → 文本)       │  │
                        │  └─────────┬──────────┘  │
                        │            │             │
                        │            ▼             │
                        │  ┌────────────────────┐  │
                        │  │ ReactAgent + Tools │  │
                        │  │ (对话 + 工具调用)   │  │
                        │  └─────────┬──────────┘  │
                        │            │             │
                        │            ▼             │
                        │  ┌────────────────────┐  │
                        │  │ DashScope TTS      │  │
                        │  │ (文本 → 语音)       │  │
                        │  └────────────────────┘  │
                        └──────────────────────────┘
```

### 项目结构

```
voice-agent-example/
├── controller/
│   ├── PageController.java          # 前端页面
│   └── VoiceWebSocketHandler.java   # WebSocket 处理
├── service/
│   ├── VoiceAgentPipeline.java      # 核心编排 (STT→Agent→TTS)
│   └── VoiceAgentService.java       # ReactAgent 封装
├── component/
│   ├── stt/DashScopeRealtimeSTT.java  # 语音识别
│   └── tts/DashScopeRealtimeTTS.java  # 语音合成
├── tools/
│   ├── BookingTool.java             # 查询预订
│   └── FlightChangeTool.java        # 改签航班
└── event/                           # 事件定义
```

## 🛠️ 技术栈

| 技术 | 用途 |
|-----|------|
| Spring Boot 3.3.x | 核心框架 |
| Spring AI Alibaba | ReactAgent + DashScope 集成 |
| Project Reactor | 响应式流处理 |
| WebSocket | 实时双向通信 |
| DashScope SDK | 实时 ASR/TTS |

## ⚠️ 注意事项

- 确保 8081 端口未被占用
- 必须使用 HTTPS 或 localhost（浏览器麦克风权限限制）
