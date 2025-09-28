package com.touhouqing.chatAiDemo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 企业数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDto {

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 统一社会信用代码
     */
    private String unifiedSocialCreditCode;

    /**
     * 法定代表人
     */
    private String legalRepresentative;

    /**
     * 注册资本
     */
    private Double registeredCapital;

    /**
     * 企业类型
     */
    private String companyType;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 注册地址
     */
    private String registeredAddress;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 中标金额（用于中标企业）
     */
    private Double awardAmount;

    /**
     * 中标排名（用于中标企业）
     */
    private String awardRank;

    /**
     * 投标金额（用于参与投标企业）
     */
    private Double bidAmount;
}