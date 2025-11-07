import { GeneratedImage } from "../../stores/conversation.store";
import { Message } from "../FunctionCallingPage/types";

declare global {
  interface Window {
    tempImageBase64?: string;
  }
}

export interface ChatConversationViewProps {
  conversationId: string;
}

// 由于后端接口和前端组件的 Message 差异所以产生了两种，后续有时间的话希望能抹平这个差异
// export interface Message {
//   id: string;
//   sender: "user" | "bot";
//   text: string;
//   timestamp: number;
//   isError?: boolean;
// }

export { Message };

export interface ChatMessage extends Message {
  images?: GeneratedImage[];
}

export interface AiCapabilities {
  deepThink: boolean;
  onlineSearch: boolean;
}
