package com.touhouqing.chatAiDemo.service;

import com.touhouqing.chatAiDemo.component.BiddingDataPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * 直接HTTP爬虫服务
 * 使用RestTemplate直接发送HTTP请求，避免WebMagic的限制
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectHttpCrawlerService {

    private final BiddingDataPipeline dataPipeline;

    /**
     * 直接爬取单个页面
     */
    public String crawlPageDirectly(String url) {
        try {
            log.info("开始直接爬取页面: {}", url);

            // 创建RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            headers.set("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            headers.set("Accept-Encoding", "gzip, deflate");
            headers.set("Connection", "keep-alive");
            headers.set("Upgrade-Insecure-Requests", "1");
            headers.set("Cache-Control", "max-age=0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    entity, 
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                String htmlContent = response.getBody();
                log.info("成功获取页面内容，长度: {}", htmlContent != null ? htmlContent.length() : 0);

                // 直接调用数据处理管道
                if (htmlContent != null && !htmlContent.trim().isEmpty()) {
                    // 创建一个模拟的Page对象数据
                    processCrawledData(url, htmlContent);
                    return "页面爬取成功，内容长度: " + htmlContent.length();
                } else {
                    return "页面内容为空";
                }
            } else {
                log.error("HTTP请求失败，状态码: {}", response.getStatusCode());
                return "HTTP请求失败，状态码: " + response.getStatusCode();
            }

        } catch (Exception e) {
            log.error("直接爬取页面失败: {}", url, e);
            return "爬取失败: " + e.getMessage();
        }
    }

    /**
     * 仅获取HTML内容，不进行数据处理
     */
    public String getHtmlContent(String url) {
        try {
            log.info("获取HTML内容: {}", url);

            // 创建RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            headers.set("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            headers.set("Accept-Encoding", "gzip, deflate");
            headers.set("Connection", "keep-alive");
            headers.set("Upgrade-Insecure-Requests", "1");
            headers.set("Cache-Control", "max-age=0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                String htmlContent = response.getBody();
                log.info("成功获取HTML内容，长度: {}", htmlContent != null ? htmlContent.length() : 0);
                return htmlContent;
            } else {
                log.error("HTTP请求失败，状态码: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("获取HTML内容失败: {}", url, e);
            return null;
        }
    }

    /**
     * 处理爬取到的数据
     */
    private void processCrawledData(String url, String htmlContent) {
        try {
            // 提取标题
            String title = extractTitle(htmlContent);
            
            log.info("开始处理爬取数据 - URL: {}, 标题: {}", url, title);

            // 创建数据对象传递给处理管道
            CrawledData data = new CrawledData();
            data.setUrl(url);
            data.setTitle(title);
            data.setHtmlContent(htmlContent);
            data.setType("bidding_detail");
            data.setCrawlTime(System.currentTimeMillis());

            // 调用数据处理管道
            dataPipeline.process(data);

        } catch (Exception e) {
            log.error("处理爬取数据失败", e);
        }
    }

    /**
     * 提取页面标题
     */
    private String extractTitle(String htmlContent) {
        try {
            // 简单的标题提取
            if (htmlContent.contains("<title>")) {
                int start = htmlContent.indexOf("<title>") + 7;
                int end = htmlContent.indexOf("</title>", start);
                if (end > start) {
                    return htmlContent.substring(start, end).trim();
                }
            }
            
            // 尝试从meta标签提取
            if (htmlContent.contains("ArticleTitle")) {
                int start = htmlContent.indexOf("ArticleTitle\" content=\"");
                if (start > 0) {
                    start += 23;
                    int end = htmlContent.indexOf("\"", start);
                    if (end > start) {
                        return htmlContent.substring(start, end).trim();
                    }
                }
            }
            
            return "未知标题";
        } catch (Exception e) {
            log.error("提取标题失败", e);
            return "标题提取失败";
        }
    }

    /**
     * 爬取数据的内部类
     */
    public static class CrawledData {
        private String url;
        private String title;
        private String htmlContent;
        private String type;
        private long crawlTime;

        // Getters and Setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getHtmlContent() { return htmlContent; }
        public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public long getCrawlTime() { return crawlTime; }
        public void setCrawlTime(long crawlTime) { this.crawlTime = crawlTime; }
    }
}
