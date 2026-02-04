package com.alibaba.cloud.ai.rag.parallel.controller;

import com.alibaba.cloud.ai.rag.parallel.model.KnowledgeBaseDocument;
import com.alibaba.cloud.ai.rag.parallel.retrieve.KnowledgeBaseRetrieveProcess;
import com.alibaba.fastjson2.JSON;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @Author NGshiyu
 * @Description test
 * @CreateTime 2026/2/3 15:50
 */
@RestController
@RequestMapping("/retrieve")
public class TestRagParallelController {
    @Autowired
    @Qualifier("dashScopeChatModel")
    ChatModel chatModel;


    /**
     * 通过领域多路召回
     */
    @GetMapping(path = "/retrieve")
    public void retrieve(@RequestParam String question) {
        KnowledgeBaseRetrieveProcess knowledgeBaseRetrieveProcess = KnowledgeBaseRetrieveProcess.builder()
                .addRetriever(KnowledgeBaseRetrieveProcess.DashScopeDocumentWrapper.builder()
                        .indexName("通用知识库1").build())
                .addRetriever(KnowledgeBaseRetrieveProcess.DashScopeDocumentWrapper.builder()
                        .indexName("通用知识库2").build())
                .build();
        String separator = AnsiOutput.toString(AnsiColor.BRIGHT_CYAN, "==============================方式一", AnsiColor.DEFAULT,
                ": 注入调用, 使用召回方法获取一个Spring Ai的Document对象，retrieve实现自DocumentRetriever，可以被自调用",
                AnsiColor.BRIGHT_CYAN, "==============================", AnsiColor.DEFAULT);
        System.out.println(separator);
        List<Document> retrieve = knowledgeBaseRetrieveProcess.retrieve(Query.builder()
                .text(question)
                .build());
        retrieve.forEach(doc -> System.out.println(AnsiOutput.toString(AnsiColor.GREEN, JSON.toJSONString(doc), AnsiColor.DEFAULT)));
        System.out.print("\n".repeat(5));
    }

    @GetMapping(path = "/retrieveByParallelThread")
    public void retrieveByParallelThread(@RequestParam String question) {
        KnowledgeBaseRetrieveProcess knowledgeBaseRetrieveProcess = KnowledgeBaseRetrieveProcess.builder()
                .addRetriever(KnowledgeBaseRetrieveProcess.DashScopeDocumentWrapper.builder()
                        .scope("server").build())
                .addRetriever(KnowledgeBaseRetrieveProcess.DashScopeDocumentWrapper.builder()
                        .scope("product").build())
                .build();
        String separator = AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, "==============================方式二",
                AnsiColor.DEFAULT, "：使用自定义的多线程检索方法,同retrieve",
                AnsiColor.BRIGHT_YELLOW, "==============================", AnsiColor.DEFAULT);
        System.out.println(separator);
        List<KnowledgeBaseDocument> knowledgeBaseDocuments = knowledgeBaseRetrieveProcess.retrieveByParallelThread(Query.builder()
                .text(question)
                .build());
        knowledgeBaseDocuments.forEach(doc -> System.out.println(AnsiOutput.toString(AnsiColor.BLUE, JSON.toJSONString(doc), AnsiColor.DEFAULT)));
        System.out.print("\n".repeat(5));
    }

    @GetMapping(path = "/advisor")
    public void advisor(@RequestParam String question) {
        String separator = AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, "==============================方式三",
                AnsiColor.DEFAULT, ":builder advisor调用，spring ai rag advisor注入chatClient",
                AnsiColor.BRIGHT_GREEN, "==============================", AnsiColor.DEFAULT);
        System.out.println(separator);
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
        content.subscribe(str -> System.out.println(AnsiOutput.toString(AnsiColor.MAGENTA, str, AnsiColor.DEFAULT)));
    }

}
