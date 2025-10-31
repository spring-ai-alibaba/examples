package com.touhouqing.chatAiDemo.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

@Data
@RelationshipProperties
public class PersonRelationship {
    @Id
    @GeneratedValue
    private Long id;

    @Property//关系属性
    private String type;
}
