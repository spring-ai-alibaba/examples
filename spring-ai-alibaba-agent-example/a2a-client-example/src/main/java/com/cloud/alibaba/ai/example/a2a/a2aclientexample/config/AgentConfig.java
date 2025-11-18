package com.cloud.alibaba.ai.example.a2a.a2aclientexample.config;

import com.alibaba.cloud.ai.a2a.registry.nacos.discovery.NacosAgentCardProvider;
import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.agent.BaseAgent;
import com.alibaba.cloud.ai.graph.agent.a2a.A2aRemoteAgent;
import com.alibaba.cloud.ai.graph.agent.a2a.AgentCardProvider;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    @Bean
    public Agent saaAgent(NacosAgentCardProvider agentCardProvider) throws GraphStateException {
        return A2aRemoteAgent.builder()
            .agentCardProvider(agentCardProvider)
            .name("SaaAgent")
            .description("Answer Spring AI Alibaba questions or query and operate datum in Spring AI Alibaba by chinese.")
            .instruction("Please answer the question about Spring AI Alibaba by chinese.")
            .outputKey("messages")
            .shareState(true)
            .build();
    }
}
