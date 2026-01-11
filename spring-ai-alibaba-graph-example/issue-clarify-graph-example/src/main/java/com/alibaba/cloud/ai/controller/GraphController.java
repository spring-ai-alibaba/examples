package com.alibaba.cloud.ai.controller;

import com.alibaba.cloud.ai.domain.Result;
import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.StateSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;

/**
 * 机票预订工作流控制器
 * <p>
 * 提供 REST API 接口，支持：
 * <ul>
 *   <li>新会话：首次请求时初始化状态并执行工作流</li>
 *   <li>多轮对话：基于 chatId 恢复历史状态，继续执行工作流</li>
 *   <li>Human in the Loop：工作流中断后，用户补充信息可恢复执行</li>
 * </ul>
 *
 * @create 2025/12/14 15:23
 */
@RestController
@Slf4j
@RequestMapping("/booking")
public class GraphController {

    /**
     * 编译后的工作流图，支持状态持久化和恢复
     */
    private final CompiledGraph compiledGraph;

    /**
     * 构造函数：编译状态图并配置内存状态保存器
     *
     * @param issueClarifyGraph 注入的状态图 Bean
     * @throws GraphStateException 图编译异常
     */
    public GraphController(StateGraph issueClarifyGraph) throws GraphStateException {
        // 创建内存保存器，用于持久化会话状态（支持多轮对话）
        var saver = new MemorySaver();
        var compileConfig = CompileConfig.builder()
                .saverConfig(SaverConfig.builder()
                        .register(saver)
                        .build())
                .build();
        // 编译状态图，生成可执行的工作流
        this.compiledGraph = issueClarifyGraph.compile(compileConfig);
    }

    /**
     * 机票预订/问答接口
     * <p>
     * 根据 chatId 自动判断是新会话还是继续已有会话：
     * <ul>
     *   <li>新会话：初始化状态，从意图识别开始执行</li>
     *   <li>已有会话：从中断点恢复执行，用户输入会更新到状态中</li>
     * </ul>
     *
     * @param query  用户输入的问题或信息
     * @param chatId 会话标识，相同 chatId 的请求会共享会话状态
     * @return 工作流执行结果（回复内容）
     */
    @GetMapping("/call")
    public Mono<String> call(
            @RequestParam(value = "query", defaultValue = "你好，你可以做什么") String query,
            @RequestParam(value = "chatId", defaultValue = "chat-id-10001") String chatId) {
        // 构建运行时配置，threadId 用于标识会话
        var config = RunnableConfig.builder()
                .threadId(chatId)
                .build();

        // 执行工作流并返回最终的 reply 字段
        return getExecutionStream(query, config)
                .last()
                .map(output ->
                        output.state().value("reply", String.class).orElse("完成")
                );
    }

    /**
     * 获取工作流执行流
     * <p>
     * 根据是否存在历史状态，决定是恢复执行还是开始新会话
     *
     * @param query  用户输入
     * @param config 运行时配置（包含 threadId）
     * @return 节点输出的响应式流
     */
    private Flux<NodeOutput> getExecutionStream(String query,
                                                RunnableConfig config) {
        // 尝试获取历史状态快照
        return compiledGraph.stateOf(config)
                // 有历史状态：从检查点恢复
                .map(snapshot -> resumeFromCheckpoint(snapshot, query, config))
                // 无历史状态：开始新会话
                .orElseGet(() -> startNewConversation(query, config));
    }

    /**
     * 从检查点恢复执行（多轮对话核心逻辑）
     * <p>
     * 当工作流在 ClarifyWaitNode 中断后，用户再次发送消息时：
     *
     * @param snapshot 历史状态快照
     * @param query    用户新输入
     * @param config   运行时配置
     * @return 节点输出的响应式流
     */
    private Flux<NodeOutput> resumeFromCheckpoint(StateSnapshot snapshot, String query, RunnableConfig config) {
        String nextNode = snapshot.next();
        // 如果流程已完成（无下一节点或到达 END），则当作新对话处理
        if (nextNode == null || nextNode.isEmpty() || END.equals(nextNode)) {
            return startNewConversation(query, config);
        }
        try {
            // 设置恢复标记，ClarifyWaitNode 会检查此标记来判断是否应该中断
            config.context().put(RunnableConfig.STATE_UPDATE_METADATA_KEY, Boolean.TRUE);
            // 更新状态：将用户新输入写入 user_query
            RunnableConfig runnableConfig = this.compiledGraph.updateState(config, Map.of(
                    "user_query", query
            ), null);
            // 从中断点继续执行工作流
            return this.compiledGraph.stream(null, runnableConfig)
                    .doOnNext(event -> {
                        System.out.println("节点输出: " + event);
                    })
                    .doOnError(error -> System.err.println("流错误: " + error.getMessage()))
                    .doOnComplete(() -> System.out.println("流完成"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开始新会话
     * <p>
     * 初始化工作流状态，包含所有必要的状态键：
     * <ul>
     *   <li>user_query: 用户输入</li>
     *   <li>knowledge_list: RAG 检索结果（初始为空）</li>
     *   <li>reply: 回复内容（初始为空）</li>
     *   <li>intent: 识别的意图（初始为空）</li>
     *   <li>slot_params: 槽位参数（初始为空槽位）</li>
     *   <li>nowDate: 当前日期（用于相对日期计算）</li>
     * </ul>
     *
     * @param query  用户输入
     * @param config 运行时配置
     * @return 节点输出的响应式流
     */
    private Flux<NodeOutput> startNewConversation(String query, RunnableConfig config) {
        // 初始化所有状态键
        Map<String, Object> initialState = Map.of(
                "user_query", query,
                "knowledge_list", Collections.emptyList(),
                "reply", (String) "",
                "intent", (String) "",
                "slot_params", Result.empty(),
                "nowDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
        // 从初始状态开始执行工作流
        return this.compiledGraph.stream(initialState, config).doOnNext(event -> {
                    System.out.println("节点输出: " + event);
                })
                .doOnError(error -> System.err.println("流错误: " + error.getMessage()))
                .doOnComplete(() -> System.out.println("流完成"));
    }

}
