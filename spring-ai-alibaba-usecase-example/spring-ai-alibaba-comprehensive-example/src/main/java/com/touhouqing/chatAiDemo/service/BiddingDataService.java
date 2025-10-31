package com.touhouqing.chatAiDemo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.touhouqing.chatAiDemo.dto.BiddingProjectDto;
import com.touhouqing.chatAiDemo.entity.graph.*;
import com.touhouqing.chatAiDemo.repository.graph.BiddingProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 招标数据服务
 * 负责将AI解析的结构化数据保存到图数据库和向量数据库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingDataService {

    private final BiddingProjectRepository projectRepository;
    private final BiddingVectorService vectorService;
    private final ObjectMapper objectMapper;

    /**
     * 检查项目是否已存在
     */
    public boolean existsBySourceUrl(String sourceUrl) {
        return projectRepository.existsBySourceUrl(sourceUrl);
    }

    /**
     * 保存招标数据到图数据库和向量数据库
     */
    @Transactional
    public void saveBiddingData(String structuredData, String htmlContent, String sourceUrl, Long crawlTime) {
        try {
            // 预处理JSON数据，修复日期格式问题
            String processedData = preprocessJsonData(structuredData);

            // 解析AI返回的JSON数据
            BiddingProjectDto dto = objectMapper.readValue(processedData, BiddingProjectDto.class);

            // 生成向量ID
            String vectorId = UUID.randomUUID().toString();

            // 创建图数据库实体
            BiddingProject project = createBiddingProject(dto, sourceUrl, vectorId, crawlTime);

            // 保存到图数据库
            BiddingProject savedProject = projectRepository.save(project);

            // 保存到向量数据库
            vectorService.saveBiddingProjectVector(savedProject, dto, htmlContent);

            log.info("成功保存招标项目到图数据库和向量数据库: {}", dto.getProjectName());

        } catch (Exception e) {
            log.error("保存招标数据失败", e);
            throw new RuntimeException("保存招标数据失败", e);
        }
    }

    /**
     * 创建招标项目实体
     */
    private BiddingProject createBiddingProject(BiddingProjectDto dto, String sourceUrl, String vectorId, Long crawlTime) {
        BiddingProject project = new BiddingProject();

        project.setProjectName(dto.getProjectName());
        project.setProjectCode(dto.getProjectCode());
        project.setBudget(dto.getBudget());
        project.setBudgetUnit(dto.getBudgetUnit());
        project.setBiddingType(dto.getBiddingType());
        project.setProjectStatus(dto.getProjectStatus());
        project.setSourceUrl(sourceUrl);
        project.setSourceWebsite(extractWebsiteName(sourceUrl));
        project.setRegion(dto.getRegion());
        project.setIndustry(dto.getIndustry());
        project.setVectorId(vectorId);

        // 解析日期
        if (dto.getPublishDate() != null) {
            project.setPublishDate(dto.getPublishDate());
        }
        if (dto.getBiddingDeadline() != null) {
            project.setBiddingDeadline(dto.getBiddingDeadline());
        }

        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        return project;
    }

    /**
     * 从URL提取网站名称
     */
    private String extractWebsiteName(String url) {
        try {
            if (url.contains("ccgp-tianjin.gov.cn")) {
                return "天津政府采购网";
            } else if (url.contains("ccgp.gov.cn")) {
                return "中国政府采购网";
            } else if (url.contains("ggzy")) {
                return "公共资源交易网";
            }
            return "未知网站";
        } catch (Exception e) {
            return "未知网站";
        }
    }

    /**
     * 预处理JSON数据，修复日期格式问题
     */
    private String preprocessJsonData(String jsonData) {
        try {
            // 修复日期格式：将 "2025-07-30" 转换为 "2025-07-30T00:00:00"
            String processed = jsonData.replaceAll(
                "\"publishDate\"\\s*:\\s*\"(\\d{4}-\\d{2}-\\d{2})\"",
                "\"publishDate\": \"$1T00:00:00\""
            );

            // 修复投标截止日期格式（如果有的话）
            processed = processed.replaceAll(
                "\"biddingDeadline\"\\s*:\\s*\"(\\d{4}-\\d{2}-\\d{2})\"",
                "\"biddingDeadline\": \"$1T23:59:59\""
            );

            log.debug("JSON预处理完成，原始: {}, 处理后: {}", jsonData, processed);
            return processed;

        } catch (Exception e) {
            log.warn("JSON预处理失败，使用原始数据: {}", e.getMessage());
            return jsonData;
        }
    }
}