import React, { useState, useEffect, useRef, useCallback } from "react";
import { useLocation } from "react-router-dom";
import { Sender } from "@ant-design/x";
import { Card, message } from "antd";
import { useStyles } from "../../style";
import GeneratedImage from "../GeneratedImage";
import { useConversationContext } from "../../../../stores/conversation.store";
import { getImage } from "../../../../api/image";
import RequestBubble from "../../../components/RequestBubble";
import ResponseBubble from "../../../components/ResponseBubble";
import {
  ExtendedChatMessage,
  GeneratedImageType,
  ImageResponse,
} from "../../types";
import { scrollToBottom } from "../../../../utils";

const ImageGenConversationView: React.FC<{ conversationId: string }> = ({
  conversationId,
}) => {
  const { styles } = useStyles();
  const location = useLocation();
  const [inputContent, setInputContent] = useState("");
  const [isGenerating, setIsGenerating] = useState(false);
  const messagesContainerRef = useRef<HTMLDivElement>(null);
  const {
    activeConversation,
    chooseActiveConversation,
    updateActiveConversation,
  } = useConversationContext();

  const isFirstLoad = useRef(true);
  const processedPrompts = useRef(new Set<string>());
  const objectUrlsRef = useRef<string[]>([]);

  useEffect(() => {
    chooseActiveConversation(conversationId);
  }, [conversationId, chooseActiveConversation]);

  useEffect(() => {
    return () => {
      objectUrlsRef.current.forEach((url) => {
        URL.revokeObjectURL(url);
      });
      objectUrlsRef.current = [];
    };
  }, []);

  // 监听消息变化，触发滚动
  useEffect(() => {
    scrollToBottom(messagesContainerRef.current);
  }, [activeConversation?.messages]);

  useEffect(() => {
    if (isFirstLoad.current && activeConversation) {
      const queryParams = new URLSearchParams(location.search);
      const urlPrompt = queryParams.get("prompt");

      if (urlPrompt && !processedPrompts.current.has(urlPrompt)) {
        // 标记此prompt已处理，避免重复生成
        processedPrompts.current.add(urlPrompt);

        // 清除URL中的prompt参数，防止刷新页面重复发送
        const newUrl = window.location.hash.split("?")[0];
        window.history.replaceState({}, document.title, newUrl);

        // 延迟一点处理以确保组件完全加载
        setTimeout(() => {
          handleGenerateImage(urlPrompt);
        }, 300);
      }

      isFirstLoad.current = false;
    }
  }, [location.search, activeConversation]);

  // 恢复存储的图像
  useEffect(() => {
    if (activeConversation) {
      let hasChanges = false;
      const updatedMessages = activeConversation.messages.map((message) => {
        if (message.role === "assistant" && message.images) {
          const updatedImages = message.images.map((image) => {
            if (
              image.dataUrl &&
              (!image.url || image.url.startsWith("blob:") || !image.blob)
            ) {
              hasChanges = true;
              return {
                ...image,
                url: image.dataUrl,
              };
            }
            return image;
          });

          return {
            ...message,
            images: updatedImages,
          };
        }
        return message;
      });

      if (hasChanges) {
        updateActiveConversation({
          ...activeConversation,
          messages: updatedMessages,
        });
      }
    }
  }, [activeConversation?.id]);

  const handleGenerateImage = async (prompt: string) => {
    if (!prompt.trim()) return;
    if (!activeConversation) return;

    setIsGenerating(true);
    console.log("生成图像开始, prompt:", prompt);

    // 添加用户消息
    const userMessage = {
      role: "user" as const,
      content: prompt,
      timestamp: Date.now(),
    };
    // 添加生成中的占位消息
    const placeholderMessage: ExtendedChatMessage = {
      role: "assistant" as const,
      content: "生成中...",
      timestamp: Date.now() + 1,
      isLoading: true,
    };

    // 更新会话，包含占位消息
    const updatedMessages = [
      ...activeConversation.messages,
      userMessage,
      placeholderMessage,
    ] as ExtendedChatMessage[];
    const updatedConversation = {
      ...activeConversation,
      messages: updatedMessages,
    };
    updateActiveConversation(updatedConversation);

    scrollToBottom(messagesContainerRef.current);

    let timeoutId: number | null = null;
    try {
      // 设置超时检测
      timeoutId = window.setTimeout(() => {
        // 如果仍在生成，则认为超时
        if (isGenerating) {
          setIsGenerating(false);

          // 替换占位消息为超时消息
          const timeoutMessages = updatedMessages.map((msg) =>
            msg === placeholderMessage
              ? ({
                  ...msg,
                  content: "图像生成超时，请重试",
                  isLoading: false,
                  isError: true,
                } as ExtendedChatMessage)
              : msg
          );

          updateActiveConversation({
            ...activeConversation,
            messages: timeoutMessages,
          });

          message.error("图像生成超时，请重试");
        }
      }, 30000); // 30秒超时

      // 调用图像生成API
      console.log("调用图像生成API...");
      let response: ImageResponse | null = null;

      try {
        response = (await getImage(prompt)) as ImageResponse;
        console.log("API响应:", response);

        // 清除超时检测
        if (timeoutId) {
          clearTimeout(timeoutId);
          timeoutId = null;
        }

        if (response && response.blob) {
          console.log(
            "收到blob响应，大小:",
            response.blob.size,
            "类型:",
            response.blob.type
          );

          // 创建URL以显示图像
          const imageUrl = URL.createObjectURL(response.blob);
          console.log("创建的Object URL:", imageUrl);

          // 保存URL以便后续清理
          objectUrlsRef.current.push(imageUrl);

          // 创建生成的图像对象
          const generatedImages: GeneratedImageType[] = [
            {
              id: `img-${Date.now()}`,
              url: imageUrl,
              prompt: prompt,
              blob: response.blob,
            },
          ];

          // 添加助手消息，包含生成的图像
          const assistantMessage: ExtendedChatMessage = {
            role: "assistant" as const,
            content: "",
            timestamp: Date.now(),
            images: generatedImages,
          };

          // 更新会话，移除占位消息，添加真实图像
          // 确保保留用户消息，然后加上新的助手消息
          const finalMessages = activeConversation.messages
            .filter((msg) => !(msg as ExtendedChatMessage).isLoading)
            .concat([assistantMessage]);

          // 检查最终消息中是否包含了用户消息，如果没有，添加它
          const hasUserMessage = finalMessages.some(
            (msg) => msg.role === "user" && msg.content === prompt
          );

          if (!hasUserMessage) {
            finalMessages.push(userMessage);
            // 重新排序消息，确保用户消息在助手消息之前
            finalMessages.sort((a, b) => a.timestamp - b.timestamp);
          }

          console.log("更新会话，添加生成的图像");
          updateActiveConversation({
            ...activeConversation,
            messages: finalMessages,
          });

          // 清空输入
          setInputContent("");
        } else {
          // 替换占位消息为错误消息
          const errorMessages = updatedMessages.map((msg) =>
            msg === placeholderMessage
              ? ({
                  ...msg,
                  content: "图像生成失败，请重试",
                  isLoading: false,
                  isError: true,
                } as ExtendedChatMessage)
              : msg
          );

          // 检查错误消息列表中是否包含用户消息
          const hasUserMessage = errorMessages.some(
            (msg) => msg.role === "user" && msg.content === prompt
          );

          if (!hasUserMessage) {
            errorMessages.push(userMessage);
            // 重新排序消息，确保用户消息在前面
            errorMessages.sort((a, b) => a.timestamp - b.timestamp);
          }

          updateActiveConversation({
            ...activeConversation,
            messages: errorMessages,
          });

          message.error("图像生成失败，请重试");
        }
      } catch (error) {
        // 清除超时检测
        if (timeoutId) {
          clearTimeout(timeoutId);
          timeoutId = null;
        }

        console.error("处理图像生成请求错误:", error);

        // 替换占位消息为错误消息
        const errorMessages = updatedMessages.map((msg) =>
          msg === placeholderMessage
            ? ({
                ...msg,
                content: "图像生成失败，请重试",
                isLoading: false,
                isError: true,
              } as ExtendedChatMessage)
            : msg
        );

        // 检查错误消息列表中是否包含用户消息
        const hasUserMessage = errorMessages.some(
          (msg) => msg.role === "user" && msg.content === prompt
        );

        if (!hasUserMessage) {
          errorMessages.push(userMessage);
          // 重新排序消息，确保用户消息在前面
          errorMessages.sort((a, b) => a.timestamp - b.timestamp);
        }

        updateActiveConversation({
          ...activeConversation,
          messages: errorMessages,
        });

        message.error("图像生成失败，请重试");
      }
    } catch (error) {
      console.error("处理图像生成请求错误:", error);
      setIsGenerating(false);

      if (timeoutId) {
        clearTimeout(timeoutId);
        timeoutId = null;
      }
    } finally {
      setIsGenerating(false);
    }
  };

  // 重新生成图像
  const handleRegenerate = async (prompt: string, imageId: string) => {
    if (!activeConversation) return;

    setIsGenerating(true);

    try {
      // 调用图像生成API
      const response = (await getImage(prompt)) as ImageResponse;

      if (response && response.blob) {
        // 创建URL以显示图像
        const imageUrl = URL.createObjectURL(response.blob);
        // 保存URL以便后续清理
        objectUrlsRef.current.push(imageUrl);

        // 创建新的图像对象
        const newImage: GeneratedImageType = {
          id: `img-${Date.now()}`,
          url: imageUrl,
          prompt: prompt,
          blob: response.blob,
        };

        // 更新消息中的图像
        const updatedMessages = activeConversation.messages.map((message) => {
          if (message.role === "assistant" && message.images) {
            return {
              ...message,
              images: message.images.map((img) =>
                img.id === imageId ? newImage : img
              ),
            };
          }
          return message;
        });

        // 更新会话
        updateActiveConversation({
          ...activeConversation,
          messages: updatedMessages,
        });
      } else {
        message.error("图像重新生成失败");
      }
    } catch (error) {
      console.error("重新生成图像错误:", error);
      message.error("图像重新生成失败，请重试");
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className={styles.container}>
      {/* 输入框区域 */}
      <div className={styles.inputArea}>
        <Sender
          value={inputContent}
          onChange={setInputContent}
          onSubmit={handleGenerateImage}
          placeholder="请输入图片生成提示词..."
          className={styles.sender}
          loading={isGenerating}
        />
      </div>

      {/* 消息和生成的图片展示区域 */}
      <div ref={messagesContainerRef} className={styles.messagesContainer}>
        {activeConversation?.messages.map((message: any) => (
          <div key={message.timestamp} className={styles.messageItem}>
            {/* 用户消息 */}
            {message.role === "user" && (
              <RequestBubble
                content={message.content}
                timestamp={message.timestamp}
              />
            )}

            {/* 占位消息或错误消息 */}
            {message.role === "assistant" && !message.images && (
              <ResponseBubble
                content={message.content}
                timestamp={message.timestamp}
                isError={message.isError}
              />
            )}

            {/* 生成的图像 */}
            {message.role === "assistant" && message.images && (
              <div className={styles.imageGallery}>
                {message.images.map((image) => (
                  <Card
                    key={image.id}
                    bodyStyle={{ padding: 0 }}
                    className={styles.imageCard}
                  >
                    <GeneratedImage
                      id={image.id}
                      url={image.url}
                      prompt={image.prompt}
                      onReload={(prompt) => handleRegenerate(prompt, image.id)}
                    />
                  </Card>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default ImageGenConversationView;
