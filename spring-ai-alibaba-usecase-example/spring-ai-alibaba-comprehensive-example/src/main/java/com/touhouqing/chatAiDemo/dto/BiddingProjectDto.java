package com.touhouqing.chatAiDemo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 招标项目数据传输对象
 * 用于AI解析后的结构化数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiddingProjectDto {

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 项目描述
     */
    private String projectDescription;

    /**
     * 项目需求详情
     */
    private String requirements;

    /**
     * 预算金额
     */
    private Double budget;

    /**
     * 预算单位
     */
    private String budgetUnit;

    /**
     * 招标类型
     */
    private String biddingType;

    /**
     * 项目状态
     */
    private String projectStatus;

    /**
     * 发布日期
     */
    private LocalDateTime publishDate;

    /**
     * 投标截止日期
     */
    private LocalDateTime biddingDeadline;

    /**
     * 来源URL
     */
    private String sourceUrl;

    /**
     * 来源网站
     */
    private String sourceWebsite;

    /**
     * 地区
     */
    private String region;

    /**
     * 行业分类
     */
    private String industry;

    /**
     * 发布机构信息
     */
    private OrganizationDto publishingOrganization;

    /**
     * 中标企业信息
     */
    private List<CompanyDto> awardedCompanies;

    /**
     * 参与投标企业信息
     */
    private List<CompanyDto> participatingCompanies;

    /**
     * 原始HTML内容
     */
    private String rawHtmlContent;
}