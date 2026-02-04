package com.alibaba.cloud.ai.rag.parallel.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author NGshiyu
 * @Description 知识库配置
 * @CreateTime 2026/2/3 15:24
 */
@Setter
@Getter
@ConfigurationProperties("knowledge-base")
public class KnowledgeBasePropertiesConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBasePropertiesConfiguration.class);

    /**
     * 配置验证以及初始化Bean
     */
    @PostConstruct
    public void validateUniqueLabelsAndInitBean() {
        Set<String> seen = new HashSet<>();
        for (KnowledgeBasePropertiesConfiguration.KnowledgeBaseConfigProperties configProperties : configs) {
            if (!seen.add(configProperties.getScope())) {
                throw new IllegalStateException("知识库配置错误：知识库作用域scope [" + configProperties.getScope() + "] 重复！");
            }
        }
        knowledgeBasePropertiesConfiguration = this;
        configMap = knowledgeBasePropertiesConfiguration.configs.stream()
                .collect(Collectors.toMap(KnowledgeBaseConfigProperties::getScope, Function.identity()));
    }

    /**
     * URL 默认百炼
     */
    private String baseUrl = "https://dashscope.aliyuncs.com";

    List<KnowledgeBaseConfigProperties> configs;

    /**
     * 自身静态对象，维持静态调用可用
     */
    static KnowledgeBasePropertiesConfiguration knowledgeBasePropertiesConfiguration;

    /**
     * 配置项
     * key: scope, value: 配置项
     */
    private static Map<String, KnowledgeBaseConfigProperties> configMap = Map.of();

    /**
     * 应用于实际检索操作的知识库配置对象
     */
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KnowledgeBaseConfig {
        private String baseUrl;
        private String workspaceId;
        private String apiKey;
        private String indexName;
        private Boolean structure = false;
    }

    public static KnowledgeBaseConfigProperties getKnowledgeBaseConfigPropertiesByScope(@NotBlank String scope) {
        return MapUtils.getObject(configMap, scope);
    }

    /**
     * 知识库配置 yaml 文件映射类
     */
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KnowledgeBaseConfigProperties {
        /**
         * 知识库所在的业务空间的ID
         */
        private String workspaceId;
        /**
         * 调用知识库的APIKey
         */
        private String apiKey;
        /**
         * 知识库的名称列表
         */
        private List<String> indexNames;
        /**
         * 作用域，用于多知识库的领域区分
         */
        private String scope;
        /**
         * 匹配分值，大于该分值则返回
         */
        private float score = 0.5f;
        /**
         * 返回数量
         */
        private Integer topNum = 10;
    }
}
