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
package com.cloud.alibaba.ai.example.agent.tool;

import com.cloud.alibaba.ai.example.agent.model.EmailInfo;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.List;
import java.util.function.BiFunction;

/**
 * A tool for sending emails based on natural language input.
 * This class implements the BiFunction interface to process email information
 * and simulate email sending functionality.
 * The tool validates email addresses, formats the email content,
 * and provides a callback mechanism for integration with AI agents.
 *
 * @author wangjx
 * @since 2026-02-13
 */
public class SendEmailTool implements BiFunction<EmailInfo, ToolContext, String> {
    @Override
    public String apply(EmailInfo args, ToolContext toolContext) {
        // 参数解析
        List<String> to = args.getTo();
        String subject = args.getSubject();
        String body = args.getBody();
        // 验证邮箱格式（简化版）
        if (to == null || to.isEmpty()){
            return "Error: No recipient email addresses provided.";
        }
        // 模拟发送邮件
        System.out.printf("Email sent to %s - Subject: %s%n body: %s", String.join(", ", to), subject, body);
        return String.format("Email sent to %s - Subject: %s", String.join(", ", to), subject);
    }


    private boolean isValidEmail(String email) {
        // 简单验证邮箱格式（实际应使用更严格的验证）
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public ToolCallback toolCallback() {
        return FunctionToolCallback.builder("send_email", this)
                .description("""
                        Send emails using natural language.
                        Use this when the user wants to send notifications, reminders, or any email
                        communication. Handles recipient extraction, subject generation, and email
                        composition.
                        Input: Natural language email request (e.g., "send them a reminder about
                        the meeting"
                        """)
                .inputType(EmailInfo.class)
                .build();

    }

}
