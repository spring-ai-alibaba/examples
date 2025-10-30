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

package com.touhouqing.chatAiDemo.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行业类别实体
 */
@Node("IndustryCategory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndustryCategory {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("name")
    private String name;
    
    @Property("code")
    private String code;
    
    @Property("description")
    private String description;
    
    @Property("level")
    private Integer level;
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    @Property("updatedAt")
    private LocalDateTime updatedAt;
    
    // 与供应商的关系
    @Relationship(type = "OPERATES_IN", direction = Relationship.Direction.INCOMING)
    private List<Supplier> suppliers;
    
    // 与父类别的关系
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.OUTGOING)
    private IndustryCategory parentCategory;
    
    // 与子类别的关系
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.INCOMING)
    private List<IndustryCategory> subCategories;
}
