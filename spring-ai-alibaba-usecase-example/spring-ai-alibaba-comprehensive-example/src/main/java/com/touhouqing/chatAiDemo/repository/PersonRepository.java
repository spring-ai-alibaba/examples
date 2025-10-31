package com.touhouqing.chatAiDemo.repository;

import com.touhouqing.chatAiDemo.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, String> {
    // 添加自定义Cypher查询
    @Query("MATCH (p:Person {name: $name}) RETURN p")
    List<Person> findByName(String name);
}
