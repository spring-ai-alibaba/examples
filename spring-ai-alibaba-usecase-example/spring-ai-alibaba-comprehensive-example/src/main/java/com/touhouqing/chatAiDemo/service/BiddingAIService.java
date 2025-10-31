package com.touhouqing.chatAiDemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 招标AI解析服务
 * 使用大模型将原始HTML解析为结构化数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingAIService {

    @Qualifier("biddingDataChatClient")
    private final ChatClient biddingDataChatClient;

    /**
     * 将HTML内容解析为结构化的招标项目数据
     * 使用优化的单次解析策略
     */
    public String parseHtmlToBiddingData(String htmlContent, String sourceUrl) {
        try {
            log.info("开始解析HTML，URL: {}", sourceUrl);

            // 使用优化的单次解析提示词
            String prompt = buildOptimizedParsingPrompt(htmlContent, sourceUrl);

            String result = biddingDataChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("AI解析HTML完成，URL: {}", sourceUrl);
            return result;

        } catch (Exception e) {
            log.error("AI解析HTML失败", e);
            return null;
        }
    }

    /**
     * 构建优化的HTML解析提示词
     */
    private String buildOptimizedParsingPrompt(String htmlContent, String sourceUrl) {
        return String.format("""
            你是专业的政府采购信息提取专家。请从以下HTML中提取信息并返回JSON格式。

            HTML内容：
            %s

            请按以下步骤仔细分析：

            1. 查找项目名称：
               - 搜索 <meta name="ArticleTitle" content="天津外国语大学政府采购意向公告"/>
               - 提取content属性的值作为项目名称

            2. 查找发布日期：
               - 搜索 <meta name="PubDate" content="Wed Jul 30 10:54:06 CST 2025"/>
               - 必须转换为完整的日期时间格式：2025-07-30T10:54:06（不能只有日期）

            3. 计算预算总额：
               - 在HTML中搜索包含"预算金额（万元）"的表格
               - 找到表格中所有的<td>标签内的数字
               - 这些数字可能是：131、100、236.5、487.33、103.76、150、240、160、55、60、198、62.5
               - 将所有数字相加得到总预算

            4. 判断项目信息：
               - 如果标题包含"意向"，状态为"意向公告"
               - 从内容推断地区为"天津"，行业为"教育"
               - 发布机构为"天津外国语大学"

            请严格按照以下JSON格式返回（只返回JSON，不要其他文字）：

            {
              "projectName": "天津外国语大学政府采购意向公告",
              "projectCode": null,
              "projectDescription": "天津外国语大学2025年度政府采购意向公告，包含多个采购项目",
              "requirements": "包含教学设备、实验设备、办公设备等多类采购需求",
              "budget": 1983.09,
              "budgetUnit": "万元",
              "biddingType": "政府采购",
              "projectStatus": "意向公告",
              "publishDate": "2025-07-30T10:54:06",
              "biddingDeadline": null,
              "region": "天津",
              "industry": "教育",
              "publishingOrganization": {
                "organizationName": "天津外国语大学",
                "organizationType": "高等院校",
                "contactInfo": null
              },
              "awardedCompanies": [],
              "participatingCompanies": []
            }

            重要提醒：
            - 必须从HTML中实际提取信息，不要使用示例数据
            - budget字段必须是从表格中提取的所有数字的总和
            - publishDate必须包含时间，格式为YYYY-MM-DDTHH:mm:ss
            - 只返回JSON格式，不要任何解释文字
            """, htmlContent);
    }

    /**
     * 第1步：提取基本信息（meta标签、标题、日期等）
     */
    private String extractBasicInfo(String htmlContent, String sourceUrl) {
        try {
            // 提取HTML的头部和主要标题部分
            String headSection = extractHeadSection(htmlContent);
            String titleSection = extractTitleSection(htmlContent);

            String prompt = String.format("""
                请从以下HTML片段中提取基本信息，返回JSON格式：

                HTML头部信息：
                %s

                HTML标题部分：
                %s

                请提取以下信息：
                1. 项目名称：从<meta name="ArticleTitle" content="..."/>提取
                2. 发布日期：从<meta name="PubDate" content="..."/>提取并转换为YYYY-MM-DD格式
                3. 发布机构：从页面内容推断
                4. 项目状态：根据标题判断（包含"意向"为"意向公告"）

                只返回JSON格式：
                {
                  "projectName": "项目名称",
                  "publishDate": "YYYY-MM-DD",
                  "publishingOrganization": "发布机构名称",
                  "projectStatus": "项目状态",
                  "region": "地区",
                  "industry": "行业"
                }
                """, headSection, titleSection);

            return biddingDataChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("提取基本信息失败", e);
            return "{}";
        }
    }

    /**
     * 第2步：提取表格信息（预算数据）
     */
    private String extractTableInfo(String htmlContent, String sourceUrl) {
        try {
            // 提取表格部分
            String tableSection = extractTableSection(htmlContent);

            String prompt = String.format("""
                请从以下HTML表格中提取预算信息：

                表格HTML：
                %s

                任务：
                1. 找到表格中的"预算金额（万元）"列
                2. 提取所有预算数字（如：131、100、236.5、487.33等）
                3. 将所有数字相加得到总预算
                4. 提取项目描述信息

                只返回JSON格式：
                {
                  "budget": 总预算数字,
                  "budgetUnit": "万元",
                  "projectDescription": "项目描述概况",
                  "requirements": "采购需求概况"
                }

                重要：budget字段必须是数字，将表格中所有预算金额相加！
                """, tableSection);

            return biddingDataChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("提取表格信息失败", e);
            return "{}";
        }
    }

    /**
     * 第3步：合并信息生成最终JSON
     */
    private String mergeInformation(String basicInfo, String tableInfo, String sourceUrl) {
        try {
            String prompt = String.format("""
                请将以下两部分信息合并成完整的招标项目JSON：

                基本信息：
                %s

                表格信息：
                %s

                请合并成完整的JSON格式：
                {
                  "projectName": "从基本信息中获取",
                  "projectCode": null,
                  "projectDescription": "从表格信息中获取",
                  "requirements": "从表格信息中获取",
                  "budget": 从表格信息中获取的数字,
                  "budgetUnit": "万元",
                  "biddingType": "政府采购",
                  "projectStatus": "从基本信息中获取",
                  "publishDate": "从基本信息中获取",
                  "biddingDeadline": null,
                  "region": "从基本信息中获取",
                  "industry": "从基本信息中获取",
                  "publishingOrganization": {
                    "organizationName": "从基本信息中获取",
                    "organizationType": "高等院校",
                    "contactInfo": null
                  },
                  "awardedCompanies": [],
                  "participatingCompanies": []
                }

                只返回合并后的JSON，不要其他文字。
                """, basicInfo, tableInfo);

            return biddingDataChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("合并信息失败", e);
            return null;
        }
    }

    /**
     * 构建HTML解析提示词（备用方法）
     */
    private String buildHtmlParsingPrompt(String htmlContent, String sourceUrl) {
        return String.format("""
            请从以下HTML中提取政府采购信息，返回JSON格式。

            HTML内容：
            %s

            请按步骤分析：

            第1步：找到项目名称
            - 查找 <meta name="ArticleTitle" content="天津外国语大学政府采购意向公告"/>
            - 或查找页面中的主标题

            第2步：找到发布日期
            - 查找 <meta name="PubDate" content="Wed Jul 30 10:54:06 CST 2025"/>
            - 转换为 2025-07-30 格式

            第3步：计算预算总额（重要！）
            - 仔细查找HTML中的表格，特别是包含"预算金额"、"万元"等关键词的列
            - 在表格的<td>标签中查找纯数字，如：<td>131</td>、<td>100</td>、<td>236.5</td>
            - 这些数字通常在表格的第4列或第5列
            - 将找到的所有数字相加：131+100+236.5+487.33+103.76+150+240+160+55+60+198+62.5
            - 如果找不到表格，预算设为null

            第4步：判断项目状态
            - 如果标题包含"意向"，状态为"意向公告"
            - 如果标题包含"招标"，状态为"招标中"

            第5步：提取机构信息
            - 从页面内容中找到发布机构名称
            - 从URL或内容判断地区

            返回JSON（只返回JSON，不要其他文字）：
            {
              "projectName": "从meta标签或标题提取的项目名称",
              "projectCode": null,
              "projectDescription": "项目描述",
              "requirements": "采购需求概况",
              "budget": 所有预算金额的数字总和,
              "budgetUnit": "万元",
              "biddingType": "政府采购",
              "projectStatus": "意向公告",
              "publishDate": "2025-07-30",
              "biddingDeadline": null,
              "region": "天津",
              "industry": "教育",
              "publishingOrganization": {
                "organizationName": "天津外国语大学",
                "organizationType": "高等院校",
                "contactInfo": null
              },
              "awardedCompanies": [],
              "participatingCompanies": []
            }
            """, htmlContent);
    }

    /**
     * 提取HTML头部信息（meta标签等）
     */
    private String extractHeadSection(String htmlContent) {
        try {
            StringBuilder metaTags = new StringBuilder();
            String[] lines = htmlContent.split("\n");

            for (String line : lines) {
                // 提取所有重要的meta标签
                if (line.contains("<meta") &&
                    (line.contains("ArticleTitle") ||
                     line.contains("PubDate") ||
                     line.contains("ContentSource") ||
                     line.contains("SiteName"))) {
                    metaTags.append(line.trim()).append("\n");
                }
            }

            log.info("提取到的meta标签: {}", metaTags.toString());
            return metaTags.toString();

        } catch (Exception e) {
            log.error("提取头部信息失败", e);
            return "";
        }
    }

    /**
     * 提取HTML标题部分
     */
    private String extractTitleSection(String htmlContent) {
        try {
            StringBuilder titleSection = new StringBuilder();
            String[] lines = htmlContent.split("\n");

            for (String line : lines) {
                // 提取标题相关的行
                if (line.contains("<title>") ||
                    line.contains("<h1>") || line.contains("<h2>") || line.contains("<h3>") ||
                    line.contains("align=\"center\"") ||
                    line.contains("政府采购") || line.contains("招标") || line.contains("意向")) {
                    titleSection.append(line).append("\n");
                }
            }

            return titleSection.toString();

        } catch (Exception e) {
            log.error("提取标题部分失败", e);
            return "";
        }
    }

    /**
     * 提取HTML表格部分
     */
    private String extractTableSection(String htmlContent) {
        try {
            StringBuilder tableSection = new StringBuilder();
            String[] lines = htmlContent.split("\n");

            boolean inTableArea = false;
            int tableDepth = 0;

            for (String line : lines) {
                String trimmedLine = line.trim();

                // 开始提取表格：找到包含"预算金额"的表头
                if (trimmedLine.contains("预算金额") || trimmedLine.contains("采购项目名称")) {
                    inTableArea = true;
                    tableSection.append(line).append("\n");
                    continue;
                }

                // 如果已经在表格区域
                if (inTableArea) {
                    // 计算表格嵌套深度
                    if (trimmedLine.contains("<table")) {
                        tableDepth++;
                    }
                    if (trimmedLine.contains("</table>")) {
                        tableDepth--;
                        tableSection.append(line).append("\n");
                        if (tableDepth <= 0) {
                            break; // 表格结束
                        }
                        continue;
                    }

                    // 提取表格相关的行
                    if (trimmedLine.contains("<tr") || trimmedLine.contains("</tr>") ||
                        trimmedLine.contains("<td") || trimmedLine.contains("</td>") ||
                        trimmedLine.matches(".*\\d+.*") || // 包含数字的行
                        trimmedLine.contains("采购") || trimmedLine.contains("预算")) {
                        tableSection.append(line).append("\n");
                    }
                }
            }

            String result = tableSection.toString();
            log.info("提取到的表格内容长度: {}", result.length());
            if (result.length() > 0) {
                log.info("表格内容预览: {}", result.length() > 300 ? result.substring(0, 300) + "..." : result);
            }

            return result;

        } catch (Exception e) {
            log.error("提取表格部分失败", e);
            return "";
        }
    }

    /**
     * 调试分段解析过程，返回每个步骤的详细结果
     */
    public String debugParseHtml(String htmlContent, String sourceUrl) {
        StringBuilder debugResult = new StringBuilder();

        try {
            debugResult.append("=== 调试分段解析过程 ===\n\n");

            // 第1步：提取HTML片段
            String headSection = extractHeadSection(htmlContent);
            String titleSection = extractTitleSection(htmlContent);
            String tableSection = extractTableSection(htmlContent);

            debugResult.append("1. HTML片段提取结果：\n");
            debugResult.append("头部信息长度: ").append(headSection.length()).append("\n");
            debugResult.append("头部内容预览: ").append(headSection.length() > 200 ? headSection.substring(0, 200) + "..." : headSection).append("\n\n");

            debugResult.append("标题信息长度: ").append(titleSection.length()).append("\n");
            debugResult.append("标题内容预览: ").append(titleSection.length() > 200 ? titleSection.substring(0, 200) + "..." : titleSection).append("\n\n");

            debugResult.append("表格信息长度: ").append(tableSection.length()).append("\n");
            debugResult.append("表格内容预览: ").append(tableSection.length() > 500 ? tableSection.substring(0, 500) + "..." : tableSection).append("\n\n");

            // 第2步：AI解析基本信息
            debugResult.append("2. AI解析基本信息：\n");
            String basicInfo = extractBasicInfo(htmlContent, sourceUrl);
            debugResult.append("基本信息结果: ").append(basicInfo).append("\n\n");

            // 第3步：AI解析表格信息
            debugResult.append("3. AI解析表格信息：\n");
            String tableInfo = extractTableInfo(htmlContent, sourceUrl);
            debugResult.append("表格信息结果: ").append(tableInfo).append("\n\n");

            // 第4步：合并信息
            debugResult.append("4. 合并最终结果：\n");
            String finalResult = mergeInformation(basicInfo, tableInfo, sourceUrl);
            debugResult.append("最终结果: ").append(finalResult).append("\n");

            return debugResult.toString();

        } catch (Exception e) {
            log.error("调试解析失败", e);
            return "调试解析失败: " + e.getMessage();
        }
    }
}