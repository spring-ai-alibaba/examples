# 前置条件 - 必读！

## ⚠️ 重要提示

本示例依赖 **Spring AI Alibaba Graph Core 1.1.0.0-SNAPSHOT** 版本才能运行，该版本包含 `InterruptableAction` 功能。

由于这是 SNAPSHOT 版本，**你需要先在本地构建并安装主仓库**。

## 📋 安装步骤

### 1. 构建主仓库

在运行本示例之前，请先执行以下命令：

```bash
# 进入主仓库目录
cd D:\spring-ai-alibaba

# 清理并安装到本地 Maven 仓库
mvn clean install -DskipTests

# 等待构建完成（可能需要几分钟）
```

### 2. 验证安装

检查本地 Maven 仓库中是否有对应版本：

```bash
# Windows PowerShell
ls ~/.m2/repository/com/alibaba/cloud/ai/spring-ai-alibaba-graph-core/1.1.0.0-SNAPSHOT/

# 或者查看目录
dir %USERPROFILE%\.m2\repository\com\alibaba\cloud\ai\spring-ai-alibaba-graph-core\1.1.0.0-SNAPSHOT\
```

应该能看到 jar 文件，例如：`spring-ai-alibaba-graph-core-1.1.0.0-SNAPSHOT.jar`

### 3. 运行示例

安装完成后，就可以运行本示例了：

```bash
cd D:\examples\spring-ai-alibaba-graph-example\interruptable-action-example

# 编译
mvn clean compile

# 运行
mvn spring-boot:run
```

## 🔍 常见问题

### Q: 为什么需要安装主仓库？

A: `InterruptableAction` 是 1.1.0.0-SNAPSHOT 的新功能，还没有发布到 Maven 中央仓库，需要从源码构建。

### Q: 能否使用已发布的稳定版本？

A: 不行。稳定版（1.0.0.3 及更早版本）不包含 `InterruptableAction` 功能。

### Q: 构建主仓库失败怎么办？

A: 
1. 确保 JDK 17+ 已安装
2. 确保 Maven 3.6+ 已安装
3. 检查网络连接（需要下载依赖）
4. 查看构建日志中的错误信息

### Q: 如果不想构建主仓库？

A: 可以等待官方发布正式版本后再使用本示例。或者查看主仓库中的测试用例：
- `D:\spring-ai-alibaba\spring-ai-alibaba-graph-core\src\test\java\com\alibaba\cloud\ai\graph\InterruptionTest.java`
- `D:\spring-ai-alibaba\spring-ai-alibaba-agent-framework\src\main\java\com\alibaba\cloud\ai\graph\agent\hook\hip\HumanInTheLoopHook.java`

## 📚 参考资源

- 主仓库地址：https://github.com/alibaba/spring-ai-alibaba
- Graph Core 源码：`D:\spring-ai-alibaba\spring-ai-alibaba-graph-core`
- InterruptableAction 接口：`spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/action/InterruptableAction.java`

## ✅ 安装检查清单

在运行本示例前，请确认：

- [ ] JDK 17+ 已安装
- [ ] Maven 3.6+ 已安装
- [ ] 主仓库已 clone 到本地
- [ ] 主仓库已成功构建（`mvn clean install -DskipTests`）
- [ ] 本地 Maven 仓库中有 `spring-ai-alibaba-graph-core-1.1.0.0-SNAPSHOT.jar`

---

**构建主仓库是强制性的前置步骤！**

