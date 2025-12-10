# Spring AI Alibaba Evaluation Example

## 接口文档
### EvaluationController 接口

#### 1. saRelevancy 方法

**接口路径：** `GET /ai/evaluation/sa/relevancy`

**功能描述：** 相关性评估器, 用来评估AI生成的响应与提供的上下文的相关性.

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/evaluation/sa/relevancy
```

#### 2. saFactChecking 方法

**接口路径：** `GET /ai/evaluation/sa/fact-checking`

**功能描述：** 事实性评估器, 根据提供的上下文评估AI生成的响应的事实准确性.

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/evaluation/sa/fact-checking
```

#### 3. saaAnswerRelevancy 方法

**接口路径：** `GET /ai/evaluation/saa/answer-relevancy`

**功能描述：** 评分评估器, 基于提供的评分标准和内容信息进行评分

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/evaluation/saa/answer-relevancy
```

#### 4. saaAnswerCorrectness 方法

**接口路径：** `GET /ai/evaluation/saa/answer-correctness`

**功能描述：** 正确性评估器, 评估Query返回的Response是否符合提供的Context信息

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/evaluation/saa/answer-correctness
```

#### 5. saaAnswerFaithfulness 方法

**接口路径：** `GET /ai/evaluation/saa/answer-faithfulness`

**功能描述：** 评分评估器, 基于提供的评分标准和内容信息进行评分

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/ai/evaluation/saa/answer-faithfulness
```
## 技术实现
### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-alibaba-starter-dashscope**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic
## 测试指导
### 使用 HTTP 文件测试
模块根目录下提供了 **[spring-ai-alibaba-evaluation-example.http](./spring-ai-alibaba-evaluation-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# saRelevancy 接口测试
curl "http://localhost:8080/ai/evaluation/sa/relevancy"
```
## 注意事项
1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-11 00:51:02*
## 模块说明
* 此示例将演示如何使用 Spring AI 提供的 AI 模型评估功能。。

## 简介
* 此示例将演示如何使用 Spring AI 提供的 AI 模型评估功能。
* [Model Evaluation参考文档](https://docs.spring.io/spring-ai/reference/api/testing.html)

## 运行项目
* 配置AK环境变量
```shell
export AI_DASHSCOPE_API_KEY=<your-api-key-id>
```
* 构建项目
```shell
mvn clean install
```
* 启动项目
```shell
mvn spring-boot:run
```

## AI模型评估接口测试
* [查看api文件](evaluation.http)
* RelevancyEvaluator相关性评估器
```shell
curl -X GET -G --data-urlencode 'query=中国的首都是哪里?' 'http://localhost:8080/ai/evaluation/sa/relevancy'
```
* FactCheckingEvaluator事实性评估器
```shell
curl -X GET -G --data-urlencode 'query=中国的首都是哪里?' 'http://localhost:8080/ai/evaluation/sa/fact-checking'
```
* AnswerRelevancyEvaluator评分评估器
```shell
curl -X GET -G --data-urlencode 'query=中国的首都是哪里?' 'http://localhost:8080/ai/evaluation/saa/answer-relevancy'
```
* AnswerCorrectnessEvaluator正确性评估器
```shell
curl -X GET -G --data-urlencode 'query=中国的首都是哪里?' 'http://localhost:8080/ai/evaluation/saa/answer-correctness'
```
* AnswerFaithfulnessEvaluator评分评估器
```shell
curl -X GET -G --data-urlencode 'query=中国的首都是哪里?' 'http://localhost:8080/ai/evaluation/saa/answer-faithfulness'
```

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:41:32*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:51:02*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*