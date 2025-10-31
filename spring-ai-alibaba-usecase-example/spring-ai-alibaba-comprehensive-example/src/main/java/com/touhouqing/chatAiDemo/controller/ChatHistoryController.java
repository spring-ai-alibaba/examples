package com.touhouqing.chatAiDemo.controller;

import com.touhouqing.chatAiDemo.entity.vo.MessageVo;
import com.touhouqing.chatAiDemo.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {

    private final ChatHistoryRepository chatHistoryRepository;

    private final ChatMemory chatMemory;

    @RequestMapping("/{type}")
    public List<String> getChatIds(@PathVariable("type") String type) {
        return chatHistoryRepository.getChatIds(type);
    }

    @RequestMapping("/{type}/{chatId}")
    public List<MessageVo> getChatHistory(@PathVariable("type") String type, @PathVariable("chatId") String chatId) {
        List<Message> messages = chatMemory.get(chatId);
        if(messages == null){
            return List.of();
        }
        return messages.stream().map(MessageVo::new).toList();
    }
}
