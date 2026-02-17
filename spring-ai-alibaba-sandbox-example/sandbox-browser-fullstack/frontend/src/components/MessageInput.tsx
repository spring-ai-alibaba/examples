import { FormEvent, useMemo, useState } from 'react'
import { Box, CircularProgress, IconButton, TextField, Tooltip } from '@mui/material'
import SendRoundedIcon from '@mui/icons-material/SendRounded'
import { copy, Language } from '../i18n'

interface Props {
  onSend: (message: string) => void
  disabled?: boolean
  language: Language
}

function MessageInput({ onSend, disabled, language }: Props) {
  const [input, setInput] = useState('')
  const text = useMemo(() => copy[language], [language])

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault()
    if (input.trim() && !disabled) {
      onSend(input.trim())
      setInput('')
    }
  }

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ p: 2 }}>
      <Box
        sx={{
          display: 'flex',
          gap: 1.5,
          alignItems: 'center',
          p: 1,
          border: '1px solid',
          borderColor: 'divider',
          borderRadius: 3,
          bgcolor: 'background.paper'
        }}
      >
        <TextField
          fullWidth
          variant="standard"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          disabled={disabled}
          placeholder={text.inputPlaceholder}
          InputProps={{ disableUnderline: true }}
        />
        <Tooltip title={text.send}>
          <span>
            <IconButton
              type="submit"
              color="primary"
              disabled={disabled || !input.trim()}
              sx={{ bgcolor: 'primary.main', color: '#fff', '&:hover': { bgcolor: 'primary.dark' } }}
            >
              {disabled ? <CircularProgress size={18} color="inherit" /> : <SendRoundedIcon />}
            </IconButton>
          </span>
        </Tooltip>
      </Box>
    </Box>
  )
}

export default MessageInput
