package com.touhouqing.chatAiDemo.entity.graph;

import org.springframework.data.neo4j.core.schema.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行业实体
 */
@Node("Industry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Industry {

    @Id
    @GeneratedValue
    private Long id;

    @Property("industryName")
    private String industryName;

    @Property("industryCode")
    private String industryCode; // 行业分类代码

    @Property("industryLevel")
    private Integer industryLevel; // 行业分类层级

    @Property("description")
    private String description;

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("updatedAt")
    private LocalDateTime updatedAt;

    // 与企业的关系
    @Relationship(type = "OPERATES_IN", direction = Relationship.Direction.INCOMING)
    private List<Company> companies;

    // 与组织的关系
    @Relationship(type = "BELONGS_TO_INDUSTRY", direction = Relationship.Direction.INCOMING)
    private List<Organization> organizations;

    // 与上级行业的关系
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.OUTGOING)
    private Industry parentIndustry;

    // 与下级行业的关系
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.INCOMING)
    private List<Industry> subIndustries;
}