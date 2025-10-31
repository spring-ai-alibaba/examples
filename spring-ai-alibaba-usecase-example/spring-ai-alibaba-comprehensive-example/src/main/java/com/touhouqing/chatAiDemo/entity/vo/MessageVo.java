package com.touhouqing.chatAiDemo.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

@Data
@NoArgsConstructor
public class MessageVo {
    private String content;
    private String role;

    public MessageVo(Message message) {
        switch (message.getMessageType()) {
            case USER:
                role = "user";
                break;
            case ASSISTANT:
                role = "assistant";
                break;
            default:
                role = "";
                break;
        }
        this.content = message.getText();
    }
}
