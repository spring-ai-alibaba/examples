package com.touhouqing.chatAiDemo.controller;

import com.touhouqing.chatAiDemo.entity.graph.BiddingProject;
import com.touhouqing.chatAiDemo.entity.vo.ApiResponse;
import com.touhouqing.chatAiDemo.repository.graph.BiddingProjectRepository;
import com.touhouqing.chatAiDemo.service.BiddingCrawlerService;
import com.touhouqing.chatAiDemo.service.BiddingChatService;
import com.touhouqing.chatAiDemo.service.BiddingQueryService;
import com.touhouqing.chatAiDemo.service.DirectHttpCrawlerService;
import com.touhouqing.chatAiDemo.service.BiddingAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 招标控制器
 * 提供招标爬虫和数据查询的API接口
 */
@Slf4j
@RestController
@RequestMapping("/bidding")
@RequiredArgsConstructor
public class BiddingController {

    private final BiddingCrawlerService crawlerService;
    private final BiddingProjectRepository projectRepository;
    private final BiddingChatService chatService;
    private final BiddingQueryService queryService;
    private final DirectHttpCrawlerService directHttpCrawlerService;
    private final BiddingAIService aiService;

    /**
     * 启动招标爬虫任务
     */
    @PostMapping("/crawl/start")
    public ApiResponse<String> startCrawling(@RequestParam(required = false) String url) {
        try {
            CompletableFuture<String> future;
            if (url != null && !url.trim().isEmpty()) {
                future = crawlerService.startCrawling(url);
            } else {
                future = crawlerService.crawlTianjinBidding();
            }

            // 异步执行，立即返回
            future.thenAccept(result -> log.info("招标爬虫任务结果: {}", result));

            return ApiResponse.success("招标爬虫任务已启动，正在后台执行");
        } catch (Exception e) {
            log.error("启动招标爬虫失败", e);
            return ApiResponse.error("启动招标爬虫失败: " + e.getMessage());
        }
    }

    /**
     * 爬取单个招标页面
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
     * 直接HTTP爬取单个页面（避免WebMagic的限制）
     */
    @PostMapping("/crawl/direct")
    public ApiResponse<String> crawlDirect(@RequestParam String url) {
        try {
            log.info("开始直接HTTP爬取: {}", url);
            String result = directHttpCrawlerService.crawlPageDirectly(url);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("直接HTTP爬取失败", e);
            return ApiResponse.error("直接HTTP爬取失败: " + e.getMessage());
        }
    }

    /**
     * 测试AI解析功能
     */
    @PostMapping("/test/ai-parse")
    public ApiResponse<String> testAiParse(@RequestParam String url) {
        try {
            log.info("开始测试AI解析: {}", url);

            // 先获取HTML内容
            String htmlContent = directHttpCrawlerService.getHtmlContent(url);
            if (htmlContent == null) {
                return ApiResponse.error("无法获取页面内容");
            }

            // 测试AI解析
            String aiResult = aiService.parseHtmlToBiddingData(htmlContent, url);

            return ApiResponse.success("AI解析结果: " + aiResult);
        } catch (Exception e) {
            log.error("AI解析测试失败", e);
            return ApiResponse.error("AI解析测试失败: " + e.getMessage());
        }
    }

    /**
     * 查看原始HTML内容
     */
    @PostMapping("/test/html-content")
    public ApiResponse<String> viewHtmlContent(@RequestParam String url) {
        try {
            log.info("获取HTML内容: {}", url);

            String htmlContent = directHttpCrawlerService.getHtmlContent(url);
            if (htmlContent == null) {
                return ApiResponse.error("无法获取页面内容");
            }

            // 返回HTML内容的前2000个字符用于查看
            String preview = htmlContent.length() > 2000 ?
                htmlContent.substring(0, 2000) + "..." : htmlContent;

            return ApiResponse.success("HTML内容预览 (长度: " + htmlContent.length() + "): " + preview);
        } catch (Exception e) {
            log.error("获取HTML内容失败", e);
            return ApiResponse.error("获取HTML内容失败: " + e.getMessage());
        }
    }

    /**
     * 调试分段解析过程
     */
    @PostMapping("/test/debug-parse")
    public ApiResponse<String> debugParse(@RequestParam String url) {
        try {
            log.info("开始调试分段解析: {}", url);

            String htmlContent = directHttpCrawlerService.getHtmlContent(url);
            if (htmlContent == null) {
                return ApiResponse.error("无法获取页面内容");
            }

            // 调用AI服务的调试方法
            String debugResult = aiService.debugParseHtml(htmlContent, url);

            return ApiResponse.success(debugResult);
        } catch (Exception e) {
            log.error("调试分段解析失败", e);
            return ApiResponse.error("调试分段解析失败: " + e.getMessage());
        }
    }

    /**
     * 测试HTML片段提取
     */
    @PostMapping("/test/extract-fragments")
    public ApiResponse<String> testExtractFragments(@RequestParam String url) {
        try {
            log.info("测试HTML片段提取: {}", url);

            String htmlContent = directHttpCrawlerService.getHtmlContent(url);
            if (htmlContent == null) {
                return ApiResponse.error("无法获取页面内容");
            }

            StringBuilder result = new StringBuilder();
            result.append("=== HTML片段提取测试 ===\n\n");

            // 查找meta标签
            result.append("1. Meta标签搜索:\n");
            String[] lines = htmlContent.split("\n");
            for (String line : lines) {
                if (line.contains("<meta") &&
                    (line.contains("ArticleTitle") || line.contains("PubDate"))) {
                    result.append("找到: ").append(line.trim()).append("\n");
                }
            }

            // 查找表格相关内容
            result.append("\n2. 表格内容搜索:\n");
            for (String line : lines) {
                if (line.contains("预算金额") || line.contains("采购项目名称")) {
                    result.append("找到表头: ").append(line.trim()).append("\n");
                    break;
                }
            }

            // 查找包含数字的td标签
            result.append("\n3. 数字内容搜索:\n");
            int count = 0;
            for (String line : lines) {
                if (line.contains("<td>") && line.matches(".*\\d+.*") && count < 5) {
                    result.append("找到数字行: ").append(line.trim()).append("\n");
                    count++;
                }
            }

            return ApiResponse.success(result.toString());

        } catch (Exception e) {
            log.error("测试HTML片段提取失败", e);
            return ApiResponse.error("测试HTML片段提取失败: " + e.getMessage());
        }
    }

    /**
     * 爬取天津政府采购网
     */
    @PostMapping("/crawl/tianjin")
    public ApiResponse<String> crawlTianjin() {
        try {
            CompletableFuture<String> future = crawlerService.crawlTianjinBidding();
            future.thenAccept(result -> log.info("天津招标爬取结果: {}", result));

            return ApiResponse.success("天津招标爬取任务已启动");
        } catch (Exception e) {
            log.error("天津招标爬取失败", e);
            return ApiResponse.error("天津招标爬取失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有招标项目
     */
    @GetMapping("/projects")
    public ApiResponse<List<BiddingProject>> getAllProjects() {
        try {
            List<BiddingProject> projects = projectRepository.findAll();
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("查询招标项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据项目名称搜索
     */
    @GetMapping("/projects/search")
    public ApiResponse<List<BiddingProject>> searchProjects(@RequestParam String keyword) {
        try {
            List<BiddingProject> projects = projectRepository.findByProjectNameContaining(keyword);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("搜索招标项目失败", e);
            return ApiResponse.error("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 根据地区查询项目
     */
    @GetMapping("/projects/region/{region}")
    public ApiResponse<List<BiddingProject>> getProjectsByRegion(@PathVariable String region) {
        try {
            List<BiddingProject> projects = projectRepository.findByRegion(region);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("根据地区查询项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据行业查询项目
     */
    @GetMapping("/projects/industry/{industry}")
    public ApiResponse<List<BiddingProject>> getProjectsByIndustry(@PathVariable String industry) {
        try {
            List<BiddingProject> projects = projectRepository.findByIndustry(industry);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("根据行业查询项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据项目状态查询
     */
    @GetMapping("/projects/status/{status}")
    public ApiResponse<List<BiddingProject>> getProjectsByStatus(@PathVariable String status) {
        try {
            List<BiddingProject> projects = projectRepository.findByProjectStatus(status);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("根据状态查询项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近的项目
     */
    @GetMapping("/projects/recent")
    public ApiResponse<List<BiddingProject>> getRecentProjects(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<BiddingProject> projects = projectRepository.findRecentProjects(limit);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("获取最近项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 智能问答：基于招标数据的问答
     */
    @PostMapping("/chat")
    public ApiResponse<String> chatWithBiddingData(@RequestParam String question) {
        try {
            String answer = chatService.chatWithBiddingData(question);
            return ApiResponse.success(answer);
        } catch (Exception e) {
            log.error("智能问答失败", e);
            return ApiResponse.error("问答服务暂时不可用: " + e.getMessage());
        }
    }

    /**
     * 语义搜索招标项目
     */
    @PostMapping("/search/semantic")
    public ApiResponse<List<BiddingProject>> semanticSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int topK,
            @RequestParam(defaultValue = "0.3") double similarityThreshold) {
        try {
            List<BiddingProject> projects = queryService.semanticSearch(query, topK, similarityThreshold);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            log.error("语义搜索失败", e);
            return ApiResponse.error("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 查询企业相关项目
     */
    @PostMapping("/company/projects")
    public ApiResponse<String> queryCompanyProjects(@RequestParam String companyName) {
        try {
            String result = chatService.queryCompanyProjects(companyName);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("查询企业项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 项目推荐
     */
    @PostMapping("/recommend")
    public ApiResponse<String> recommendProjects(@RequestParam String requirements) {
        try {
            String recommendation = chatService.recommendProjects(requirements);
            return ApiResponse.success(recommendation);
        } catch (Exception e) {
            log.error("项目推荐失败", e);
            return ApiResponse.error("推荐服务暂时不可用: " + e.getMessage());
        }
    }

    /**
     * 相似项目推荐
     */
    @GetMapping("/projects/{projectId}/similar")
    public ApiResponse<List<BiddingProject>> getSimilarProjects(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<BiddingProject> similarProjects = queryService.recommendSimilarProjects(projectId, limit);
            return ApiResponse.success(similarProjects);
        } catch (Exception e) {
            log.error("获取相似项目失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
}