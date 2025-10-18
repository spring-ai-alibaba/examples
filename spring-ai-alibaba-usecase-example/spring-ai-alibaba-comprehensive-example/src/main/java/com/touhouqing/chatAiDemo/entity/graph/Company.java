package com.touhouqing.chatAiDemo.entity.graph;

import org.springframework.data.neo4j.core.schema.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 企业实体 - 图数据库核心节点
 */
@Node("Company")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue
    private Long id;

    @Property("companyName")
    private String companyName;

    @Property("unifiedSocialCreditCode")
    private String unifiedSocialCreditCode; // 统一社会信用代码

    @Property("legalRepresentative")
    private String legalRepresentative; // 法定代表人

    @Property("registeredCapital")
    private Double registeredCapital; // 注册资本

    @Property("establishmentDate")
    private LocalDateTime establishmentDate; // 成立日期

    @Property("companyType")
    private String companyType; // 企业类型

    @Property("businessScope")
    private String businessScope; // 经营范围

    @Property("registeredAddress")
    private String registeredAddress; // 注册地址

    @Property("contactPhone")
    private String contactPhone;

    @Property("email")
    private String email;

    @Property("website")
    private String website;

    @Property("companyScale")
    private String companyScale; // 企业规模：大型、中型、小型、微型

    @Property("creditRating")
    private String creditRating; // 信用等级

    @Property("vectorId")
    private String vectorId; // 关联向量数据库的ID

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("updatedAt")
    private LocalDateTime updatedAt;

    // 与中标项目的关系
    @Relationship(type = "AWARDED_TO", direction = Relationship.Direction.INCOMING)
    private List<BiddingProject> awardedProjects;

    // 与参与项目的关系
    @Relationship(type = "PARTICIPATED_BY", direction = Relationship.Direction.INCOMING)
    private List<BiddingProject> participatedProjects;

    // 与行业的关系
    @Relationship(type = "OPERATES_IN", direction = Relationship.Direction.OUTGOING)
    private List<Industry> industries;

    // 与地区的关系
    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.OUTGOING)
    private Region location;

    // 企业间合作关系
    @Relationship(type = "COOPERATES_WITH", direction = Relationship.Direction.OUTGOING)
    private List<Company> partners;
}