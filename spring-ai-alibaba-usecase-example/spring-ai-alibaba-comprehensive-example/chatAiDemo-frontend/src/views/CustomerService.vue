<template>
  <div class="customer-service" :class="{ 'dark': isDark }">
    <div class="chat-container">
      <div class="sidebar">
        <div class="history-header">
          <h2>咨询记录</h2>
          <button class="new-chat" @click="startNewChat">
            <PlusIcon class="icon" />
            新咨询
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
            <span class="title">{{ chat.title || '新咨询' }}</span>
          </div>
        </div>
      </div>
      
      <div class="chat-main">
        <div class="service-header">
          <div class="service-info">
            <ComputerDesktopIcon class="avatar" />
            <div class="info">
              <h3>小青</h3>
              <p>程序员智能客服</p>
            </div>
          </div>
        </div>

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

    <!-- 预约成功弹窗 -->
    <div v-if="showBookingModal" class="booking-modal">
      <div class="modal-content">
        <h3>预约成功！</h3>
        <div class="booking-info" v-html="bookingInfo"></div>
        <button @click="showBookingModal = false">确定</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useDark } from '@vueuse/core'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { 
  ChatBubbleLeftRightIcon, 
  PaperAirplaneIcon,
  PlusIcon,
  ComputerDesktopIcon
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
const showBookingModal = ref(false)
const bookingInfo = ref('')

// 配置 marked
marked.setOptions({
  breaks: true,  // 支持换行
  gfm: true,     // 支持 GitHub Flavored Markdown
  sanitize: false // 允许 HTML
})

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

// 发送消息
const sendMessage = async (content) => {
  if (isStreaming.value || (!content && !userInput.value.trim())) return
  
  // 使用传入的 content 或用户输入框的内容
  const messageContent = content || userInput.value.trim()
  
  // 添加用户消息
  const userMessage = {
    role: 'user',
    content: messageContent,
    timestamp: new Date()
  }
  currentMessages.value.push(userMessage)
  
  // 清空输入
  if (!content) {  // 只有在非传入内容时才清空输入框
    userInput.value = ''
    adjustTextareaHeight()
  }
  await scrollToBottom()
  
  // 添加助手消息占位
  const assistantMessage = {
    role: 'assistant',
    content: '',
    timestamp: new Date(),
    isMarkdown: true  // 添加标记表示这是 Markdown 内容
  }
  currentMessages.value.push(assistantMessage)
  isStreaming.value = true
  
  let accumulatedContent = ''
  
  try {
    const reader = await chatAPI.sendServiceMessage(messageContent, currentChatId.value)
    const decoder = new TextDecoder('utf-8')
    
    while (true) {
      try {
        const { value, done } = await reader.read()
        if (done) break
        
        // 累积新内容
        accumulatedContent += decoder.decode(value)
        
        await nextTick(() => {
          // 更新消息
          const updatedMessage = {
            ...assistantMessage,
            content: accumulatedContent,
            isMarkdown: true  // 保持 Markdown 标记
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

    // 检查是否包含预约信息
    if (accumulatedContent.includes('预约编号')) {
      const bookingMatch = accumulatedContent.match(/【(.*?)】/s)
      if (bookingMatch) {
        // 使用 marked 处理预约信息中的 Markdown
        bookingInfo.value = DOMPurify.sanitize(
          marked.parse(bookingMatch[1]),
          {
            ADD_TAGS: ['code', 'pre', 'span'],
            ADD_ATTR: ['class', 'language']
          }
        )
        showBookingModal.value = true
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    assistantMessage.content = '抱歉，发生了错误，请稍后重试。'
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

// 加载特定对话
const loadChat = async (chatId) => {
  currentChatId.value = chatId
  try {
    const messages = await chatAPI.getChatMessages(chatId, 'service')
    currentMessages.value = messages.map(msg => ({
      ...msg,
      isMarkdown: msg.role === 'assistant'  // 为助手消息添加 Markdown 标记
    }))
  } catch (error) {
    console.error('加载对话消息失败:', error)
    currentMessages.value = []
  }
}

// 加载聊天历史
const loadChatHistory = async () => {
  try {
    const history = await chatAPI.getChatHistory('service')
    chatHistory.value = history || []
    if (history && history.length > 0) {
      await loadChat(history[0].id)
    } else {
      await startNewChat()  // 等待 startNewChat 完成
    }
  } catch (error) {
    console.error('加载聊天历史失败:', error)
    chatHistory.value = []
    await startNewChat()  // 等待 startNewChat 完成
  }
}

// 开始新对话
const startNewChat = async () => {  // 添加 async
  const newChatId = Date.now().toString()
  currentChatId.value = newChatId
  currentMessages.value = []
  
  // 添加新对话到历史列表
  const newChat = {
    id: newChatId,
    title: `咨询 ${newChatId.slice(-6)}`
  }
  chatHistory.value = [newChat, ...chatHistory.value]

  // 发送初始问候语
  await sendMessage('你好')
}

onMounted(() => {
  loadChatHistory()
  adjustTextareaHeight()
})
</script>

<style scoped lang="scss">
.customer-service {
  position: fixed;
  top: 80px;
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
    position: relative;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(
        135deg,
        rgba(34, 197, 94, 0.05) 0%,
        rgba(16, 185, 129, 0.05) 50%,
        rgba(5, 150, 105, 0.05) 100%
      );
      pointer-events: none;
    }

    .history-header {
      flex-shrink: 0;
      padding: var(--space-6);
      border-bottom: 1px solid var(--border-color);
      background: var(--glass-bg);
      position: relative;
      z-index: 1;

      h2 {
        font-size: var(--text-xl);
        font-weight: 700;
        color: var(--text-color);
        margin-bottom: var(--space-4);
        display: flex;
        align-items: center;
        gap: var(--space-2);

        &::before {
          content: '🎧';
          font-size: var(--text-lg);
        }
      }

      .new-chat {
        display: flex;
        align-items: center;
        gap: var(--space-2);
        width: 100%;
        padding: var(--space-3) var(--space-4);
        border-radius: var(--radius-xl);
        background: linear-gradient(135deg, var(--success-500), var(--success-600));
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
      position: relative;
      z-index: 1;

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
          background: var(--success-500);
          color: white;
          box-shadow: 0 0 20px rgba(34, 197, 94, 0.3);

          .icon {
            color: white;
          }
        }

        .icon {
          width: 20px;
          height: 20px;
          color: var(--success-500);
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
    position: relative;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(
        135deg,
        rgba(59, 130, 246, 0.02) 0%,
        rgba(16, 185, 129, 0.02) 50%,
        rgba(34, 197, 94, 0.02) 100%
      );
      pointer-events: none;
    }

    .service-header {
      flex-shrink: 0;
      padding: var(--space-6) var(--space-8);
      border-bottom: 1px solid var(--border-color);
      background: var(--glass-bg);
      backdrop-filter: blur(10px);
      position: relative;
      z-index: 1;

      .service-info {
        display: flex;
        align-items: center;
        gap: var(--space-4);

        .avatar {
          width: 56px;
          height: 56px;
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          background: linear-gradient(135deg, var(--success-500), var(--success-600));
          border-radius: var(--radius-2xl);
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
            background: var(--gradient-glass);
            pointer-events: none;
          }

          &:hover {
            transform: scale(1.05);
            box-shadow: var(--shadow-lg);
          }
        }

        .info {
          flex: 1;

          h3 {
            font-size: var(--text-xl);
            font-weight: 700;
            color: var(--text-color);
            margin-bottom: var(--space-1);
            display: flex;
            align-items: center;
            gap: var(--space-2);

            &::after {
              content: '🟢';
              font-size: var(--text-sm);
              animation: pulse 2s infinite;
            }
          }

          p {
            font-size: var(--text-sm);
            color: var(--text-secondary);
            font-weight: 500;
          }
        }
      }
    }
    
    .messages {
      flex: 1;
      overflow-y: auto;
      padding: var(--space-8);
      position: relative;
      z-index: 1;

      /* 自定义滚动条 */
      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-track {
        background: transparent;
      }

      &::-webkit-scrollbar-thumb {
        background: rgba(34, 197, 94, 0.3);
        border-radius: var(--radius-full);

        &:hover {
          background: rgba(34, 197, 94, 0.5);
        }
      }
    }

    .input-area {
      flex-shrink: 0;
      padding: var(--space-6);
      background: var(--glass-bg);
      border-top: 1px solid var(--border-color);
      backdrop-filter: blur(10px);
      display: flex;
      gap: var(--space-4);
      align-items: flex-end;
      position: relative;
      z-index: 1;

      textarea {
        flex: 1;
        resize: none;
        border: 2px solid var(--border-color);
        background: var(--card-bg);
        border-radius: var(--radius-2xl);
        padding: var(--space-4) var(--space-5);
        color: var(--text-color);
        font-family: var(--font-family-sans);
        font-size: var(--text-base);
        line-height: var(--leading-relaxed);
        max-height: 120px;
        min-height: 48px;
        transition: var(--transition-all);
        box-shadow: var(--shadow-sm);

        &:focus {
          outline: none;
          border-color: var(--success-500);
          box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.2);
          transform: translateY(-1px);
        }

        &::placeholder {
          color: var(--text-tertiary);
        }
      }
      
      .send-button {
        background: linear-gradient(135deg, var(--success-500), var(--success-600));
        color: white;
        border: none;
        border-radius: var(--radius-2xl);
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
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
          box-shadow: 0 0 20px rgba(34, 197, 94, 0.4);

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

  .booking-modal {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(4px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: var(--z-50);
    animation: fadeIn 0.3s ease-out;

    .modal-content {
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      padding: var(--space-10);
      border-radius: var(--radius-3xl);
      max-width: 500px;
      width: 90%;
      text-align: center;
      box-shadow: var(--shadow-2xl);
      backdrop-filter: blur(20px);
      animation: slideInUp 0.4s ease-out;
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(
          135deg,
          rgba(34, 197, 94, 0.05) 0%,
          rgba(16, 185, 129, 0.05) 100%
        );
        pointer-events: none;
      }

      h3 {
        font-size: var(--text-2xl);
        font-weight: 700;
        margin-bottom: var(--space-4);
        color: var(--text-color);
        position: relative;
        z-index: 1;

        &::before {
          content: '📅';
          margin-right: var(--space-2);
        }
      }

      .booking-info {
        margin: var(--space-6) 0;
        text-align: left;
        line-height: var(--leading-relaxed);
        color: var(--text-secondary);
        background: var(--bg-secondary);
        padding: var(--space-5);
        border-radius: var(--radius-xl);
        border: 1px solid var(--border-color);
        position: relative;
        z-index: 1;
      }

      button {
        padding: var(--space-3) var(--space-8);
        background: linear-gradient(135deg, var(--success-500), var(--success-600));
        color: white;
        border: none;
        border-radius: var(--radius-xl);
        cursor: pointer;
        font-weight: 600;
        font-size: var(--text-base);
        transition: var(--transition-all);
        box-shadow: var(--shadow-md);
        position: relative;
        z-index: 1;

        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--shadow-lg);
        }
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .customer-service {
    top: 70px;

    .chat-container {
      padding: var(--space-4);
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

      .service-header {
        padding: var(--space-4) var(--space-5);

        .service-info {
          gap: var(--space-3);

          .avatar {
            width: 48px;
            height: 48px;
          }

          .info h3 {
            font-size: var(--text-lg);
          }
        }
      }

      .messages {
        padding: var(--space-4);
      }

      .input-area {
        padding: var(--space-4);
        gap: var(--space-3);

        textarea {
          padding: var(--space-3) var(--space-4);
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

    .booking-modal .modal-content {
      padding: var(--space-8);
      margin: var(--space-4);

      h3 {
        font-size: var(--text-xl);
      }

      button {
        padding: var(--space-2) var(--space-6);
        font-size: var(--text-sm);
      }
    }
  }
}

@media (max-width: 480px) {
  .customer-service {
    top: 60px;

    .chat-container {
      padding: var(--space-3);
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


</style> 
