# Spring AI Alibaba Module RAG 使用示例

## 1. 简介

Spring AI Module RAG: https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#modules

Spring AI implements a Modular RAG architecture inspired by the concept of modularity detailed in the paper "Modular RAG: Transforming RAG Systems into LEGO-like Reconfigurable Frameworks".

## 2. 组成部分

### 2.1 Pre-Retrieval

> 增强和转换用户输入，使其更有效地执行检索任务，解决格式不正确的查询、query 语义不清晰、或不受支持的语言等。

1. **QueryAugmenter 查询增强**：使用附加的上下文数据信息增强用户 query，提供大模型回答问题时的必要上下文信息；
2. **QueryTransformer**：查询改写：因为用户的输入通常是片面的，关键信息较少，不便于大模型理解和回答问题。因此需要使用 prompt 调优手段或者大模型改写用户 query；
3. **QueryExpander**：查询扩展：将用户 query 扩展为多个语义不同的变体以获得不同视角，有助于检索额外的上下文信息并增加找到相关结果的机会。

### 2.2 Retrieval

> 负责查询向量存储等数据系统并检索和用户 query 相关性最高的 Document。

1. **DocumentRetriever**：检索器，根据 QueryExpander 使用不同的数据源进行检索，例如 搜索引擎、向量存储、数据库或知识图等；
2. **DocumentJoiner**：将从多个 query 和从多个数据源检索到的 Document 合并为一个 Document 集合；

### 2.3 Post-Retrieval

> 负责处理检索到的 Document 以获得最佳的输出结果，解决模型中的*中间丢失*和上下文长度限制等。

1. **DocumentRanker**：根据 Document 和用户 query 的相关性对 Document 进行排序和排名；
2. **DocumentSelector**：用于从检索到的 Document 列表中删除不相关或冗余文档；
3. **DocumentCompressor**：用于压缩每个 Document，减少检索到的信息中的噪音和冗余。

### 2.4 生成

生成用户 Query 对应的大模型输出。

## 3. 接口文档

### ModuleRagController 接口

#### 1. ragWithMemory 方法

**接口路径：** `POST /module-rag/rag/memory/{conversationId}`

**功能描述：** 使用基本RAG流程，带对话记忆功能的智能问答。

**主要特性：**
- 支持多轮对话上下文
- 自动维护会话记忆
- 向量检索增强
- 适用于连续对话场景

**请求参数：**
- `conversationId`: 会话ID，用于维护上下文
- `prompt`: 用户问题

**示例请求：**
```http
POST /module-rag/rag/memory/123
Content-Type: application/json

{
  "prompt": "Who are the characters going on an adventure in the North Pole?"
}
```

```bash
curl -X POST http://127.0.0.1:10014/module-rag/rag/memory/123 \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Who are the characters going on an adventure in the North Pole?"}'
```

#### 2. ragWithCompression 方法

**接口路径：** `POST /module-rag/rag/compression/{conversationId}`

**功能描述：** 使用带文档压缩的RAG流程，提高回答质量。

**主要特性：**
- 检索后文档压缩
- 减少冗余信息
- 提高相关性
- 优化上下文利用

**示例请求：**
```http
POST /module-rag/rag/compression/123
Content-Type: application/json

{
  "prompt": "What places do they visit?"
}
```

```bash
curl -X POST http://127.0.0.1:10014/module-rag/rag/compression/123 \
  -H "Content-Type: application/json" \
  -d '{"prompt": "What places do they visit?"}'
```

## 4. 启动示例

### 前置条件

在启动示例之前，您本地应该有一个可以正常使用的 Elasticsearch。

### 4.1 环境准备

1. 安装并启动 Elasticsearch：
```bash
# 使用 Docker
docker run -d \
  --name elasticsearch \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  elasticsearch:8.11.0
```

2. 设置环境变量：
```bash
export AI_DASHSCOPE_API_KEY=your_api_key
```

### 4.2 配置文件

`application.yaml` 配置示例：
```yaml
spring:
  ai:
    dashscope:
      api-key: ${AI_DASHSCOPE_API_KEY}
      chat:
        model: qwen-plus
        options:
          temperature: 0.7
          max-tokens: 2000
  elasticsearch:
    uris: http://localhost:9200
```

### 4.3 启动应用

```bash
mvn spring-boot:run
```

应用启动在 `http://localhost:10014`

## 5. 使用示例

### 5.1 基础RAG对话

不使用压缩时的基本RAG流程：

```shell
curl -X POST http://127.0.0.1:10014/module-rag/rag/memory/123 \
    -d '{"prompt": "Who are the characters going on an adventure in the North Pole?"}'

# 输出示例：
# I'm sorry, but I don't have the information needed to answer your question.
# Could you please provide more details or clarify your query?
# If it's outside my current knowledge base, I may not be able to assist accurately.
```

继续对话：
```shell
curl -X POST http://127.0.0.1:10014/module-rag/rag/memory/123 \
    -d '{"prompt": "What places do they visit?"}'

# 输出示例：
# I understand. Here's how I would respond:
#
# I'm sorry, but I don't have specific information about the characters going on an adventure in the North Pole.
# Could you provide more context or details? If it's from a particular story, book, or game,
# letting me know might help me assist you better.
```

### 5.2 带压缩的RAG对话

使用文档压缩提高回答质量：

```shell
curl -X POST http://127.0.0.1:10014/module-rag/rag/compression/123 \
    -d '{"prompt": "Who are the characters going on an adventure in the North Pole?"}'

# 输出示例：
# Understood. Here's a polite and clear response for the user:
#
# I'm sorry, but I don't have the specific information about the characters going on an adventure in the North Pole.
# If this is from a particular story, book, or other source, providing more details might help me assist you better.
```

继续对话：
```shell
curl -X POST http://127.0.0.1:10014/module-rag/rag/compression/123 \
    -d '{"prompt": "What places do they visit?"}'

# 输出示例：
# Certainly! Here's a polite response to inform the user that the query is outside my knowledge base:
#
# I'm sorry, but I don't have the specific information about the characters going on an adventure in the North Pole.
# If this is from a particular story, book, or other source, providing more details might help me assist you better.
```

## 6. 技术实现细节

### 6.1 模块化RAG流程

```java
// 基础RAG流程
public ChatResponse basicRAG(String prompt, String conversationId) {
    // 1. Query Enhancement
    Query enhancedQuery = queryAugmenter.augment(prompt);

    // 2. Document Retrieval
    List<Document> documents = documentRetriever.retrieve(enhancedQuery);

    // 3. Context Preparation
    String context = documentJoiner.join(documents);

    // 4. Response Generation
    return chatClient.call()
        .prompt(prompt)
        .user(user -> user.text(context))
        .chatResponse();
}

// 带压缩的RAG流程
public ChatResponse compressedRAG(String prompt, String conversationId) {
    // 1. Query Enhancement
    Query enhancedQuery = queryAugmenter.augment(prompt);

    // 2. Document Retrieval
    List<Document> documents = documentRetriever.retrieve(enhancedQuery);

    // 3. Document Compression
    List<Document> compressedDocs = documentCompressor.compress(documents);

    // 4. Context Preparation
    String context = documentJoiner.join(compressedDocs);

    // 5. Response Generation
    return chatClient.call()
        .prompt(prompt)
        .user(user -> user.text(context))
        .chatResponse();
}
```

### 6.2 对话记忆管理

```java
@Service
public class ConversationMemoryService {

    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    public void addMessage(String conversationId, Message message) {
        conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>())
                         .add(message);
    }

    public List<Message> getHistory(String conversationId) {
        return conversationHistory.getOrDefault(conversationId, Collections.emptyList());
    }

    public String buildContext(String conversationId, String currentQuery) {
        List<Message> history = getHistory(conversationId);
        StringBuilder context = new StringBuilder();

        // 添加历史对话上下文
        history.forEach(msg -> context.append(msg.toString()).append("\n"));

        // 添加当前查询
        context.append("Current query: ").append(currentQuery);

        return context.toString();
    }
}
```

### 6.3 文档压缩策略

```java
@Component
public class DocumentCompressor {

    private final ChatClient chatClient;

    public List<Document> compress(List<Document> documents) {
        return documents.stream()
            .map(this::compressDocument)
            .filter(doc -> doc.getMetadata().containsKey("relevance_score"))
            .filter(doc -> (Double) doc.getMetadata().get("relevance_score") > 0.5)
            .collect(Collectors.toList());
    }

    private Document compressDocument(Document document) {
        String prompt = "Please compress the following text while preserving key information:\n" +
                       document.getContent();

        String compressedContent = chatClient.call()
            .prompt(prompt)
            .content();

        return new Document(compressedContent, document.getMetadata());
    }
}
```

## 7. 配置说明

### 7.1 Elasticsearch配置

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: elastic
    password: changeme
    connection-timeout: 5s
    socket-timeout: 30s
```

### 7.2 向量存储配置

```yaml
spring:
  ai:
    vectorstore:
      elasticsearch:
        index-name: module-rag-index
        dimensions: 1536
        similarity-function: cosine
```

### 7.3 RAG组件配置

```yaml
rag:
  retrieval:
    top-k: 5
    similarity-threshold: 0.7
  compression:
    enabled: true
    max-compression-ratio: 0.5
  memory:
    max-history-length: 20
    ttl: 3600 # 1 hour
```

## 8. 性能优化

### 8.1 检索优化

- 使用合适的索引结构
- 调整 `top-k` 参数平衡精度和性能
- 实现结果缓存机制

### 8.2 压缩优化

- 并行处理文档压缩
- 设置合理的压缩比例
- 过滤低相关性文档

### 8.3 记忆管理

- 定期清理过期对话
- 限制历史记录长度
- 使用滑动窗口机制

## 9. 扩展功能

### 9.1 自定义检索器

```java
public class HybridRetriever implements DocumentRetriever {
    private final VectorStore vectorStore;
    private final SearchEngine searchEngine;

    @Override
    public List<Document> retrieve(Query query) {
        // 向量检索
        List<Document> vectorResults = vectorStore.similaritySearch(query);

        // 关键词检索
        List<Document> keywordResults = searchEngine.search(query.getText());

        // 结果合并和去重
        return mergeAndDeduplicate(vectorResults, keywordResults);
    }
}
```

### 9.2 查询扩展

```java
public class LLMQueryExpander implements QueryExpander {
    private final ChatClient chatClient;

    @Override
    public List<Query> expand(Query originalQuery) {
        String prompt = "Expand the following query with 3 different perspectives: " +
                       originalQuery.getText();

        String expansion = chatClient.call()
            .prompt(prompt)
            .content();

        return parseExpansions(expansion);
    }
}
```

## 10. 监控和日志

### 10.1 性能监控

```java
@Aspect
@Component
public class RAGPerformanceMonitor {

    @Around("execution(* com.alibaba.cloud.ai.example..*.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        log.info("Method {} executed in {} ms",
                joinPoint.getSignature().getName(), duration);

        return result;
    }
}
```

### 10.2 详细日志配置

```yaml
logging:
  level:
    com.alibaba.cloud.ai.example: DEBUG
    org.springframework.ai: DEBUG
    org.elasticsearch.client: WARN
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

## 11. 测试指导

### 11.1 单元测试

```java
@SpringBootTest
class ModuleRagControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testRAGWithMemory() {
        String url = "/module-rag/rag/memory/test-conversation";
        Map<String, String> request = Map.of("prompt", "What is AI?");

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("AI");
    }
}
```

### 11.2 集成测试

```java
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ModuleRagIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(
        "docker.elastic.co/elasticsearch/elasticsearch:8.11.0"
    );

    @DynamicPropertySource
    static void elasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
    }
}
```

## 12. 常见问题

### 12.1 检索结果质量差

- 检查嵌入模型配置
- 调整相似度阈值
- 优化查询预处理

### 12.2 对话上下文丢失

- 验证会话ID传递
- 检查记忆存储机制
- 确认上下文构建逻辑

### 12.3 压缩效果不明显

- 调整压缩提示词
- 修改压缩比例参数
- 检查文档相关性过滤

---

*更新时间: 2025-12-09*
*版本: 1.1.0*