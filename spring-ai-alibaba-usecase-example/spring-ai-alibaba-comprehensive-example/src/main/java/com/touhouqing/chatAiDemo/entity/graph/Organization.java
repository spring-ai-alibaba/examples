package com.touhouqing.chatAiDemo.entity.graph;

import org.springframework.data.neo4j.core.schema.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购组织/机构实体
 */
@Node("Organization")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue
    private Long id;

    @Property("organizationName")
    private String organizationName;

    @Property("organizationCode")
    private String organizationCode; // 组织机构代码

    @Property("organizationType")
    private String organizationType; // 政府机关、事业单位、国有企业等

    @Property("administrativeLevel")
    private String administrativeLevel; // 国家级、省级、市级、县级等

    @Property("address")
    private String address;

    @Property("contactPerson")
    private String contactPerson;

    @Property("contactPhone")
    private String contactPhone;

    @Property("email")
    private String email;

    @Property("website")
    private String website;

    @Property("vectorId")
    private String vectorId; // 关联向量数据库的ID

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("updatedAt")
    private LocalDateTime updatedAt;

    // 与发布项目的关系
    @Relationship(type = "PUBLISHED_BY", direction = Relationship.Direction.INCOMING)
    private List<BiddingProject> publishedProjects;

    // 与地区的关系
    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.OUTGOING)
    private Region location;

    // 与行业的关系
    @Relationship(type = "BELONGS_TO_INDUSTRY", direction = Relationship.Direction.OUTGOING)
    private Industry industry;
}