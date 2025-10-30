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

package com.touhouqing.chatAiDemo.controller;

import com.touhouqing.chatAiDemo.entity.ProcurementProject;
import com.touhouqing.chatAiDemo.entity.ProcurementOrganization;
import com.touhouqing.chatAiDemo.entity.vo.ApiResponse;
import com.touhouqing.chatAiDemo.repository.ProcurementProjectRepository;
import com.touhouqing.chatAiDemo.repository.ProcurementOrganizationRepository;
import com.touhouqing.chatAiDemo.service.ProcurementCrawlerService;
import com.touhouqing.chatAiDemo.service.ProcurementAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/procurement")
@RequiredArgsConstructor
public class ProcurementController {
    
    private final ProcurementCrawlerService crawlerService;
    private final ProcurementAIService aiService;
    private final ProcurementProjectRepository projectRepository;
    private final ProcurementOrganizationRepository organizationRepository;
    
    /**
     * 启动爬虫任务
     */
    @PostMapping("/crawl/start")
    public ApiResponse<String> startCrawling(@RequestParam(required = false) String url) {
        try {
            CompletableFuture<String> future;
            if (url != null && !url.trim().isEmpty()) {
                future = crawlerService.startCrawling(url);
            } else {
                future = crawlerService.crawlTianjinProcurement();
            }

            // 异步执行，立即返回
            future.thenAccept(result -> log.info("爬虫任务结果: {}", result));

            return ApiResponse.success("爬虫任务已启动，正在后台执行");
        } catch (Exception e) {
            log.error("启动爬虫失败", e);
            return ApiResponse.error("启动爬虫失败: " + e.getMessage());
        }
    }

    /**
     * 爬取单个页面
     */
    @PostMapping("/crawl/single")
    public ApiResponse<String> crawlSinglePage(@RequestParam String url) {
        try {
            CompletableFuture<String> future = crawlerService.crawlSinglePage(url);
            future.thenAccept(result -> log.info("单页面爬取结果: {}", result));

            return ApiResponse.success("单页面爬取任务已启动");
        } catch (Exception e) {
            log.error("单页面爬取失败", e);
            return ApiResponse.error("单页面爬取失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询所有采购项目
     */
    @GetMapping("/projects")
    public ApiResponse<List<ProcurementProject>> getAllProjects() {
        try {
            List<ProcurementProject> projects = projectRepository.findAll();
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("查询采购项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询采购项目详情
     */
    @GetMapping("/projects/{id}")
    public ApiResponse<ProcurementProject> getProjectById(@PathVariable Long id) {
        try {
            Optional<ProcurementProject> project = projectRepository.findProjectWithAllRelations(id);
            if (project.isPresent()) {
                return ApiResponse.success(project.get());
            } else {
                return ApiResponse.error("项目不存在");
            }
        } catch (Exception e) {
            log.error("查询项目详情失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据关键词搜索项目
     */
    @GetMapping("/projects/search")
    public ApiResponse<List<ProcurementProject>> searchProjects(@RequestParam String keyword) {
        try {
            List<ProcurementProject> projects = projectRepository.searchByKeyword(keyword);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("搜索项目失败", e);
            return ApiResponse.error("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据预算范围查询项目
     */
    @GetMapping("/projects/budget")
    public ApiResponse<List<ProcurementProject>> getProjectsByBudget(
            @RequestParam Double minBudget,
            @RequestParam Double maxBudget) {
        try {
            List<ProcurementProject> projects = projectRepository.findByBudgetRange(minBudget, maxBudget);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("按预算查询项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询最近的项目
     */
    @GetMapping("/projects/recent")
    public ApiResponse<List<ProcurementProject>> getRecentProjects(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<ProcurementProject> projects = projectRepository.findRecentProjects(limit);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("查询最近项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有采购单位
     */
    @GetMapping("/organizations")
    public ApiResponse<List<ProcurementOrganization>> getAllOrganizations() {
        try {
            List<ProcurementOrganization> organizations = organizationRepository.findAll();
            return ApiResponse.success(organizations);
        } catch (Exception e) {
            log.error("查询采购单位失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据采购单位查询项目
     */
    @GetMapping("/organizations/{name}/projects")
    public ApiResponse<List<ProcurementProject>> getProjectsByOrganization(@PathVariable String name) {
        try {
            List<ProcurementProject> projects = projectRepository.findByOrganizationName(name);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("按单位查询项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * AI分析项目内容
     */
    @PostMapping("/analyze")
    public ApiResponse<String> analyzeProject(@RequestParam String title,
                                       @RequestParam String content,
                                       @RequestParam(required = false) String tableData) {
        try {
            String analysis = aiService.analyzeProcurementData(title, content, tableData);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            log.error("AI分析失败", e);
            return ApiResponse.error("分析失败: " + e.getMessage());
        }
    }

    /**
     * 获取采购趋势分析
     */
    @GetMapping("/trends")
    public ApiResponse<String> getTrends() {
        try {
            List<ProcurementProject> recentProjects = projectRepository.findRecentProjects(50);
            StringBuilder projectData = new StringBuilder();

            for (ProcurementProject project : recentProjects) {
                projectData.append("项目: ").append(project.getProjectName())
                          .append(", 预算: ").append(project.getBudget())
                          .append(", 单位: ").append(project.getOrganization() != null ?
                                  project.getOrganization().getName() : "未知")
                          .append("\n");
            }

            String trends = aiService.analyzeTrends(projectData.toString());
            return ApiResponse.success(trends);
        } catch (Exception e) {
            log.error("趋势分析失败", e);
            return ApiResponse.error("分析失败: " + e.getMessage());
        }
    }
}
