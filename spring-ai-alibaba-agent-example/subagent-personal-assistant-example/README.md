# Spring AI Alibaba SubAgent Personal Assistant Example

本示例展示如何使用 Spring AI Alibaba 的 ReactAgent 框架构建一个多智能体监督者模式系统，通过主智能体协调多个子智能体，实现日历代理与邮件发送功能。
## 功能特性

- **多智能体**: Subagent 使用 Tool Calling 模式被 Supervisor Agent 调用。
- **人工介入**: 通过在 Supervisor Agent 配置 hooks 加入中断功能。

## 快速开始

### 前置条件

- Java 21+
- Maven 3.6+
- DashScope API Key

### 运行步骤

1. **设置 API Key**

   ```bash
   export DASHSCOPE_API_KEY=your-api-key
   ```

2. **构建项目**

   ```bash
   cd subagent-personal-assistant-example
   mvn clean package -DskipTests
   ```

3. **运行应用**

   ```bash
   mvn spring-boot:run
   ```

4. **访问API**

  ``` shell
  curl --location 'http://127.0.0.1:8080/react/agent/supervisorAgent?query=Schedule%20a%20meeting%20with%20the%20design%20team%20next%20Tuesday%20at%202pm%20for%201%20hour%2C%20and%20send%20them%20an%20email%20reminder%20about%20reviewing%20the%20new%20mockups.&threadId=user-session-124&nodeId=_AGENT_HOOK_HITL' \
--header 'Content-Type: application/json' \
--header 'Accept: text/event-stream' \
--data ''
  ````
## Agent说明
* supervisor_agent: 监督者智能体，负责协调多个子智能体，实现日历代理与邮件发送功能。
* calendar_agent: 日历日程助理，检查可用时间和安排日历事件。
* email_agent: 创建邮件主题和邮件发送。
## 工具说明

| 工具名称 | 功能                | 输入参数                  |
|---------|-------------------|-----------------------|
| `get_user_email_tool` | 获取用户邮箱            | UserInfo                   |
| `get_current_date_time` | 获取当前日期和时间     | 无                      |
| `get_available_time_slots` |  获取可用时间 slots |AvailableTimeInfo  |
| `create_calendar_event` | 创建日历事件          | CalendarInfo      |
| `send_email` | 发送邮件            | EmailInfo     |

## 示例对话

```
用户: Schedule a meeting with the design team next Tuesday at 2pm for 1 hour, and send them an email reminder about reviewing the new mockups.

Agent: 
I'll help you schedule a meeting with the design team and send them an email reminder. Let me break this down into steps:

1. First, I need to get the email addresses of the design team members
2. Then schedule the calendar event for next Tuesday at 2pm for 1 hour
3. Finally, send an email reminder about reviewing the new mockups

Let me start by getting the design team members' information:

================================= Tool Message =================================

id: call_e06f221fa93b4e8ea77e62d0
name: get_user_email_tool
responseData: "Available user list for [{\"departmentName\":\"design(设计团队)\",\"email\":\"wangwu@agent.cn\",\"userName\":\"wangwu(王五)\"}]"
Now I have the design team member information. I'll schedule the meeting for next Tuesday at 2pm for 1 hour with Wang Wu (王五) from the design team:

检测到中断，需要人工审批
工具: calendar_agent
参数: {"input": "Schedule a meeting with wangwu@agent.cn next Tuesday at 2pm for 1 hour. Subject: Design Team Meeting - Mockup Review"}
描述: The AI is requesting to use the tool: calendar_agent.
Description: Calendar event pending approval
With the following arguments: {"input": "Schedule a meeting with wangwu@agent.cn next Tuesday at 2pm for 1 hour. Subject: Design Team Meeting - Mockup Review"}
Do you approve?
检测到中断,等待人工介入... node: _AGENT_HOOK_HITL
人工介入开始...
检测到中断，需要人工审批
工具: email_agent
参数: {"input": "Send an email to wangwu@agent.cn with subject 'Reminder: Design Team Meeting - Mockup Review' and content 'Hi Wang Wu, This is a reminder about our upcoming meeting next Tuesday (February 17, 2026) at 2:00 PM to review the new mockups. Please come prepared with your feedback. Looking forward to our discussion!'"}
描述: The AI is requesting to use the tool: email_agent.
Description: Outbound email pending approval
With the following arguments: {"input": "Send an email to wangwu@agent.cn with subject 'Reminder: Design Team Meeting - Mockup Review' and content 'Hi Wang Wu, This is a reminder about our upcoming meeting next Tuesday (February 17, 2026) at 2:00 PM to review the new mockups. Please come prepared with your feedback. Looking forward to our discussion!'"}
Do you approve?
检测到中断,等待人工介入... node: _AGENT_HOOK_HITL
人工介入开始...
Email sent to wangwu@agent.cn - Subject: Reminder: Design Team Meeting - Mockup Review
 body: Hi Wang Wu, This is a reminder about our upcoming meeting next Tuesday (February 17, 2026) at 2:00 PM to review the new mockups. Please come prepared with your feedback. Looking forward to our discussion!
```
## 注意事项
1. 当检测到人工接入时，需要带着nodeId重新发起请求。
2. 当前模型使用的qwen3-max-2026-01-23，模型不同可能导致示例结果有所偏差。

## 相关链接

- [Spring AI Alibaba 文档](https://java2ai.com)
- [Spring AI Alibaba GitHub](https://github.com/alibaba/spring-ai-alibaba)