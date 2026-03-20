export interface ChatMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
}

export interface BrowserInfo {
  desktopUrl: string
  sessionId: string
  status: string
}
