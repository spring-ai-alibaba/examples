package com.touhouqing.chatAiDemo.config;

import org.neo4j.driver.Driver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Neo4j配置类
 * 手动配置所有必要的Neo4j组件以解决Spring Boot 3.x兼容性问题
 */
@Configuration
public class Neo4jTestConfiguration {

    /**
     * 配置Neo4jClient
     */
    @Bean
    @ConditionalOnMissingBean
    public Neo4jClient neo4jClient(Driver driver, DatabaseSelectionProvider databaseSelectionProvider) {
        return Neo4jClient.with(driver)
                .withDatabaseSelectionProvider(databaseSelectionProvider)
                .build();
    }

    /**
     * 配置DatabaseSelectionProvider
     */
    @Bean
    @ConditionalOnMissingBean
    public DatabaseSelectionProvider databaseSelectionProvider() {
        return DatabaseSelectionProvider.getDefaultSelectionProvider();
    }

    /**
     * 配置Neo4jMappingContext
     */
    @Bean
    @ConditionalOnMissingBean
    public Neo4jMappingContext neo4jMappingContext() {
        return new Neo4jMappingContext();
    }

    /**
     * 配置Neo4jTemplate
     */
    @Bean(name = "neo4jTemplate")
    @ConditionalOnMissingBean
    public Neo4jTemplate neo4jTemplate(Neo4jClient neo4jClient, Neo4jMappingContext neo4jMappingContext) {
        return new Neo4jTemplate(neo4jClient, neo4jMappingContext);
    }

    /**
     * 配置Neo4jOperations
     */
    @Bean(name = "neo4jOperations")
    @ConditionalOnMissingBean(Neo4jOperations.class)
    public Neo4jOperations neo4jOperations(Neo4jTemplate neo4jTemplate) {
        return neo4jTemplate;
    }

    /**
     * 配置事务管理器
     */
    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager transactionManager(Driver driver, DatabaseSelectionProvider databaseSelectionProvider) {
        return new Neo4jTransactionManager(driver, databaseSelectionProvider);
    }
}
