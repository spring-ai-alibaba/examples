package com.touhouqing.chatAiDemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.touhouqing.chatAiDemo.mapper")
@SpringBootApplication
public class ChatAiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAiDemoApplication.class, args);
        // 移除自动启动的爬虫，改为通过API接口手动触发
        System.out.println("应用启动完成！");
        System.out.println("政府采购爬虫API已就绪，可通过 /procurement/crawl/start 接口启动爬虫任务");
        System.out.println("示例URL: http://www.ccgp-tianjin.gov.cn/portal/documentView.do?method=view&id=750651121&ver=2");
    }

}
