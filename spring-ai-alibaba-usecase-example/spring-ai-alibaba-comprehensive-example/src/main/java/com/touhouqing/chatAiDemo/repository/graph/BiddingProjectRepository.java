package com.touhouqing.chatAiDemo.repository.graph;

import com.touhouqing.chatAiDemo.entity.graph.BiddingProject;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 招标项目图数据库Repository
 */
@Repository
public interface BiddingProjectRepository extends Neo4jRepository<BiddingProject, Long> {

    /**
     * 根据来源URL查找项目
     */
    @Query("MATCH (p:BiddingProject {sourceUrl: $sourceUrl}) RETURN p")
    Optional<BiddingProject> findBySourceUrl(String sourceUrl);

    /**
     * 检查项目是否存在
     */
    @Query("MATCH (p:BiddingProject {sourceUrl: $sourceUrl}) RETURN count(p) > 0")
    boolean existsBySourceUrl(String sourceUrl);

    /**
     * 根据项目名称搜索
     */
    @Query("MATCH (p:BiddingProject) WHERE p.projectName CONTAINS $keyword RETURN p")
    List<BiddingProject> findByProjectNameContaining(String keyword);

    /**
     * 根据地区查找项目
     */
    @Query("MATCH (p:BiddingProject {region: $region}) RETURN p")
    List<BiddingProject> findByRegion(String region);

    /**
     * 根据行业查找项目
     */
    @Query("MATCH (p:BiddingProject {industry: $industry}) RETURN p")
    List<BiddingProject> findByIndustry(String industry);

    /**
     * 根据项目状态查找
     */
    @Query("MATCH (p:BiddingProject {projectStatus: $status}) RETURN p")
    List<BiddingProject> findByProjectStatus(String status);

    /**
     * 查找项目及其关联的发布机构
     */
    @Query("MATCH (p:BiddingProject)-[:PUBLISHED_BY]->(o:Organization) WHERE p.id = $projectId RETURN p, o")
    Optional<BiddingProject> findProjectWithOrganization(Long projectId);

    /**
     * 查找项目及其所有关联实体
     */
    @Query("MATCH (p:BiddingProject) WHERE p.id = $projectId " +
           "OPTIONAL MATCH (p)-[:PUBLISHED_BY]->(o:Organization) " +
           "OPTIONAL MATCH (p)-[:AWARDED_TO]->(c:Company) " +
           "OPTIONAL MATCH (p)-[:PARTICIPATED_BY]->(pc:Company) " +
           "RETURN p, o, collect(c), collect(pc)")
    Optional<BiddingProject> findProjectWithAllRelations(Long projectId);

    /**
     * 根据预算范围查找项目
     */
    @Query("MATCH (p:BiddingProject) WHERE p.budget >= $minBudget AND p.budget <= $maxBudget RETURN p")
    List<BiddingProject> findByBudgetRange(Double minBudget, Double maxBudget);

    /**
     * 查找最近的项目
     */
    @Query("MATCH (p:BiddingProject) RETURN p ORDER BY p.publishDate DESC LIMIT $limit")
    List<BiddingProject> findRecentProjects(int limit);
}