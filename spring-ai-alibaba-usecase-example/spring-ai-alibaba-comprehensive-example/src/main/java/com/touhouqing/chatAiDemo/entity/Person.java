package com.touhouqing.chatAiDemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Person {
    @Id
    private String name;

    private Integer born;
}
