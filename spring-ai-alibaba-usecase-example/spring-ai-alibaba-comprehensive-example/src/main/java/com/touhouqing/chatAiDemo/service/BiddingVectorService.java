package com.touhouqing.chatAiDemo.service;

import com.touhouqing.chatAiDemo.dto.BiddingProjectDto;
import com.touhouqing.chatAiDemo.entity.graph.BiddingProject;
import com.touhouqing.chatAiDemo.entity.vector.VectorDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 招标向量数据库服务
 * 负责将招标数据存储到向量数据库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingVectorService {

    private final VectorStore vectorStore;

    /**
     * 保存招标项目到向量数据库
     */
    public void saveBiddingProjectVector(BiddingProject project, BiddingProjectDto dto, String htmlContent) {
        try {
            List<Document> documents = new ArrayList<>();

            // 1. 项目描述文档
            if (dto.getProjectDescription() != null && !dto.getProjectDescription().trim().isEmpty()) {
                Document projectDoc = createProjectDescriptionDocument(project, dto);
                documents.add(projectDoc);
            }

            // 2. 项目需求文档
            if (dto.getRequirements() != null && !dto.getRequirements().trim().isEmpty()) {
                Document requirementDoc = createProjectRequirementDocument(project, dto);
                documents.add(requirementDoc);
            }

            // 3. 招标公告文档（基于HTML内容提取的文本）
            Document announcementDoc = createAnnouncementDocument(project, dto, htmlContent);
            documents.add(announcementDoc);

            // 4. 企业相关文档（如果有中标企业信息）
            if (dto.getAwardedCompanies() != null && !dto.getAwardedCompanies().isEmpty()) {
                Document companyDoc = createCompanyDocument(project, dto);
                documents.add(companyDoc);
            }

            // 批量保存到向量数据库
            vectorStore.add(documents);

            log.info("成功保存 {} 个文档到向量数据库，项目: {}", documents.size(), project.getProjectName());

        } catch (Exception e) {
            log.error("保存向量数据失败", e);
            throw new RuntimeException("保存向量数据失败", e);
        }
    }

    /**
     * 创建项目描述文档
     */
    private Document createProjectDescriptionDocument(BiddingProject project, BiddingProjectDto dto) {
        String content = String.format("""
            项目名称：%s
            项目编号：%s
            项目描述：%s
            预算：%s %s
            招标类型：%s
            项目状态：%s
            地区：%s
            行业：%s
            """,
            project.getProjectName(),
            project.getProjectCode(),
            dto.getProjectDescription(),
            project.getBudget(),
            project.getBudgetUnit(),
            project.getBiddingType(),
            project.getProjectStatus(),
            project.getRegion(),
            project.getIndustry()
        );

        Map<String, Object> metadata = createBaseMetadata(project, VectorDocument.DocumentType.PROJECT_DESCRIPTION);

        Document document = new Document(content, metadata);
        return document;
    }

    /**
     * 创建项目需求文档
     */
    private Document createProjectRequirementDocument(BiddingProject project, BiddingProjectDto dto) {
        String content = String.format("""
            项目名称：%s
            项目需求：%s
            技术要求：%s
            """,
            project.getProjectName(),
            dto.getRequirements(),
            dto.getRequirements()
        );

        Map<String, Object> metadata = createBaseMetadata(project, VectorDocument.DocumentType.PROJECT_REQUIREMENTS);

        return new Document(content, metadata);
    }

    /**
     * 创建招标公告文档
     */
    private Document createAnnouncementDocument(BiddingProject project, BiddingProjectDto dto, String htmlContent) {
        // 从HTML中提取纯文本内容
        String textContent = extractTextFromHtml(htmlContent);

        String content = String.format("""
            招标公告
            项目名称：%s
            发布机构：%s
            公告内容：%s
            """,
            project.getProjectName(),
            dto.getPublishingOrganization() != null ? dto.getPublishingOrganization().getOrganizationName() : "未知",
            textContent.length() > 2000 ? textContent.substring(0, 2000) + "..." : textContent
        );

        Map<String, Object> metadata = createBaseMetadata(project, VectorDocument.DocumentType.BIDDING_ANNOUNCEMENT);

        return new Document(content, metadata);
    }

    /**
     * 创建企业相关文档
     */
    private Document createCompanyDocument(BiddingProject project, BiddingProjectDto dto) {
        StringBuilder content = new StringBuilder();
        content.append("项目名称：").append(project.getProjectName()).append("\n");
        content.append("中标企业信息：\n");

        dto.getAwardedCompanies().forEach(company -> {
            content.append("企业名称：").append(company.getCompanyName()).append("\n");
            content.append("中标金额：").append(company.getAwardAmount()).append("\n");
        });

        Map<String, Object> metadata = createBaseMetadata(project, VectorDocument.DocumentType.AWARD_RESULT);

        return new Document(content.toString(), metadata);
    }

    /**
     * 创建基础元数据
     * 确保所有值都不为null，向量数据库不允许null值
     */
    private Map<String, Object> createBaseMetadata(BiddingProject project, VectorDocument.DocumentType documentType) {
        Map<String, Object> metadata = new HashMap<>();

        // 使用安全的方法添加metadata，避免null值
        putIfNotNull(metadata, "graph_entity_id", project.getId());
        putIfNotNull(metadata, "graph_entity_type", "BiddingProject");
        putIfNotNull(metadata, "document_type", documentType.name());
        putIfNotNull(metadata, "project_name", project.getProjectName());
        putIfNotNull(metadata, "source_url", project.getSourceUrl());
        putIfNotNull(metadata, "source_website", project.getSourceWebsite());
        putIfNotNull(metadata, "region", project.getRegion());
        putIfNotNull(metadata, "industry", project.getIndustry());
        putIfNotNull(metadata, "project_status", project.getProjectStatus());
        putIfNotNull(metadata, "created_at", LocalDateTime.now().toString());

        return metadata;
    }

    /**
     * 安全地添加metadata，如果值为null则使用默认值
     */
    private void putIfNotNull(Map<String, Object> metadata, String key, Object value) {
        if (value != null) {
            metadata.put(key, value);
        } else {
            // 为null值提供默认值
            switch (key) {
                case "project_name" -> metadata.put(key, "未知项目");
                case "source_url" -> metadata.put(key, "");
                case "source_website" -> metadata.put(key, "未知网站");
                case "region" -> metadata.put(key, "未知地区");
                case "industry" -> metadata.put(key, "未知行业");
                case "project_status" -> metadata.put(key, "未知状态");
                default -> metadata.put(key, "");
            }
        }
    }

    /**
     * 从HTML中提取纯文本
     */
    private String extractTextFromHtml(String htmlContent) {
        if (htmlContent == null) return "";

        // 简单的HTML标签移除（实际项目中可以使用Jsoup等库）
        return htmlContent
                .replaceAll("<[^>]+>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}