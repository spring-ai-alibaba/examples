package com.alibaba.cloud.ai.rag.parallel.retrieve;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import com.alibaba.cloud.ai.rag.parallel.config.KnowledgeBaseConfigFinder;
import com.alibaba.cloud.ai.rag.parallel.config.KnowledgeBasePropertiesConfiguration;
import com.alibaba.cloud.ai.rag.parallel.model.KnowledgeBaseDocument;
import com.alibaba.fastjson2.JSON;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * @Author NGshiyu
 * @Description Spring AI DocumentRetriever 个性化实现
 * @CreateTime 2026/2/3 16:56
 */
@Service
public class KnowledgeBaseRetrieveProcess implements DocumentRetriever {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseRetrieveProcess.class);

    /**
     * 实例字段存储配置，避免 ThreadLocal 线程切换问题
     * 注意：通过 Builder 创建的实例在响应式流中使用
     * 如果是 Spring 管理的单例 Bean 注入使用，需要注意线程安全
     */
    private List<DashScopeDocumentWrapper> documentRetrievers = new ArrayList<>();

    public void setDocumentRetriever(List<DashScopeDocumentWrapper> documentRetrievers) {
        // 创建不可变列表，防止并发修改
        this.documentRetrievers = Optional.ofNullable(documentRetrievers)
                .map(ArrayList::new)
                .map(Collections::unmodifiableList)
                .orElse(Collections.emptyList());
    }

    /**
     * 创建一个配置好的 DocumentRetriever Builder
     */
    public static ConfigurableRetrieverBuilder builder() {
        return new ConfigurableRetrieverBuilder();
    }

    /**
     * 可配置的 DocumentRetriever 构建器
     */
    public static class ConfigurableRetrieverBuilder {
        private final List<DashScopeDocumentWrapper> documentRetrievers = new ArrayList<>();

        public ConfigurableRetrieverBuilder retrievers(List<DashScopeDocumentWrapper> documentRetrievers) {
            this.documentRetrievers.addAll(Optional.ofNullable(documentRetrievers).orElseGet(ArrayList::new));
            return this;
        }

        public ConfigurableRetrieverBuilder addRetriever(DashScopeDocumentWrapper retriever) {
            this.documentRetrievers.add(retriever);
            return this;
        }

        public ConfigurableRetrieverBuilder addRetrievers(List<DashScopeDocumentWrapper> retrievers) {
            this.documentRetrievers.addAll(Optional.ofNullable(retrievers).orElseGet(ArrayList::new));
            return this;
        }

        /**
         * 构建配置好的 KnowledgeBaseProcess 实例
         */
        public KnowledgeBaseRetrieveProcess build() {
            KnowledgeBaseRetrieveProcess process = new KnowledgeBaseRetrieveProcess();
            process.setDocumentRetriever(this.documentRetrievers);
            return process;  // 直接返回 KnowledgeBaseProcess，不使用匿名类
        }
    }

    /**
     * Retrieves relevant documents from an underlying data source based on the
     * given
     * query.
     *
     * @param query The query to use for retrieving documents
     *
     * @return The list of relevant documents
     */
    @NotNull
    @Override
    public List<Document> retrieve(@NotNull Query query) {
        return new ArrayList<>() {
            {
                addAll(retrieveByParallelThread(query));
            }
        };
    }

    @NotNull
    @Override
    public List<Document> apply(@NotNull Query query) {
        return retrieve(query);
    }

    /**
     * 多线程获取知识库召回
     *
     * @param query 召回的参数
     *
     * @return {@link List }<{@link KnowledgeBaseDocument }>
     */
    public List<KnowledgeBaseDocument> retrieveByParallelThread(Query query) {
        // documentRetrievers 已是不可变列表，直接使用即可
        List<WrapperAndConfig> wrapperAndConfigs = buildQueryWrapperAndConfigs(this.documentRetrievers);
        if (CollectionUtils.isNotEmpty(wrapperAndConfigs)) {
            // 构建 LLM 接入鉴权参数以及地址
            List<CompletableFuture<List<KnowledgeBaseDocument>>> list = wrapperAndConfigs.stream()
                    .map(wrapper -> CompletableFuture.supplyAsync(() -> getKnowledgeBaseDocuments(query, wrapper), ForkJoinPool.commonPool())
                            .exceptionallyAsync(throwable -> {
                                logger.error("知识库召回异常：{},知识库：{},异常信息: ", wrapper.config.getIndexName(), throwable.getMessage(),
                                        throwable);
                                throw new RuntimeException("知识库召回异常");
                            }, ForkJoinPool.commonPool()))
                    .toList();
            CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
            return list.stream().map(cf -> {
                try {
                    return cf.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }).filter(Objects::nonNull).flatMap(List::stream).toList();

            //在召回完成之后，可以再次全量rerank
        }
        else {
            return new ArrayList<>();
        }
    }

    /**
     * 实际从知识库获取知识的方法
     *
     * @param query            问题
     * @param wrapperAndConfig 知识库检索的参数和知识库的配置
     *
     * @return {@link List }<{@link KnowledgeBaseDocument }>
     */// *
    @NotNull
    private static List<KnowledgeBaseDocument> getKnowledgeBaseDocuments(Query query,
                                                                         WrapperAndConfig wrapperAndConfig) {
        KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig directIndexConfig = wrapperAndConfig.config;
        DashScopeDocumentRetrieverOptions options = wrapperAndConfig.options;
        String indexName = directIndexConfig.getIndexName();
        String workspaceId = directIndexConfig.getWorkspaceId();
        // 召回的知识库数据和参数构建
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(directIndexConfig.getApiKey())
                .baseUrl(directIndexConfig.getBaseUrl())
                .workSpaceId(workspaceId)
                .build();
        options.setIndexName(indexName);
        logger.info("开始知识库检索召回,检索的目标知识库：【{}，workspaceId: {}】，Query：{}，检索参数：{}, rerankModelName:{}", directIndexConfig.getIndexName(),
                directIndexConfig.getWorkspaceId(), JSON.toJSONString(JSON.toJSONString(query)),
                JSON.toJSONString(options),
                JSON.toJSONString(options.getRerankModelName()));
        DashScopeDocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(dashScopeApi, options);
        List<Document> retrieve = getDocumentList(query, dashScopeDocumentRetriever, options);
        // 构造自定义的返回
        return retrieve.stream().map(document -> {
            Double score = Double.valueOf(document.getMetadata().get("_score").toString());
            KnowledgeBaseDocument knowledgeBaseDocument = new KnowledgeBaseDocument(document.getId(), document.getText(), document.getMetadata(), score);
            knowledgeBaseDocument.setIndexName(directIndexConfig.getIndexName());
            knowledgeBaseDocument.setWorkspaceId(directIndexConfig.getWorkspaceId());
            knowledgeBaseDocument.setStructure(directIndexConfig.getStructure());
            return knowledgeBaseDocument;
        }).toList();
    }

    private static List<Document> getDocumentList(Query query, DashScopeDocumentRetriever dashScopeDocumentRetriever, DashScopeDocumentRetrieverOptions options) {
        int maxRetries = 3;
        int retryDelay = 1000;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // 参数校验
                if (query == null || dashScopeDocumentRetriever == null) {
                    logger.warn("查询参数或检索器为空");
                    return Collections.emptyList();
                }
                return dashScopeDocumentRetriever.retrieve(query);
            } catch (Exception e) {
                logger.warn("知识库[{}]检索失败，第[{}]次尝试，错误信息：{}", options.getIndexName(), attempt, e.getMessage());
                if (attempt == maxRetries) {
                    logger.error("知识库[{}]检索经过[{}]次重试后仍然失败", options.getIndexName(), maxRetries, e);
                    throw new RuntimeException("知识库[" + options.getIndexName() + "]检索失败：" + e.getMessage(), e);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep((long) retryDelay * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试过程中线程被中断", ie);
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * 获取知识库的检索配置项
     *
     * @param documentRetrievers 检索的参数
     *
     * @return {@link List
     * }<{@link KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig }>
     */
    @NotNull
    private static List<WrapperAndConfig> buildQueryWrapperAndConfigs(
            List<DashScopeDocumentWrapper> documentRetrievers) {
        List<WrapperAndConfig> configs = new ArrayList<>();
        documentRetrievers.forEach(wrapper -> {
            // indexName不空，scope不空
            if (StringUtils.isNotBlank(wrapper.getIndexName()) && StringUtils.isNotBlank(wrapper.getScope())) {
                KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig config = KnowledgeBaseConfigFinder
                        .findDirectIndexConfigByIndexNameInDirectScope(wrapper.getScope(), wrapper.getIndexName());
                Optional.ofNullable(config).ifPresent(directConfig -> {
                    config.setStructure(wrapper.getStructure());
                    configs.add(new WrapperAndConfig(config, wrapper.getDashScopeDocumentRetrieverOptions()));
                });
            }
            // indexName不空，scope空
            else if (StringUtils.isNotBlank(wrapper.getIndexName()) && StringUtils.isBlank(wrapper.getScope())) {
                KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig config = KnowledgeBaseConfigFinder
                        .findDirectIndexConfigByIndexName(wrapper.getIndexName());
                Optional.ofNullable(config).ifPresent(directConfig -> {
                    config.setStructure(wrapper.getStructure());
                    configs.add(new WrapperAndConfig(config, wrapper.getDashScopeDocumentRetrieverOptions()));
                });
            }
            // indexName空，scope不空
            else if (StringUtils.isBlank(wrapper.getIndexName()) && StringUtils.isNotBlank(wrapper.getScope())) {
                List<KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig> directIndexConfigsByScope = KnowledgeBaseConfigFinder
                        .findDirectIndexConfigsByScope(wrapper.getScope());
                Optional.ofNullable(directIndexConfigsByScope)
                        .ifPresent(directConfig -> directIndexConfigsByScope.stream()
                                .map(config -> {
                                    config.setStructure(wrapper.getStructure());
                                    return new WrapperAndConfig(config, wrapper.getDashScopeDocumentRetrieverOptions());
                                })
                                .forEach(configs::add));
            }
        });
        return configs;
    }


    private record WrapperAndConfig(KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig config,
                                    DashScopeDocumentRetrieverOptions options) {
    }

    /**
     * 多路召回的知识库配置注入使用的模型
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DashScopeDocumentWrapper {
        private String indexName;
        private String scope;
        /**
         * 是否结构化知识库，true：是结构化
         */
        private Boolean structure = false;
        ;
        private DashScopeDocumentRetrieverOptions dashScopeDocumentRetrieverOptions = new DashScopeDocumentRetrieverOptions();

        public void setScope(String scope, String indexName) {
            this.scope = scope;
            this.indexName = indexName;
        }

        public static DashScopeDocumentWrapper.Builder builder() {
            return new DashScopeDocumentWrapper.Builder();
        }

        public static class Builder {

            protected DashScopeDocumentWrapper dashScopeDocumentWrapper;

            public Builder() {
                this.dashScopeDocumentWrapper = new DashScopeDocumentWrapper();
            }

            public DashScopeDocumentWrapper.Builder indexName(@NotBlank String indexName) {
                this.dashScopeDocumentWrapper.setIndexName(indexName);
                return this;
            }

            public DashScopeDocumentWrapper.Builder scope(@NotBlank String scope, @NotBlank String indexName) {
                this.dashScopeDocumentWrapper.setScope(scope);
                this.dashScopeDocumentWrapper.setIndexName(indexName);
                return this;
            }

            public DashScopeDocumentWrapper.Builder scope(@NotBlank String scope) {
                this.dashScopeDocumentWrapper.setScope(scope);
                return this;
            }

            public DashScopeDocumentWrapper.Builder structure(@NotBlank boolean structure) {
                this.dashScopeDocumentWrapper.setStructure(structure);
                return this;
            }

            public DashScopeDocumentWrapper.Builder options(DashScopeDocumentRetrieverOptions dashScopeDocumentRetrieverOptions) {
                this.dashScopeDocumentWrapper.setDashScopeDocumentRetrieverOptions(dashScopeDocumentRetrieverOptions);
                return this;
            }

            public DashScopeDocumentWrapper build() {
                return this.dashScopeDocumentWrapper;
            }

        }
    }
}
