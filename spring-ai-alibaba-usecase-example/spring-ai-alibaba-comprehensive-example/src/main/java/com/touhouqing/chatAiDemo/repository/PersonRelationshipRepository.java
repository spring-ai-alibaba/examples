package com.touhouqing.chatAiDemo.repository;

import com.touhouqing.chatAiDemo.entity.PersonRelationship;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PersonRelationshipRepository extends Neo4jRepository<PersonRelationship, Long> {
}
