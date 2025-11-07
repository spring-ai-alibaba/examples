/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.touhouqing.chatAiDemo.repository;

import com.touhouqing.chatAiDemo.entity.ProcurementProject;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcurementProjectRepository extends Neo4jRepository<ProcurementProject, Long> {
    
    // 根据项目名称查找
    @Query("MATCH (p:ProcurementProject {projectName: $projectName}) RETURN p")
    Optional<ProcurementProject> findByProjectName(String projectName);
    
    // 根据源URL查找
    @Query("MATCH (p:ProcurementProject {sourceUrl: $sourceUrl}) RETURN p")
    Optional<ProcurementProject> findBySourceUrl(String sourceUrl);
    
    // 根据预算范围查找项目
    @Query("MATCH (p:ProcurementProject) WHERE p.budget >= $minBudget AND p.budget <= $maxBudget RETURN p")
    List<ProcurementProject> findByBudgetRange(Double minBudget, Double maxBudget);
    
    // 根据采购单位查找项目
    @Query("MATCH (p:ProcurementProject)-[:PROCURED_BY]->(o:ProcurementOrganization {name: $organizationName}) RETURN p")
    List<ProcurementProject> findByOrganizationName(String organizationName);
    
    // 根据项目类别查找项目
    @Query("MATCH (p:ProcurementProject)-[:BELONGS_TO]->(c:ProjectCategory {name: $categoryName}) RETURN p")
    List<ProcurementProject> findByCategoryName(String categoryName);
    
    // 查找最近的项目
    @Query("MATCH (p:ProcurementProject) RETURN p ORDER BY p.createdAt DESC LIMIT $limit")
    List<ProcurementProject> findRecentProjects(int limit);
    
    // 根据关键词搜索项目
    @Query("MATCH (p:ProcurementProject) WHERE p.projectName CONTAINS $keyword OR p.description CONTAINS $keyword RETURN p")
    List<ProcurementProject> searchByKeyword(String keyword);
    
    // 获取项目及其关联的采购单位
    @Query("MATCH (p:ProcurementProject)-[:PROCURED_BY]->(o:ProcurementOrganization) WHERE p.id = $projectId RETURN p, o")
    Optional<ProcurementProject> findProjectWithOrganization(Long projectId);
    
    // 获取项目的完整关系图
    @Query("MATCH (p:ProcurementProject) WHERE p.id = $projectId " +
           "OPTIONAL MATCH (p)-[:PROCURED_BY]->(o:ProcurementOrganization) " +
           "OPTIONAL MATCH (p)-[:BELONGS_TO]->(c:ProjectCategory) " +
           "OPTIONAL MATCH (p)-[:AWARDED_TO]->(s:Supplier) " +
           "RETURN p, o, c, collect(s)")
    Optional<ProcurementProject> findProjectWithAllRelations(Long projectId);
}
