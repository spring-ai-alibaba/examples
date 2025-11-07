/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
