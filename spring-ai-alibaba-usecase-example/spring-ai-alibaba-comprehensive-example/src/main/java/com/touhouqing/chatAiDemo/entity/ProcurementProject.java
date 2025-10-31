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
 * 采购项目实体
 */
@Node("ProcurementProject")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcurementProject {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("projectName")
    private String projectName;
    
    @Property("description")
    private String description;
    
    @Property("budget")
    private Double budget;
    
    @Property("budgetUnit")
    private String budgetUnit; // 万元
    
    @Property("procurementTime")
    private String procurementTime;
    
    @Property("publishDate")
    private String publishDate;
    
    @Property("sourceUrl")
    private String sourceUrl;
    
    @Property("procurementPolicy")
    private String procurementPolicy;
    
    @Property("remarks")
    private String remarks;
    
    @Property("rawContent")
    private String rawContent;
    
    @Property("aiAnalysis")
    private String aiAnalysis; // AI分析结果
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    @Property("updatedAt")
    private LocalDateTime updatedAt;
    
    // 与采购单位的关系
    @Relationship(type = "PROCURED_BY", direction = Relationship.Direction.OUTGOING)
    private ProcurementOrganization organization;
    
    // 与供应商的关系（如果有中标信息）
    @Relationship(type = "AWARDED_TO", direction = Relationship.Direction.OUTGOING)
    private List<Supplier> suppliers;
    
    // 与项目类别的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private ProjectCategory category;
}
