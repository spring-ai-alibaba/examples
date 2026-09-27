# Spring AI Alibaba OpenAI-DashScope 多模态示例

## 项目简介
本项目演示如何通过 **Spring AI** 的 OpenAI 客户端接口调用阿里云 **DashScope** 的多模态模型（如 `qwen-vl-max-latest`），实现图片分析功能。
虽然依赖了 Spring AI 的 OpenAI 模块，但底层实际调用的是 DashScope 的兼容模式接口。

---

## 核心功能
1. **图片 URL 分析** 
   通过 GET 请求传入图片 URL 和描述文本，由 DashScope 多模态模型分析图片内容。
2. **图片文件上传分析** 
   通过 POST 请求上传本地图片文件，结合提示词进行多模态分析。

---

## 技术架构
- **框架**：Spring Boot 3.x + Spring AI OpenAI Starter
- **多模态模型**：阿里云 DashScope 的 `qwen-vl-max-latest`
- **核心类**：
  - [OpenAiChatClientController](src/main/java/com/alibaba/cloud/ai/example/multi/controller/OpenAiChatClientController.java#L35-L139)：提供 REST API 接口
  - `ChatClient`：封装 DashScope 兼容模式的调用逻辑

---

## 环境准备
1. **API 密钥** 
   配置阿里云 DashScope 的 API Key：
   ```bash
   export AI_DASHSCOPE_API_KEY=your_api_key_here
   ```
2. **依赖服务** 
   无需额外依赖，DashScope 服务通过公网访问。

---

## 快速启动
1. **构建项目** 
   ```bash
   mvn clean install
   ```
2. **运行服务** 
   ```bash
   cd openai-dashscope-multi-model
   mvn spring-boot:run
   ```
3. **访问接口** 
   服务启动后，默认监听端口 `10014`。
---

## 特性说明
1. **多模态兼容性** 
   通过以下配置切换 DashScope 多模态模型：
   ```yaml
   spring:
     ai:
       openai:
         base-url: https://dashscope.aliyuncs.com/compatible-mode  # DashScope 兼容模式
         chat:
           options:
             model: qwen-vl-max-latest  # DashScope 多模态模型
   ```
2. **文件上传限制** 
   默认支持最大 10MB 文件上传，可通过 `application.yml` 修改：
   ```yaml
   spring:
     servlet:
       multipart:
         max-file-size: 10MB
   ```
