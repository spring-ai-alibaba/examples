<template>
  <div class="chat-pdf" :class="{ 'dark': isDark }">
    <div class="chat-container">
      <!-- 左侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <a href="#" class="logo-link" @click="handleLogoClick">
            <DocumentTextIcon class="logo" />
            <h1 class="title">ChatPDF</h1>
          </a>
        </div>

        <div class="history-list">
          <div class="history-header">
            <span>历史记录</span>
            <button class="new-chat-btn" @click="startNewChat">
              <PlusIcon class="icon" />
              新聊天
            </button>
          </div>
          <div 
            v-for="chat in chatHistory" 
            :key="chat.id"
            class="history-item"
            :class="{ 'active': currentChatId === chat.id }"
            @click="loadChat(chat.id)"
          >
            <DocumentTextIcon class="icon" />
            <span class="title">{{ chat.title || 'PDF对话' }}</span>
          </div>
        </div>
      </div>
      
      <!-- 主要内容区域 -->
      <div class="chat-main">
        <!-- 未上传文件时显示上传界面 -->
        <div v-if="!currentPdfName" class="upload-welcome">
          <h1 class="main-title">
            与任何 <span class="highlight">PDF</span> 对话
          </h1>
          <div 
            class="drop-zone"
            @dragover.prevent="handleDragOver"
            @dragleave.prevent="handleDragLeave"
            @drop.prevent="handleDrop"
            :class="{ 
              'dragging': isDragging,
              'uploading': isUploading 
            }"
          >
            <div class="upload-content">
              <!-- 添加上传状态显示 -->
              <div v-if="isUploading" class="upload-status">
                <div class="spinner"></div>
                <div class="upload-progress">
                  <p class="status-text">正在上传文件...</p>
                  <p class="filename">{{ uploadingFileName }}</p>
                </div>
              </div>
              <template v-else>
                <DocumentArrowUpIcon class="upload-icon" />
                <p class="upload-text">点击上传，或将PDF拖拽到此处</p>
                <input 
                  type="file"
                  accept=".pdf"
                  @change="handleFileUpload"
                  :disabled="isUploading"
                  class="file-input"
                >
                <button 
                  class="upload-button"
                  :class="{ 'uploading': isUploading }"
                  @click="triggerFileInput"
                >
                  <ArrowUpTrayIcon class="icon" />
                  上传PDF
                </button>
              </template>
            </div>
          </div>
        </div>

        <!-- 已上传文件时显示分栏界面 -->
        <div v-else class="split-view">
          <!-- PDF 预览组件 -->
          <PDFViewer 
            :file="pdfFile"
            :fileName="currentPdfName"
          />

          <!-- 聊天区域 -->
          <div class="chat-view">
            <div class="messages" ref="messagesRef">
              <ChatMessage
                v-for="(message, index) in currentMessages"
                :key="index"
                :message="message"
                :is-stream="isStreaming && index === currentMessages.length - 1"
              />
            </div>
            
            <div class="input-area">
              <textarea
                v-model="userInput"
                @keydown.enter.prevent="sendMessage()"
                placeholder="请输入您的问题..."
                rows="1"
                ref="inputRef"
              ></textarea>
              <button 
                class="send-button" 
                @click="sendMessage()"
                :disabled="isStreaming || !userInput.trim()"
              >
                <PaperAirplaneIcon class="icon" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { useDark } from '@vueuse/core'
import {
  DocumentArrowUpIcon,
  DocumentTextIcon,
  PaperAirplaneIcon,
  ArrowUpTrayIcon,
  PlusIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import { chatAPI } from '../services/api'
import { useRouter } from 'vue-router'
import PDFViewer from '../components/PDFViewer.vue'

const isDark = useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: ''
})
const router = useRouter()
const messagesRef = ref(null)
const inputRef = ref(null)
const userInput = ref('')
const isStreaming = ref(false)
const isUploading = ref(false)
const currentChatId = ref(null)
const currentMessages = ref([])
const chatHistory = ref([])
const currentPdfName = ref('')
const isDragging = ref(false)
const BASE_URL = 'http://localhost:8080'

// 自动调整输入框高度
const adjustTextareaHeight = () => {
  const textarea = inputRef.value
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = textarea.scrollHeight + 'px'
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

// 添加下载状态
const isDownloadingPdf = ref(false)

// 添加 pdfFile ref
const pdfFile = ref(null)

// 修改资源清理函数
const cleanupResources = () => {
  currentPdfName.value = ''
  currentMessages.value = []
  pdfFile.value = null
  currentChatId.value = null
  isDownloadingPdf.value = false
  isUploading.value = false
  uploadingFileName.value = ''
  userInput.value = ''
  isStreaming.value = false
  
  // 重置输入框高度
  if (inputRef.value) {
    inputRef.value.style.height = 'auto'
  }
}

// 修改 logo 点击处理
const handleLogoClick = (event) => {
  event.preventDefault()
  cleanupResources()
  router.push('/')
}

// 修改 startNewChat 方法
const startNewChat = () => {
  try {
    // 清理所有状态
    cleanupResources()
    
    // 重置文件相关状态
    pdfFile.value = null
    currentPdfName.value = ''
    currentChatId.value = null
    
    // 重置消息
    currentMessages.value = []
    
    // 重置上传状态
    isUploading.value = false
    uploadingFileName.value = ''
    
    // 重置输入
    userInput.value = ''
    if (inputRef.value) {
      inputRef.value.style.height = 'auto'
    }
    
    // 重置滚动位置
    if (messagesRef.value) {
      messagesRef.value.scrollTop = 0
    }
  } catch (error) {
    console.error('开始新对话失败:', error)
  }
}

// 修改 loadChat 方法
const loadChat = async (chatId) => {
  if (!chatId) return
  
  cleanupResources()
  currentChatId.value = chatId
  
  try {
    // 加载消息历史
    const messages = await chatAPI.getChatMessages(chatId, 'pdf')
    currentMessages.value = messages.map(msg => ({
      ...msg,
      isMarkdown: msg.role === 'assistant'
    }))

    // 从服务器获取 PDF
    isDownloadingPdf.value = true
    const response = await fetch(`${BASE_URL}/ai/pdf/file/${chatId}`)
    if (!response.ok) throw new Error('获取 PDF 失败')
    
    // 获取文件名
    const contentDisposition = response.headers.get('content-disposition')
    let filename = 'document.pdf'
    if (contentDisposition) {
      const matches = contentDisposition.match(/filename=["']?([^"']+)["']?/)
      if (matches && matches[1]) {
        filename = decodeURIComponent(matches[1])
      }
    }
    
    // 更新当前文件名和历史记录中的标题
    currentPdfName.value = filename
    const chatIndex = chatHistory.value.findIndex(c => c.id === chatId)
    if (chatIndex !== -1) {
      chatHistory.value[chatIndex].title = filename
    }
    
    const blob = await response.blob()
    pdfFile.value = new File([blob], filename, { type: 'application/pdf' })
  } catch (error) {
    console.error('加载失败:', error)
    const errorMessage = {
      role: 'assistant',
      content: '加载失败，请重试。',
      timestamp: new Date(),
      isMarkdown: true
    }
    currentMessages.value.push(errorMessage)
  } finally {
    isDownloadingPdf.value = false
  }
}

// 加载聊天历史
const loadChatHistory = async () => {
  try {
    const history = await chatAPI.getChatHistory('pdf')
    chatHistory.value = history || []
    if (history && history.length > 0) {
      await loadChat(history[0].id)
    }
  } catch (error) {
    console.error('加载聊天历史失败:', error)
    chatHistory.value = []
  }
}

// 处理文件拖放
const handleDrop = async (event) => {
  event.preventDefault()
  isDragging.value = false
  
  const files = event.dataTransfer.files
  if (files.length === 0) return
  
  // 获取第一个文件
  const file = files[0]
  
  // 检查是否为 PDF 文件
  if (file.type !== 'application/pdf') {
    alert('请上传 PDF 文件')
    return
  }
  
  // 设置上传状态和文件名
  isUploading.value = true
  uploadingFileName.value = file.name
  
  try {
    // 创建 FormData
    const formData = new FormData()
    formData.append('file', file)
    
    // 生成临时 chatId 或使用现有的
    const uploadChatId = currentChatId.value || `pdf_${Date.now()}`
    
    // 发送上传请求，修正 API 路径
    const response = await fetch(`${BASE_URL}/ai/pdf/upload/${uploadChatId}`, {
      method: 'POST',
      body: formData
    })
    
    if (!response.ok) {
      throw new Error(`上传失败: ${response.status}`)
    }
    
    const data = await response.json()
    
    // 保存聊天 ID 和文件名
    currentChatId.value = data.chatId || uploadChatId
    currentPdfName.value = file.name
    pdfFile.value = file
    
    // 添加到聊天历史
    const newChat = {
      id: currentChatId.value,
      title: `PDF对话: ${file.name.slice(0, 20)}${file.name.length > 20 ? '...' : ''}`
    }
    
    // 更新聊天历史 - 避免重复添加
    if (!chatHistory.value.some(chat => chat.id === currentChatId.value)) {
      chatHistory.value = [newChat, ...chatHistory.value]
    }
    
    // 清空消息
    currentMessages.value = []
    
    // 添加系统消息
    currentMessages.value.push({
      role: 'assistant',
      content: `已上传 PDF 文件: ${file.name}。您可以开始提问了。`,
      timestamp: new Date(),
      isMarkdown: true
    })
    
  } catch (error) {
    console.error('上传失败:', error)
    alert('文件上传失败，请重试')
  } finally {
    isUploading.value = false
    uploadingFileName.value = ''
  }
}

// 处理拖拽悬停
const handleDragOver = (event) => {
  event.preventDefault()
  isDragging.value = true
}

// 处理拖拽离开
const handleDragLeave = (event) => {
  event.preventDefault()
  isDragging.value = false
}

const triggerFileInput = () => {
  const fileInput = document.querySelector('.file-input')
  fileInput.click()
}

// 添加上传文件名状态（如果还没有的话）
const uploadingFileName = ref('')

// 修改 sendMessage 方法
const sendMessage = async () => {
  if (!userInput.value.trim() || isStreaming.value) return
  
  // 添加用户消息到聊天记录
  const userMessage = {
    role: 'user',
    content: userInput.value,
    timestamp: new Date()
  }
  currentMessages.value.push(userMessage)
  
  // 清空输入框并调整高度
  const input = userInput.value
  userInput.value = ''
  if (inputRef.value) {
    inputRef.value.style.height = 'auto'
  }
  
  // 滚动到底部
  await scrollToBottom()
  
  // 添加一个空的助手消息作为流式响应的容器
  const assistantMessageIndex = currentMessages.value.length
  currentMessages.value.push({
    role: 'assistant',
    content: '',
    timestamp: new Date(),
    isMarkdown: true
  })
  
  try {
    isStreaming.value = true
    
    // 发送请求到服务器
    const reader = await chatAPI.sendPdfMessage(input, currentChatId.value)
    const decoder = new TextDecoder()
    let result = ''
    
    // 处理流式响应
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value, { stream: true })
      console.log('收到流式响应块:', chunk)
      result += chunk
      
      // 使用索引直接替换整个消息对象，强制触发响应式更新
      currentMessages.value[assistantMessageIndex] = {
        role: 'assistant',
        content: result,
        timestamp: new Date(),
        isMarkdown: true
      }
      
      // 确保 DOM 更新并滚动到底部
      await nextTick()
      await scrollToBottom()
    }
    
  } catch (error) {
    console.error('发送消息失败:', error)
    currentMessages.value[assistantMessageIndex] = {
      role: 'assistant',
      content: '发送消息失败，请重试。',
      timestamp: new Date(),
      isMarkdown: true
    }
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

// 同样需要修改文件上传处理函数
const handleFileUpload = async (event) => {
  const files = event.target.files
  if (files.length === 0) return
  
  const file = files[0]
  
  // 检查是否为 PDF 文件
  if (file.type !== 'application/pdf') {
    alert('请上传 PDF 文件')
    return
  }
  
  // 设置上传状态和文件名
  isUploading.value = true
  uploadingFileName.value = file.name
  
  try {
    // 创建 FormData
    const formData = new FormData()
    formData.append('file', file)
    
    // 生成临时 chatId 或使用现有的
    const uploadChatId = currentChatId.value || `pdf_${Date.now()}`
    
    // 发送上传请求，修正 API 路径
    const response = await fetch(`${BASE_URL}/ai/pdf/upload/${uploadChatId}`, {
      method: 'POST',
      body: formData
    })
    
    if (!response.ok) {
      throw new Error(`上传失败: ${response.status}`)
    }
    
    const data = await response.json()
    
    // 保存聊天 ID 和文件名
    currentChatId.value = data.chatId || uploadChatId
    currentPdfName.value = file.name
    pdfFile.value = file
    
    // 添加到聊天历史
    const newChat = {
      id: currentChatId.value,
      title: `PDF对话: ${file.name.slice(0, 20)}${file.name.length > 20 ? '...' : ''}`
    }
    
    // 更新聊天历史 - 避免重复添加
    if (!chatHistory.value.some(chat => chat.id === currentChatId.value)) {
      chatHistory.value = [newChat, ...chatHistory.value]
    }
    
    // 清空消息
    currentMessages.value = []
    
    // 添加系统消息
    currentMessages.value.push({
      role: 'assistant',
      content: `已上传 PDF 文件: ${file.name}。您可以开始提问了。`,
      timestamp: new Date(),
      isMarkdown: true
    })
    
  } catch (error) {
    console.error('上传失败:', error)
    alert('文件上传失败，请重试')
  } finally {
    isUploading.value = false
    uploadingFileName.value = ''
    // 清空文件输入，允许重新选择同一文件
    event.target.value = ''
  }
}

// 监听清理事件
onMounted(() => {
  loadChatHistory()
  adjustTextareaHeight()
})

onUnmounted(() => {
  // 移除事件监听器
  window.removeEventListener('cleanupChatPDF', cleanupResources)
})
</script>

<style scoped lang="scss">
.chat-pdf {
  position: fixed;
  top: 80px;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--bg-color);
  overflow: hidden;

  .chat-container {
    display: flex;
    max-width: 1800px;
    width: 100%;
    height: 100%;
    margin: 0 auto;
    padding: var(--space-6);
    gap: var(--space-6);
    overflow: hidden;
  }

  .sidebar {
    width: 320px;
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-3xl);
    box-shadow: var(--card-shadow);
    backdrop-filter: blur(20px);
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .sidebar-header {
      padding: var(--space-6);
      display: flex;
      align-items: center;
      gap: var(--space-3);
      border-bottom: 1px solid var(--border-color);

      .logo-link {
        display: flex;
        align-items: center;
        gap: var(--space-2);
        text-decoration: none;
        color: inherit;
        transition: var(--transition-all);

        &:hover {
          transform: translateY(-1px);
        }
      }

      .logo {
        width: 32px;
        height: 32px;
        color: #d97706;
      }

      .title {
        font-size: var(--text-xl);
        font-weight: 800;
        background: linear-gradient(120deg, #d97706 0%, #f59e0b 50%, #fbbf24 100%);
        -webkit-background-clip: text;
        background-clip: text;
        -webkit-text-fill-color: transparent;
      }
    }

    .history-list {
      flex: 1;
      overflow-y: auto;
      padding: var(--space-4) 0;

      .history-header {
        padding: var(--space-3) var(--space-6);
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: var(--space-3);

        span {
          font-size: var(--text-xs);
          font-weight: 700;
          color: var(--text-tertiary);
          text-transform: uppercase;
          letter-spacing: 0.5px;

          &::before {
            content: '📚';
            margin-right: var(--space-2);
          }
        }

        .new-chat-btn {
          display: flex;
          align-items: center;
          gap: var(--space-2);
          padding: var(--space-2) var(--space-3);
          border: none;
          border-radius: var(--radius-xl);
          background: linear-gradient(135deg, #d97706, #f59e0b);
          color: white;
          font-size: var(--text-xs);
          font-weight: 600;
          cursor: pointer;
          transition: var(--transition-all);

          &:hover {
            transform: translateY(-1px);
          }

          .icon {
            width: 14px;
            height: 14px;
          }
        }
      }

      .history-item {
        display: flex;
        align-items: center;
        gap: var(--space-3);
        padding: var(--space-3) var(--space-6);
        cursor: pointer;
        transition: var(--transition-all);
        border-radius: var(--radius-xl);
        margin: 0 var(--space-3) var(--space-2) var(--space-3);

        &:hover {
          background: var(--bg-secondary);
          transform: translateX(4px);
        }

        &.active {
          background: linear-gradient(135deg, #d97706, #f59e0b);
          color: white;

          .icon {
            color: white;
          }

          .title {
            color: white;
          }
        }

        .icon {
          width: 20px;
          height: 20px;
          color: #d97706;
        }

        .title {
          flex: 1;
          font-size: var(--text-sm);
          font-weight: 500;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          color: var(--text-color);
        }
      }
    }
  }

  .chat-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-3xl);
    box-shadow: var(--card-shadow);
    overflow: hidden;

    .upload-welcome {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: var(--space-10);
      gap: var(--space-8);

      .main-title {
        font-size: var(--text-4xl);
        font-weight: 800;
        text-align: center;

        .highlight {
          background: linear-gradient(120deg, #d97706 0%, #f59e0b 50%, #fbbf24 100%);
          -webkit-background-clip: text;
          background-clip: text;
          -webkit-text-fill-color: transparent;
        }
      }

      .drop-zone {
        width: 100%;
        max-width: 600px;
        min-height: 400px;
        border: 3px dashed #d97706;
        border-radius: var(--radius-3xl);
        background: rgba(217, 119, 6, 0.05);
        transition: var(--transition-all);
        cursor: pointer;

        &:hover {
          border-color: #f59e0b;
          background: rgba(217, 119, 6, 0.1);
          transform: translateY(-2px);
        }

        &.dragging {
          border-color: #fbbf24;
          background: rgba(217, 119, 6, 0.15);
          transform: scale(1.02);
        }

        &.uploading {
          border-color: #3b82f6;
          background: rgba(59, 130, 246, 0.1);
        }

        .upload-content {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          gap: var(--space-6);
          padding: var(--space-8);
          height: 100%;

          .upload-status {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: var(--space-6);

            .spinner {
              width: 48px;
              height: 48px;
              border: 3px solid rgba(59, 130, 246, 0.2);
              border-left-color: var(--primary);
              border-radius: 50%;
              animation: spin 1s linear infinite;
            }

            .upload-progress {
              text-align: center;

              .status-text {
                font-size: var(--text-lg);
                font-weight: 600;
                color: var(--primary);
                margin-bottom: var(--space-2);
              }

              .filename {
                font-size: var(--text-sm);
                color: var(--text-secondary);
                max-width: 300px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                background: var(--bg-secondary);
                padding: var(--space-2) var(--space-4);
                border-radius: var(--radius-full);
                border: 1px solid var(--border-color);
              }
            }
          }

          .upload-icon {
            width: 64px;
            height: 64px;
            color: #d97706;
            animation: float 3s ease-in-out infinite;
          }

          .upload-text {
            font-size: var(--text-lg);
            font-weight: 500;
            color: var(--text-secondary);
            text-align: center;
            line-height: var(--leading-relaxed);
            margin-bottom: var(--space-2);
          }

          .file-input {
            display: none;
          }

          .upload-button {
            background: linear-gradient(135deg, #d97706, #f59e0b);
            color: white;
            border: none;
            padding: var(--space-3) var(--space-6);
            border-radius: var(--radius-xl);
            font-size: var(--text-base);
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: var(--space-2);
            cursor: pointer;
            transition: var(--transition-all);
            box-shadow: var(--shadow-md);

            &:hover {
              transform: translateY(-2px);
              box-shadow: var(--shadow-2xl);
            }

            &.uploading {
              background: var(--bg-tertiary);
              color: var(--text-tertiary);
              cursor: not-allowed;
              transform: none;
              box-shadow: none;
            }

            .icon {
              width: 20px;
              height: 20px;
            }
          }
        }
      }
    }

    .split-view {
      flex: 1;
      display: flex;
      height: 100%;
      overflow: hidden;

      .chat-view {
        flex: 1;
        min-width: 400px;
        max-width: 50%;
        display: flex;
        flex-direction: column;
        background: var(--card-bg);
        height: 100%;
        overflow: hidden;

        .messages {
          flex: 1;
          overflow-y: auto;
          padding: var(--space-6);
          scroll-behavior: smooth;

          &::-webkit-scrollbar {
            width: 6px;
          }

          &::-webkit-scrollbar-track {
            background: transparent;
          }

          &::-webkit-scrollbar-thumb {
            background: var(--border-color);
            border-radius: 3px;

            &:hover {
              background: var(--text-tertiary);
            }
          }
        }

        .input-area {
          flex-shrink: 0;
          padding: var(--space-6);
          background: var(--card-bg);
          border-top: 1px solid var(--border-color);
          display: flex;
          gap: var(--space-4);
          align-items: flex-end;

          textarea {
            flex: 1;
            resize: none;
            border: 1px solid var(--border-color);
            background: var(--bg-color);
            border-radius: var(--radius-xl);
            padding: var(--space-4);
            color: var(--text-color);
            font-family: inherit;
            font-size: var(--text-base);
            line-height: var(--leading-relaxed);
            min-height: 44px;
            max-height: 150px;
            transition: var(--transition-all);

            &:focus {
              outline: none;
              border-color: #d97706;
              box-shadow: 0 0 0 3px rgba(217, 119, 6, 0.1);
            }

            &:disabled {
              background: var(--bg-tertiary);
              cursor: not-allowed;
              opacity: 0.6;
            }

            &::placeholder {
              color: var(--text-tertiary);
            }
          }

          .send-button {
            background: linear-gradient(135deg, #d97706, #f59e0b);
            color: white;
            border: none;
            border-radius: var(--radius-xl);
            width: 44px;
            height: 44px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: var(--transition-all);
            box-shadow: var(--shadow-sm);
            flex-shrink: 0;

            &:hover:not(:disabled) {
              transform: translateY(-1px);
              box-shadow: var(--shadow-md);
            }

            &:disabled {
              background: var(--bg-tertiary);
              cursor: not-allowed;
              opacity: 0.6;
              transform: none;
              box-shadow: none;
            }

            .icon {
              width: 20px;
              height: 20px;
            }
          }
        }
      }
    }
  }
}

/* 动画定义 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .chat-pdf {
    .chat-container {
      padding: var(--space-4);
      gap: var(--space-4);
    }

    .sidebar {
      width: 280px;
    }

    .split-view {
      flex-direction: column;

      .chat-view {
        width: 100%;
        min-width: 0;
        max-width: none;
        height: 100%;

        .messages {
          padding: var(--space-4);
        }

        .input-area {
          padding: var(--space-4);

          textarea {
            padding: var(--space-3);
            font-size: var(--text-sm);
          }

          .send-button {
            width: 40px;
            height: 40px;

            .icon {
              width: 18px;
              height: 18px;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .chat-pdf {
    top: 70px;

    .chat-container {
      padding: var(--space-3);
      flex-direction: column;
      gap: var(--space-3);
    }

    .sidebar {
      width: 100%;
      height: auto;
      max-height: 200px;
      order: 2;
      border-radius: var(--radius-2xl);
      margin-top: var(--space-3);

      .sidebar-header {
        padding: var(--space-4);

        .title {
          font-size: var(--text-lg);
        }
      }

      .history-list {
        padding: var(--space-3) 0;

        .history-header {
          padding: var(--space-2) var(--space-4);

          span {
            font-size: var(--text-xs);
          }

          .new-chat-btn {
            padding: var(--space-1) var(--space-2);
            font-size: var(--text-xs);
          }
        }

        .history-item {
          margin: 0 var(--space-2) var(--space-1) var(--space-2);
          padding: var(--space-2) var(--space-3);

          .icon {
            width: 16px;
            height: 16px;
          }

          .title {
            font-size: var(--text-xs);
          }
        }
      }
    }

    .chat-main {
      flex: 1;
      order: 1;
      border-radius: var(--radius-2xl);
    }

    .split-view {
      .chat-view {
        .input-area {
          padding: var(--space-3);
          gap: var(--space-3);

          .send-button {
            width: 36px;
            height: 36px;

            .icon {
              width: 16px;
              height: 16px;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .chat-pdf {
    top: 60px;

    .chat-container {
      padding: var(--space-2);
    }

    .sidebar {
      height: 160px;

      .sidebar-header {
        padding: var(--space-3);

        .logo {
          width: 24px;
          height: 24px;
        }

        .title {
          font-size: var(--text-base);
        }
      }
    }
  }
}

/* 深色模式样式 */
html.dark {
  .chat-pdf {
    .split-view {
      .chat-view {
        background: var(--card-bg);

        .messages {
          &::-webkit-scrollbar-thumb {
            background: var(--border-color);

            &:hover {
              background: var(--text-tertiary);
            }
          }
        }

        .input-area {
          background: var(--card-bg);
          border-top-color: var(--border-color);

          textarea {
            background: var(--bg-color);
            border-color: var(--border-color);
            color: var(--text-color);

            &:focus {
              border-color: #f59e0b;
              box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
            }

            &::placeholder {
              color: var(--text-tertiary);
            }
          }

          .send-button {
            background: linear-gradient(135deg, #f59e0b, #fbbf24);

            &:hover:not(:disabled) {
              box-shadow: 0 8px 25px rgba(245, 158, 11, 0.3);
            }
          }
        }
      }
    }
  }
}
</style>
