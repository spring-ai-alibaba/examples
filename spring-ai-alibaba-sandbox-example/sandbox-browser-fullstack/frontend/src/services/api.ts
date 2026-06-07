import axios from 'axios'
import { BrowserInfo } from '../types/chat'

const API_BASE = '/api'

export async function streamChat(
  sessionId: string,
  message: string,
  onChunk: (chunk: string) => void
): Promise<void> {
  const eventSource = new EventSource(
    `${API_BASE}/chat/stream?sessionId=${sessionId}&message=${encodeURIComponent(message)}`
  )

  return new Promise((resolve, reject) => {
    eventSource.addEventListener('message', (event) => {
      const chunk = event.data
      if (chunk === '[DONE]') {
        eventSource.close()
        resolve()
      } else {
        onChunk(chunk)
      }
    })

    eventSource.addEventListener('error', (error) => {
      eventSource.close()
      reject(error)
    })
  })
}

export async function getBrowserInfo(sessionId: string): Promise<BrowserInfo | null> {
  try {
    const response = await axios.get<BrowserInfo>(
      `${API_BASE}/browser/info?sessionId=${sessionId}`
    )
    return response.data
  } catch (error) {
    return null
  }
}
