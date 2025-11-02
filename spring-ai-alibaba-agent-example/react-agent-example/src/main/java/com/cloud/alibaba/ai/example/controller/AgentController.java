package com.cloud.alibaba.ai.example.controller;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class AgentController {

    private final ReactAgent reactAgent;

    private final Map<String, InterruptionMetadata> map = new ConcurrentHashMap<>();

    public AgentController(ReactAgent reactAgent) {
        this.reactAgent = reactAgent;
    }

    @GetMapping("/invoke")
    @ResponseBody
    public List<InterruptionMetadata.ToolFeedback> invoke(@RequestParam("query") String query,
                       @RequestParam("threadId") String threadId
    ) throws Exception {
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        InterruptionMetadata metadata = (InterruptionMetadata) reactAgent.invokeAndGetOutput(query, runnableConfig).orElseThrow();
        map.put(threadId, metadata);
        return metadata.toolFeedbacks();
    }

    @PostMapping("/feedback")
    @ResponseBody
    public String feedback(@RequestBody List<Feedback> feedbacks,
                         @RequestParam("threadId") String threadId
    ) throws Exception {
        InterruptionMetadata metadata = map.get(threadId);
        if(metadata == null) {
            return "no metadata found";
        }
        if(metadata.toolFeedbacks().size() != feedbacks.size()) {
            return "feedback size not match";
        }

        InterruptionMetadata.Builder newBuilder = InterruptionMetadata.builder()
                .nodeId(metadata.node())
                .state(metadata.state());
        for (int i = 0; i < feedbacks.size(); i++) {
            var toolFeedback = metadata.toolFeedbacks().get(i);
            InterruptionMetadata.ToolFeedback.Builder editedFeedbackBuilder = InterruptionMetadata.ToolFeedback
                    .builder(toolFeedback);
            if(feedbacks.get(i).isApproved()) {
                editedFeedbackBuilder.result(InterruptionMetadata.ToolFeedback.FeedbackResult.APPROVED);
            } else {
                editedFeedbackBuilder.result(InterruptionMetadata.ToolFeedback.FeedbackResult.REJECTED)
                        .description(feedbacks.get(i).feedback());
            }
            newBuilder.addToolFeedback(editedFeedbackBuilder.build());
        }
        RunnableConfig resumeRunnableConfig = RunnableConfig.builder().threadId(threadId)
                .addMetadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY, newBuilder.build())
                .build();
        reactAgent.invokeAndGetOutput("", resumeRunnableConfig);
        return "success";
    }

    @GetMapping
    public String index() {
        return "index";
    }
}
