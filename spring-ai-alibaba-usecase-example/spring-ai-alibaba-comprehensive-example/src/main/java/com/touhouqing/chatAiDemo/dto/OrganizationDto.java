package com.touhouqing.chatAiDemo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 组织机构数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDto {

    /**
     * 机构名称
     */
    private String organizationName;

    /**
     * 机构代码
     */
    private String organizationCode;

    /**
     * 机构类型
     */
    private String organizationType;

    /**
     * 行政级别
     */
    private String administrativeLevel;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 网站
     */
    private String website;

    /**
     * 联系信息（综合）
     */
    private String contactInfo;
}