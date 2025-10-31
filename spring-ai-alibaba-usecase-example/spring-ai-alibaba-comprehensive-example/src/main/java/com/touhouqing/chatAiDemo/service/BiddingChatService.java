package com.touhouqing.chatAiDemo.service;

import com.touhouqing.chatAiDemo.entity.graph.BiddingProject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 招标智能问答服务
 * 结合向量检索和图关系查询实现智能问答
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final BiddingQueryService queryService;

    /**
     * 智能问答：基于RAG的招标信息查询
     */
    public String chatWithBiddingData(String question) {
        try {
            // 1. 向量检索相关内容
            List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(question)
                    .topK(5)
                    .similarityThreshold(0.3)
                    .build()
            );

            // 2. 构建上下文
            String context = buildContext(relevantDocs);

            // 3. 构建提示词
            String prompt = buildChatPrompt(question, context);

            // 4. 调用大模型生成回答
            String answer = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("招标智能问答完成，问题: {}", question);
            return answer;

        } catch (Exception e) {
            log.error("招标智能问答失败", e);
            return "抱歉，查询招标信息时出现错误，请稍后重试。";
        }
    }

    /**
     * 企业相关项目查询
     */
    public String queryCompanyProjects(String companyName) {
        try {
            // 1. 通过图关系查询企业相关项目
            List<BiddingProject> projects = queryService.findProjectsByCompany(companyName);

            if (projects.isEmpty()) {
                return String.format("未找到与企业\"%s\"相关的招标项目。", companyName);
            }

            // 2. 构建项目信息摘要
            String projectSummary = projects.stream()
                    .limit(10) // 限制显示数量
                    .map(this::formatProjectInfo)
                    .collect(Collectors.joining("\n\n"));

            // 3. 生成回答
            String prompt = String.format("""
                请基于以下招标项目信息，为用户总结企业"%s"的相关项目情况：

                %s

                请从以下角度进行分析：
                1. 项目数量和规模
                2. 主要涉及的行业和地区
                3. 项目类型分布
                4. 总体特点和趋势
                """, companyName, projectSummary);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("查询企业项目失败", e);
            return "查询企业项目信息时出现错误。";
        }
    }

    /**
     * 项目推荐服务
     */
    public String recommendProjects(String requirements) {
        try {
            // 1. 语义搜索匹配需求的项目
            List<BiddingProject> projects = queryService.semanticSearch(requirements, 10, 0.4);

            if (projects.isEmpty()) {
                return "根据您的需求，暂未找到匹配的招标项目。建议您调整搜索条件或关注相关行业的最新招标信息。";
            }

            // 2. 构建项目信息
            String projectInfo = projects.stream()
                    .map(this::formatProjectInfo)
                    .collect(Collectors.joining("\n\n"));

            // 3. 生成推荐回答
            String prompt = String.format("""
                用户需求：%s

                基于以下匹配的招标项目，请为用户提供项目推荐：

                %s

                请从以下角度提供建议：
                1. 最匹配的项目及原因
                2. 项目的机会和挑战
                3. 参与建议和注意事项
                """, requirements, projectInfo);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("项目推荐失败", e);
            return "项目推荐服务暂时不可用，请稍后重试。";
        }
    }

    /**
     * 构建上下文信息
     */
    private String buildContext(List<Document> documents) {
        return documents.stream()
                .map(doc -> doc.getText())
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * 构建聊天提示词
     */
    private String buildChatPrompt(String question, String context) {
        return String.format("""
            你是一个专业的招标信息助手，请基于以下招标信息回答用户的问题。

            招标信息上下文：
            %s

            用户问题：%s

            请注意：
            1. 只基于提供的招标信息回答问题
            2. 如果信息不足，请明确说明
            3. 提供准确、有用的信息
            4. 保持专业和友好的语调
            """, context, question);
    }

    /**
     * 格式化项目信息
     */
    private String formatProjectInfo(BiddingProject project) {
        return String.format("""
            项目名称：%s
            项目编号：%s
            预算：%s %s
            招标类型：%s
            项目状态：%s
            地区：%s
            行业：%s
            发布日期：%s
            来源：%s
            """,
            project.getProjectName(),
            project.getProjectCode() != null ? project.getProjectCode() : "未知",
            project.getBudget() != null ? project.getBudget() : "未公布",
            project.getBudgetUnit() != null ? project.getBudgetUnit() : "",
            project.getBiddingType() != null ? project.getBiddingType() : "未知",
            project.getProjectStatus() != null ? project.getProjectStatus() : "未知",
            project.getRegion() != null ? project.getRegion() : "未知",
            project.getIndustry() != null ? project.getIndustry() : "未知",
            project.getPublishDate() != null ? project.getPublishDate().toString() : "未知",
            project.getSourceWebsite() != null ? project.getSourceWebsite() : "未知"
        );
    }
}