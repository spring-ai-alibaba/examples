package com.touhouqing.chatAiDemo.entity.graph;

import org.springframework.data.neo4j.core.schema.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 招标项目实体 - 图数据库核心节点
 */
@Node("BiddingProject")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiddingProject {

    @Id
    @GeneratedValue
    private Long id;

    @Property("projectName")
    private String projectName;

    @Property("projectCode")
    private String projectCode;

    @Property("budget")
    private Double budget;

    @Property("budgetUnit")
    private String budgetUnit;

    @Property("biddingType")
    private String biddingType; // 公开招标、邀请招标、竞争性谈判等

    @Property("projectStatus")
    private String projectStatus; // 招标中、已开标、已中标等

    @Property("publishDate")
    private LocalDateTime publishDate;

    @Property("biddingDeadline")
    private LocalDateTime biddingDeadline;

    @Property("sourceUrl")
    private String sourceUrl;

    @Property("sourceWebsite")
    private String sourceWebsite; // 来源网站

    @Property("region")
    private String region; // 地区

    @Property("industry")
    private String industry; // 行业分类

    @Property("vectorId")
    private String vectorId; // 关联向量数据库的ID

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("updatedAt")
    private LocalDateTime updatedAt;

    // 与采购单位的关系
    @Relationship(type = "PUBLISHED_BY", direction = Relationship.Direction.OUTGOING)
    private Organization publishingOrganization;

    // 与中标企业的关系
    @Relationship(type = "AWARDED_TO", direction = Relationship.Direction.OUTGOING)
    private List<Company> awardedCompanies;

    // 与参与投标企业的关系
    @Relationship(type = "PARTICIPATED_BY", direction = Relationship.Direction.OUTGOING)
    private List<Company> participatingCompanies;

    // 与项目类别的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private ProjectCategory category;

    // 与地区的关系
    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.OUTGOING)
    private Region projectRegion;
}