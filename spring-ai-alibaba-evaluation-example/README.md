# Spring AI Alibaba Evaluation Example

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