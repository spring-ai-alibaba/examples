package com.touhouqing.chatAiDemo.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购项目类别实体
 */
@Node("ProcurementProjectCategory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCategory {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("name")
    private String name;
    
    @Property("code")
    private String code;
    
    @Property("description")
    private String description;
    
    @Property("level")
    private Integer level; // 分类层级
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    @Property("updatedAt")
    private LocalDateTime updatedAt;
    
    // 与采购项目的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.INCOMING)
    private List<ProcurementProject> projects;
    
    // 与父类别的关系
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.OUTGOING)
    private ProjectCategory parentCategory;
    
    // 与子类别的关系
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.INCOMING)
    private List<ProjectCategory> subCategories;
}
