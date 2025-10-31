package com.touhouqing.chatAiDemo.component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BiddingWebPageProcessor implements PageProcessor {

    // 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me()
            .setRetryTimes(5)  // 增加重试次数
            .setSleepTime(3000)  // 增加延迟时间
            .setTimeOut(30000)  // 设置30秒超时
            .setCharset("UTF-8")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
            .addHeader("Accept-Encoding", "gzip, deflate")  // 支持压缩
            .addHeader("Connection", "keep-alive")
            .addHeader("Upgrade-Insecure-Requests", "1")
            .addHeader("Cache-Control", "max-age=0");

    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();
        log.info("正在处理页面: {}", url);

        try {
            // 判断是否为详情页面
            if (isDetailPage(url)) {
                processDetailPage(page);
            } else {
                processListPage(page);
            }
        } catch (Exception e) {
            log.error("处理页面失败: {}", url, e);
            page.setSkip(true);
        }
    }

    /**
     * 判断是否为详情页面
     */
    private boolean isDetailPage(String url) {
        return url.contains("documentView.do") ||
               url.contains("detail") ||
               url.contains("show") ||
               (url.contains("view") && url.contains("id="));
    }
    
    /**
     * 处理详情页面 - 只提取原始HTML内容
     */
    private void processDetailPage(Page page) {
        try {
            String url = page.getUrl().toString();
            String htmlContent = page.getHtml().toString();

            // 基本验证：确保页面有实际内容
            if (htmlContent == null || htmlContent.length() < 500) {
                log.warn("页面内容过短，跳过: {}", url);
                page.setSkip(true);
                return;
            }

            // 提取页面标题（仅用于日志记录）
            String title = extractTitle(page);

            // 只传递原始数据，不做任何结构化处理
            page.putField("type", "bidding_detail");
            page.putField("url", url);
            page.putField("title", title);
            page.putField("htmlContent", htmlContent);
            page.putField("crawlTime", System.currentTimeMillis());

            log.info("成功提取详情页面: {}", title != null ? title : url);

        } catch (Exception e) {
            log.error("处理详情页面出错: {}", page.getUrl(), e);
            page.setSkip(true);
        }
    }
    
    /**
     * 处理列表页面 - 发现新的详情页面链接
     */
    private void processListPage(Page page) {
        try {
            // 查找详情页面链接
            page.addTargetRequests(page.getHtml().links()
                    .regex(".*documentView\\.do\\?.*id=\\d+.*").all());

            page.addTargetRequests(page.getHtml().links()
                    .regex(".*detail.*").all());

            page.addTargetRequests(page.getHtml().links()
                    .regex(".*show.*id=\\d+.*").all());

            // 查找分页链接
            page.addTargetRequests(page.getHtml().links()
                    .regex(".*page=\\d+.*").all());

            log.info("从列表页面发现新链接: {}", page.getTargetRequests().size());

        } catch (Exception e) {
            log.error("处理列表页面出错: {}", page.getUrl(), e);
        }
    }

    /**
     * 提取页面标题
     */
    private String extractTitle(Page page) {
        String title = null;

        // 尝试多种方式提取标题
        title = page.getHtml().xpath("//title/text()").toString();

        if (title == null || title.trim().isEmpty()) {
            title = page.getHtml().xpath("//h1/text()").toString();
        }

        if (title == null || title.trim().isEmpty()) {
            title = page.getHtml().xpath("//h2/text()").toString();
        }

        return title != null ? title.trim() : "未知标题";
    }

    @Override
    public Site getSite() {
        return site;
    }
}
