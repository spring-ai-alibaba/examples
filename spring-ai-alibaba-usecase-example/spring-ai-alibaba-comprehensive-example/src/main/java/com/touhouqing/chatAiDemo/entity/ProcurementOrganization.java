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
 * 采购单位实体
 */
@Node("ProcurementOrganization")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcurementOrganization {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("name")
    private String name;
    
    @Property("type")
    private String type; // 医院、学校、政府机关等
    
    @Property("level")
    private String level; // 市级、区级、国家级等
    
    @Property("address")
    private String address;
    
    @Property("contact")
    private String contact;
    
    @Property("description")
    private String description;
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    @Property("updatedAt")
    private LocalDateTime updatedAt;
    
    // 与采购项目的关系
    @Relationship(type = "PROCURED_BY", direction = Relationship.Direction.INCOMING)
    private List<ProcurementProject> projects;
    
    // 与上级单位的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private ProcurementOrganization parentOrganization;
    
    // 与下级单位的关系
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.INCOMING)
    private List<ProcurementOrganization> subOrganizations;
}
