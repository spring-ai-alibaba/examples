package com.touhouqing.chatAiDemo.entity.graph;

import org.springframework.data.neo4j.core.schema.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 招标项目类别实体
 */
@Node("BiddingProjectCategory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCategory {

    @Id
    @GeneratedValue
    private Long id;

    @Property("categoryName")
    private String categoryName;

    @Property("categoryCode")
    private String categoryCode;

    @Property("description")
    private String description;

    @Property("level")
    private Integer level; // 分类层级

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("updatedAt")
    private LocalDateTime updatedAt;

    // 与招标项目的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.INCOMING)
    private List<BiddingProject> projects;

    // 与父类别的关系
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.OUTGOING)
    private ProjectCategory parentCategory;

    // 与子类别的关系
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.INCOMING)
    private List<ProjectCategory> subCategories;
}