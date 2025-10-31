package com.touhouqing.chatAiDemo;

import com.touhouqing.chatAiDemo.service.ProcurementCrawlerService;
import com.touhouqing.chatAiDemo.service.ProcurementAIService;
import com.touhouqing.chatAiDemo.repository.ProcurementProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ProcurementCrawlerTest {
    
    @Autowired(required = false)
    private ProcurementCrawlerService crawlerService;
    
    @Autowired(required = false)
    private ProcurementAIService aiService;
    
    @Autowired(required = false)
    private ProcurementProjectRepository projectRepository;
    
    @Test
    public void testServiceInjection() {
        // 测试服务是否正确注入
        System.out.println("爬虫服务注入状态: " + (crawlerService != null ? "成功" : "失败"));
        System.out.println("AI服务注入状态: " + (aiService != null ? "成功" : "失败"));
        System.out.println("项目仓库注入状态: " + (projectRepository != null ? "成功" : "失败"));
    }
    
    @Test
    public void testAIAnalysis() {
        if (aiService != null) {
            try {
                String title = "天津市中医药研究院附属医院政府采购意向公告";
                String content = "门急诊改造项目设计服务项目，预算86万元";
                String tableData = "序号|项目名称|预算金额|采购时间\n1|门急诊改造项目|86万元|2025-08";
                
                String analysis = aiService.analyzeProcurementData(title, content, tableData);
                System.out.println("AI分析结果: " + analysis);
            } catch (Exception e) {
                System.out.println("AI分析测试失败: " + e.getMessage());
            }
        } else {
            System.out.println("AI服务未注入，跳过测试");
        }
    }
}
