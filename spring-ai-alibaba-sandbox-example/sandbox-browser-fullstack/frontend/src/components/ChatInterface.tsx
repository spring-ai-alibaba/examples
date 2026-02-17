import { useEffect, useMemo, useRef, useState } from 'react'
import { Box, Chip, Divider, Stack, Typography } from '@mui/material'
import MessageList from './MessageList'
import MessageInput from './MessageInput'
import { streamChat } from '../services/api'
import { ChatMessage } from '../types/chat'
import { copy, Language } from '../i18n'

interface Props {
  sessionId: string
  language: Language
  onSendStart?: () => void
}

function ChatInterface({ sessionId, language, onSendStart }: Props) {
  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const text = useMemo(() => copy[language], [language])
  const suggestions = useMemo(
    () =>
      language === 'zh'
        ? [
            '打开 github.com 并搜索 spring-ai-alibaba',
            '访问 baidu.com 并总结首页模块',
            '去 stackoverflow.com 查找 Spring AI 相关帖子'
          ]
        : [
            'Open github.com and search spring-ai-alibaba',
            'Visit baidu.com and summarize homepage sections',
            'Go to stackoverflow.com and find Spring AI posts'
          ],
    [language]
  )
  const messagesEndRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  const handleSendMessage = async (content: string) => {
    onSendStart?.()

    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      role: 'user',
      content,
      timestamp: new Date()
    }
    setMessages(prev => [...prev, userMessage])
    setIsLoading(true)

    const assistantMessage: ChatMessage = {
      id: (Date.now() + 1).toString(),
      role: 'assistant',
      content: '',
      timestamp: new Date()
    }
    setMessages(prev => [...prev, assistantMessage])

    try {
      await streamChat(sessionId, content, (chunk) => {
        setMessages(prev => {
          const updated = [...prev]
          const lastMsg = updated[updated.length - 1]
          if (lastMsg?.role === 'assistant') {
            lastMsg.content += chunk
          }
          return updated
        })
      })
    } catch (error) {
      setMessages(prev => {
        const updated = [...prev]
        const lastMsg = updated[updated.length - 1]
        if (lastMsg?.role === 'assistant' && !lastMsg.content.trim()) {
          lastMsg.content = text.requestFailed
        }
        return updated
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: { xs: 620, md: 720 } }}>
      <Box sx={{ px: 3, py: 2 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center" gap={2}>
          <Box>
            <Typography variant="h6">{text.chatTitle}</Typography>
            <Typography variant="body2" color="text.secondary">
              {text.chatSubtitle}
            </Typography>
          </Box>
          <Chip
            label={isLoading ? text.runningTask : text.idle}
            color={isLoading ? 'warning' : 'success'}
            variant={isLoading ? 'filled' : 'outlined'}
          />
        </Stack>
      </Box>

      <Divider />

      <Box sx={{ px: 3, py: 1.5, display: 'flex', gap: 1, overflowX: 'auto' }}>
        {suggestions.map(prompt => (
          <Chip
            key={prompt}
            label={prompt}
            onClick={() => !isLoading && handleSendMessage(prompt)}
            color="primary"
            variant="outlined"
            clickable={!isLoading}
          />
        ))}
      </Box>

      <Divider />
      <MessageList messages={messages} language={language} />
      <div ref={messagesEndRef} />
      <Divider />
      <MessageInput onSend={handleSendMessage} disabled={isLoading} language={language} />
    </Box>
  )
}

export default ChatInterface
