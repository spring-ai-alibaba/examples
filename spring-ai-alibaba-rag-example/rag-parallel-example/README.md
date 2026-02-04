# rag-parallel

RAG多路召回最佳实践：
实际业务中的知识库过于庞大会导致召回效果不好，因此需要拆分多个知识库，或因业务诉求，需要分领域管理，提供此示例适配实际业务，补充现有框架无法自动执行多路召回的空白

## 目录介绍

```plain text
parallel/
├── config/
│   ├── KnowledgeBaseConfigFinder.java        //配置获取工具
│   └── KnowledgeBasePropertiesConfiguration.java    //yml配置映射
├── controller/
│   └── TestRagParallelController.java        //演示controller
├── model/
│   └── KnowledgeBaseDocument.java            //自定义适配业务需求的模型
├── retrieve/
│   └── KnowledgeBaseRetrieveProcess.java     //知识库检索的Spring AI 自定义实现，可以适配业务诉求做不同源实现以及自定义逻辑
├── util/
│   └── ConsoleColor.java                    
└── RagParallelApplication.java
```

## 设计思路

1. KnowledgeBasePropertiesConfiguration：自定义的知识库配置，将所有的知识库统一使用此配置映射，便于管理，通过划分`scope`的方式区分不同领域知识库 
2. KnowledgeBaseConfigFinder： 配置项自动获取，通过知识库的`indexName`名称，知识库的`scope`，或 `scope + indexName`自动获取相关配置
3. KnowledgeBaseRetrieveProcess：基于 Spring AI RAG `DocumentRetriever`的自定义实现，具备自主手动检索能力，多路召回能力，同时可以嵌入Spring AI Advisor Chain 实现自调用

## 使用方法示例

> 注入 `application.yml` 中的所需配置key

### 1.手动检索 `TestRagParallelController#retrieve == TestRagParallelController#retrieveByParallelThread` 

```java
// 手动注入目标检索知识库
KnowledgeBaseRetrieveProcess knowledgeBaseRetrieveProcess = KnowledgeBaseRetrieveProcess.builder()
                .addRetriever(KnowledgeBaseRetrieveProcess.DashScopeDocumentWrapper.builder()
                        .scope("scope1").build())
                .addRetriever(KnowledgeBaseRetrieveProcess.DashScopeDocumentWrapper.builder()
                        .scope("scope2").build())
                .build();
List<KnowledgeBaseDocument> knowledgeBaseDocuments = knowledgeBaseRetrieveProcess.retrieveByParallelThread(Query.builder()
        .text(question)
        .build());
//两种方式效果相同
//List<KnowledgeBaseDocument> knowledgeBaseDocuments = knowledgeBaseRetrieveProcess.retrieve(Query.builder()
//        .text(question)
//        .build());

```

### 2. advisor chain 调用 `TestRagParallelController#advisor`

```java
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                // 检索文档 - 使用 Builder 创建配置好的 KnowledgeBaseProcess 实例
                .documentRetriever(
                        KnowledgeBaseRetrieveProcess.builder()
                                .addRetrievers(List.of(KnowledgeBaseRetrieveProcess.DashScopeDocumentWrapper.builder()
                                        .scope("server").build()))
                                .build()  // 返回 KnowledgeBaseProcess 实例
                )
                // 将检索到的文档进行拼接
                //.documentJoiner(concatenationDocumentJoiner)
                // 对检索到的文档进行处理，处理分值,按分值过滤并排序
                //.documentPostProcessors(documentScoreProcess,scoreThresholdProcess,contentPrintProcess)
                // 对生成的查询进行上下文增强
                //.queryAugmenter(contextualQueryAugmenter)
                .build();
        Flux<String> content = ChatClient.builder(chatModel).build()
                .prompt()
                .user(question)
                .advisors(retrievalAugmentationAdvisor).stream()
                .content();
```