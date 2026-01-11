# Spring AI Alibaba 多平台和多模型使用示例

## 接口文档
### MorePlatformController 接口

#### 1. chat 方法

**接口路径：** `GET /no-platform/{platform}/{prompt}`

**功能描述：** 提供 chat 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/no-platform/{platform}/{prompt}
```


### MoreModelCallController 接口

#### 1. modelChat 方法

**接口路径：** `GET /no-model/{model}/{prompt}`

**功能描述：** 提供 modelChat 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- AI 对话交互
- 智能问答系统
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/no-model/{model}/{prompt}
```
## 技术实现
### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-alibaba-starter-dashscope**: 核心依赖
- **spring-ai-starter-model-ollama**: 核心依赖
- **spring-ai-starter-model-openai**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic
## 测试指导
### 使用 HTTP 文件测试
模块根目录下提供了 **[spring-ai-alibaba-more-platform-and-model-example.http](./spring-ai-alibaba-more-platform-and-model-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# chat 接口测试
curl "http://localhost:8080/no-platform/{platform}/{prompt}"
```

```bash
# modelChat 接口测试
curl "http://localhost:8080/no-model/{model}/{prompt}"
```
## 注意事项
1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-11 00:51:02*
## 模块说明
本示例展示如何在 Spring AI Alibaba 中使用多个不同的模型平台和平台上的不同模型。。

## 示例说明
本示例展示如何在 Spring AI Alibaba 中使用多个不同的模型平台和平台上的不同模型。

> 此示例项目已经完成代码编写，不需要任何改动！
> 关于如何部署 ollama 及模型，请参考 [Ollama Docker 部署](../docker-compose/ollama/README.md)

## 名词解释
> 注意区分开概念。

* 平台：DashScope，OpenAI，Ollama 等
* 模型：DashScope 上的 Deepseek-r1 qwen-plug 等

## 多平台示例
在 pom.xml 中引入 Spring AI 和 Spring AI Alibaba Starter 依赖。

> **注意指定版本，此示例项目版本已经在根 pom 中指定。**

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
</dependency>
```

在 controller 类中注入不同的 ChatModel 实现。

> 此处需要使用 @Qualifier 注解指定具体的 ChatModel 实现。

```java
private final ChatModel dashScopeChatModel;

private final ChatModel ollamaChatModel;

public MoreClientController(
        @Qualifier("dashScopeChatModel") ChatModel dashScopeChatModel,
        @Qualifier("ollamaChatModel") ChatModel OllamaChatModel
) {
    this.dashScopeChatModel = dashScopeChatModel;
    this.ollamaChatModel = OllamaChatModel;
}
```

> 不使用构造注入时，使用注解联合注入。推荐使用构造注入，

```java
@Autowired
@Qualifier("dashScopeChatModel")
private ChatModel getDashScopeChatModel;
```

启动项目，发送请求，查看输出，同时可在控制台中看到 ChatModel 的不同实现 bean。

> Spring AI Alibaba DashScope 最新版本已经适配 DeepSeek Reasoning Content。

```shell
$ curl 127.0.0.1:10014/no-platform/ollama/hi

Hello! How can I assist you today? 😊

$ curl 127.0.0.1:10014/no-platform/dashscope/hi

Hello! How can I assist you today?
```

## 多模型示例
此示例以 DashScope 平台中的模型为例。

```java
// 声明可用模型
private final Set<String> modelList = Set.of(
        "deepseek-r1",
        "deepseek-v3",
        "qwen-plus",
        "qwen-max"
);
```

构建运行时 options：

```java
ChatOptions runtimeOptions = ChatOptions.builder().model(model).build();
```

发起模型调用：

```java
Generation gen = dashScopeChatModel.call(
                    new Prompt(prompt, runtimeOptions))
            .getResult();
```

完整代码：

```java
@RestController
@RequestMapping("/no-model")
public class MoreModelCallController {

	private final Set<String> modelList = Set.of(
			"deepseek-r1",
			"deepseek-v3",
			"qwen-plus",
			"qwen-max"
	);

	private final ChatModel dashScopeChatModel;

	public MoreModelCallController(
			@Qualifier("dashScopeChatModel") ChatModel dashScopeChatModel
	) {
		this.dashScopeChatModel = dashScopeChatModel;
	}

	@GetMapping("/{model}/{prompt}")
	public String modelChat(
			@PathVariable("model") String model,
			@PathVariable("prompt") String prompt
	) {

		if (!modelList.contains(model)) {
			return "model not exist";
		}

		System.out.println("===============================================");
		System.out.println("当前输入的模型为：" + model);
		System.out.println("默认模型为：" + DashScopeApi.ChatModel.QWEN_PLUS.getModel());
		System.out.println("===============================================");

		ChatOptions runtimeOptions = ChatOptions.builder().model(model).build();

		Generation gen = dashScopeChatModel.call(
						new Prompt(prompt, runtimeOptions))
				.getResult();

		return gen.getOutput().getText();
	}

}
```

发起请求：

```shell

## ChatClient 多模型和多平台示例


### 多模型
```shell
curl -G "http://localhost:10014/more-model-chat-client" \
     --data-urlencode "prompt=你好" \
     --header "models=deepseek-r1"
```

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:41:32*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:51:02*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*