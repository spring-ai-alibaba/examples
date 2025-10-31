package com.touhouqing.chatAiDemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Movie {
    @Id
    private String title;

    @Property//属性
    private String description;

    @Relationship(type = "ACTED_IN", direction = Relationship.Direction.INCOMING)
    private List<Roles> actorsAndRoles = new ArrayList<>();

    @Relationship(type = "DIRECTED", direction = Relationship.Direction.INCOMING)
    private List<Person> directors = new ArrayList<>();

    public Movie(String title, String description) {
        this.title = title;
        this.description = description;
        this.actorsAndRoles = new ArrayList<>();
        this.directors = new ArrayList<>();
    }
}
