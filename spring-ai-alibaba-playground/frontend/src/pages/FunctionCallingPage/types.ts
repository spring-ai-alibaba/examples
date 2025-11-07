import { BaseMessage, Message } from "../../types/message";

export { Message };

export interface McpConversationViewProps {
  conversationId: string;
}

export interface InputResultProps {
  messages: Message[];
  title?: string;
}

export interface FunctionCallingUiMessage extends BaseMessage {}
