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

package com.alibaba.cloud.ai.example.controller;

import com.alibaba.cloud.ai.request.EvidenceRequest;
import com.alibaba.cloud.ai.request.SchemaInitRequest;
import com.alibaba.cloud.ai.service.SimpleVectorStoreManagementService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/simple")
public class SimpleVectorManagementController {

    @Autowired
    private SimpleVectorStoreManagementService simpleVectorStoreService;

    @PostMapping("/add/evidence")
    public Boolean addEvidence(@RequestBody List<EvidenceRequest> evidenceRequests) {
        return simpleVectorStoreService.addEvidence(evidenceRequests);
    }

    @PostMapping("/search")
    public List<Document> search(@RequestBody SearchRequest searchRequestDTO) throws Exception {
        return simpleVectorStoreService.search(searchRequestDTO);
    }

    @PostMapping("/delete")
    public Boolean deleteDocuments(@RequestBody com.alibaba.cloud.ai.request.DeleteRequest deleteRequest) throws Exception {
        return simpleVectorStoreService.deleteDocuments(deleteRequest);
    }


    @PostMapping("/init/schema")
    public Boolean schema(@RequestBody SchemaInitRequest schemaInitRequest) throws Exception {
        simpleVectorStoreService.schema(schemaInitRequest);
        return true;
    }

}
