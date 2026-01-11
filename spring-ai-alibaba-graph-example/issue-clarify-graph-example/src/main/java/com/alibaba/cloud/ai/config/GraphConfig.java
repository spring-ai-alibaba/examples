package com.alibaba.cloud.ai.config;

import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.domain.Result;
import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.KeyStrategyFactoryBuilder;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.alibaba.cloud.ai.node.ClarifyNode;
import com.alibaba.cloud.ai.node.ClarifyWaitNode;
import com.alibaba.cloud.ai.node.IntentNode;
import com.alibaba.cloud.ai.node.KnowledgeReplyNode;
import com.alibaba.cloud.ai.node.OrderNode;
import com.alibaba.cloud.ai.node.RagNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * 问题澄清工作流图配置类
 * <p>
 * 本配置类构建了一个智能机票客服工作流，包含以下核心能力：
 * <ul>
 *   <li>意图识别：判断用户是要预订机票还是咨询知识</li>
 *   <li>知识问答：基于 RAG 检索知识库回答用户问题</li>
 *   <li>槽位提取：从用户输入中提取预订所需信息</li>
 *   <li>Human in the Loop：信息不完整时中断等待用户补充</li>
 * </ul>
 *
 * @create 2025/12/14 14:29
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class GraphConfig {

    /** DashScope 文档检索器，用于 RAG 知识库检索 */
    private final DashScopeDocumentRetriever dashScopeDocumentRetriever;

    /** 聊天模型，用于意图识别、槽位提取和知识回复 */
    private final ChatModel chatModel;

    /** 槽位提取的系统提示词模板 */
    @Value("classpath:/prompts/clarify_prompt.st")
    Resource systemResource;

    /**
     * 构建问题澄清工作流状态图
     * <p>
     * 工作流结构：
     * <pre>
     * START → 意图识别 → [KNOWLEDGE] → RAG检索 → 知识回复 → END
     *                  → [BOOKING]  → 槽位提取 → 等待用户 → [信息完整] → 订票 → END
     *                                          ↑         → [信息不完整] ↓
     *                                          └──────────────────────────┘
     * </pre>
     *
     * @return StateGraph 状态图实例
     * @throws GraphStateException 图状态异常
     */
    @Bean
    public StateGraph issueClarifyGraph() throws GraphStateException {
        // ========== 1. 配置状态键策略 ==========
        // 所有状态键都使用替换策略，即新值覆盖旧值
        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
                .addPatternStrategy("user_query", new ReplaceStrategy())      // 用户输入
                .addPatternStrategy("knowledge_list", new ReplaceStrategy())  // RAG 检索结果
                .addPatternStrategy("reply", new ReplaceStrategy())           // 回复内容
                .addPatternStrategy("intent", new ReplaceStrategy())          // 识别的意图
                .addPatternStrategy("nowDate", new ReplaceStrategy())         // 当前日期
                .addPatternStrategy("slot_params", new ReplaceStrategy())     // 槽位参数
                .build();

        // ========== 2. 创建工作流节点 ==========
        // 意图识别节点：判断用户意图是 BOOKING（预订）还是 KNOWLEDGE（问答）
        var intentNode = new IntentNode(chatModel, "user_query", "intent");
        // RAG 检索节点：从知识库检索相关文档
        var ragNode = new RagNode(dashScopeDocumentRetriever, "user_query", "knowledge_list");
        // 知识回复节点：根据检索结果生成回复
        var knowledgeReplyNode = new KnowledgeReplyNode(chatModel, "user_query", "reply");
        // 槽位提取节点：从用户输入中提取出发日期、出发城市、到达城市
        var clarifyNode = new ClarifyNode(chatModel, systemResource, "user_query", "slot_params");
        // 等待节点：Human in the Loop，信息不完整时中断等待用户补充
        var clarifyWaitNode = new ClarifyWaitNode("slot_params");
        // 订票节点：槽位完整后执行模拟订票
        var orderNode = new OrderNode("slot_params", "reply");

        // ========== 3. 定义条件边：判断槽位是否完整 ==========
        // status=1 表示信息不完整，需要返回继续提取；status=2 表示信息完整，可以订票
        var edged = edge_async(state -> {
            var slotParams = state.value("slot_params", Result.class).orElse(Result.empty());
            return "1".equals(slotParams.status()) ? "back" : "next";
        });

        // ========== 4. 构建状态图 ==========
        StateGraph graph = new StateGraph(keyStrategyFactory)
                // 添加节点
                .addNode("_intent_node_", node_async(intentNode))
                .addNode("_rag_node_", node_async(ragNode))
                .addNode("_knowledge_reply_node_", node_async(knowledgeReplyNode))
                .addNode("_clarify_node_", node_async(clarifyNode))
                .addNode("_clarify_wait_node_", clarifyWaitNode)  // 注意：此节点不使用 node_async，因为它实现了 InterruptableAction
                .addNode("_order_node_", node_async(orderNode))
                // 添加边：起点 → 意图识别
                .addEdge(START, "_intent_node_")
                // 添加条件边：根据意图分流到不同分支
                .addConditionalEdges("_intent_node_", edge_async(
                        state -> state.value("intent", String.class).orElse("KNOWLEDGE")
                ), Map.of("KNOWLEDGE", "_rag_node_", "BOOKING", "_clarify_node_"))
                // 预订分支：槽位提取 → 等待用户
                .addEdge("_clarify_node_", "_clarify_wait_node_")
                // 知识问答分支：RAG检索 → 知识回复 → 结束
                .addEdge("_rag_node_", "_knowledge_reply_node_")
                .addEdge("_knowledge_reply_node_", END)
                // 等待节点的条件边：信息不完整返回槽位提取，信息完整进入订票
                .addConditionalEdges("_clarify_wait_node_", edged, Map.of("back", "_clarify_node_", "next", "_order_node_"))
                // 订票 → 结束
                .addEdge("_order_node_", END);

        // ========== 5. 输出 Mermaid 图表示（用于调试） ==========
        GraphRepresentation representation = graph.getGraph(GraphRepresentation.Type.MERMAID, "Issue Clarify Graph");
        System.out.println("======================================");
        System.out.println(representation.content());
        System.out.println("======================================");
        return graph;
    }
}
