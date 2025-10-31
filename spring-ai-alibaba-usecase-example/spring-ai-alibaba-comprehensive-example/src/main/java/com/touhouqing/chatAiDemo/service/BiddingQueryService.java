package com.touhouqing.chatAiDemo.service;

import com.touhouqing.chatAiDemo.entity.graph.BiddingProject;
import com.touhouqing.chatAiDemo.repository.graph.BiddingProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 招标查询服务
 * 实现图数据库与向量数据库的关联查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingQueryService {

    private final VectorStore vectorStore;
    private final BiddingProjectRepository projectRepository;

    /**
     * 语义搜索招标项目
     * 结合向量搜索和图关系查询
     */
    public List<BiddingProject> semanticSearch(String query, int topK, double similarityThreshold) {
        try {
            // 1. 向量搜索
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(topK)
                    .similarityThreshold(similarityThreshold)
                    .build();

            List<Document> vectorResults = vectorStore.similaritySearch(searchRequest);

            // 2. 通过向量搜索结果获取图数据库实体ID
            List<Long> projectIds = vectorResults.stream()
                    .map(doc -> doc.getMetadata().get("graph_entity_id"))
                    .filter(id -> id != null)
                    .map(id -> Long.valueOf(id.toString()))
                    .distinct()
                    .collect(Collectors.toList());

            // 3. 从图数据库获取完整的项目信息
            List<BiddingProject> projects = new ArrayList<>();
            for (Long projectId : projectIds) {
                Optional<BiddingProject> project = projectRepository.findById(projectId);
                project.ifPresent(projects::add);
            }

            log.info("语义搜索完成，查询: {}, 找到 {} 个相关项目", query, projects.size());
            return projects;

        } catch (Exception e) {
            log.error("语义搜索失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据企业查找相关项目
     * 通过图关系扩展搜索结果
     */
    public List<BiddingProject> findProjectsByCompany(String companyName) {
        try {
            // 1. 先进行语义搜索找到相关的企业信息
            List<Document> vectorResults = vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query("企业名称：" + companyName)
                    .topK(10)
                    .similarityThreshold(0.3)
                    .build()
            );

            // 2. 获取项目ID
            List<Long> projectIds = vectorResults.stream()
                    .map(doc -> doc.getMetadata().get("graph_entity_id"))
                    .filter(id -> id != null)
                    .map(id -> Long.valueOf(id.toString()))
                    .distinct()
                    .collect(Collectors.toList());

            // 3. 通过图数据库查询扩展相关项目
            List<BiddingProject> projects = new ArrayList<>();
            for (Long projectId : projectIds) {
                Optional<BiddingProject> project = projectRepository.findProjectWithAllRelations(projectId);
                project.ifPresent(projects::add);
            }

            return projects;

        } catch (Exception e) {
            log.error("根据企业查找项目失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 智能推荐相似项目
     * 基于项目内容的语义相似度
     */
    public List<BiddingProject> recommendSimilarProjects(Long projectId, int limit) {
        try {
            // 1. 获取原项目信息
            Optional<BiddingProject> originalProject = projectRepository.findById(projectId);
            if (originalProject.isEmpty()) {
                return new ArrayList<>();
            }

            BiddingProject project = originalProject.get();

            // 2. 构建查询文本
            String queryText = String.format("%s %s %s %s",
                project.getProjectName(),
                project.getIndustry(),
                project.getRegion(),
                project.getBiddingType()
            );

            // 3. 向量搜索相似项目
            List<Document> vectorResults = vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(queryText)
                    .topK(limit + 1) // +1 因为可能包含自己
                    .similarityThreshold(0.4)
                    .build()
            );

            // 4. 过滤掉原项目，获取其他相似项目
            List<BiddingProject> similarProjects = vectorResults.stream()
                    .map(doc -> doc.getMetadata().get("graph_entity_id"))
                    .filter(id -> id != null)
                    .map(id -> Long.valueOf(id.toString()))
                    .filter(id -> !id.equals(projectId)) // 排除原项目
                    .distinct()
                    .limit(limit)
                    .map(id -> projectRepository.findById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            return similarProjects;

        } catch (Exception e) {
            log.error("推荐相似项目失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 复合查询：结合多个条件进行搜索
     */
    public List<BiddingProject> complexSearch(String semanticQuery, String region, String industry,
                                            Double minBudget, Double maxBudget) {
        try {
            List<BiddingProject> results = new ArrayList<>();

            // 1. 如果有语义查询，先进行向量搜索
            if (semanticQuery != null && !semanticQuery.trim().isEmpty()) {
                results = semanticSearch(semanticQuery, 50, 0.3);
            } else {
                // 否则获取所有项目
                results = projectRepository.findAll();
            }

            // 2. 应用图数据库过滤条件
            return results.stream()
                    .filter(project -> region == null || region.equals(project.getRegion()))
                    .filter(project -> industry == null || industry.equals(project.getIndustry()))
                    .filter(project -> minBudget == null || project.getBudget() == null || project.getBudget() >= minBudget)
                    .filter(project -> maxBudget == null || project.getBudget() == null || project.getBudget() <= maxBudget)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("复合查询失败", e);
            return new ArrayList<>();
        }
    }
}