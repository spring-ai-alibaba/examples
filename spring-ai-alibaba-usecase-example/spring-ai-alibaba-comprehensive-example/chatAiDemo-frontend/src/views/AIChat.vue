<template>
  <div class="ai-chat" :class="{ 'dark': isDark }">
    <div class="chat-container">
      <div class="sidebar">
        <div class="history-header">
          <h2>聊天记录</h2>
          <button class="new-chat" @click="startNewChat">
            <PlusIcon class="icon" />
            新对话
          </button>
        </div>
        <div class="history-list">
          <div 
            v-for="chat in chatHistory" 
            :key="chat.id"
            class="history-item"
            :class="{ 'active': currentChatId === chat.id }"
            @click="loadChat(chat.id)"
          >
            <ChatBubbleLeftRightIcon class="icon" />
            <span class="title">{{ chat.title || '新对话' }}</span>
          </div>
        </div>
      </div>
      
      <div class="chat-main">
        <div class="messages" ref="messagesRef">
          <ChatMessage
            v-for="(message, index) in currentMessages"
            :key="index"
            :message="message"
            :is-stream="isStreaming && index === currentMessages.length - 1"
          />
        </div>
        
        <div class="input-area">
          <div v-if="selectedFiles.length > 0" class="selected-files">
            <div v-for="(file, index) in selectedFiles" :key="index" class="file-item">
              <div class="file-info">
                <DocumentIcon class="icon" />
                <span class="file-name">{{ file.name }}</span>
                <span class="file-size">({{ formatFileSize(file.size) }})</span>
              </div>
              <button class="remove-btn" @click="removeFile(index)">
                <XMarkIcon class="icon" />
              </button>
            </div>
          </div>

          <div class="input-row">
            <div class="file-upload">
              <input 
                type="file" 
                ref="fileInput"
                @change="handleFileUpload"
                accept="image/*,audio/*,video/*"
                multiple
                class="hidden"
              >
              <button 
                class="upload-btn"
                @click="triggerFileInput"
                :disabled="isStreaming"
              >
                <PaperClipIcon class="icon" />
              </button>
            </div>

            <textarea
              v-model="userInput"
              @keydown.enter.prevent="sendMessage"
              :placeholder="getPlaceholder()"
              rows="1"
              ref="inputRef"
            ></textarea>
            <button 
              class="send-button" 
              @click="sendMessage"
              :disabled="isStreaming || (!userInput.trim() && !selectedFiles.length)"
            >
              <PaperAirplaneIcon class="icon" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useDark } from '@vueuse/core'
import { 
  ChatBubbleLeftRightIcon, 
  PaperAirplaneIcon,
  PlusIcon,
  PaperClipIcon,
  DocumentIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import { chatAPI } from '../services/api'

const isDark = useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: ''
})
const messagesRef = ref(null)
const inputRef = ref(null)
const userInput = ref('')
const isStreaming = ref(false)
const currentChatId = ref(null)
const currentMessages = ref([])
const chatHistory = ref([])
const fileInput = ref(null)
const selectedFiles = ref([])

// 自动调整输入框高度
const adjustTextareaHeight = () => {
  const textarea = inputRef.value
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = textarea.scrollHeight + 'px'
  }else{
    textarea.style.height = '50px'
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

// 文件类型限制
const FILE_LIMITS = {
  image: { 
    maxSize: 10 * 1024 * 1024,  // 单个文件 10MB
    maxFiles: 3,                 // 最多 3 个文件
    description: '图片文件'
  },
  audio: { 
    maxSize: 10 * 1024 * 1024,  // 单个文件 10MB
    maxDuration: 180,           // 3分钟
    maxFiles: 3,                // 最多 3 个文件
    description: '音频文件'
  },
  video: { 
    maxSize: 150 * 1024 * 1024, // 单个文件 150MB
    maxDuration: 40,            // 40秒
    maxFiles: 3,                // 最多 3 个文件
    description: '视频文件'
  }
}

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 检查文件是否符合要求
const validateFile = async (file) => {
  const type = file.type.split('/')[0]
  const limit = FILE_LIMITS[type]
  
  if (!limit) {
    return { valid: false, error: '不支持的文件类型' }
  }
  
  if (file.size > limit.maxSize) {
    return { valid: false, error: `文件大小不能超过${limit.maxSize / 1024 / 1024}MB` }
  }
  
  if ((type === 'audio' || type === 'video') && limit.maxDuration) {
    try {
      const duration = await getMediaDuration(file)
      if (duration > limit.maxDuration) {
        return { 
          valid: false, 
          error: `${type === 'audio' ? '音频' : '视频'}时长不能超过${limit.maxDuration}秒`
        }
      }
    } catch (error) {
      return { valid: false, error: '无法读取媒体文件时长' }
    }
  }
  
  return { valid: true }
}

// 获取媒体文件时长
const getMediaDuration = (file) => {
  return new Promise((resolve, reject) => {
    const element = file.type.startsWith('audio/') ? new Audio() : document.createElement('video')
    element.preload = 'metadata'
    
    element.onloadedmetadata = () => {
      resolve(element.duration)
      URL.revokeObjectURL(element.src)
    }
    
    element.onerror = () => {
      reject(new Error('无法读取媒体文件'))
      URL.revokeObjectURL(element.src)
    }
    
    element.src = URL.createObjectURL(file)
  })
}

// 修改文件上传处理函数
const handleFileUpload = async (event) => {
  const files = Array.from(event.target.files || [])
  if (!files.length) return
  
  // 检查所有文件类型是否一致
  const firstFileType = files[0].type.split('/')[0]
  const hasInconsistentType = files.some(file => file.type.split('/')[0] !== firstFileType)
  
  if (hasInconsistentType) {
    alert('请选择相同类型的文件（图片、音频或视频）')
    event.target.value = ''
    return
  }

  // 验证所有文件
  for (const file of files) {
    const { valid, error } = await validateFile(file)
    if (!valid) {
      alert(error)
      event.target.value = ''
      selectedFiles.value = []
      return
    }
  }

  // 检查文件总大小
  const totalSize = files.reduce((sum, file) => sum + file.size, 0)
  const limit = FILE_LIMITS[firstFileType]
  if (totalSize > limit.maxSize * 3) { // 允许最多3个文件的总大小
    alert(`${firstFileType === 'image' ? '图片' : firstFileType === 'audio' ? '音频' : '视频'}文件总大小不能超过${(limit.maxSize * 3) / 1024 / 1024}MB`)
    event.target.value = ''
    selectedFiles.value = []
    return
  }

  selectedFiles.value = files
}

// 修改文件输入提示
const getPlaceholder = () => {
  if (selectedFiles.value.length > 0) {
    const type = selectedFiles.value[0].type.split('/')[0]
    const desc = FILE_LIMITS[type].description
    return `已选择 ${selectedFiles.value.length} 个${desc}，可继续输入消息...`
  }
  return '输入消息，可上传图片、音频或视频...'
}

// 修改发送消息函数
const sendMessage = async () => {
  if (isStreaming.value) return
  if (!userInput.value.trim() && !selectedFiles.value.length) return
  
  const messageContent = userInput.value.trim()
  
  // 添加用户消息
  const userMessage = {
    role: 'user',
    content: messageContent,
    timestamp: new Date()
  }
  currentMessages.value.push(userMessage)
  
  // 清空输入
  userInput.value = ''
  adjustTextareaHeight()
  await scrollToBottom()
  
  // 准备发送数据
  const formData = new FormData()
  if (messageContent) {
    formData.append('prompt', messageContent)
  }
  selectedFiles.value.forEach(file => {
    formData.append('files', file)
  })
  
  // 添加助手消息占位
  const assistantMessage = {
    role: 'assistant',
    content: '',
    timestamp: new Date()
  }
  currentMessages.value.push(assistantMessage)
  isStreaming.value = true
  
  try {
    const reader = await chatAPI.sendMessage(formData, currentChatId.value)
    const decoder = new TextDecoder('utf-8')
    let accumulatedContent = ''  // 添加累积内容变量
    
    while (true) {
      try {
        const { value, done } = await reader.read()
        if (done) break
        
        // 累积新内容
        accumulatedContent += decoder.decode(value)  // 追加新内容
        
        await nextTick(() => {
          // 更新消息，使用累积的内容
          const updatedMessage = {
            ...assistantMessage,
            content: accumulatedContent  // 使用累积的内容
          }
          const lastIndex = currentMessages.value.length - 1
          currentMessages.value.splice(lastIndex, 1, updatedMessage)
        })
        await scrollToBottom()
      } catch (readError) {
        console.error('读取流错误:', readError)
        break
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    assistantMessage.content = '抱歉，发生了错误，请稍后重试。'
  } finally {
    isStreaming.value = false
    selectedFiles.value = [] // 清空已选文件
    fileInput.value.value = '' // 清空文件输入
    await scrollToBottom()
  }
}

// 加载特定对话
const loadChat = async (chatId) => {
  currentChatId.value = chatId
  try {
    const messages = await chatAPI.getChatMessages(chatId, 'chat')
    currentMessages.value = messages
  } catch (error) {
    console.error('加载对话消息失败:', error)
    currentMessages.value = []
  }
}

// 加载聊天历史
const loadChatHistory = async () => {
  try {
    const history = await chatAPI.getChatHistory('chat')
    chatHistory.value = history || []
    if (history && history.length > 0) {
      await loadChat(history[0].id)
    } else {
      startNewChat()
    }
  } catch (error) {
    console.error('加载聊天历史失败:', error)
    chatHistory.value = []
    startNewChat()
  }
}

// 开始新对话
const startNewChat = () => {
  const newChatId = Date.now().toString()
  currentChatId.value = newChatId
  currentMessages.value = []
  
  // 添加新对话到聊天历史列表
  const newChat = {
    id: newChatId,
    title: `对话 ${newChatId.slice(-6)}`
  }
  chatHistory.value = [newChat, ...chatHistory.value] // 将新对话添加到列表开头
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

// 移除文件
const removeFile = (index) => {
  selectedFiles.value = selectedFiles.value.filter((_, i) => i !== index)
  if (selectedFiles.value.length === 0) {
    fileInput.value.value = ''  // 清空文件输入
  }
}

onMounted(() => {
  loadChatHistory()
  adjustTextareaHeight()
})
</script>

<style scoped lang="scss">
.ai-chat {
  position: fixed;
  top: 80px; // 调整导航栏高度
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--bg-color);
  overflow: hidden;

  .chat-container {
    display: flex;
    max-width: 1600px;
    width: 100%;
    height: 100%;
    margin: 0 auto;
    padding: var(--space-6);
    gap: var(--space-6);
    overflow: hidden;
  }

  .sidebar {
    width: 320px;
    display: flex;
    flex-direction: column;
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-3xl);
    box-shadow: var(--card-shadow);
    backdrop-filter: blur(20px);
    overflow: hidden;

    .history-header {
      flex-shrink: 0;
      padding: var(--space-6);
      border-bottom: 1px solid var(--border-color);
      background: var(--glass-bg);

      h2 {
        font-size: var(--text-xl);
        font-weight: 700;
        color: var(--text-color);
        margin-bottom: var(--space-4);
      }

      .new-chat {
        display: flex;
        align-items: center;
        gap: var(--space-2);
        width: 100%;
        padding: var(--space-3) var(--space-4);
        border-radius: var(--radius-xl);
        background: var(--gradient-primary);
        color: white;
        border: none;
        cursor: pointer;
        font-weight: 600;
        font-size: var(--text-sm);
        transition: var(--transition-all);
        box-shadow: var(--shadow-md);

        &:hover {
          transform: translateY(-1px);
          box-shadow: var(--shadow-lg);
        }

        .icon {
          width: 18px;
          height: 18px;
        }
      }
    }

    .history-list {
      flex: 1;
      overflow-y: auto;
      padding: var(--space-4);

      .history-item {
        display: flex;
        align-items: center;
        gap: var(--space-3);
        padding: var(--space-3) var(--space-4);
        border-radius: var(--radius-xl);
        cursor: pointer;
        transition: var(--transition-all);
        margin-bottom: var(--space-2);
        border: 1px solid transparent;

        &:hover {
          background: var(--bg-secondary);
          border-color: var(--border-hover);
          transform: translateX(4px);
        }

        &.active {
          background: var(--primary);
          color: white;
          box-shadow: var(--shadow-glow);

          .icon {
            color: white;
          }
        }

        .icon {
          width: 20px;
          height: 20px;
          color: var(--primary);
          transition: var(--transition-all);
        }

        .title {
          flex: 1;
          font-size: var(--text-sm);
          font-weight: 500;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
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
    backdrop-filter: blur(20px);
    overflow: hidden;

    .messages {
      flex: 1;
      overflow-y: auto;
      padding: var(--space-8);

      /* 自定义滚动条 */
      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-track {
        background: transparent;
      }

      &::-webkit-scrollbar-thumb {
        background: var(--border-color);
        border-radius: var(--radius-full);

        &:hover {
          background: var(--border-hover);
        }
      }
    }

    .input-area {
      flex-shrink: 0;
      padding: var(--space-6);
      background: var(--glass-bg);
      border-top: 1px solid var(--border-color);
      backdrop-filter: blur(10px);

      .selected-files {
        background: var(--bg-secondary);
        border-radius: var(--radius-2xl);
        padding: var(--space-4);
        margin-bottom: var(--space-4);
        border: 1px solid var(--border-color);

        .file-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: var(--space-3) var(--space-4);
          background: var(--card-bg);
          border-radius: var(--radius-xl);
          margin-bottom: var(--space-3);
          border: 1px solid var(--border-color);
          transition: var(--transition-all);

          &:last-child {
            margin-bottom: 0;
          }

          &:hover {
            background: var(--bg-tertiary);
            border-color: var(--primary);
            transform: translateY(-1px);
          }

          .file-info {
            display: flex;
            align-items: center;
            gap: var(--space-3);

            .icon {
              width: 24px;
              height: 24px;
              color: var(--primary);
            }

            .file-name {
              font-size: var(--text-sm);
              color: var(--text-color);
              font-weight: 600;
            }

            .file-size {
              font-size: var(--text-xs);
              color: var(--text-tertiary);
              background: var(--bg-tertiary);
              padding: var(--space-1) var(--space-2);
              border-radius: var(--radius-full);
              margin-left: var(--space-2);
            }
          }

          .remove-btn {
            width: 32px;
            height: 32px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: none;
            background: var(--bg-tertiary);
            color: var(--text-tertiary);
            cursor: pointer;
            border-radius: var(--radius-lg);
            transition: var(--transition-all);

            &:hover {
              background: var(--error-500);
              color: white;
              transform: scale(1.1);
            }

            .icon {
              width: 16px;
              height: 16px;
            }
          }
        }
      }

      .input-row {
        display: flex;
        gap: var(--space-3);
        align-items: flex-end;
        background: var(--card-bg);
        padding: var(--space-4);
        border-radius: var(--radius-2xl);
        border: 2px solid var(--border-color);
        box-shadow: var(--shadow-lg);
        transition: var(--transition-all);

        &:focus-within {
          border-color: var(--primary);
          box-shadow: var(--shadow-glow);
        }

        .file-upload {
          .hidden {
            display: none;
          }

          .upload-btn {
            width: 48px;
            height: 48px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: none;
            border-radius: var(--radius-xl);
            background: var(--bg-secondary);
            color: var(--primary);
            cursor: pointer;
            transition: var(--transition-all);
            position: relative;
            overflow: hidden;

            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              background: var(--gradient-primary);
              opacity: 0;
              transition: var(--transition-all);
            }

            &:hover:not(:disabled) {
              transform: translateY(-2px);
              box-shadow: var(--shadow-md);

              &::before {
                opacity: 1;
              }

              .icon {
                color: white;
                z-index: 1;
                position: relative;
              }
            }

            &:disabled {
              opacity: 0.5;
              cursor: not-allowed;
            }

            .icon {
              width: 20px;
              height: 20px;
              transition: var(--transition-all);
            }
          }
        }

        textarea {
          flex: 1;
          resize: none;
          border: none;
          background: transparent;
          padding: var(--space-3) var(--space-4);
          color: var(--text-color);
          font-family: var(--font-family-sans);
          font-size: var(--text-base);
          line-height: var(--leading-relaxed);
          max-height: 120px;
          min-height: 48px;

          &:focus {
            outline: none;
          }

          &::placeholder {
            color: var(--text-tertiary);
          }
        }

        .send-button {
          width: 48px;
          height: 48px;
          display: flex;
          align-items: center;
          justify-content: center;
          border: none;
          border-radius: var(--radius-xl);
          background: var(--gradient-primary);
          color: white;
          cursor: pointer;
          transition: var(--transition-all);
          box-shadow: var(--shadow-md);
          position: relative;
          overflow: hidden;

          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.2) 50%, transparent 70%);
            transform: translateX(-100%);
            transition: transform 0.6s;
          }

          &:hover:not(:disabled) {
            transform: translateY(-2px) scale(1.05);
            box-shadow: var(--shadow-glow);

            &::before {
              transform: translateX(100%);
            }
          }

          &:disabled {
            background: var(--bg-tertiary);
            color: var(--text-tertiary);
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
          }

          .icon {
            width: 20px;
            height: 20px;
            transition: var(--transition-all);
          }
        }
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .ai-chat .chat-container {
    padding: var(--space-4);
    gap: var(--space-4);
  }

  .sidebar {
    width: 280px;
  }
}

@media (max-width: 768px) {
  .ai-chat {
    top: 70px; // 移动端导航栏高度调整

    .chat-container {
      padding: var(--space-3);
      flex-direction: column;
      gap: 0;
    }

    .sidebar {
      width: 100%;
      height: 200px;
      order: 2;
      border-radius: var(--radius-2xl) var(--radius-2xl) 0 0;
      margin-top: var(--space-3);

      .history-header {
        padding: var(--space-4);

        h2 {
          font-size: var(--text-lg);
          margin-bottom: var(--space-3);
        }

        .new-chat {
          padding: var(--space-2) var(--space-3);
          font-size: var(--text-xs);
        }
      }

      .history-list {
        padding: var(--space-3);

        .history-item {
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

      .messages {
        padding: var(--space-4);
      }

      .input-area {
        padding: var(--space-4);

        .input-row {
          padding: var(--space-3);
          gap: var(--space-2);

          .file-upload .upload-btn,
          .send-button {
            width: 40px;
            height: 40px;

            .icon {
              width: 18px;
              height: 18px;
            }
          }

          textarea {
            padding: var(--space-2) var(--space-3);
            font-size: var(--text-sm);
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .ai-chat {
    top: 60px;

    .chat-container {
      padding: var(--space-2);
    }

    .sidebar {
      height: 160px;

      .history-header {
        padding: var(--space-3);

        .new-chat {
          padding: var(--space-1) var(--space-2);

          .icon {
            width: 14px;
            height: 14px;
          }
        }
      }
    }

    .chat-main {
      .messages {
        padding: var(--space-3);
      }

      .input-area {
        padding: var(--space-3);

        .input-row {
          .file-upload .upload-btn,
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

/* 动画增强 */
.history-item {
  animation: slideInLeft 0.3s ease-out;
}

.file-item {
  animation: slideInUp 0.3s ease-out;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 加载状态 */
.loading-message {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4);
  color: var(--text-secondary);
  font-style: italic;

  .loading-dots {
    display: flex;
    gap: var(--space-1);

    .dot {
      width: 4px;
      height: 4px;
      background: var(--primary);
      border-radius: 50%;
      animation: pulse 1.4s ease-in-out infinite both;

      &:nth-child(1) { animation-delay: -0.32s; }
      &:nth-child(2) { animation-delay: -0.16s; }
      &:nth-child(3) { animation-delay: 0s; }
    }
  }
}
</style> 
