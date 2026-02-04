package com.alibaba.cloud.ai.rag.parallel.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.alibaba.cloud.ai.rag.parallel.config.KnowledgeBasePropertiesConfiguration.getKnowledgeBaseConfigPropertiesByScope;
import static com.alibaba.cloud.ai.rag.parallel.config.KnowledgeBasePropertiesConfiguration.knowledgeBasePropertiesConfiguration;

/**
 * @Author NGshiyu
 * @Description 配置发现
 * @CreateTime 2026/2/3 15:38
 */
@Slf4j
public class KnowledgeBaseConfigFinder {


    /**
     * 获取指定的知识库配置，并移除不相关的知识库
     *
     * @param indexNames 知识库名称
     *
     * @return {@link  com.alibaba.cloud.ai.rag.parallel.config.KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties} 知识库配置
     */
    public static List<KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig> findDirectIndexConfigByIndexNames(@NotEmpty List<String> indexNames) {
        List<KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig> configs = new ArrayList<>();
        knowledgeBasePropertiesConfiguration.configs.stream()
                .filter(config -> config.getIndexNames().stream().anyMatch(indexNames::contains))
                .forEach(config -> {
                    KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig newConfig = new KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig();
                    newConfig.setBaseUrl(knowledgeBasePropertiesConfiguration.getBaseUrl());
                    newConfig.setWorkspaceId(config.getWorkspaceId());
                    newConfig.setApiKey(config.getApiKey());
                    Optional<String> first = config.getIndexNames().stream().filter(indexNames::contains).findFirst();
                    first.ifPresentOrElse(indexName -> {
                        newConfig.setIndexName(indexName);
                        configs.add(newConfig);
                    }, () -> {
                    });
                });
        return configs;
    }

    /**
     * 获取指定的知识库配置
     *
     * @param indexName 知识库名称
     *
     * @return {@link  com.alibaba.cloud.ai.rag.parallel.config.KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties} 知识库配置
     */
    public static KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig findDirectIndexConfigByIndexName(@NotBlank String indexName) {
        Optional<KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties> any = knowledgeBasePropertiesConfiguration.configs
                .stream().
                filter(config -> config.getIndexNames().contains(indexName))
                .findAny();
        if (any.isPresent()) {
            KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties config = any.get();
            KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig newConfig = new KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig();
            newConfig.setBaseUrl(knowledgeBasePropertiesConfiguration.getBaseUrl());
            newConfig.setWorkspaceId(config.getWorkspaceId());
            newConfig.setApiKey(config.getApiKey());
            newConfig.setIndexName(indexName);
            return newConfig;
        }
        else {
            log.error("ConfigFinderError-findDirectIndexConfigByScopeAndIndexName: KnowledgeBaseConfig not found, indexName [{}]", indexName);
            return null;
        }
    }

    /**
     * 获取指定的知识库配置-通过知识库名称在指定作用域寻找
     *
     * @param scope     作用域名称
     * @param indexName 知识库名称
     *
     * @return {@link  com.alibaba.cloud.ai.rag.parallel.config.KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties} 知识库配置
     */
    public static KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig findDirectIndexConfigByIndexNameInDirectScope(@NotBlank String scope, @NotBlank String indexName) {
        KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties properties = getKnowledgeBaseConfigPropertiesByScope(scope);
        KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig newConfig = new KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig();
        newConfig.setBaseUrl(knowledgeBasePropertiesConfiguration.getBaseUrl());
        newConfig.setWorkspaceId(properties.getWorkspaceId());
        newConfig.setApiKey(properties.getApiKey());
        newConfig.setIndexName(indexName);
        return newConfig;
    }

    /**
     * 获取指定的知识库配置-通过作用域寻找
     *
     * @param scope 作用域名称
     *
     * @return {@link  com.alibaba.cloud.ai.rag.parallel.config.KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties} 知识库配置
     */
    public static List<KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig> findDirectIndexConfigsByScope(@NotBlank String scope) {
        Optional<KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties> any = knowledgeBasePropertiesConfiguration.configs
                .stream().filter(config -> config.getScope().equals(scope))
                .findAny();
        if (any.isPresent()) {
            KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties config = any.get();
            List<String> indexNames = config.getIndexNames();
            return indexNames.stream().map(indexName -> {
                KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig newConfig = new KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfig();
                newConfig.setBaseUrl(knowledgeBasePropertiesConfiguration.getBaseUrl());
                newConfig.setWorkspaceId(config.getWorkspaceId());
                newConfig.setApiKey(config.getApiKey());
                newConfig.setIndexName(indexName);
                return newConfig;
            }).toList();
        }
        else {
            log.error("ConfigFinderError-findDirectIndexConfigsByScope: KnowledgeBaseConfig not found, scope [{}]", scope);
            return null;
        }

    }


}
