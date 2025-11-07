package com.alibaba.cloud.ai;

import com.alibaba.cloud.ai.service.TimeService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author yingzi
 * @since 2025/10/26
 */
@SpringBootApplication
public class NacosRegisterMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosRegisterMcpServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider timeTools(TimeService timeService) {
        return MethodToolCallbackProvider.builder().toolObjects(timeService).build();
    }
}
