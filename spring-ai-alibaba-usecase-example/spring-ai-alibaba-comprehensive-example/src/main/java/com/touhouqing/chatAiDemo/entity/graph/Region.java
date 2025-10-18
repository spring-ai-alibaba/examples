package com.touhouqing.chatAiDemo.entity.graph;

import org.springframework.data.neo4j.core.schema.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地区实体
 */
@Node("Region")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    @Id
    @GeneratedValue
    private Long id;

    @Property("regionName")
    private String regionName;

    @Property("regionCode")
    private String regionCode; // 行政区划代码

    @Property("regionLevel")
    private String regionLevel; // 省、市、县、区等

    @Property("parentRegionCode")
    private String parentRegionCode; // 上级区域代码

    @Property("longitude")
    private Double longitude; // 经度

    @Property("latitude")
    private Double latitude; // 纬度

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("updatedAt")
    private LocalDateTime updatedAt;

    // 与项目的关系
    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.INCOMING)
    private List<BiddingProject> projects;

    // 与企业的关系
    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.INCOMING)
    private List<Company> companies;

    // 与组织的关系
    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.INCOMING)
    private List<Organization> organizations;

    // 与上级地区的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private Region parentRegion;

    // 与下级地区的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.INCOMING)
    private List<Region> subRegions;
}