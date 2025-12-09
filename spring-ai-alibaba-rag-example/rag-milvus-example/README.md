# Spring AI Alibaba Example - Rag Milvus

这个项目展示了如何使用 Spring AI Alibaba 集成 Milvus 进行向量搜索。

## Quick Start

- JDK 17
- Milvus (Docker Compose)

### 1. 安装和配置 Milvus

通过 Docker Compose 安装 Milvus：

- docker-compose 的 yml 文件见目录 `/spring-ai-alibaba-examples/docker-compose/milvus/docker-compose.yml`
- 详细安装文档见地址：https://milvus.io/docs/install_standalone-docker-compose.md

### 2. 启动和配置步骤

1. 启动 Milvus
2. 打开浏览器访问 `localhost:8000`，访问 dashboard
3. 在 default 数据库创建 collection

   ![img.png](img.png)

4. 打开 Milvus 向量检索
5. 修改 application.yml 中的配置
6. 运行本项目，在项目启动时会将数据导入 Milvus
7. 在 dashboard 手动加载集合

如果加载集合完成，将看到以下输出：

![img_1.png](img2.jpg)

其中 Milvus 的配置如下：

> 注意：不同版本 Milvus 的配置略有不同，Milvus2.3.0 版本才原生支持 Cosine 距离，请根据实际情况调整

## 接口文档

### RagController 接口

#### 1. generation 方法

**接口路径：** `GET /ai/chat`

**功能描述：** 基于 Milvus 向量检索的 RAG 问答

**请求参数：**
- `prompt` (可选): 提问内容，默认为空

**使用示例：**
```bash
curl "http://localhost:8080/ai/chat?prompt=如何使用 spring ai alibaba 开发 ai 应用"
```

**响应示例：**
```text
要使用 Spring AI Alibaba 开发 AI 应用，您可以按照以下步骤进行： 1. ......
```

#### 2. select 方法

**接口路径：** `GET /ai/select`

**功能描述：** 向量查询测试

**使用示例：**
```bash
curl http://localhost:8080/ai/select
```

**响应示例：**
```json
[
  {
    "id": "fae19802-f6c0-42a6-8408-6c68dba55722",
    "text": "17. SpringAIAlibaba支持自然语言处理、计算机视觉、语音处理和数据分析与预测功能。",
    "media": null,
    "metadata": {
      "distance": 0.25151956
    },
    "score": 0.7484804391860962
  }
]
```

## 项目演示

### 1. 导入

```shell
# 注意：在执行查询前，应该手动加载集合。避免查询错误

# 向量查询
curl http://localhost:8080/ai/select
```

### 2. 查询提问

浏览器访问：

http://localhost:8080/ai/chat?prompt="如何使用 spring ai alibaba 开发 ai 应用"

## 技术实现

### 核心组件
- **Spring Boot**: Web应用框架
- **Spring AI Alibaba**: AI功能集成
- **Milvus**: 向量数据库
- **DashScope**: 向量嵌入服务

### RAG流程
1. 用户输入问题
2. 将问题转换为向量嵌入
3. 在Milvus中检索相关文档
4. 将检索结果和原始问题一起发送给LLM
5. 返回基于检索内容的回答

## 注意事项

1. **Milvus版本兼容性**: 不同版本的Milvus配置略有差异
2. **集合加载**: 执行查询前必须手动加载集合
3. **向量嵌入**: 确保DashScope API密钥配置正确
4. **内存要求**: Milvus运行需要足够的内存空间

## 配置要求

### application.yml 配置示例
```yaml
spring:
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
    milvus:
      client:
        host: localhost
        port: 19530
        database: default
      collection:
        name: vector_store
        dimension: 1536
```

### 环境变量
- `DASHSCOPE_API_KEY`: 阿里云DashScope API密钥