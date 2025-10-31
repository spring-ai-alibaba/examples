package com.touhouqing.chatAiDemo.component;

import com.touhouqing.chatAiDemo.entity.ProcurementProject;
import com.touhouqing.chatAiDemo.entity.ProcurementOrganization;
import com.touhouqing.chatAiDemo.repository.ProcurementProjectRepository;
import com.touhouqing.chatAiDemo.repository.ProcurementOrganizationRepository;
import com.touhouqing.chatAiDemo.service.ProcurementAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcurementDataPipeline implements Pipeline {
    
    private final ProcurementProjectRepository projectRepository;
    private final ProcurementOrganizationRepository organizationRepository;
    private final ProcurementAIService aiService;
    
    @Override
    public void process(ResultItems resultItems, Task task) {
        try {
            String type = resultItems.get("type");
            if (!"procurement_detail".equals(type)) {
                return;
            }
            
            String title = resultItems.get("title");
            String publishDate = resultItems.get("publishDate");
            String publishSource = resultItems.get("publishSource");
            String content = resultItems.get("content");
            String tableData = resultItems.get("tableData");
            String url = resultItems.get("url");
            
            log.info("开始处理采购数据: {}", title);
            
            // 检查是否已存在
            Optional<ProcurementProject> existingProject = projectRepository.findBySourceUrl(url);
            if (existingProject.isPresent()) {
                log.info("项目已存在，跳过: {}", title);
                return;
            }
            
            // 使用AI分析数据
            String aiAnalysis = aiService.analyzeProcurementData(title, content, tableData);
            
            // 解析表格数据提取项目信息
            ProcurementProjectInfo projectInfo = parseProjectInfo(title, content, tableData, aiAnalysis);
            
            // 创建或获取采购单位
            ProcurementOrganization organization = createOrGetOrganization(publishSource, aiAnalysis);
            
            // 创建采购项目
            ProcurementProject project = new ProcurementProject();
            project.setProjectName(projectInfo.getProjectName());
            project.setDescription(projectInfo.getDescription());
            project.setBudget(projectInfo.getBudget());
            project.setBudgetUnit(projectInfo.getBudgetUnit());
            project.setProcurementTime(projectInfo.getProcurementTime());
            project.setPublishDate(publishDate);
            project.setSourceUrl(url);
            project.setProcurementPolicy(projectInfo.getProcurementPolicy());
            project.setRemarks(projectInfo.getRemarks());
            project.setRawContent(content);
            project.setAiAnalysis(aiAnalysis);
            project.setCreatedAt(LocalDateTime.now());
            project.setUpdatedAt(LocalDateTime.now());
            project.setOrganization(organization);
            
            // 保存到图数据库
            projectRepository.save(project);
            
            log.info("成功保存采购项目: {}", title);
            
        } catch (Exception e) {
            log.error("处理采购数据出错", e);
        }
    }
    
    /**
     * 解析项目信息
     */
    private ProcurementProjectInfo parseProjectInfo(String title, String content, String tableData, String aiAnalysis) {
        ProcurementProjectInfo info = new ProcurementProjectInfo();
        
        // 从标题中提取项目名称
        info.setProjectName(extractProjectName(title));
        
        // 从表格数据中提取预算信息
        info.setBudget(extractBudget(tableData));
        info.setBudgetUnit("万元");
        
        // 从表格数据中提取采购时间
        info.setProcurementTime(extractProcurementTime(tableData));
        
        // 从表格数据中提取采购政策
        info.setProcurementPolicy(extractProcurementPolicy(tableData));
        
        // 从内容中提取描述
        info.setDescription(extractDescription(content));
        
        // 从表格数据中提取备注
        info.setRemarks(extractRemarks(tableData));
        
        return info;
    }
    
    /**
     * 创建或获取采购单位
     */
    private ProcurementOrganization createOrGetOrganization(String publishSource, String aiAnalysis) {
        if (publishSource == null || publishSource.trim().isEmpty()) {
            publishSource = "未知单位";
        }
        
        Optional<ProcurementOrganization> existing = organizationRepository.findByName(publishSource);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // 创建新的采购单位
        ProcurementOrganization organization = new ProcurementOrganization();
        organization.setName(publishSource);
        organization.setType(determineOrganizationType(publishSource, aiAnalysis));
        organization.setLevel(determineOrganizationLevel(publishSource, aiAnalysis));
        organization.setCreatedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());
        
        return organizationRepository.save(organization);
    }
    
    // 辅助方法
    private String extractProjectName(String title) {
        if (title != null && title.contains("政府采购意向公告")) {
            return title.replace("政府采购意向公告", "").trim();
        }
        return title;
    }
    
    private Double extractBudget(String tableData) {
        if (tableData == null) return null;
        
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*万元");
        Matcher matcher = pattern.matcher(tableData);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return null;
    }
    
    private String extractProcurementTime(String tableData) {
        if (tableData == null) return null;
        
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2})");
        Matcher matcher = pattern.matcher(tableData);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private String extractProcurementPolicy(String tableData) {
        if (tableData == null) return null;
        
        if (tableData.contains("非专门面向中小企业")) {
            return "非专门面向中小企业";
        } else if (tableData.contains("专门面向中小企业")) {
            return "专门面向中小企业";
        }
        return null;
    }
    
    private String extractDescription(String content) {
        if (content == null) return null;
        
        // 提取主要目标描述
        Pattern pattern = Pattern.compile("采购标的需实现的主要目标：([^。]+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return content.length() > 500 ? content.substring(0, 500) + "..." : content;
    }
    
    private String extractRemarks(String tableData) {
        if (tableData == null) return null;
        
        String[] lines = tableData.split("\n");
        for (String line : lines) {
            if (line.contains("备注") || line.contains("其他")) {
                return line.trim();
            }
        }
        return null;
    }
    
    private String determineOrganizationType(String name, String aiAnalysis) {
        if (name.contains("医院")) return "医院";
        if (name.contains("学校") || name.contains("大学")) return "教育机构";
        if (name.contains("政府") || name.contains("局") || name.contains("委")) return "政府机关";
        return "其他";
    }
    
    private String determineOrganizationLevel(String name, String aiAnalysis) {
        if (name.contains("天津市")) return "市级";
        if (name.contains("区") || name.contains("县")) return "区县级";
        return "其他";
    }
    
    // 内部类用于存储解析的项目信息
    private static class ProcurementProjectInfo {
        private String projectName;
        private String description;
        private Double budget;
        private String budgetUnit;
        private String procurementTime;
        private String procurementPolicy;
        private String remarks;
        
        // getters and setters
        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getBudget() { return budget; }
        public void setBudget(Double budget) { this.budget = budget; }
        public String getBudgetUnit() { return budgetUnit; }
        public void setBudgetUnit(String budgetUnit) { this.budgetUnit = budgetUnit; }
        public String getProcurementTime() { return procurementTime; }
        public void setProcurementTime(String procurementTime) { this.procurementTime = procurementTime; }
        public String getProcurementPolicy() { return procurementPolicy; }
        public void setProcurementPolicy(String procurementPolicy) { this.procurementPolicy = procurementPolicy; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }
}
