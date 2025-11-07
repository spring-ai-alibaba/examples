/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
