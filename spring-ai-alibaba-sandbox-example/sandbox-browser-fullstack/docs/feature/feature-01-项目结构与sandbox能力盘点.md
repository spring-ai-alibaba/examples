# feature-01 项目结构与sandbox能力盘点

## 目标
梳理当前示例项目中关键文件/目录职责，并识别其使用到的 sandbox 能力与边界。

## 验收标准
- 给出 backend/frontend/部署文件的作用说明。
- 给出 sandbox 相关核心类与调用链。
- 标注与实现一致性相关的风险点。

## 接口契约 / Mock
本任务为代码与文档盘点，不新增接口契约。

## 盘点结果（摘要）
- 项目为双子目录：`backend`（Spring Boot）+ `frontend`（React+Vite）。
- sandbox 主链路：`SandboxConfiguration` 初始化 `SandboxService` -> `BrowserUseAgent` 创建 `BrowserSandbox` -> 注册 `ToolkitInit.BrowserNavigateTool` 给 `ReactAgent`。
- 可见能力：浏览器会话隔离、容器生命周期管理、VNC 桌面可视化、LLM 驱动浏览器导航。
- 边界：当前代码未暴露通用命令执行/文件系统读写工具，主要是浏览器自动化能力。

## 验收与测试
- 验证方式：静态代码审阅。
- 结果：已完成盘点，未执行运行时测试。

## 改动历史
| Change ID | DateTime | Summary | Files | Verification | Notes |
|---|---|---|---|---|---|
| CHG-20260214-001 | 2026-02-14 14:00 | 初始化任务台账与本任务盘点文档 | docs/feature.csv; docs/_history.md; docs/feature/feature-01-项目结构与sandbox能力盘点.md | 未测 | 等待评审 |
