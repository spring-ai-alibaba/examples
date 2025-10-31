package com.touhouqing.chatAiDemo.component;

import com.touhouqing.chatAiDemo.service.BiddingAIService;
import com.touhouqing.chatAiDemo.service.BiddingDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 招标数据处理管道
 * 接收爬虫的原始HTML数据，通过AI解析后存储到图数据库和向量数据库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BiddingDataPipeline implements Pipeline {

    private final BiddingAIService aiService;
    private final BiddingDataService dataService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        try {
            String type = resultItems.get("type");
            if (!"bidding_detail".equals(type)) {
                return;
            }

            String url = resultItems.get("url");
            String title = resultItems.get("title");
            String htmlContent = resultItems.get("htmlContent");
            Long crawlTime = resultItems.get("crawlTime");

            log.info("开始处理招标数据: {}", title);

            // 检查是否已存在
            if (dataService.existsBySourceUrl(url)) {
                log.info("项目已存在，跳过: {}", title);
                return;
            }

            // 使用AI解析原始HTML内容
            String structuredData = aiService.parseHtmlToBiddingData(htmlContent, url);

            if (structuredData == null || structuredData.trim().isEmpty()) {
                log.warn("AI解析失败，跳过: {}", title);
                return;
            }

            // 保存到图数据库和向量数据库
            dataService.saveBiddingData(structuredData, htmlContent, url, crawlTime);

            log.info("成功保存招标项目: {}", title);

        } catch (Exception e) {
            log.error("处理招标数据出错", e);
        }
    }

    /**
     * 处理直接爬取的数据
     */
    public void process(com.touhouqing.chatAiDemo.service.DirectHttpCrawlerService.CrawledData data) {
        try {
            if (!"bidding_detail".equals(data.getType())) {
                return;
            }

            String url = data.getUrl();
            String title = data.getTitle();
            String htmlContent = data.getHtmlContent();
            Long crawlTime = data.getCrawlTime();

            log.info("开始处理招标数据: {}", title);

            // 检查是否已存在
            if (dataService.existsBySourceUrl(url)) {
                log.info("项目已存在，跳过: {}", title);
                return;
            }

            // 使用AI解析原始HTML内容
            String structuredData = aiService.parseHtmlToBiddingData(htmlContent, url);

            if (structuredData == null || structuredData.trim().isEmpty()) {
                log.warn("AI解析失败，跳过: {}", title);
                return;
            }

            // 保存到图数据库和向量数据库
            dataService.saveBiddingData(structuredData, htmlContent, url, crawlTime);

            log.info("成功保存招标项目: {}", title);

        } catch (Exception e) {
            log.error("处理招标数据出错", e);
        }
    }
}