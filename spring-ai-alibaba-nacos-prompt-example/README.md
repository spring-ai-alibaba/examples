# Spring AI Alibaba 动态 Prompt 最佳实践
Spring AI Alibaba 使用 Nacos 的配置中心能力来动态管理 AI 应用的 Prompt。以此来实现动态更新 Prompt 的功能。
## 接口文档
### PromptController 接口

#### 1. generateJoke 方法

**接口路径：** `GET /nacos/books`

**功能描述：** 提供 generateJoke 相关功能

**主要特性：**
- 基于 Spring Boot REST API 实现
- 返回 JSON 格式响应
- 支持 UTF-8 编码

**使用场景：**
- 数据处理和响应
- API 集成测试

**示例请求：**
```bash
GET http://localhost:8080/nacos/books
```
## 技术实现
### 核心组件
- **Spring Boot**: 应用框架
- **Spring AI Alibaba**: AI 功能集成
- **REST Controller**: HTTP 接口处理
- **spring-boot-starter-web**: 核心依赖
- **spring-ai-alibaba-starter-dashscope**: 核心依赖
- **spring-ai-alibaba-starter-nacos-prompt**: 核心依赖

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量
- 默认端口：8080
- 默认上下文路径：/basic
## 测试指导
### 使用 HTTP 文件测试
模块根目录下提供了 **[spring-ai-alibaba-nacos-prompt-example.http](./spring-ai-alibaba-nacos-prompt-example.http)** 文件，包含所有接口的测试用例：
- 可在 IDE 中直接执行
- 支持参数自定义
- 提供默认示例参数

### 使用 curl 测试
```bash
# generateJoke 接口测试
curl "http://localhost:8080/nacos/books"
```
## 注意事项
1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置
2. **网络连接**: 需要能够访问阿里云 DashScope 服务
3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容
4. **端口配置**: 确保端口 8080 未被占用

---

*此 README.md 由自动化工具生成于 2025-12-11 00:51:02*
## 模块说明
Spring AI Alibaba 使用 Nacos 的配置中心能力来动态管理 AI 应用的 Prompt。以此来实现动态更新 Prompt 的功能。。

## 环境准备
Nacos: 具备配置中心能力的 Nacos，本例中使用 Nacos 2.3.0。最新版本的 Nacos 3.X 亦可。

## AI 工程创建
Example 工程地址：https://github.com/springaialibaba/spring-ai-alibaba-nacos-prompt-example

### Pom.xml
> Tips: 项目中已经引入了 Spring AI Alibaba Bom 和 Spring Boot Bom。因此这里省略了版本号。有关 bom 定义参考如上的 Github 仓库地址。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-nacos-prompt</artifactId>
</dependency>
```

### Application.yml
在配置文件中加入 Nacos 监听的 DataID 以及 Nacos Server 的用户名和密码等信息。

```yml
server:
  port: 10010

spring:
  application:
    name: spring-ai-alibaba-nacos-prompt-example

  # 指定监听的 prompt 配置
  config:
    import:
      - "optional:nacos:prompt-config.json"
  nacos:
    username: nacos
    password: nacos

  ai:
    # 开启 nacos 的 prompt tmpl 监听功能
    nacos:
      prompt:
        template:
          enabled: true

```

### Controller
```java
@RestController
@RequestMapping("/nacos")
public class PromptController {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PromptController.class);

    private final ChatClient client;

    private final ConfigurablePromptTemplateFactory promptTemplateFactory;

    public PromptController(
            ChatModel chatModel,
            ConfigurablePromptTemplateFactory promptTemplateFactory
    ) {

        this.client = ChatClient.builder(chatModel).build();
        this.promptTemplateFactory = promptTemplateFactory;
    }

    @GetMapping("/books")
    public Flux<String> generateJoke(
            @RequestParam(value = "author", required = false, defaultValue = "鲁迅") String authorName
    ) {

        // 使用 nacos 的 prompt tmpl 创建 prompt
        ConfigurablePromptTemplate template = promptTemplateFactory.create(
                "author",
                "please list the three most famous books by this {author}."
        );
        Prompt prompt = template.create(Map.of("author", authorName));
        logger.info("最终构建的 prompt 为：{}", prompt.getContents());

        return client.prompt(prompt)
                .stream()
                .content();
    }

}
```

## Nacos 配置添加
1. 启动 Nacos 服务；
2. 写入配置，dataId 为：spring.ai.alibaba.configurable.prompt
3. 在配置中写入如下配置：

    ```json
    [
      {
        "name": "author",
        "template": "列出 {author} 有名的著作",
        "model": {
          "key": "余华"
        }
      }
    ]
    ```

## 功能演示
完成上述配置之后，启动项目：

1. 在启动日志中，可以看到如下输出，表明已经开始监听此 DataID 的配置：

   ```shell
   OnPromptTemplateConfigChange,templateName:author,template:列出 {author} 有名的著作，只需要书名清单,model:{key=余华}
   ```

2. 发送请求查看输出：

   > Tips: 这里输出了鲁迅的作品集是因为在 controller 中设置了 defaultValue 为鲁迅.

   ```java
   GET http://127.0.0.1:10010/nacos/books
   
   1. 《呐喊》  
   2. 《彷徨》  
   3. 《朝花夕拾》  
   4. 《阿Q正传》  
   5. 《野草》  
   6. 《坟》  
   7. 《热风》  
   8. 《华盖集》  
   9. 《华盖集续编》  
   10. 《故事新编》  
   11. 《三闲集》  
   12. 《二心集》  
   13. 《南腔北调集》  
   14. 《伪自由书》  
   15. 《准风月谈》  
   16. 《花边文学》  
   17. 《且介亭杂文》  
   18. 《且介亭杂文二集》  
   19. 《且介亭杂文末编》
   ```

   查看控制台输出：

   ```shell
   列出 鲁迅 有名的著作，只需要书名清单
   ```

3. 动态更新 Nacos 的 Prompt 配置，再次查看请求查看效果

   > Tips: 因为 controller 中设置了 defaultValue 为鲁迅，因此 Prompt 变更仍然和文学作家相关。

   变更 Prompt 为：

   ```json
   [
     {
       "name":"author",
       "template":"介绍 {author}，列出其生平经历和文学成就",
       "model":{
         "key":"余华"
       }
     }
   ]
   ```

   **点击发布**之后，看到控制台输出如下，证明变更成功：

   ```text
   OnPromptTemplateConfigChange,templateName:author,template:介绍 {author}，列出其生平经历和文学成就,model:{key=余华
   ```

4. 再次发送请求：

   ```shell
   GET http://127.0.0.1:10010/nacos/books
   
   鲁迅（1881年9月25日－1936年10月19日），原名周树人，字豫才，浙江绍兴人，是中国现代文学史上最重要的作家之一，同时也是思想家、革命家和教育家。他以其犀利的笔锋和深刻的社会批判精神，成为中国新文化运动的旗手和奠基人之一。
   
   ---
   
   ### **生平经历**
   
   1. **早年生活与求学**  
      - 1881年出生于浙江绍兴的一个书香门第家庭，父亲周伯宜是秀才。
      - 少年时因家庭变故而经历了从富到贫的生活变化，这对他后来的思想形成产生了深远影响。
      - 1898年，考入南京水师学堂，后转入江南陆师学堂附设矿务铁路学堂学习。
   
   2. **留学日本**  
      - 1902年赴日本留学，先在东京弘文学院学习日语，后进入仙台医学专门学校（今东北大学医学部）学习医学。
      - 在日本期间，逐渐认识到“医其心”比“医其身”更重要，决定弃医从文，以文学唤醒国民的精神。
   
   3. **回国与教书生涯**  
      - 1909年回国后，先后在北京大学、北京师范大学等高校任教。
      - 从事文学创作的同时，积极参与新文化运动，推动白话文和新文学的发展。
   
   4. **创作高峰**  
      - 1918年发表第一篇白话小说《狂人日记》，标志着中国现代小说的开端。
      - 此后，鲁迅的创作进入高峰期，发表了大量杂文、小说、散文等作品。
   
   5. **晚年生活**  
      - 1927年后定居上海，继续写作并参与左翼文化活动。
      - 1936年因肺病在上海逝世，享年55岁。
   
   ---
   
   ### **文学成就**
   
   鲁迅的作品涵盖了小说、散文、杂文、诗歌等多个领域，具有极高的艺术价值和社会意义。
   
   #### 1. **小说**
      - 鲁迅的小说以深刻揭示社会问题和人性弱点著称。
      - 代表作：
        - 《呐喊》：包括《狂人日记》《阿Q正传》《故乡》等，揭露封建制度对人的压迫。
        - 《彷徨》：包括《祝福》《伤逝》等，描写知识分子的迷茫与挣扎。
      - 被誉为“中国现代小说之父”。
   
   #### 2. **散文**
      - 散文集《朝花夕拾》是鲁迅回忆童年和青少年生活的作品，语言优美，情感真挚。
      - 代表作：《从百草园到三味书屋》《藤野先生》。
   
   #### 3. **杂文**
      - 鲁迅的杂文以犀利的笔触批判社会现实，被誉为“匕首与投枪”。
      - 杂文集：《热风》《华盖集》《坟》《且介亭杂文》等。
      - 主要针对封建礼教、军阀统治、文化保守主义等进行批判。
   
   #### 4. **翻译与研究**
      - 鲁迅还翻译了许多外国文学作品，如俄国果戈里的《死魂灵》。
      - 同时致力于中国古代文化的整理与研究，出版了《中国小说史略》《汉文学史纲要》等学术著作。
   
   ---
   
   ### **历史地位与影响**
   
   1. **对中国文学的贡献**  
      - 鲁迅开创了中国现代文学的新局面，他的作品奠定了中国现代文学的基础。
      - 提倡白话文，推动文学语言的现代化。
   
   2. **对社会思想的影响**  
      - 鲁迅的思想深刻影响了几代中国人，特别是在反对封建礼教、倡导思想解放方面。
      - 他的杂文成为批评社会不公和揭露黑暗的典范。
   
   3. **国际声誉**  
      - 鲁迅的作品被翻译成多种语言，在世界范围内享有盛誉。
      - 被誉为“中国的良心”和“民族魂”。
   
   鲁迅的一生，既是对传统文化的反思，也是对现代社会的探索。他的作品至今仍具有强大的生命力和现实意义。
   ```

   最终构建的 Prompt 为：

   ```text
   介绍 鲁迅，列出其生平经历和文学成就
   ```

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:41:32*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*

---

*此 README.md 由自动化工具融合更新于 2025-12-11 00:51:02*

*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*