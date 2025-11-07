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
import {
  mapStoredMessagesToUIMessages,
  scrollToBottom,
} from "../../../../utils";
// 导入通用气泡组件
import ResponseBubble from "../../../components/ResponseBubble";
import RequestBubble from "../../../components/RequestBubble";
import { Message } from "../../../ChatPage/types";
import { getToolCalling } from "../../../../api/toolCalling";
import { useStyles } from "../../style";

interface FunctionCallingConversationViewProps {
  conversationId: string;
}

interface FunctionCallingUiMessage extends BaseMessage {
  role: "user" | "assistant";
  content: string;
  timestamp: number;
}

const FunctionCallingConversationView = ({
  conversationId,
}: FunctionCallingConversationViewProps) => {
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

  // 跟踪组件是否首次加载，用于处理URL中的prompt参数
  const isFirstLoad = useRef(true);
  const processedPrompts = useRef(new Set<string>());

  // 选择正确的对话
  useEffect(() => {
    chooseActiveConversation(conversationId);
  }, [conversationId, chooseActiveConversation]);

  // 从存储的会话中加载消息
  useEffect(() => {
    if (activeConversation) {
      // 确保消息数组存在并且有内容
      if (
        activeConversation.messages &&
        activeConversation.messages.length > 0
      ) {
        const filteredMessages = activeConversation.messages.filter(
          (msg) => !(msg as FunctionCallingUiMessage).isLoading
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
    const timer = setTimeout(() => {
      scrollToBottom(msgContainerRef.current);
      clearTimeout(timer);
    }, 200);

    return () => {
      clearTimeout(timer);
    };
  }, [activeConversation?.messages]);

  // 处理URL中的prompt参数
  useEffect(() => {
    if (isFirstLoad.current && activeConversation) {
      const queryParams = new URLSearchParams(location.search);
      const urlPrompt = queryParams.get("prompt");

      if (urlPrompt && !processedPrompts.current.has(urlPrompt)) {
        // 标记此prompt已处理，避免重复处理
        processedPrompts.current.add(urlPrompt);
        console.log("从URL参数获取提示词:", urlPrompt);

        // 清除URL中的prompt参数，防止刷新页面重复发送
        const newUrl = window.location.hash.split("?")[0];
        window.history.replaceState({}, document.title, newUrl);

        // 设置输入内容并自动发送
        setTimeout(() => {
          handleSendMessage(urlPrompt);
        }, 300);
      }

      isFirstLoad.current = false;
    }
  }, [location.search, activeConversation]);

  const handleSendMessage = async (text: string) => {
    const createMessage = (
      text: string,
      timestamp: number
    ): FunctionCallingUiMessage => ({
      role: "user",
      content: text,
      timestamp,
    });

    const sendRequest = async (
      text: string,
      userTimestamp: number,
      userMessage: FunctionCallingUiMessage
    ) => {
      const result = await getToolCalling(text, conversationId);

      let toolText = "";
      // 如果是工具调用，可以先显示工具调用的中间状态
      if (result.toolName) {
        toolText = `调用工具: ${result.toolName}\n${result.toolParameters}`;
        appendAssistantMessage(
          toolText,
          "assistant",
          result.status !== "SUCCESS",
          userTimestamp,
          userMessage
        );
      }

      // 显示最终结果
      const responseText =
        result.toolResult || result.toolResponse || "工具调用失败";

      const totalText = toolText
        ? `<tool>${toolText}</tool>\n${responseText}`
        : responseText;

      appendAssistantMessage(
        totalText,
        "assistant",
        result.status !== "SUCCESS",
        userTimestamp,
        userMessage
      );
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
    const appendAssistantMessageWithBase = (
      messageContent: string,
      role: "assistant",
      isError: boolean,
      userTimestamp: number,
      userMessage: FunctionCallingUiMessage
    ) => {
      appendAssistantMessage(
        messageContent,
        role,
        isError,
        userTimestamp,
        userMessage,
        remainingMessages as FunctionCallingUiMessage[]
      );
    };

    const sendRequest = async (
      text: string,
      userTimestamp: number,
      userMessage: FunctionCallingUiMessage
    ) => {
      const result = await getToolCalling(text, conversationId);

      let toolText = "";
      // 如果是工具调用，可以先显示工具调用的中间状态
      if (result.toolName) {
        toolText = `调用工具: ${result.toolName}\n${result.toolParameters}`;
        appendAssistantMessageWithBase(
          toolText,
          "assistant",
          result.status !== "SUCCESS",
          userTimestamp,
          userMessage
        );
      }

      // 显示最终结果
      const responseText =
        result.toolResult || result.toolResponse || "工具调用失败";

      const totalText = toolText
        ? `<tool>${toolText}</tool>\n${responseText}`
        : responseText;

      appendAssistantMessageWithBase(
        totalText,
        "assistant",
        result.status !== "SUCCESS",
        userTimestamp,
        userMessage
      );
    };

    try {
      await sendRequest(
        userMessage.content,
        userMessage.timestamp,
        userMessage as FunctionCallingUiMessage
      );
    } catch (error) {
      console.error("重新生成消息错误:", error);
      appendAssistantMessage(
        "抱歉，重新生成回复时出现错误。",
        "assistant",
        true,
        userMessage.timestamp,
        userMessage as FunctionCallingUiMessage,
        remainingMessages as FunctionCallingUiMessage[]
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
        ? ({ ...msg, content: newContent } as FunctionCallingUiMessage)
        : msg
    ) as FunctionCallingUiMessage[];

    // 立即更新会话状态
    updateActiveConversation({
      ...activeConversation,
      messages: updatedMessages,
    });

    // 直接重新生成回复
    setIsLoading(true);

    // 创建一个使用正确baseMessages的更新函数
    const appendAssistantMessageWithBase = (
      messageContent: string,
      role: "assistant",
      isError: boolean,
      userTimestamp: number,
      userMessage: FunctionCallingUiMessage
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
    const updatedUserMessage: FunctionCallingUiMessage = {
      ...message,
      content: newContent,
    } as FunctionCallingUiMessage;

    const sendRequest = async (
      text: string,
      userTimestamp: number,
      userMessage: FunctionCallingUiMessage
    ) => {
      const result = await getToolCalling(text, conversationId);

      let toolText = "";
      // 如果是工具调用，可以先显示工具调用的中间状态
      if (result.toolName) {
        toolText = `调用工具: ${result.toolName}\n${result.toolParameters}`;
        appendAssistantMessageWithBase(
          toolText,
          "assistant",
          result.status !== "SUCCESS",
          userTimestamp,
          userMessage
        );
      }

      // 显示最终结果
      const responseText =
        result.toolResult || result.toolResponse || "工具调用失败";

      const totalText = toolText
        ? `<tool>${toolText}</tool>\n${responseText}`
        : responseText;

      appendAssistantMessageWithBase(
        totalText,
        "assistant",
        result.status !== "SUCCESS",
        userTimestamp,
        userMessage
      );
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
    <BasePage title="天气查询 Function Calling" conversationId={conversationId}>
      <div className={styles.container}>
        {/* 左侧面板 - 代码展示和输入框 */}
        <div className={styles.leftPanel}>
          <CodeInfo />
          <div className={styles.senderWrapper}>
            <Sender
              value={inputContent}
              onChange={setInputContent}
              onSubmit={handleSendMessage}
              placeholder="例如：北京今天的天气..."
              className={styles.sender}
              loading={isLoading}
            />
          </div>
        </div>

        {/* 右侧面板  */}
        <div className={styles.rightPanel}>
          <div
            className={`${styles.card} ${styles.resultPanel}`}
            // ref={msgContainerRef}
          >
            <h2 className={styles.panelTitle}>地图查询&中英翻译功能演示</h2>
            <div className={styles.messagesContainer} ref={msgContainerRef}>
              {messages.length === 0 && !conversationId ? (
                <ResponseBubble
                  content="你好！我可以帮你查询全球各地的天气信息。例如，你可以问我北京今天的天气怎么样？或上海明天会下雨吗？或纽约本周末的气温如何？请告诉我你想了解哪个地区的天气信息。"
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

export default FunctionCallingConversationView;
