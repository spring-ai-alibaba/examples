import { MouseEvent, useEffect, useMemo, useRef, useState } from 'react'
import {
  AppBar,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Container,
  Stack,
  ToggleButton,
  ToggleButtonGroup,
  Toolbar,
  Typography
} from '@mui/material'
import VisibilityIcon from '@mui/icons-material/Visibility'
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff'
import TravelExploreIcon from '@mui/icons-material/TravelExplore'
import { v4 as uuidv4 } from 'uuid'
import ChatInterface from './components/ChatInterface'
import VNCPanel from './components/VNCPanel'
import { getBrowserInfo } from './services/api'
import { BrowserInfo } from './types/chat'
import { copy, Language } from './i18n'

function App() {
  const MIN_RIGHT_PANEL_WIDTH = 300
  const MIN_LEFT_PANEL_WIDTH = 420
  const EXPANDED_RIGHT_PANEL_WIDTH = 520

  const [sessionId] = useState(() => uuidv4())
  const [browserInfo, setBrowserInfo] = useState<BrowserInfo | null>(null)
  const [showVNC, setShowVNC] = useState(false)
  const [rightPanelExpanded, setRightPanelExpanded] = useState(true)
  const [rightPanelWidth, setRightPanelWidth] = useState(EXPANDED_RIGHT_PANEL_WIDTH)
  const [isDraggingSplitter, setIsDraggingSplitter] = useState(false)
  const [language, setLanguage] = useState<Language>('zh')
  const splitLayoutRef = useRef<HTMLDivElement>(null)

  const text = useMemo(() => copy[language], [language])

  const refreshBrowserInfo = async () => {
    const info = await getBrowserInfo(sessionId)
    if (info) {
      setBrowserInfo(info)
    }
  }

  useEffect(() => {
    const intervalMs = showVNC && !browserInfo ? 900 : 3000
    const interval = setInterval(async () => {
      await refreshBrowserInfo()
    }, intervalMs)

    return () => clearInterval(interval)
  }, [sessionId, showVNC, browserInfo])

  const handleToggleVnc = () => {
    setShowVNC(!showVNC)
  }

  const clampRightPanelWidth = (nextWidth: number) => {
    const containerWidth = splitLayoutRef.current?.clientWidth
    if (!containerWidth) {
      return Math.max(nextWidth, MIN_RIGHT_PANEL_WIDTH)
    }
    const maxRightWidth = Math.max(MIN_RIGHT_PANEL_WIDTH, containerWidth - MIN_LEFT_PANEL_WIDTH)
    return Math.min(Math.max(nextWidth, MIN_RIGHT_PANEL_WIDTH), maxRightWidth)
  }

  const handleSplitterMouseDown = (event: MouseEvent<HTMLDivElement>) => {
    event.preventDefault()
    setIsDraggingSplitter(true)
    const startX = event.clientX
    const startWidth = rightPanelWidth

    const onMouseMove = (moveEvent: globalThis.MouseEvent) => {
      // Moving left enlarges the right panel, moving right shrinks it.
      const delta = startX - moveEvent.clientX
      setRightPanelWidth(clampRightPanelWidth(startWidth + delta))
    }

    const onMouseUp = () => {
      setIsDraggingSplitter(false)
      window.removeEventListener('mousemove', onMouseMove)
      window.removeEventListener('mouseup', onMouseUp)
    }

    window.addEventListener('mousemove', onMouseMove)
    window.addEventListener('mouseup', onMouseUp)
  }

  useEffect(() => {
    const container = splitLayoutRef.current
    if (!container || !showVNC) {
      return
    }

    const observer = new ResizeObserver(() => {
      setRightPanelWidth(prev => clampRightPanelWidth(prev))
    })
    observer.observe(container)
    return () => observer.disconnect()
  }, [showVNC])

  const handleSendStart = async () => {
    setShowVNC(true)
    setRightPanelExpanded(true)
    setRightPanelWidth(EXPANDED_RIGHT_PANEL_WIDTH)
    await refreshBrowserInfo()
  }

  return (
    <Box sx={{ minHeight: '100vh' }}>
      <AppBar position="sticky" elevation={0} sx={{ bgcolor: 'primary.main' }}>
        <Toolbar sx={{ minHeight: 72, gap: 2, flexWrap: 'wrap', py: 1 }}>
          <TravelExploreIcon />
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h6">{text.appTitle}</Typography>
            <Typography variant="caption" sx={{ opacity: 0.9 }}>
              {text.appSubtitle}
            </Typography>
          </Box>

          <ToggleButtonGroup
            size="small"
            exclusive
            value={language}
            onChange={(_, value: Language | null) => value && setLanguage(value)}
            sx={{ bgcolor: 'rgba(255,255,255,0.16)' }}
          >
            <ToggleButton value="en" sx={{ color: '#fff', '&.Mui-selected': { color: '#fff' } }}>EN</ToggleButton>
            <ToggleButton value="zh" sx={{ color: '#fff', '&.Mui-selected': { color: '#fff' } }}>中文</ToggleButton>
          </ToggleButtonGroup>

          <Button
            onClick={handleToggleVnc}
            color="secondary"
            startIcon={showVNC ? <VisibilityOffIcon /> : <VisibilityIcon />}
          >
            {showVNC ? text.hideBrowser : text.showBrowser}
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ py: 3 }}>
        <Stack
          direction={{ xs: 'column', xl: 'row' }}
          spacing={{ xs: 3, xl: showVNC ? 0 : 3 }}
          ref={splitLayoutRef}
        >
          <Box
            sx={{
              flex: showVNC ? { xs: '1 1 auto', xl: `1 1 calc(100% - ${rightPanelWidth}px)` } : '1 1 auto',
              minWidth: 0
            }}
          >
            <Card>
              <CardContent sx={{ p: 0 }}>
                <ChatInterface
                  sessionId={sessionId}
                  language={language}
                  onSendStart={handleSendStart}
                />
              </CardContent>
            </Card>
          </Box>

          {showVNC ? (
            <>
              <Box
                onMouseDown={handleSplitterMouseDown}
                sx={{
                  display: { xs: 'none', xl: 'flex' },
                  width: 14,
                  alignItems: 'center',
                  justifyContent: 'center',
                  cursor: 'col-resize',
                  userSelect: 'none',
                  bgcolor: isDraggingSplitter ? 'action.selected' : 'transparent',
                  transition: 'background-color 0.2s ease'
                }}
              >
                <Box
                  sx={{
                    width: 4,
                    height: '55%',
                    borderRadius: 999,
                    bgcolor: isDraggingSplitter ? 'primary.main' : 'divider'
                  }}
                />
              </Box>

              <Box
                sx={{
                  flex: { xs: '1 1 auto', xl: `0 0 ${rightPanelWidth}px` },
                  minWidth: { xl: `${MIN_RIGHT_PANEL_WIDTH}px` }
                }}
              >
                <Card>
                  <CardContent sx={{ p: 0 }}>
                    {browserInfo ? (
                      <VNCPanel
                        browserInfo={browserInfo}
                        language={language}
                        compact={!rightPanelExpanded && rightPanelWidth < 430}
                      />
                    ) : (
                      <Box sx={{ p: 3, minHeight: rightPanelExpanded ? 420 : 220 }}>
                        <Typography variant="h6" gutterBottom>{text.vncTitle}</Typography>
                        <Typography variant="body2" color="text.secondary">
                          {text.browserWaiting}
                        </Typography>
                      </Box>
                    )}
                  </CardContent>
                </Card>
              </Box>
            </>
          ) : (
            <Box sx={{ flex: '0 0 300px' }}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {text.browserPanelTitle}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {text.browserPanelHint}
                  </Typography>
                  <Stack direction="row" spacing={1} sx={{ mt: 2, flexWrap: 'wrap', gap: 1 }}>
                    <Chip label={text.sseStreaming} color="primary" variant="outlined" />
                    <Chip label={text.sessionIsolation} color="secondary" variant="outlined" />
                    <Chip label={text.liveVnc} color="success" variant="outlined" />
                  </Stack>
                </CardContent>
              </Card>
            </Box>
          )}
        </Stack>
      </Container>
    </Box>
  )
}

export default App
