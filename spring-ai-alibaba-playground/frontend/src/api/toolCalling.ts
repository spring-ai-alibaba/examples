import { BASE_URL } from "../const";

// Tool calling API interface
export interface ToolCallResponse {
  status: "SUCCESS" | "FAILURE" | "UNKNOWN" | "RUNNING";
  toolName: string;
  toolParameters: string;
  toolResult: string;
  toolStartTime: string;
  toolEndTime: string;
  errorMessage?: string;
  toolInput: string;
  toolCostTime: number;
  toolResponse?: string;
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export const getToolCalling = async (
  prompt: string,
  chatId?: string
): Promise<ToolCallResponse> => {
  const url = BASE_URL + "/tool-call?prompt=" + encodeURIComponent(prompt);

  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };

  if (chatId) {
    headers["chatId"] = chatId;
  }

  const res = await fetch(url.toString(), {
    method: "GET",
    headers: headers,
  });

  if (!res.ok) {
    throw new Error("Failed to call tool");
  }

  const data: ApiResponse<ToolCallResponse> = await res.json();
  if (data.code !== 10000) {
    throw new Error(data.message || "Failed to call tool");
  }

  return data.data;
};
