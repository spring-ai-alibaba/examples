import { message } from "antd";
import type { GetProp, UploadProps } from "antd";
import { ChatMessage } from "./stores/conversation.store";
import { Message } from "./menuPages/functionCallingPage/types";

type FileType = Parameters<GetProp<UploadProps, "beforeUpload">>[0];

// 限制文件大小
export const litFileSize = (file: FileType, size: number) => {
  const isLt2M = file.size / 1024 / size < 1;
  if (!isLt2M) {
    message.error(`Image must smaller than ${size}KB!`);
  }
  return isLt2M;
};

export const decoder = new TextDecoder("utf-8");

// 聊天持久化消息映射为UI表示
export const mapStoredMessagesToUIMessages = (
  messages: ChatMessage[]
): Message[] => {
  if (!messages || !Array.isArray(messages)) {
    console.warn("无效的消息数组:", messages);
    return [];
  }

  return (
    messages
      ?.filter((msg) => !msg.isLoading) // 过滤掉加载中的消息
      ?.map((msg) => {
        return {
          id: `msg-${msg.timestamp}`,
          text: msg.content || "",
          sender: msg.role === "user" ? "user" : "bot",
          timestamp: msg.timestamp,
        };
      }) ?? []
  );
};

export const scrollToBottom = (container: HTMLElement | null) => {
  if (!container) return;
  requestAnimationFrame(() => {
    const lastMessage = container.lastElementChild as HTMLElement;

    if (lastMessage) {
      // 使用平滑滚动
      lastMessage.scrollIntoView({ behavior: "smooth", block: "end" });
    }
  });
};

export const throttle = (func: (...args: any[]) => void, time: number) => {
  let inThrottle: boolean;
  return function (...args: any[]) {
    const context = this;
    if (!inThrottle) {
      func.apply(context, args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), time);
    }
  };
};
