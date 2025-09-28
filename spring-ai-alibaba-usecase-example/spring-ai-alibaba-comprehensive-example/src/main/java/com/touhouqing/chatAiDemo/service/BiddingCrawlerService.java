package com.touhouqing.chatAiDemo.service;

import com.touhouqing.chatAiDemo.component.BiddingWebPageProcessor;
import com.touhouqing.chatAiDemo.component.BiddingDataPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.util.concurrent.CompletableFuture;

/**
 * 招标爬虫服务
 * 整合爬虫、AI解析、数据存储的完整流程
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingCrawlerService {

    private final BiddingDataPipeline dataPipeline;

    /**
     * 启动招标爬虫任务
     */
    public CompletableFuture<String> startCrawling(String startUrl) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始爬取招标数据，起始URL: {}", startUrl);

                Spider spider = Spider.create(new BiddingWebPageProcessor())
                        .addUrl(startUrl)
                        .addPipeline(dataPipeline)
                        .thread(3); // 使用3个线程

                spider.run();

                String result = "招标爬虫任务完成，共处理 " + spider.getPageCount() + " 个页面";
                log.info(result);
                return result;

            } catch (Exception e) {
                log.error("招标爬虫任务执行失败", e);
                return "招标爬虫任务失败: " + e.getMessage();
            }
        });
    }

    /**
     * 爬取单个招标页面
     */
    public CompletableFuture<String> crawlSinglePage(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始爬取单个招标页面: {}", url);

                Spider spider = Spider.create(new BiddingWebPageProcessor())
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
     * 爬取天津政府采购网
     */
    public CompletableFuture<String> crawlTianjinBidding() {
        String baseUrl = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1665&ver=2&st=1";
        return startCrawling(baseUrl);
    }

    /**
     * 爬取中国政府采购网
     */
    public CompletableFuture<String> crawlChinaGovProcurement() {
        String baseUrl = "http://www.ccgp.gov.cn/cggg/";
        return startCrawling(baseUrl);
    }

    /**
     * 爬取指定地区的公共资源交易网
     */
    public CompletableFuture<String> crawlRegionalBidding(String region, String url) {
        log.info("开始爬取{}地区的招标信息", region);
        return startCrawling(url);
    }
}