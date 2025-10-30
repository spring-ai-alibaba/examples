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

package com.touhouqing.chatAiDemo.component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GovProcurementPageProcessor implements PageProcessor {

    // 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(2000)
            .setCharset("UTF-8")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();
        log.info("正在处理页面: {}", url);
        
        // 判断是否为详情页面
        if (url.contains("documentView.do")) {
            processDetailPage(page);
        } else if (url.contains("topicView.do") || url.contains("portal")) {
            processListPage(page);
        }
    }
    
    /**
     * 处理详情页面
     */
    private void processDetailPage(Page page) {
        try {
            // 尝试多种方式提取标题
            String title = null;

            // 方法1: 查找h1标签
            title = page.getHtml().xpath("//h1/text()").toString();

            // 方法2: 查找包含"公告"的强调文本
            if (title == null || title.trim().isEmpty()) {
                title = page.getHtml().xpath("//strong[contains(text(), '公告')]/text()").toString();
            }

            // 方法3: 查找页面标题
            if (title == null || title.trim().isEmpty()) {
                title = page.getHtml().xpath("//title/text()").toString();
            }

            // 方法4: 查找任何包含"政府采购"或"公告"的文本
            if (title == null || title.trim().isEmpty()) {
                title = page.getHtml().xpath("//text()[contains(., '政府采购') or contains(., '公告')]").toString();
            }

            // 提取发布日期
            String publishDate = page.getHtml().xpath("//text()[contains(., '发布日期')]").regex("发布日期：(.+?)\\s").toString();
            if (publishDate == null || publishDate.trim().isEmpty()) {
                publishDate = page.getHtml().xpath("//text()[contains(., '发布日期')]").regex("(\\d{4}年\\d{1,2}月\\d{1,2}日)").toString();
            }

            // 提取发布来源
            String publishSource = page.getHtml().xpath("//text()[contains(., '发布来源')]").regex("发布来源：(.+?)\\s").toString();
            if (publishSource == null || publishSource.trim().isEmpty()) {
                publishSource = page.getHtml().xpath("//text()[contains(., '发布来源')]").regex("发布来源：(.+?)$").toString();
            }

            // 提取正文内容
            String content = page.getHtml().xpath("//body//text()").all().toString();

            // 提取表格数据（采购意向表格）
            String tableData = extractTableData(page);

            // 记录调试信息
            log.debug("页面URL: {}", page.getUrl());
            log.debug("提取的标题: {}", title);
            log.debug("发布日期: {}", publishDate);
            log.debug("发布来源: {}", publishSource);

            // 检查是否有有效内容（降低阈值并检查关键信息）
            boolean hasValidContent = (content != null && content.length() > 50) ||
                                    (title != null && title.contains("政府采购")) ||
                                    (publishSource != null && !publishSource.trim().isEmpty()) ||
                                    (tableData != null && !tableData.trim().isEmpty());

            if (hasValidContent) {
                // 从HTML中提取更清晰的标题
                String cleanTitle = title;
                if (title != null && title.contains("天津市中医药研究院附属医院政府采购意向公告")) {
                    cleanTitle = "天津市中医药研究院附属医院政府采购意向公告";
                } else if (title != null && title.contains("政府采购")) {
                    // 尝试从title中提取更简洁的标题
                    String[] lines = title.split("\n");
                    for (String line : lines) {
                        if (line.contains("政府采购") && line.length() < 100) {
                            cleanTitle = line.trim();
                            break;
                        }
                    }
                }

                page.putField("title", cleanTitle != null ? cleanTitle.trim() : "未知标题");
                page.putField("publishDate", publishDate);
                page.putField("publishSource", publishSource);
                page.putField("content", content);
                page.putField("tableData", tableData);
                page.putField("url", page.getUrl().toString());
                page.putField("type", "procurement_detail");

                log.info("成功提取详情页面数据: {}", cleanTitle != null ? cleanTitle : "未知标题");
            } else {
                page.setSkip(true);
                log.warn("跳过页面，内容不足: {}", page.getUrl());
            }

        } catch (Exception e) {
            log.error("处理详情页面出错: {}", page.getUrl(), e);
            page.setSkip(true);
        }
    }
    
    /**
     * 处理列表页面
     */
    private void processListPage(Page page) {
        try {
            // 查找详情页面链接
            page.addTargetRequests(page.getHtml().links()
                    .regex(".*documentView\\.do\\?method=view&id=\\d+.*").all());
            
            // 查找分页链接
            page.addTargetRequests(page.getHtml().links()
                    .regex(".*topicView\\.do.*").all());
                    
            log.info("从列表页面发现新链接: {}", page.getTargetRequests().size());
            
        } catch (Exception e) {
            log.error("处理列表页面出错: {}", page.getUrl(), e);
        }
    }
    
    /**
     * 提取表格数据
     */
    private String extractTableData(Page page) {
        try {
            StringBuilder tableData = new StringBuilder();
            
            // 查找包含采购信息的表格
            page.getHtml().xpath("//table").nodes().forEach(table -> {
                table.xpath("//tr").nodes().forEach(row -> {
                    StringBuilder rowData = new StringBuilder();
                    row.xpath("//td/text()").all().forEach(cell -> {
                        if (cell != null && !cell.trim().isEmpty()) {
                            rowData.append(cell.trim()).append("|");
                        }
                    });
                    if (rowData.length() > 0) {
                        tableData.append(rowData.toString()).append("\n");
                    }
                });
            });
            
            return tableData.toString();
        } catch (Exception e) {
            log.error("提取表格数据出错", e);
            return "";
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
