package com.touhouqing.chatAiDemo.repository;

import com.touhouqing.chatAiDemo.entity.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends Neo4jRepository<Movie, String> {
    
    // 根据电影标题查找电影
    Optional<Movie> findByTitle(String title);
    
    // 查找某个演员参演的所有电影
    @Query("MATCH (p:Person)-[:ACTED_IN]->(m:Movie) WHERE p.name = $actorName RETURN m")
    List<Movie> findMoviesByActor(String actorName);
    
    // 查找某个导演执导的所有电影
    @Query("MATCH (p:Person)-[:DIRECTED]->(m:Movie) WHERE p.name = $directorName RETURN m")
    List<Movie> findMoviesByDirector(String directorName);
    
    // 查找电影及其所有演员和导演
    @Query("MATCH (m:Movie) WHERE m.title = $title " +
           "OPTIONAL MATCH (m)<-[:ACTED_IN]-(actor:Person) " +
           "OPTIONAL MATCH (m)<-[:DIRECTED]-(director:Person) " +
           "RETURN m, collect(DISTINCT actor) as actors, collect(DISTINCT director) as directors")
    List<Movie> findMovieWithCast(String title);
} 