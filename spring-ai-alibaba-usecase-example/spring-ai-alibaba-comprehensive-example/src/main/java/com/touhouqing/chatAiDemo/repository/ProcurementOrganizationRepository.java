package com.touhouqing.chatAiDemo.repository;

import com.touhouqing.chatAiDemo.entity.ProcurementOrganization;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcurementOrganizationRepository extends Neo4jRepository<ProcurementOrganization, Long> {
    
    // 根据名称查找组织
    @Query("MATCH (o:ProcurementOrganization {name: $name}) RETURN o")
    Optional<ProcurementOrganization> findByName(String name);
    
    // 根据类型查找组织
    @Query("MATCH (o:ProcurementOrganization {type: $type}) RETURN o")
    List<ProcurementOrganization> findByType(String type);
    
    // 根据级别查找组织
    @Query("MATCH (o:ProcurementOrganization {level: $level}) RETURN o")
    List<ProcurementOrganization> findByLevel(String level);
    
    // 查找组织及其所有项目
    @Query("MATCH (o:ProcurementOrganization)-[:PROCURED_BY]-(p:ProcurementProject) WHERE o.name = $name RETURN o, collect(p)")
    Optional<ProcurementOrganization> findOrganizationWithProjects(String name);
    
    // 查找组织的项目统计
    @Query("MATCH (o:ProcurementOrganization)-[:PROCURED_BY]-(p:ProcurementProject) " +
           "WHERE o.name = $name " +
           "RETURN o.name as organizationName, count(p) as projectCount, sum(p.budget) as totalBudget")
    List<Object> getOrganizationStatistics(String name);
    
    // 查找最活跃的采购单位
    @Query("MATCH (o:ProcurementOrganization)-[:PROCURED_BY]-(p:ProcurementProject) " +
           "RETURN o, count(p) as projectCount " +
           "ORDER BY projectCount DESC LIMIT $limit")
    List<Object> findMostActiveOrganizations(int limit);
}
