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

package com.touhouqing.chatAiDemo.service;

import com.touhouqing.chatAiDemo.component.GovProcurementPageProcessor;
import com.touhouqing.chatAiDemo.component.ProcurementDataPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcurementCrawlerService {
    
    private final ProcurementDataPipeline dataPipeline;
    
    /**
     * 启动爬虫任务
     */
    public CompletableFuture<String> startCrawling(String startUrl) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始爬取政府采购数据，起始URL: {}", startUrl);
                
                Spider spider = Spider.create(new GovProcurementPageProcessor())
                        .addUrl(startUrl)
                        .addPipeline(dataPipeline)
                        .thread(3); // 使用3个线程
                
                spider.run();
                
                String result = "爬虫任务完成，共处理 " + spider.getPageCount() + " 个页面";
                log.info(result);
                return result;
                
            } catch (Exception e) {
                log.error("爬虫任务执行失败", e);
                return "爬虫任务失败: " + e.getMessage();
            }
        });
    }
    
    /**
     * 爬取单个页面
     */
    public CompletableFuture<String> crawlSinglePage(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始爬取单个页面: {}", url);
                
                Spider spider = Spider.create(new GovProcurementPageProcessor())
                        .addUrl(url)
                        .addPipeline(dataPipeline)
                        .thread(1);
                
                spider.run();
                
                String result = "单页面爬取完成: " + url;
                log.info(result);
                return result;
                
            } catch (Exception e) {
                log.error("单页面爬取失败", e);
                return "单页面爬取失败: " + e.getMessage();
            }
        });
    }
    
    /**
     * 爬取天津政府采购网的采购意向
     */
    public CompletableFuture<String> crawlTianjinProcurement() {
        String baseUrl = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1665&ver=2&st=1";
        return startCrawling(baseUrl);
    }
    
    /**
     * 爬取指定的采购公告详情页
     */
    public CompletableFuture<String> crawlProcurementDetail(String detailUrl) {
        return crawlSinglePage(detailUrl);
    }
}
