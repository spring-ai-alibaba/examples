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

import com.touhouqing.chatAiDemo.entity.vo.ApiResponse;
import com.touhouqing.chatAiDemo.service.ProcurementCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/procurement/test")
@RequiredArgsConstructor
public class ProcurementTestController {

    private final ProcurementCrawlerService crawlerService;

    /**
     * 测试爬取示例页面
     */
    @PostMapping("/example")
    public ApiResponse<String> testExamplePage() {
        try {
            String exampleUrl = "http://www.ccgp-tianjin.gov.cn/portal/documentView.do?method=view&id=750651121&ver=2";

            crawlerService.crawlSinglePage(exampleUrl)
                    .thenAccept(result -> log.info("示例页面爬取结果: {}", result));

            return ApiResponse.success("示例页面爬取任务已启动，URL: " + exampleUrl);
        } catch (Exception e) {
            log.error("测试爬取失败", e);
            return ApiResponse.error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("政府采购爬虫服务运行正常");
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    public ApiResponse<String> getSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("政府采购数据爬虫系统\n");
        info.append("支持的功能:\n");
        info.append("1. 爬取政府采购公告\n");
        info.append("2. AI智能分析采购内容\n");
        info.append("3. 存储到Neo4j图数据库\n");
        info.append("4. 提供RESTful API查询\n");
        info.append("\n可用的API接口:\n");
        info.append("- POST /procurement/crawl/start - 启动爬虫\n");
        info.append("- POST /procurement/crawl/single - 爬取单页\n");
        info.append("- GET /procurement/projects - 查询所有项目\n");
        info.append("- GET /procurement/projects/search - 搜索项目\n");
        info.append("- POST /procurement/analyze - AI分析\n");

        return ApiResponse.success(info.toString());
    }
}
