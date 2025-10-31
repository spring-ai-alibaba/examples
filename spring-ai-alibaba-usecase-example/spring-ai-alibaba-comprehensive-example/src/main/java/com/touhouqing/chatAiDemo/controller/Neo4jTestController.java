package com.touhouqing.chatAiDemo.controller;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Neo4j连接测试控制器
 */
@RestController
@RequestMapping("/test")
public class Neo4jTestController {

    @Autowired
    private Driver driver;

    @Value("${spring.neo4j.uri}")
    private String neo4jUri;

    @Value("${spring.neo4j.authentication.username}")
    private String neo4jUsername;

    @GetMapping("/neo4j")
    public String testNeo4jConnection() {
        try (Session session = driver.session()) {
            Result result = session.run("RETURN 'Neo4j连接成功!' as message");
            if (result.hasNext()) {
                return "Neo4j连接成功! URI: " + neo4jUri + ", Username: " + neo4jUsername +
                       ", Message: " + result.next().get("message").asString();
            }
            return "Neo4j连接成功，但没有返回数据";
        } catch (Exception e) {
            return "Neo4j连接失败: " + e.getMessage() +
                   ", URI: " + neo4jUri + ", Username: " + neo4jUsername;
        }
    }
}
