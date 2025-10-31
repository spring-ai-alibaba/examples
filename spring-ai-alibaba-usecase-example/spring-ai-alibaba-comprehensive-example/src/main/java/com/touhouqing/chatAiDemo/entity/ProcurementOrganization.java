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
 * 采购单位实体
 */
@Node("ProcurementOrganization")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcurementOrganization {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("name")
    private String name;
    
    @Property("type")
    private String type; // 医院、学校、政府机关等
    
    @Property("level")
    private String level; // 市级、区级、国家级等
    
    @Property("address")
    private String address;
    
    @Property("contact")
    private String contact;
    
    @Property("description")
    private String description;
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    @Property("updatedAt")
    private LocalDateTime updatedAt;
    
    // 与采购项目的关系
    @Relationship(type = "PROCURED_BY", direction = Relationship.Direction.INCOMING)
    private List<ProcurementProject> projects;
    
    // 与上级单位的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private ProcurementOrganization parentOrganization;
    
    // 与下级单位的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.INCOMING)
    private List<ProcurementOrganization> subOrganizations;
}
