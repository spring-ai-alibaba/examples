# InterruptableAction 示例

本案例基于 Spring AI Alibaba Graph 框架实现

## 概述

`InterruptableAction` 是 Spring AI Alibaba Graph Core 1.1.0+ 版本引入的新特性，允许节点根据运行时状态动态决定是否中断工作流执行。

相比传统的 `interruptBefore`（静态配置，编译时决定），`InterruptableAction` 支持：
- 运行时动态判断是否需要中断
- 基于状态、配置进行条件判断
- 携带丰富的中断元数据

> **⚠️ 重要说明**
> 
> 本示例为 `InterruptableAction` 功能的代码演示，展示了正确的实现方式和 API 用法。
> 
> 当前框架版本中，完整的中断功能可能需要进一步的框架支持才能完全生效。本示例可作为学习参考和功能验证使用。

## 前置条件

**重要：本示例依赖 Spring AI Alibaba Graph Core 1.1.0.0-SNAPSHOT 版本，需要先构建主仓库。**

```bash
# 进入主仓库目录
cd D:\spring-ai-alibaba

# 构建并安装到本地 Maven 仓库
mvn clean install -DskipTests -pl spring-ai-alibaba-graph-core -am
```

详细说明请查看 [PREREQUISITE.md](./PREREQUISITE.md)

## 示例场景

本项目包含两个典型业务场景：

### 1. 订单审批场景

当订单金额超过 10000 元时，自动中断流程等待人工审批。

**工作流程**：
```
START → order_approval → final_process → END
         ↓ (金额 > 10000)
         中断，等待审批
         ↓ (审批后)
         继续执行
```

**核心实现**：
```java
public class OrderApprovalNode implements NodeAction, InterruptableAction {
    private static final double APPROVAL_THRESHOLD = 10000.0;

    @Override
    public Optional<InterruptionMetadata> interrupt(
            String nodeId, OverAllState state, RunnableConfig config) {
        
        // 检查是否已有审批反馈
        Optional<Object> feedback = config.metadata(
            RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY
        );
        if (feedback.isPresent()) {
            return Optional.empty(); // 已审批，继续执行
        }
        
        // 检查订单金额
        Double orderAmount = state.value("order_amount", 0.0);
        if (orderAmount > APPROVAL_THRESHOLD) {
            return Optional.of(
                InterruptionMetadata.builder(nodeId, state)
                    .addMetadata("reason", "订单金额超过阈值，需要人工审批")
                    .addMetadata("order_amount", orderAmount)
                    .build()
            );
        }
        
        return Optional.empty();
    }
}
```

### 2. 敏感操作确认场景

执行敏感操作（如删除用户、修改系统配置）前需要人工确认。

**工作流程**：
```
START → sensitive_operation → final_process → END
         ↓ (敏感操作)
         中断，等待确认
         ↓ (确认后)
         继续执行
```

**核心实现**：
```java
public class SensitiveOperationNode implements NodeAction, InterruptableAction {
    private static final Set<String> SENSITIVE_OPERATIONS = Set.of(
        "delete_user", "delete_database", "modify_system_config"
    );

    @Override
    public Optional<InterruptionMetadata> interrupt(
            String nodeId, OverAllState state, RunnableConfig config) {
        
        // 检查是否为敏感操作
        String operation = state.value("operation", "");
        if (SENSITIVE_OPERATIONS.contains(operation)) {
            return Optional.of(
                InterruptionMetadata.builder(nodeId, state)
                    .addMetadata("reason", "敏感操作需要人工确认")
                    .addMetadata("operation", operation)
                    .addMetadata("risk_level", getRiskLevel(operation))
                    .build()
            );
        }
        
        return Optional.empty();
    }
}
```

## 快速开始

### 1. 运行应用

```bash
cd interruptable-action-example
mvn spring-boot:run
```

应用启动在 `http://localhost:8080`

### 2. 测试订单审批场景

> **注意**：当前版本中，中断功能可能无法完全生效，工作流会直接执行完成。这是框架层面的已知问题，不影响代码示例的正确性。

测试普通订单（金额 < 10000，直接处理）：
```bash
curl "http://localhost:8080/interruptable/order/process?orderId=ORD001&amount=5000&threadId=test-1"
```

测试高额订单（金额 > 10000，应触发中断）：
```bash
curl "http://localhost:8080/interruptable/order/process?orderId=ORD002&amount=15000&threadId=test-2"
```

审批后恢复执行：
```bash
curl -X POST "http://localhost:8080/interruptable/order/resume?approved=true&threadId=test-2"
```

### 3. 测试敏感操作场景

> **注意**：当前版本中，中断功能可能无法完全生效，工作流会直接执行完成。这是框架层面的已知问题，不影响代码示例的正确性。

执行普通操作（非敏感，直接执行）：
```bash
curl -X POST "http://localhost:8080/interruptable/operation/execute?operation=normal_task&params=userId:123&threadId=test-3"
```

执行敏感操作（应触发中断）：
```bash
curl -X POST "http://localhost:8080/interruptable/operation/execute?operation=delete_user&params=userId:123&threadId=test-4"
```

确认后继续执行：
```bash
curl -X POST "http://localhost:8080/interruptable/operation/confirm?confirmed=true&threadId=test-4"
```

## 核心接口

```java
public interface InterruptableAction {
    /**
     * 判断是否需要中断工作流
     * @return Optional.of(InterruptionMetadata) - 需要中断
     *         Optional.empty() - 继续执行
     */
    Optional<InterruptionMetadata> interrupt(
        String nodeId, 
        OverAllState state, 
        RunnableConfig config
    );
}
```

## 执行流程

```
1. NodeExecutor 执行节点前
   ↓
2. 检查节点是否实现 InterruptableAction
   ↓
3. 调用 interrupt() 方法
   ↓
4. 返回值判断
   ├─ Optional.of(metadata) → 中断执行，保存状态
   └─ Optional.empty() → 继续执行，调用 apply() 方法
```

## 恢复执行

中断后通过 `HUMAN_FEEDBACK_METADATA_KEY` 传递反馈信息恢复执行：

```java
Map<String, Object> feedback = Map.of("approved", true);

RunnableConfig resumeConfig = RunnableConfig.builder()
    .threadId(originalThreadId)
    .addMetadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY, feedback)
    .build();

compiledGraph.stream(null, resumeConfig);
```

## 与 interruptBefore 的对比

| 特性 | interruptBefore | InterruptableAction |
|------|----------------|---------------------|
| 配置方式 | 编译时静态配置 | 节点内部动态判断 |
| 条件判断 | 无条件中断 | 支持复杂条件判断 |
| 元数据支持 | 无 | 丰富的 InterruptionMetadata |
| 适用场景 | 简单固定中断点 | 复杂的动态中断逻辑 |

## 最佳实践

1. 在 `interrupt()` 方法中先检查反馈，避免重复中断
2. 使用唯一的 `threadId` 区分不同工作流实例
3. 在 `InterruptionMetadata` 中传递足够的上下文信息

## 相关资源

- [Spring AI Alibaba 官网](https://java2ai.com)
- [Spring AI Alibaba GitHub](https://github.com/alibaba/spring-ai-alibaba)
- [完整测试用例](./test-api.http)
