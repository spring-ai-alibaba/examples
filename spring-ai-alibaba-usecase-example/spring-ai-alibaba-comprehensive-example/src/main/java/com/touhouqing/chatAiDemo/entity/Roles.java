package com.touhouqing.chatAiDemo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@Data
@NoArgsConstructor
@RelationshipProperties
public class Roles {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private Person person;

    private String roleName;

    public Roles(Person person, String roleName) {
        this.person = person;
        this.roleName = roleName;
    }
}
