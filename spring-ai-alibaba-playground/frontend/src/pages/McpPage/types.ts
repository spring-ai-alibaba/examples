import { createStyles } from "antd-style";

// MCP API response interfaces
export interface McpApiResponse {
  code: number;
  message: string;
  data: McpServer[];
}

export interface McpServer {
  id: string;
  name: string;
  desc: string | null;
  env: Record<string, string>;
  toolList: McpTool[];
}

export interface McpTool {
  name: string;
  params: string; // JSON schema string
  desc: string;
}

// Formatted interfaces for UI
export interface McpServerFormatted {
  id: string;
  name: string;
  icon: string;
  description: string;
  tools: McpToolFormatted[];
  env?: Record<string, string>;
}

export interface McpToolFormatted {
  id: string;
  name: string;
  description: string;
  params: Record<string, any>; // Parsed params
  schema?: any; // Parsed JSON schema
}

export interface FormField {
  key: string;
  label: string;
  fieldType: string;
  placeholder: string;
  required: boolean;
  description?: string;
}
