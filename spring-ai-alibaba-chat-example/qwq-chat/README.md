# Spring AI Alibaba QWQ 32B Chat Client Example

演示如何基于 DashScope API 接入 QWQ 32B 模型。

## 接入文档

https://help.aliyun.com/zh/model-studio/getting-started/models

错误码文档：

https://help.aliyun.com/zh/model-studio/developer-reference/error-code

## 请求演示

```shell
$ curl http://localhost:10002/client/stream/chat

<think>好的，用户让我</think>
<think>介绍自己，我之前</think>
<think>已经回答过一次了</think>
<think>，现在又问</think>
<think>同样的问题。用户</think>
<think>可能是想再确认一下</think>
<think>我的功能，或者需要</think>
<think>更详细的介绍？</think>
<think>也有可能他们想</think>
<think>测试我的一致性hink>
<think>又全面，同时保持简洁</think>
<think>。首先，回顾之前的回答</think>
<think>，已经涵盖了基本</think>
<think>功能、支持的语言、应用场景</think>
<think>。这次可能需要添加</think>
<think>一些信息，比如</think>
<think>最近的更新或者</think>
<think>更多例子，让用户</think>
<think>觉得有新内容</think>
<think>。不过根据指示</think>
<think>，不能编造新</think>
<think>功能，所以只能</think>
<think>在原有基础上调整</think>
<think>结构或补充细节。</think>
<think>用户可能希望了解我的应用场景</think>
<think>，或者想确认</think>
<think>我的能力是否符合他们的</think>
<think>需求。需要强调</think>
<think>我的多语能力和</think>
<think>具体应用实例，比如编程</think>
<think>、逻辑推理等。</think>
<think>另外，可以加入</think>
<think>一些鼓励用户提问的</think> >
<think>语句，促进进一步互动</think>
<think>。检查是否有需要</think>
<think>避免的内容，比如不</think>
<think>提及未实现的功能。</think>
<think>确保语气友好，使用</think>
<think>表情符号增加亲切</think>
<think>。最后，保持回答自然</think>
<think>流畅，避免重复之前的</think>
<think>结构，但信息</think>
<think>要准确一致。</think>

你好！我是是义千问（Qwen），阿里巴巴集团旗下的超大规模语言模型。我能够帮助你完成各种任务，比如：

- **回答问题**：无论是常识、专业知识，还是复杂问题，我都会尽力为你解答。
- **创作文字**：写故事、公文、邮件、剧本、诗歌等，我都可以尝试。
- **逻辑与编程**：解决数学问题、编写代码、进行逻辑推理。
- **多语言支持**：除了中文，我还支持英文、德语、法语、西班牙语等多种语言。
- **表达观点与互动**：聊日常话题、玩游戏，甚至讨论观点。

我的目标是成为一位全能的AI助手，无论你需要学习、工作还是娱乐上的帮助，我都会用友好且实用的方式回应你。有什么需要我帮忙的吗？😊
```
