package com.touhouqing.chatAiDemo.entity.vector;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 向量数据库文档实体
 * 用于封装存储到向量数据库的文档信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VectorDocument {

    /**
     * 文档唯一标识
     */
    private String id;

    /**
     * 文档内容
     */
    private String content;

    /**
     * 文档类型
     */
    private DocumentType documentType;

    /**
     * 关联的图数据库实体ID
     */
    private Long graphEntityId;

    /**
     * 关联的图数据库实体类型
     */
    private String graphEntityType;

    /**
     * 来源URL
     */
    private String sourceUrl;

    /**
     * 来源网站
     */
    private String sourceWebsite;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 额外的元数据
     */
    private Map<String, Object> metadata;

    /**
     * 文档类型枚举
     */
    public enum DocumentType {
        PROJECT_DESCRIPTION("项目描述"),
        PROJECT_REQUIREMENTS("项目需求"),
        COMPANY_PROFILE("企业简介"),
        ORGANIZATION_INFO("机构信息"),
        BIDDING_ANNOUNCEMENT("招标公告"),
        AWARD_RESULT("中标结果"),
        CONTRACT_INFO("合同信息");

        private final String description;

        DocumentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}