package com.touhouqing.chatAiDemo.repository.graph;

import com.touhouqing.chatAiDemo.entity.graph.Organization;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 组织机构图数据库Repository
 */
@Repository
public interface OrganizationRepository extends Neo4jRepository<Organization, Long> {

    /**
     * 根据机构名称查找
     */
    @Query("MATCH (o:Organization {organizationName: $organizationName}) RETURN o")
    Optional<Organization> findByOrganizationName(String organizationName);

    /**
     * 根据机构代码查找
     */
    @Query("MATCH (o:Organization {organizationCode: $organizationCode}) RETURN o")
    Optional<Organization> findByOrganizationCode(String organizationCode);

    /**
     * 根据机构类型查找
     */
    @Query("MATCH (o:Organization {organizationType: $organizationType}) RETURN o")
    List<Organization> findByOrganizationType(String organizationType);

    /**
     * 根据行政级别查找
     */
    @Query("MATCH (o:Organization {administrativeLevel: $administrativeLevel}) RETURN o")
    List<Organization> findByAdministrativeLevel(String administrativeLevel);

    /**
     * 查找机构发布的所有项目
     */
    @Query("MATCH (o:Organization)<-[:PUBLISHED_BY]-(p:BiddingProject) WHERE o.id = $organizationId RETURN p")
    List<Object> findPublishedProjects(Long organizationId);

    /**
     * 根据地区查找机构
     */
    @Query("MATCH (o:Organization)-[:LOCATED_IN]->(r:Region {regionName: $regionName}) RETURN o")
    List<Organization> findByRegion(String regionName);

    /**
     * 查找机构及其所有关联信息
     */
    @Query("MATCH (o:Organization) WHERE o.id = $organizationId " +
           "OPTIONAL MATCH (o)-[:LOCATED_IN]->(r:Region) " +
           "OPTIONAL MATCH (o)-[:BELONGS_TO_INDUSTRY]->(i:Industry) " +
           "OPTIONAL MATCH (o)<-[:PUBLISHED_BY]-(p:BiddingProject) " +
           "RETURN o, r, i, collect(p)")
    Optional<Organization> findOrganizationWithAllRelations(Long organizationId);
}