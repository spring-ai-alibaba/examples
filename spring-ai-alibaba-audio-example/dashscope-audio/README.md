# Spring AI Alibaba Audio Example

演示使用阿里通义大模型进行音频处理。包含语音合成（TTS）、语音转录（ASR）和语音翻译。

## 官方文档

- [阿里云百炼 - API参考](https://bailian.console.aliyun.com/cn-beijing/?tab=api#/api)
- 语音合成、语音识别、语音翻译相关接口文档

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- DashScope API Key（[获取地址](https://bailian.console.aliyun.com/)）

### 配置

在运行应用前，需要设置环境变量：

```bash
export AI_DASHSCOPE_API_KEY=your_api_key_here
```

或者在 `application.yml` 中配置：

```yaml
spring:
  ai:
    dashscope:
      api-key: your_api_key_here
```

### 运行

```bash
mvn spring-boot:run
```

服务默认运行在 `http://localhost:10009`

## HTTP 请求示例

完整的 HTTP 请求示例文件：[dashscope-audio.http](./dashscope-audio.http)

该文件包含所有接口的测试请求，可在 IntelliJ IDEA 或 VS Code 中直接运行。

## 功能接口列表

### 语音合成 (TTS)

基础路径：`/ai/video/tts`

| 接口 | 方法 | 描述 | 模型 |
|------|------|------|------|
| `/sambert/stream` | GET | Sambert 流式语音合成 | sambert-zhichu-v1 |
| `/sambert/complete` | GET | Sambert 完整语音合成 | sambert-zhichu-v1 |
| `/qwen3-tts/call` | GET | Qwen3-TTS 同步调用 | qwen3-tts-flash |
| `/qwen3-tts/stream` | GET | Qwen3-TTS 流式调用 | qwen3-tts-flash |

### 语音转录与翻译 (Transcription)

基础路径：`/ai/video/transcription`

#### LiveTranslate (语音翻译)

| 接口 | 方法 | 描述 | 模型 |
|------|------|------|------|
| `/qwen3-call` | GET | 同步语音翻译 | qwen3-livetranslate-flash |
| `/qwen3-livetranslate/stream` | GET | 流式语音翻译 | qwen3-livetranslate-flash |

#### WebSocket 实时语音识别

| 接口 | 方法 | 描述 | 模型 |
|------|------|------|------|
| `/websocket/short` | GET | 短语音实时翻译 | gummy-chat-v1 |
| `/websocket/long` | GET | 长语音实时翻译 | gummy-realtime-v1 |
| `/websocket/paraformer` | GET | Paraformer 高精度识别 | paraformer-realtime-v2 |
| `/websocket/funasr` | GET | Fun-ASR 实时识别 | fun-asr-realtime |

#### 文件识别 (ASR)

| 接口 | 方法 | 描述 | 模型 |
|------|------|------|------|
| `/asr/paraformer/call` | GET | Paraformer 文件识别 | paraformer-v2 |
| `/asr/funasr/call` | GET | Fun-ASR 文件识别 | fun-asr |
| `/asr/qwen/call` | GET | Qwen-ASR 同步调用 | qwen3-asr-flash |
| `/asr/qwen/stream` | GET | Qwen-ASR 流式调用 | qwen3-asr-flash |

## 使用示例

### 1. Sambert 语音合成

```http
GET http://localhost:10009/ai/video/tts/sambert/stream
```

返回 MP3 格式的音频流。

### 2. Qwen3-TTS 语音合成

```http
GET http://localhost:10009/ai/video/tts/qwen3-tts/call
```

返回音频 URL 和保存路径信息。

### 3. 语音翻译

```http
GET http://localhost:10009/ai/video/transcription/qwen3-call
```

对指定音频进行语音识别和翻译。

### 4. 实时语音识别

```http
GET http://localhost:10009/ai/video/transcription/websocket/short
```

通过 WebSocket 进行实时语音识别和翻译。

## 技术架构

- **Spring Boot**: Web 框架
- **Spring AI Alibaba**: 阿里通义大模型集成
- **Reactor**: 响应式流处理
- **DashScope Audio API**: 阿里云音频服务

## 模型说明

### 语音合成模型

| 模型 | 说明 |
|------|------|
| sambert-zhichu-v1 | 神经网络语音合成，支持多种音色 |
| qwen3-tts-flash | 通义千问语音合成，高质量语音生成 |

### 语音识别模型

| 模型 | 说明 |
|------|------|
| qwen3-livetranslate-flash | 实时语音翻译 |
| qwen3-asr-flash | 高精度语音识别 |
| paraformer-v2 | 阿里语音识别模型 |
| fun-asr | 语音识别模型 |
| gummy-chat-v1 | 短语音实时翻译 |
| gummy-realtime-v1 | 长语音实时翻译 |
| paraformer-realtime-v2 | Paraformer 实时识别 |
| fun-asr-realtime | Fun-ASR 实时识别 |

## 注意事项

1. 所有接口需要先设置 `AI_DASHSCOPE_API_KEY` 环境变量
2. WebSocket 接口需要准备测试音频文件
3. 流式接口返回 `Flux<String>` 或 `Flux<byte[]>`
4. 音频文件默认保存在 `src/main/resources/audio/` 目录下
