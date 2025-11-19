# Spring AI Alibaba 多平台和多模型使用示例

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
# 错误模型请求
$ curl 127.0.0.1:10014/no-model/qwen-xxx/hi

model not exist

# deepseek-r1 模型请求
$ curl 127.0.0.1:10014/no-model/deepseek-r1/hi

Hello! How can I assist you today?

# qwen-plus 模型请求
$ curl 127.0.0.1:10014/no-model/qwen-plus/hi

Hello! How can I assist you today?

# qwen-max 模型请求
$ curl 127.0.0.1:10014/no-model/qwen-max/hi

Hello! How can I assist you today?
```

至此，我们便完成了如何在 Spring AI Alibaba 中使用多个不同的模型平台和平台上的不同模型的示例。

## ChatClient 多模型和多平台示例

### 多模型

```shell
curl -G "http://localhost:10014/more-model-chat-client" \
     --data-urlencode "prompt=你好" \
     --header "models=deepseek-r1"
```
