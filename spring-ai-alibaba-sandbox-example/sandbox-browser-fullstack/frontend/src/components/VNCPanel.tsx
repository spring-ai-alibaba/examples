import { MouseEvent, useEffect, useMemo, useRef, useState } from 'react'
import { Box, Chip, Divider, Link, Stack, Typography } from '@mui/material'
import OpenInNewIcon from '@mui/icons-material/OpenInNew'
import DragIndicatorIcon from '@mui/icons-material/DragIndicator'
import { BrowserInfo } from '../types/chat'
import { copy, Language } from '../i18n'

interface Props {
  browserInfo: BrowserInfo
  language: Language
  compact?: boolean
}

function VNCPanel({ browserInfo, language, compact = false }: Props) {
  const text = useMemo(() => copy[language], [language])
  const previewWrapRef = useRef<HTMLDivElement>(null)
  const previewHostRef = useRef<HTMLDivElement>(null)
  const [previewWidth, setPreviewWidth] = useState(compact ? 280 : 420)
  const [isResizing, setIsResizing] = useState(false)
  const REMOTE_WIDTH = 1366
  const REMOTE_HEIGHT = 768
  const BASE_RATIO = REMOTE_WIDTH / REMOTE_HEIGHT
  const MIN_WIDTH = compact ? 220 : 320

  useEffect(() => {
    const element = previewHostRef.current
    if (!element) {
      return
    }

    const getWidthBounds = () => {
      const widthLimit = element.clientWidth - 8
      const heightLimit = (element.clientHeight - 8) * BASE_RATIO
      const maxWidth = Math.max(80, Math.min(widthLimit, heightLimit))
      const minWidth = Math.min(MIN_WIDTH, maxWidth)
      return { minWidth, maxWidth }
    }

    const clampWidthToContainer = () => {
      const { minWidth, maxWidth } = getWidthBounds()
      setPreviewWidth(prev => Math.min(Math.max(prev, minWidth), maxWidth))
    }

    clampWidthToContainer()
    const observer = new ResizeObserver(clampWidthToContainer)
    observer.observe(element)
    return () => observer.disconnect()
  }, [MIN_WIDTH, BASE_RATIO])

  const handleResizeStart = (event: MouseEvent) => {
    event.preventDefault()
    setIsResizing(true)
    const startY = event.clientY
    const startWidth = previewWidth

    const onMouseMove = (moveEvent: globalThis.MouseEvent) => {
      const wrap = previewHostRef.current
      if (!wrap) {
        return
      }
      // Drag down to enlarge, drag up to shrink. Keep the frame in 16:9-ish ratio.
      const deltaY = moveEvent.clientY - startY
      const widthFromHeightDelta = deltaY * BASE_RATIO
      const widthLimit = wrap.clientWidth - 8
      const heightLimit = (wrap.clientHeight - 8) * BASE_RATIO
      const maxWidth = Math.max(80, Math.min(widthLimit, heightLimit))
      const minWidth = Math.min(MIN_WIDTH, maxWidth)
      const nextWidth = Math.min(Math.max(startWidth + widthFromHeightDelta, minWidth), maxWidth)
      setPreviewWidth(nextWidth)
    }

    const onMouseUp = () => {
      setIsResizing(false)
      window.removeEventListener('mousemove', onMouseMove)
      window.removeEventListener('mouseup', onMouseUp)
    }

    window.addEventListener('mousemove', onMouseMove)
    window.addEventListener('mouseup', onMouseUp)
  }

  const previewHeight = Math.round(previewWidth / BASE_RATIO)
  const scaledDesktopUrl = useMemo(() => {
    try {
      const url = new URL(browserInfo.desktopUrl)
      url.searchParams.set('resize', 'scale')
      url.searchParams.set('scale', '1')
      return url.toString()
    } catch {
      const hasQuery = browserInfo.desktopUrl.includes('?')
      let url = browserInfo.desktopUrl
      url = url.replace(/([?&])resize=[^&#]*/g, '$1').replace(/([?&])scale=[^&#]*/g, '$1')
      url = url.replace(/[?&]$/, '')
      url = `${url}${hasQuery ? '&' : '?'}resize=scale&scale=1`
      return url
    }
  }, [browserInfo.desktopUrl])

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: compact ? { xs: 320, xl: 360 } : { xs: 520, xl: 640 } }}>
      <Box sx={{ px: compact ? 2 : 3, py: compact ? 1.2 : 2 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center" gap={1}>
          <Box>
            <Typography variant={compact ? 'subtitle1' : 'h6'}>{text.vncTitle}</Typography>
            <Typography variant={compact ? 'caption' : 'body2'} color="text.secondary">
              {text.vncSubtitle}
            </Typography>
          </Box>
          <Chip color="success" label={browserInfo.status || 'active'} size="small" />
        </Stack>
      </Box>
      <Divider />
      {!compact ? (
        <>
          <Box sx={{ px: 3, py: 1.2 }}>
            <Typography variant="caption" color="text.secondary">
              {text.previewResizeHint}
            </Typography>
            <Box>
              <Link href={browserInfo.desktopUrl} target="_blank" rel="noreferrer" sx={{ display: 'inline-flex', mt: 0.8 }}>
                {text.openNewTab} <OpenInNewIcon sx={{ fontSize: 16, ml: 0.4 }} />
              </Link>
            </Box>
          </Box>
          <Divider />
        </>
      ) : null}
      <Box
        ref={previewWrapRef}
        sx={{
          flex: 1,
          py: 1.2,
          px: 1.2,
          bgcolor: '#111827',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          overflow: 'hidden'
        }}
      >
        <Box
          ref={previewHostRef}
          sx={{
            width: '100%',
            flex: 1,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            overflow: 'hidden'
          }}
        >
        <Box
          sx={{
            width: `${previewWidth}px`,
            height: `${previewHeight}px`,
            border: '1px solid rgba(255,255,255,0.18)',
            borderRadius: 1,
            overflow: 'hidden',
            position: 'relative',
            bgcolor: '#0f172a'
          }}
        >
          <Box
            sx={{
              position: 'absolute',
              inset: 0,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              bgcolor: '#0f172a'
            }}
          >
            <iframe
              src={scaledDesktopUrl}
              style={{
                width: '100%',
                height: '100%',
                border: 0,
                pointerEvents: isResizing ? 'none' : 'auto'
              }}
              title="Sidebar Browser Preview"
            />
          </Box>
        </Box>
        </Box>
        <Box
          onMouseDown={handleResizeStart}
          sx={{
            mt: 0.8,
            mb: 0.2,
            width: 52,
            height: 22,
            borderRadius: 999,
            border: '1px solid rgba(255,255,255,0.22)',
            bgcolor: 'rgba(255,255,255,0.08)',
            color: 'rgba(255,255,255,0.8)',
            cursor: 'ns-resize',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}
        >
          <DragIndicatorIcon fontSize="small" />
        </Box>
      </Box>
    </Box>
  )
}

export default VNCPanel
