package com.touhouqing.chatAiDemo.repository.graph;

import com.touhouqing.chatAiDemo.entity.graph.Company;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 企业图数据库Repository
 */
@Repository
public interface CompanyRepository extends Neo4jRepository<Company, Long> {

    /**
     * 根据企业名称查找
     */
    @Query("MATCH (c:Company {companyName: $companyName}) RETURN c")
    Optional<Company> findByCompanyName(String companyName);

    /**
     * 根据统一社会信用代码查找
     */
    @Query("MATCH (c:Company {unifiedSocialCreditCode: $code}) RETURN c")
    Optional<Company> findByUnifiedSocialCreditCode(String code);

    /**
     * 查找企业的所有中标项目
     */
    @Query("MATCH (c:Company)<-[:AWARDED_TO]-(p:BiddingProject) WHERE c.id = $companyId RETURN p")
    List<Object> findAwardedProjects(Long companyId);

    /**
     * 查找企业的所有参与项目
     */
    @Query("MATCH (c:Company)<-[:PARTICIPATED_BY]-(p:BiddingProject) WHERE c.id = $companyId RETURN p")
    List<Object> findParticipatedProjects(Long companyId);

    /**
     * 查找企业的合作伙伴
     */
    @Query("MATCH (c:Company)-[:COOPERATES_WITH]->(partner:Company) WHERE c.id = $companyId RETURN partner")
    List<Company> findPartners(Long companyId);

    /**
     * 根据地区查找企业
     */
    @Query("MATCH (c:Company)-[:LOCATED_IN]->(r:Region {regionName: $regionName}) RETURN c")
    List<Company> findByRegion(String regionName);

    /**
     * 根据行业查找企业
     */
    @Query("MATCH (c:Company)-[:OPERATES_IN]->(i:Industry {industryName: $industryName}) RETURN c")
    List<Company> findByIndustry(String industryName);

    /**
     * 查找企业及其所有关联信息
     */
    @Query("MATCH (c:Company) WHERE c.id = $companyId " +
           "OPTIONAL MATCH (c)-[:LOCATED_IN]->(r:Region) " +
           "OPTIONAL MATCH (c)-[:OPERATES_IN]->(i:Industry) " +
           "OPTIONAL MATCH (c)<-[:AWARDED_TO]-(ap:BiddingProject) " +
           "OPTIONAL MATCH (c)<-[:PARTICIPATED_BY]-(pp:BiddingProject) " +
           "RETURN c, r, collect(i), collect(ap), collect(pp)")
    Optional<Company> findCompanyWithAllRelations(Long companyId);

    /**
     * 根据企业规模查找
     */
    @Query("MATCH (c:Company {companyScale: $scale}) RETURN c")
    List<Company> findByCompanyScale(String scale);

    /**
     * 根据信用等级查找
     */
    @Query("MATCH (c:Company {creditRating: $rating}) RETURN c")
    List<Company> findByCreditRating(String rating);
}