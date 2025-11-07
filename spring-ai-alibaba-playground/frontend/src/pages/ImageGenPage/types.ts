export interface ImageResponse {
  blob?: Blob;
  status?: number;
}

export interface GeneratedImageType {
  id: string;
  url: string;
  prompt: string;
  blob?: Blob;
}

export interface ExtendedChatMessage {
  role: "user" | "assistant";
  content: string;
  timestamp: number;
  images?: GeneratedImageType[];
  isLoading?: boolean;
  isError?: boolean;
}
