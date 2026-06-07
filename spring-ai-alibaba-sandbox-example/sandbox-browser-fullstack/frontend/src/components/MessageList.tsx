import { Avatar, Box, Paper, Stack, Typography } from '@mui/material'
import SmartToyIcon from '@mui/icons-material/SmartToy'
import PersonIcon from '@mui/icons-material/Person'
import { ChatMessage } from '../types/chat'
import { copy, Language } from '../i18n'

interface Props {
  messages: ChatMessage[]
  language: Language
}

function MessageList({ messages, language }: Props) {
  const text = copy[language]

  return (
    <Box sx={{ flex: 1, overflowY: 'auto', px: 3, py: 2, bgcolor: '#f7f9fc' }}>
      {messages.length === 0 ? (
        <Paper variant="outlined" sx={{ p: 3, borderStyle: 'dashed' }}>
          <Typography variant="body1" gutterBottom>
            {text.welcomeTitle}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {text.welcomeHint}
          </Typography>
        </Paper>
      ) : (
        <Stack spacing={2}>
          {messages.map((message) => (
            <Stack
              key={message.id}
              direction={message.role === 'user' ? 'row-reverse' : 'row'}
              spacing={1.5}
              alignItems="flex-start"
            >
              <Avatar
                sx={{
                  bgcolor: message.role === 'user' ? 'primary.main' : 'secondary.main',
                  width: 34,
                  height: 34
                }}
              >
                {message.role === 'user' ? <PersonIcon fontSize="small" /> : <SmartToyIcon fontSize="small" />}
              </Avatar>
              <Paper
                elevation={0}
                sx={{
                  maxWidth: { xs: '88%', md: '86%' },
                  px: 2,
                  py: 1.5,
                  border: '1px solid',
                  borderColor: message.role === 'user' ? 'primary.light' : 'divider',
                  bgcolor: message.role === 'user' ? 'primary.50' : 'background.paper'
                }}
              >
                <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 0.6 }}>
                  {message.role === 'user' ? text.you : text.agentName} • {message.timestamp.toLocaleTimeString()}
                </Typography>
                <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.65 }}>
                  {message.content}
                </Typography>
              </Paper>
            </Stack>
          ))}
        </Stack>
      )}
    </Box>
  )
}

export default MessageList
