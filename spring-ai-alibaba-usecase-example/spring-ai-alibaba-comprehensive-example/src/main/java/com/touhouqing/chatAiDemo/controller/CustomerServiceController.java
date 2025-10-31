package com.touhouqing.chatAiDemo.controller;


import com.touhouqing.chatAiDemo.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class CustomerServiceController {
    private final ChatClient serviceChatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value="/service", produces = "text/html;charset=utf-8")
    public Flux<String> service(String prompt, String chatId){
        chatHistoryRepository.save("service", chatId);
        return serviceChatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
