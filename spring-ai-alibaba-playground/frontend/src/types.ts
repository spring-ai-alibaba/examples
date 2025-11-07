import { XAgent } from "@ant-design/x/es/useXAgent";

// 功能菜单配置
export interface FunctionMenuItem {
  key: string;
  icon: React.ReactNode;
  label: string;
  link?: string;
  render?: (props: any) => React.ReactNode;
}

export interface CommonOption {
  label: string;
  value: string;
}

export interface ModelOption extends CommonOption {
  desc: string;
}

export interface CommonMenuPageComponentProps {
  agent: XAgent;
  onRequest: (message: string) => void;
  messages: any[];
  setMessages: (messages: any[]) => void;
}
