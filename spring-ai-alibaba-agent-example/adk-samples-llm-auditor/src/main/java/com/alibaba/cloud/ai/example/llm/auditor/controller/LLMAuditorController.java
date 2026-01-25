/*
 * Copyright 2026-2027 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.example.llm.auditor.controller;

import com.alibaba.cloud.ai.example.llm.auditor.hook.CriticAgentHook;
import com.alibaba.cloud.ai.example.llm.auditor.hook.ReviserAgentHook;
import com.alibaba.cloud.ai.example.llm.auditor.tool.WebSearchTool;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SequentialAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author : zhengyuchao
 * @date : 2026/1/22
 */
@RestController
@RequestMapping("/ai")
public class LLMAuditorController {
    private static final Logger log = LoggerFactory.getLogger(LLMAuditorController.class);
    private ChatModel chatModel;

    public LLMAuditorController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    // 你的 Tavily API Key (建议放在配置文件 application.properties 中，通过 @Value 注入)
    @Value("${search.tavily.api-key}")
    private String tavilyApiKey;

    @PostConstruct
    public void init() {

    }

    private static final String reviserPrompt = """
            你是一名专业编辑，为一家高度值得信赖的出版物工作。
            在这个任务中，你会得到一组问题和答案，这些问题和答案将被打印到出版物上。出版物审稿人已经仔细检查了答案文本并提供了发现。
            你的任务是尽量减少对答案文本的修改，使其准确，同时保持整体结构、风格和长度与原文相似。
            
            审稿人已经确定了答案文本中的主张（包括事实和逻辑论点），并使用以下裁决验证了每个主张是否准确：
            
            *准确：在索赔中提供的信息是正确的，完整的，并与所提供的上下文和可靠来源一致。
            *不准确：与提供的上下文和可靠来源相比，索赔中提供的信息包含错误、遗漏或不一致。
            *有争议：可靠和权威的来源提供了关于索赔的相互矛盾的信息，表明对客观信息缺乏明确的一致意见。
            *不支持：尽管您努力搜索，但没有找到可靠的来源来证实索赔中提供的信息。
            *不适用：该声明表达了主观意见、个人信仰，或涉及不需要外部验证的虚构内容。
            
            每一类申索的编辑指引：
            
            *准确的声明：不需要编辑。
            *不准确的说法：如果可能的话，你应该根据审稿人的理由来纠正它们。
            *有争议的观点：你应该尝试提出一个论点的两个（或更多）方面，使答案更平衡。
            *不被支持的观点：如果它们不是答案的中心，你可以省略不被支持的观点。否则，你可能会软化这些说法，或者表示它们是没有根据的。
            *不适用的声明：不需要编辑。
            
            作为最后的手段，你可以省略一个主张，如果他们不是答案的中心，不可能解决。你还应该做必要的修改，以确保修改后的答案是连贯和流畅的。你不应该在回答文本中引入任何新的主张或作出任何新的陈述。您的编辑应该是最小的，并保持整体结构和样式不变。
            
            输出格式:
            
            *如果答案是准确的，你应该输出与你给出的完全相同的答案文本。
            *如果答案不准确，有争议或不支持，那么你应该输出修改后的答案文本。
            *在答案之后，输出一行 ---END-OF-EDIT--- 并停止。
            
            下面是一些关于这项任务的例子：
            
            ===例1 ===
            
            问：谁是美国第一任总统？
            
            答案：乔治·华盛顿是美国的第一任总统。
            
            发现:
            
            *说法1：乔治·华盛顿是美国第一任总统。
            *结论：准确
            *理由：多个可靠来源证实乔治·华盛顿是美国第一任总统。
            *总体结论：准确
            *全面论证：答案准确且完整地回答了问题。
            
            您期望的答复：
            
            乔治·华盛顿是美国第一任总统。
            ---END-OF-EDIT---
            
            ===例2 ===
            
            问题：太阳的形状是什么？
            
            答：太阳呈立方体，非常热。
            
            发现:
            
            *说法1：太阳是立方体的。
            *结论：不准确
            理由：美国国家航空航天局表示，太阳是一个热等离子体球体，所以它不是立方体。它是一个球体。
            *说法2：太阳很热。
            *结论：准确
            理由：根据我的知识和搜索结果，太阳非常热。
            *总体结论：不准确
            *整体论证：答案说太阳是立方体的，这是不正确的。
            
            您期望的答复：
            
            太阳是球形的，非常热。
            ---END-OF-EDIT---
            
            所有Message中含有问答对和审稿人提供的调查结果：
            """;

    private static final String criticAgentPrompt= """
            你是一名专业的调查记者，擅长批判性思维和在高度可信的出版物上发表之前核实信息。
            在这个任务中，你会得到一组问题和答案，这些问题和答案将被打印到出版物上。出版物编辑让你仔细检查答案文本。
            
            #你的任务
            
            你的任务包括三个关键步骤：首先，确定答案中出现的所有主张。第二，确定每个索赔的可靠性。最后，提供一个全面的评估。
            
            ##步骤1：识别索赔
            
            仔细阅读提供的答案文本。从答案中提取出每一个明显的主张。主张可以是对世界的事实陈述，也可以是为支持一个观点而提出的逻辑论证。
            
            ##步骤2：验证每个索赔
            
            对于您在步骤1中确定的每个索赔，执行以下操作：
            
            *考虑上下文：考虑原始问题和答案中已经确定的任何其他主张。
            *咨询外部资源：利用你的一般知识和/或在网上搜索，找到支持或反对该主张的证据。目的是咨询可靠和权威的来源。
            *决定裁决：根据您的评估，对索赔作出以下裁决之一：
            *准确：在索赔中提供的信息是正确的，完整的，并与所提供的上下文和可靠来源一致。
            *不准确：与提供的上下文和可靠来源相比，索赔中提供的信息包含错误、遗漏或不一致。
            *有争议：可靠和权威的来源提供了关于索赔的相互矛盾的信息，表明对客观信息缺乏明确的一致意见。
            *不支持：尽管您努力搜索，但没有找到可靠的来源来证实索赔中提供的信息。
            *不适用：该声明表达了主观意见、个人信仰，或涉及不需要外部验证的虚构内容。
            *提供理由：对于每一个结论，清楚地解释你的评估背后的理由。参考你所咨询的资料来源或解释为什么选择“不适用”的判决。
            
            步骤3：提供一个全面的评估
            
            在你评估了每一个单独的主张之后，为整个答案文本提供一个总体结论，并为你的总体结论提供一个总体的理由。解释对个别索赔的评估是如何导致你进行整体评估的，以及作为一个整体的答案是否成功地解决了最初的问题。
            
            #提示
            
            你的工作是迭代的。在每一步中，你应该从文本中选择一个或多个声明并验证它们。然后，继续进行下一项或多项索赔。您可以依靠以前的声明来验证当前的声明。
            
            您可以采取各种行动来帮助您进行验证：
            *你可以用自己的知识来验证文本中的信息，注明“基于我的知识…”。然而，非琐碎的事实声明也应该通过其他来源进行验证，比如搜索。高度似是而非或主观的主张可以用你自己的知识来验证。
            *你可能会发现不需要事实核查的信息，并将其标记为“不适用”。
            *你可以在网上搜索支持或反对这种说法的信息。
            *如果获得的证据不足，你可以对每项索赔进行多次搜查。
            *在你的推理中，请参考你迄今为止通过方括号索引收集到的证据。
            *您可以检查上下文，以验证索赔是否与上下文一致。仔细阅读上下文，以确定文本应该遵循的特定用户说明，文本应该忠实于的事实等。
            *你应该在获得所有你需要的信息后对整篇文章得出最后的结论。
            
            #输出格式
            
            输出的最后一个块应该是一个markdown格式的列表，总结了验证结果。对于您验证的每个CLAIM，您应该输出该CLAIM（作为一个独立的语句）、答案文本中相应的部分、判决和证明。
            
            user信息为你需要反复检查的问题：
            """;

    @GetMapping("/agent")
    public String agent(@RequestParam(value = "query", defaultValue = "中国的首都是哪里?") String query){
        ReactAgent criticAgent = ReactAgent.builder()
                .name("critic_agent")
                .description("")
                .model(chatModel)
                .instruction(criticAgentPrompt)
                .tools(WebSearchTool.getFunctionToolCallback(tavilyApiKey))
                .hooks(new CriticAgentHook())
                .outputKey("critic_agent_output")
                .build();

        ReactAgent reviserAgent = ReactAgent.builder()
                .name("reviser_agent")
                .description("")
                .model(chatModel)
                .instruction(reviserPrompt)
                .hooks(new ReviserAgentHook())
                .outputKey("reviser_agent_output")
                .build();

        SequentialAgent llmAuditor = SequentialAgent.builder()
                .name("llm_auditor")
                .description("评估llm生成的答案，使用Web，并改进响应以确保与真实世界保持一致")
                .subAgents(List.of(criticAgent,reviserAgent))
                .build();

        try {
            Optional<OverAllState> overAllState = llmAuditor.invoke(query);
            OverAllState state = overAllState.get();
            StringBuilder output = new StringBuilder();
            // 访问各个Agent的输出
            state.value("critic_agent_output", AssistantMessage.class).ifPresent(r -> {
                output.append("===============critic_agent_ouput============\n")
                        .append("critic_agent_output:    \n")
                        .append(r.getText()).append("\n")
                        .append("===============end============\n")
                ;

            });
            state.value("reviser_agent_output", AssistantMessage.class).ifPresent(r -> {
                output.append("===============reviser_agent_output============\n")
                        .append("reviser_agent_output:    \n")
                        .append(r.getText()).append("\n")
                        .append("===============end============\n")
                ;
            });
            return output.toString();
        } catch (GraphRunnerException e) {
            throw new RuntimeException(e);
        }
    }

}
