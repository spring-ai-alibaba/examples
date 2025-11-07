import { atom, useAtom } from "jotai";
import { MenuPage } from "./functionMenu.store";
import { GetProp } from "antd";
import { Bubble } from "@ant-design/x";
import { useEffect, useRef } from "react";

/**
 * 生成图像的数据结构
 */
export interface GeneratedImage {
  id: string;
  url: string;
  prompt: string;
  blob?: Blob;
  dataUrl?: string;
}

/**
 * 聊天消息的数据结构
 */
export interface ChatMessage {
  role: "user" | "assistant";
  content: string;
  timestamp: number;
  isError?: boolean;
  isLoading?: boolean;
  images?: GeneratedImage[];
}

/**
 * AI模型能力配置
 */
export interface AiCapabilities {
  deepThink: boolean;
  onlineSearch: boolean;
}

/**
 * 对话会话的数据结构
 */
export interface Conversation {
  id: string;
  title: string;
  type: MenuPage;
  messages: ChatMessage[];
  createdAt: number;
  capabilities?: AiCapabilities;
}

/**
 * 基础消息接口，定义所有消息类型必须包含的字段
 */
export interface BaseMessage {
  role: "user" | "assistant";
  content: string;
  timestamp: number;
  isError?: boolean;
  isLoading?: boolean;
}

// 存储键名
const STORAGE_KEY = "app_conversations";

/**
 * 将Blob对象转换为DataURL格式
 * @param blob - 要转换的Blob对象
 * @returns 转换后的DataURL字符串
 */
const blobToDataUrl = (blob: Blob): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = reject;
    reader.readAsDataURL(blob);
  });
};

/**
 * 处理文件数据，将blob转换为dataUrl
 * 起初是专为图片生成页面使用，后来在其他文件生成的时候也有用到
 * TODO: 这里如果有时间的话，可以考虑将这个函数抽离出来，做成一个通用的/语义更好的函数
 * @param conversations - 要处理的会话列表
 * @returns 处理后可以存储的会话列表
 */
const prepareConversationsForStorage = async (
  conversations: Conversation[]
): Promise<Conversation[]> => {
  const processedConversations = JSON.parse(
    JSON.stringify(conversations)
  ) as Conversation[];

  // 处理每个会话
  for (const conversation of processedConversations) {
    // 处理每条消息
    for (const message of conversation.messages) {
      // 处理图片
      if (message.images) {
        for (let i = 0; i < message.images.length; i++) {
          const image = message.images[i];
          // 查找原始会话中对应的图片，因为 JSON.parse/stringify 会丢失 Blob 对象
          const originalConversation = conversations.find(
            (c) => c.id === conversation.id
          );
          const originalMessage = originalConversation?.messages.find(
            (m) => m.timestamp === message.timestamp
          );
          const originalImage = originalMessage?.images?.[i];

          // 如果原始图片有blob但没有dataUrl，转换并保存
          if (
            originalImage?.blob &&
            originalImage.blob instanceof Blob &&
            !originalImage.dataUrl
          ) {
            try {
              // 为了避免异步问题，我们这里不等待转换完成
              // 只在下次保存时才会包含dataUrl
              blobToDataUrl(originalImage.blob)
                .then((dataUrl) => {
                  // 更新原始对象的 dataUrl
                  originalImage.dataUrl = dataUrl;
                })
                .catch((e) => {
                  console.error("转换文件失败", e);
                });
            } catch (e) {
              console.error("转换文件失败", e);
            }
          }

          // 移除blob字段，太大了，不适合存储在localStorage
          delete image.blob;
        }
      }
    }
  }

  return processedConversations;
};

/**
 * 从localStorage加载会话数据
 * @returns 存储的会话列表，如果没有则返回空数组
 */
const loadConversationsFromStorage = (): Conversation[] => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      return JSON.parse(saved);
    }
  } catch (error) {
    console.error("Failed to load conversations from localStorage:", error);
  }
  return [];
};

// 初始化时从 localStorage 加载数据
const initialConversations = loadConversationsFromStorage();

const conversationsAtom = atom<Conversation[]>(initialConversations);
const activeConversationAtom = atom<Conversation | null>(null);
const aiCapabilitiesAtom = atom<AiCapabilities>({
  deepThink: false,
  onlineSearch: false,
});

/**
 * 用于管理对话上下文的React Hook
 * 提供对话的创建、更新、删除和读取功能
 * 自动将对话数据持久化到localStorage
 * @returns 包含对话状态和操作方法的对象
 */
export const useConversationContext = () => {
  const [conversations, setConversations] = useAtom(conversationsAtom);
  const [activeConversation, setActiveConversation] = useAtom(
    activeConversationAtom
  );
  const [aiCapabilities, setAiCapabilities] = useAtom(aiCapabilitiesAtom);

  // 跟踪是否有待保存的更改
  const pendingSaveRef = useRef<number | null>(null);

  // 当 conversations 变化时保存到 localStorage
  useEffect(() => {
    // 使用防抖来减少保存频率
    if (pendingSaveRef.current) {
      clearTimeout(pendingSaveRef.current);
    }

    pendingSaveRef.current = setTimeout(async () => {
      try {
        // 在保存前处理数据
        const processedConversations = await prepareConversationsForStorage(
          conversations
        );
        localStorage.setItem(
          STORAGE_KEY,
          JSON.stringify(processedConversations)
        );
      } catch (error) {
        console.error("Failed to save conversations to localStorage:", error);
      }
    }, 500);

    return () => {
      if (pendingSaveRef.current) {
        clearTimeout(pendingSaveRef.current);
      }
    };
  }, [conversations]);

  /**
   * 更新特定AI能力的状态
   * @param key - 要更新的能力名称
   * @param value - 是否启用该能力
   */
  const updateCapability = (key: keyof AiCapabilities, value: boolean) => {
    // 当前逻辑是互斥的（只能启用一种能力），但未来可能会支持多种同时启用
    const newCapabilities = Object.keys(aiCapabilities).reduce((acc, k) => {
      acc[k as keyof AiCapabilities] = false;
      return acc;
    }, {} as AiCapabilities);

    if (value) {
      newCapabilities[key] = true;
    }

    setAiCapabilities(newCapabilities);

    if (activeConversation) {
      const updatedConversation = {
        ...activeConversation,
        capabilities: newCapabilities,
      };
      updateActiveConversation(updatedConversation);
    }
  };

  /**
   * 切换特定AI能力的状态
   * @param key - 要切换的能力名称
   */
  const toggleCapability = (key: keyof AiCapabilities) => {
    const currentValue = aiCapabilities[key];
    updateCapability(key, !currentValue);
  };

  useEffect(() => {
    if (activeConversation?.capabilities) {
      setAiCapabilities(activeConversation.capabilities);
    } else {
      setAiCapabilities({
        deepThink: false,
        onlineSearch: false,
      });
    }
  }, [activeConversation?.id]);

  /**
   * 创建新的对话会话
   * @param type - 对话类型
  //  * @param items - 初始消息项
   * @param content - 用户输入内容，可选，用于生成标题
   * @returns 新创建的对话会话对象
   */
  const createConversation = (
    type: MenuPage,
    // items: GetProp<typeof Bubble.List, "items">,
    content?: string
  ) => {
    // UUID
    const timestamp = Date.now();
    let title = "";

    if (content && content.trim()) {
      const maxLength = 15;
      title =
        content.trim().length > maxLength
          ? `${content.trim().substring(0, maxLength)}...`
          : content.trim();
    } else {
      title = `对话 ${timestamp.toString().slice(-8)}`;
    }

    const newConversation: Conversation = {
      id: timestamp.toString(),
      title,
      type,
      messages: [],
      createdAt: timestamp,
      capabilities: { ...aiCapabilities }, // 保存当前能力设置
    };
    setConversations([newConversation, ...conversations]);
    setActiveConversation(newConversation);
    return newConversation;
  };

  /**
   * 删除指定ID的对话会话
   * @param conversationId - 要删除的对话ID
   */
  const deleteConversation = (conversationId: string) => {
    setConversations(
      conversations.filter((conv) => conv.id !== conversationId)
    );
    if (activeConversation?.id === conversationId) {
      setActiveConversation(null);
    }
  };

  /**
   * 替换所有会话
   * @param newConversations - 新的会话列表
   */
  const replaceAllConversations = (conversations: Conversation[]) => {
    setConversations([...conversations]);
  };

  /**
   * 更新指定的对话会话，会同时更新localStorage
   * @param conversation - 要更新的对话会话对象
   */
  const updateActiveConversation = (conversation: Conversation) => {
    // 更新 conversations 数组
    const updatedConversations = conversations.map((conv) =>
      conv.id === conversation.id ? conversation : conv
    );

    setConversations(updatedConversations);

    // 如果是当前活跃的对话，也更新 activeConversation
    if (activeConversation?.id === conversation.id) {
      setActiveConversation(conversation);
    }
  };

  /**
   * 更新指定的对话会话，但不更新localStorage
   * @param conversation - 要更新的对话会话对象
   */
  const updateConversationWithoutLocalStorage = (
    conversation: Conversation
  ) => {
    // 更新 conversations 数组
    const updatedConversations = conversations.map((conv) =>
      conv.id === conversation.id ? conversation : conv
    );

    setConversations(updatedConversations);

    // 如果是当前活跃的对话，也更新 activeConversation
    if (activeConversation?.id === conversation.id) {
      setActiveConversation(conversation);
    }
  };

  /**
   * 更新对话的标题
   * @param conversationId - 要更新标题的对话ID
   * @param newTitle - 新的标题
   */
  const updateConversationTitle = (
    conversationId: string,
    newTitle: string
  ) => {
    if (!newTitle.trim()) return; // 不允许空标题

    const updatedConversations = conversations.map((conv) =>
      conv.id === conversationId ? { ...conv, title: newTitle } : conv
    );

    setConversations(updatedConversations);

    // 如果是当前活跃的对话，也更新 activeConversation
    if (activeConversation?.id === conversationId) {
      setActiveConversation({ ...activeConversation, title: newTitle });
    }
  };

  /**
   * 选择指定ID的对话作为当前活跃对话
   * @param conversationId - 要选择的对话ID
   */
  const chooseActiveConversation = (conversationId: string) => {
    const conversation = conversations.find(
      (conv) => conv.id === conversationId
    );
    if (conversation) {
      setActiveConversation(conversation);
    }
  };

  /**
   * 清除当前活跃的对话
   */
  const clearActiveConversation = () => {
    setActiveConversation(null);
  };

  /**
   * 向会话中添加助手回复消息
   * @param messageContent - 消息内容
   * @param role - 消息角色
   * @param isError - 是否为错误消息
   * @param userTimestamp - 用户消息的时间戳
   * @param userMessage - 用户消息对象
   * @param baseMessages - 可选的基础消息列表，用于避免状态更新延迟问题
   */
  const appendAssistantMessage = <T extends BaseMessage>(
    messageContent: string,
    role: "assistant",
    isError: boolean = false,
    userTimestamp: number,
    userMessage: T,
    baseMessages?: T[]
  ) => {
    if (!activeConversation) return;

    // 使用传入的baseMessages或当前的activeConversation.messages
    const currentMessages = baseMessages || activeConversation.messages;

    // 确保用户消息存在
    const existingUserMessage = currentMessages.find(
      (msg) => msg.timestamp === userTimestamp && msg.role === "user"
    );

    const messagesWithUser = existingUserMessage
      ? currentMessages
      : [...currentMessages, userMessage];

    // 查找用户消息的索引
    const userMessageIndex = messagesWithUser.findIndex(
      (msg) => msg.timestamp === userTimestamp && msg.role === "user"
    );

    if (userMessageIndex === -1) {
      console.error("找不到对应的用户消息");
      return;
    }

    // 查找该用户消息之后的第一个assistant消息
    const existingAssistantIndex = messagesWithUser.findIndex(
      (msg, index) => index > userMessageIndex && msg.role === "assistant"
    );

    let finalMessages: T[];

    if (existingAssistantIndex !== -1) {
      // 更新现有的assistant消息
      finalMessages = messagesWithUser
        .map((msg, index) =>
          index === existingAssistantIndex
            ? ({ ...msg, content: messageContent, isError } as T)
            : msg
        )
        .filter((msg) => !(msg as BaseMessage).isLoading) as T[];
    } else {
      // 创建新的assistant消息
      const assistantTimestamp = Date.now();
      const assistantMessage = {
        role,
        content: messageContent,
        timestamp: assistantTimestamp,
        isError,
      } as T;

      finalMessages = messagesWithUser
        .filter((msg) => !(msg as BaseMessage).isLoading)
        .concat([assistantMessage]) as T[];
    }

    if (isError) {
      console.log("更新错误后的消息列表:", finalMessages);
    }

    updateActiveConversation({
      ...activeConversation,
      messages: finalMessages,
    });
  };

  /**
   * 处理消息发送的完整流程
   * @param text - 消息文本
   * @param sendRequest - 发送请求的函数
   * @param createMessage - 创建消息对象的函数
   * @param setLoading - 设置加载状态的函数
   * @param setInputContent - 设置输入内容的函数
   */
  const processSendMessage = async <T extends BaseMessage>({
    text,
    sendRequest,
    createMessage,
    setLoading,
    setInputContent,
  }: {
    text: string;
    sendRequest: (text: string, timestamp: number, message: T) => Promise<void>;
    createMessage: (text: string, timestamp: number) => T;
    setLoading: (loading: boolean) => void;
    setInputContent: (content: string) => void;
  }) => {
    if (!text.trim() || !activeConversation) return;

    setLoading(true);
    setInputContent(""); // 清空输入框

    const userTimestamp = Date.now();
    const userMessage = createMessage(text, userTimestamp);

    // 立即保存用户消息到localStorage(即使后续API调用失败)
    const updatedWithUserMessage = [
      ...activeConversation.messages,
      userMessage,
    ] as T[];

    updateActiveConversation({
      ...activeConversation,
      messages: updatedWithUserMessage,
    });

    try {
      await sendRequest(text, userTimestamp, userMessage);
    } catch (error) {
      console.error("处理请求错误:", error);
      appendAssistantMessage(
        "抱歉，处理您的请求时出现错误。",
        "assistant",
        true,
        userTimestamp,
        userMessage
      );
    } finally {
      setLoading(false);
    }
  };

  /**
   * 删除指定时间戳的消息及其之后的所有消息
   * @param messageTimestamp - 要删除的消息时间戳
   * @returns 删除后的消息列表
   */
  const deleteMessageAndAfter = (messageTimestamp: number) => {
    if (!activeConversation) return [];

    const updatedMessages = activeConversation.messages.filter(
      (msg) => msg.timestamp < messageTimestamp
    );

    updateActiveConversation({
      ...activeConversation,
      messages: updatedMessages,
    });

    return updatedMessages;
  };

  /**
   * 更新指定消息的内容
   * @param messageTimestamp - 要更新的消息时间戳
   * @param newContent - 新的消息内容
   */
  const updateMessageContent = (
    messageTimestamp: number,
    newContent: string
  ) => {
    if (!activeConversation) return;

    const updatedMessages = activeConversation.messages.map((msg) =>
      msg.timestamp === messageTimestamp ? { ...msg, content: newContent } : msg
    );

    updateActiveConversation({
      ...activeConversation,
      messages: updatedMessages,
    });
  };

  return {
    conversations,
    activeConversation,
    aiCapabilities,
    updateCapability,
    toggleCapability,
    createConversation,
    deleteConversation,
    replaceAllConversations,
    updateActiveConversation,
    updateConversationWithoutLocalStorage,
    chooseActiveConversation,
    clearActiveConversation,
    updateConversationTitle,
    appendAssistantMessage,
    processSendMessage,
    deleteMessageAndAfter,
    updateMessageContent,
  };
};
