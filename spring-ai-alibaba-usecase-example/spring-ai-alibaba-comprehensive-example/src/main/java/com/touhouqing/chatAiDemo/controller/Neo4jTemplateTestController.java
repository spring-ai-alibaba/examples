package com.touhouqing.chatAiDemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Neo4jTemplate测试控制器
 */
@RestController
@RequestMapping("/test")
public class Neo4jTemplateTestController {

    @Autowired(required = false)
    private Neo4jTemplate neo4jTemplate;

    @GetMapping("/neo4j-template")
    public String testNeo4jTemplate() {
        if (neo4jTemplate == null) {
            return "Neo4jTemplate bean 未找到";
        }
        
        try {
            // 执行一个简单的查询来测试连接
            Long count = neo4jTemplate.count("MATCH (n) RETURN count(n)");
            return "Neo4jTemplate 工作正常! 节点数量: " + count;
        } catch (Exception e) {
            return "Neo4jTemplate 连接失败: " + e.getMessage();
        }
    }
}
