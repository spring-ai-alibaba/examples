import React, { useState, useEffect, useRef } from "react";
import { useLocation } from "react-router-dom";
import { Sender } from "@ant-design/x";
import CodeInfo from "../CodeInfo";
import {
  ChatMessage,
  useConversationContext,
  BaseMessage,
} from "../../../../stores/conversation.store";
import BasePage from "../../../components/BasePage";
import { getRag } from "../../../../api/rag";
import {
  mapStoredMessagesToUIMessages,
  scrollToBottom,
} from "../../../../utils";
import ResponseBubble from "../../../components/ResponseBubble";
import RequestBubble from "../../../components/RequestBubble";
import { Message } from "../../../ChatPage/types";
import { useStyles } from "../../style";

interface RagConversationViewProps {
  conversationId: string;
}

interface RagUiMessage extends BaseMessage {
  role: "user" | "assistant";
  content: string;
  timestamp: number;
}

const RagConversationView = ({ conversationId }: RagConversationViewProps) => {
  const { styles } = useStyles();
  const location = useLocation();
  const [inputContent, setInputContent] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const msgContainerRef = useRef<HTMLDivElement>(null);
  const {
    activeConversation,
    chooseActiveConversation,
    processSendMessage,
    appendAssistantMessage,
    deleteMessageAndAfter,
    updateMessageContent,
    updateActiveConversation,
  } = useConversationContext();

  const isFirstLoad = useRef(true);
  const processedPrompts = useRef(new Set<string>());

  useEffect(() => {
    chooseActiveConversation(conversationId);
  }, [conversationId, chooseActiveConversation]);

  useEffect(() => {
    if (activeConversation) {
      if (
        activeConversation.messages &&
        activeConversation.messages.length > 0
      ) {
        const filteredMessages = activeConversation.messages.filter(
          (msg) => !(msg as ChatMessage).isLoading
        );

        if (filteredMessages.length > 0) {
          const uiMessages = mapStoredMessagesToUIMessages(
            filteredMessages as ChatMessage[]
          );
          setMessages(uiMessages);
        } else {
          setMessages([]);
        }
      } else {
        setMessages([]);
      }
    }
  }, [activeConversation?.id, activeConversation?.messages]);

  useEffect(() => {
    if (isFirstLoad.current && activeConversation) {
      const queryParams = new URLSearchParams(location.search);
      const urlPrompt = queryParams.get("prompt");

      if (urlPrompt && !processedPrompts.current.has(urlPrompt)) {
        processedPrompts.current.add(urlPrompt);
        console.log("从URL参数获取提示词:", urlPrompt);

        const newUrl = window.location.hash.split("?")[0];
        window.history.replaceState({}, document.title, newUrl);

        setTimeout(() => {
          handleSendMessage(urlPrompt);
        }, 300);
      }

      isFirstLoad.current = false;
    }
  }, [location.search, activeConversation]);

  useEffect(() => {
    const timer = setTimeout(() => {
      scrollToBottom(msgContainerRef.current);
      clearTimeout(timer);
    }, 200);

    return () => {
      clearTimeout(timer);
    };
  }, [activeConversation?.messages]);

  const updateConversationMessages = (
    messageContent: string,
    role: "assistant",
    isError: boolean = false,
    userTimestamp: number,
    userMessage: RagUiMessage
  ) => {
    appendAssistantMessage(
      messageContent,
      role,
      isError,
      userTimestamp,
      userMessage
    );
  };

  const handleSendMessage = async (text: string) => {
    const createMessage = (text: string, timestamp: number): RagUiMessage => ({
      role: "user",
      content: text,
      timestamp,
    });

    const sendRequest = async (
      text: string,
      userTimestamp: number,
      userMessage: RagUiMessage
    ) => {
      let accumulatedText = "";
      const response = await getRag(
        text,
        (value) => {
          const chunk = new TextDecoder().decode(value);
          accumulatedText += chunk;
          updateConversationMessages(
            accumulatedText,
            "assistant",
            false,
            userTimestamp,
            userMessage
          );
        },
        { chatId: conversationId }
      );

      if (!response.ok) {
        throw new Error("RAG查询失败");
      }
    };

    await processSendMessage({
      text,
      sendRequest,
      createMessage,
      setLoading: setIsLoading,
      setInputContent,
    });
  };

  // 处理重新生成消息（revert操作）
  const handleReloadMessage = async (messageTimestamp: number) => {
    if (!activeConversation || isLoading) return;

    // 找到要重新生成的消息
    const messageIndex = activeConversation.messages.findIndex(
      (msg) => msg.timestamp === messageTimestamp
    );

    if (messageIndex === -1) return;

    // 找到对应的用户消息（应该在assistant消息之前）
    const userMessage = activeConversation.messages
      .slice(0, messageIndex)
      .reverse()
      .find((msg) => msg.role === "user");

    if (!userMessage) return;

    // 删除当前assistant消息及其之后的所有消息（revert操作）
    const remainingMessages = deleteMessageAndAfter(messageTimestamp);

    // 直接重新生成回复，不创建新的用户消息
    setIsLoading(true);

    // 创建一个使用正确baseMessages的更新函数
    const updateConversationMessagesWithBase = (
      messageContent: string,
      role: "assistant",
      isError: boolean = false,
      userTimestamp: number,
      userMessage: RagUiMessage
    ) => {
      appendAssistantMessage(
        messageContent,
        role,
        isError,
        userTimestamp,
        userMessage,
        remainingMessages as RagUiMessage[]
      );
    };

    const sendRequest = async (
      text: string,
      userTimestamp: number,
      userMessage: RagUiMessage
    ) => {
      let accumulatedText = "";
      const response = await getRag(
        text,
        (value) => {
          const chunk = new TextDecoder().decode(value);
          accumulatedText += chunk;
          updateConversationMessagesWithBase(
            accumulatedText,
            "assistant",
            false,
            userTimestamp,
            userMessage
          );
        },
        { chatId: conversationId }
      );

      if (!response.ok) {
        throw new Error("RAG查询失败");
      }
    };

    try {
      await sendRequest(
        userMessage.content,
        userMessage.timestamp,
        userMessage as RagUiMessage
      );
    } catch (error) {
      console.error("重新生成消息错误:", error);
      appendAssistantMessage(
        "抱歉，重新生成回复时出现错误。",
        "assistant",
        true,
        userMessage.timestamp,
        userMessage as RagUiMessage,
        remainingMessages as RagUiMessage[]
      );
    } finally {
      setIsLoading(false);
    }
  };

  // 处理编辑消息
  const handleEditConfirm = async (
    messageTimestamp: number,
    newContent: string
  ) => {
    if (!activeConversation || isLoading) return;

    // 找到要编辑的消息
    const message = activeConversation.messages.find(
      (msg) => msg.timestamp === messageTimestamp
    );

    if (!message || message.role !== "user") return;

    // 删除当前消息之后的所有消息（保留用户消息本身）
    const remainingMessages = activeConversation.messages.filter(
      (msg) => msg.timestamp <= messageTimestamp
    );

    // 更新用户消息内容
    const updatedMessages = remainingMessages.map((msg) =>
      msg.timestamp === messageTimestamp
        ? ({ ...msg, content: newContent } as RagUiMessage)
        : msg
    ) as RagUiMessage[];

    // 立即更新会话状态
    updateActiveConversation({
      ...activeConversation,
      messages: updatedMessages,
    });

    // 直接重新生成回复
    setIsLoading(true);

    // 创建一个使用正确baseMessages的更新函数
    const updateConversationMessagesWithBase = (
      messageContent: string,
      role: "assistant",
      isError: boolean = false,
      userTimestamp: number,
      userMessage: RagUiMessage
    ) => {
      appendAssistantMessage(
        messageContent,
        role,
        isError,
        userTimestamp,
        userMessage,
        updatedMessages
      );
    };

    // 创建更新后的用户消息
    const updatedUserMessage: RagUiMessage = {
      ...message,
      content: newContent,
    } as RagUiMessage;

    const sendRequest = async (
      text: string,
      userTimestamp: number,
      userMessage: RagUiMessage
    ) => {
      let accumulatedText = "";
      const response = await getRag(
        text,
        (value) => {
          const chunk = new TextDecoder().decode(value);
          accumulatedText += chunk;
          updateConversationMessagesWithBase(
            accumulatedText,
            "assistant",
            false,
            userTimestamp,
            userMessage
          );
        },
        { chatId: conversationId }
      );

      if (!response.ok) {
        throw new Error("RAG查询失败");
      }
    };

    try {
      await sendRequest(newContent, message.timestamp, updatedUserMessage);
    } catch (error) {
      console.error("重新生成消息错误:", error);
      appendAssistantMessage(
        "抱歉，重新生成回复时出现错误。",
        "assistant",
        true,
        message.timestamp,
        updatedUserMessage,
        updatedMessages
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <BasePage title="RAG对话" conversationId={conversationId}>
      <div className={styles.container}>
        <div className={styles.leftPanel}>
          <CodeInfo />
          <div className={styles.senderWrapper}>
            <Sender
              value={inputContent}
              onChange={setInputContent}
              onSubmit={handleSendMessage}
              placeholder="请输入您想查询的内容，例如：什么是RAG？..."
              className={styles.sender}
              loading={isLoading}
            />
          </div>
        </div>

        <div className={styles.rightPanel}>
          <div
            className={`${styles.card} ${styles.resultPanel}`}
            ref={msgContainerRef}
          >
            <h2 className={styles.panelTitle}>RAG功能演示</h2>
            <div className={styles.messagesContainer}>
              {messages.length === 0 && !conversationId ? (
                <ResponseBubble
                  content="你好！我是一个基于RAG技术的智能助手。我可以基于知识库回答你的问题。你可以问我任何问题，我会尽力从知识库中找到相关信息来回答你。"
                  timestamp={Date.now()}
                />
              ) : (
                messages.map((message) =>
                  message.sender === "user" ? (
                    <RequestBubble
                      key={message.id}
                      content={message.text}
                      timestamp={message.timestamp}
                      onEditConfirm={(newContent) =>
                        handleEditConfirm(message.timestamp, newContent)
                      }
                    />
                  ) : (
                    <ResponseBubble
                      key={message.id}
                      content={message.text}
                      timestamp={message.timestamp}
                      isError={message.isError}
                      onReload={() => handleReloadMessage(message.timestamp)}
                    />
                  )
                )
              )}
            </div>
          </div>
        </div>
      </div>
    </BasePage>
  );
};

export default RagConversationView;
