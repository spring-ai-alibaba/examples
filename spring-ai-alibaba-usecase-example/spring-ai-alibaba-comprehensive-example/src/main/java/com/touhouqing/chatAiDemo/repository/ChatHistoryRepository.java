package com.touhouqing.chatAiDemo.repository;

import java.util.List;

public interface ChatHistoryRepository {

    /**
     * 保存聊天记录
     * @param type 业务类型,如chat,service,pdf
     * @param chatId 聊天id
     */
    void save(String type, String chatId);

    /**
     * 获取聊天记录
     * @param type 业务类型,如chat,service,pdf
     * @return 聊天id列表
     */
    List<String> getChatIds(String type);

}
