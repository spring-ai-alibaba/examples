<template>
  <div class="game-chat" :class="{ 'dark': isDark }">
    <div class="game-container">
      <!-- 游戏开始界面 -->
      <div v-if="!isGameStarted" class="game-start">
        <h2>哄哄模拟器</h2>
        <div class="input-area">
          <textarea
            v-model="angerReason"
            placeholder="请输入女友生气的原因（可选）..."
            rows="3"
          ></textarea>
          <button class="start-button" @click="startGame">
            开始游戏
          </button>
        </div>
      </div>

      <!-- 聊天界面 -->
      <div v-else class="chat-main">
        <!-- 游戏统计信息 -->
        <div class="game-stats">
          <div class="stat-item">
            <span class="label">
              <HeartIcon class="heart-icon" :class="{ 'beating': forgiveness >= 100 }" />
              女友原谅值
            </span>
            <div class="progress-bar">
              <div 
                class="progress" 
                :style="{ width: `${forgiveness}%` }"
                :class="{ 
                  'low': forgiveness < 30,
                  'medium': forgiveness >= 30 && forgiveness < 70,
                  'high': forgiveness >= 70 
                }"
              ></div>
            </div>
            <span class="value">{{ forgiveness }}%</span>
          </div>
          <div class="stat-item">
            <span class="label">对话轮次</span>
            <span class="value">{{ currentRound }}/{{ MAX_ROUNDS }}</span>
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
            placeholder="输入消息..."
            rows="1"
            ref="inputRef"
            :disabled="isGameOver"
          ></textarea>
          <button 
            class="send-button" 
            @click="sendMessage()"
            :disabled="isStreaming || !userInput.trim() || isGameOver"
          >
            <PaperAirplaneIcon class="icon" />
          </button>
        </div>
      </div>

      <!-- 游戏结束提示 -->
      <div v-if="isGameOver" class="game-over" :class="{ 'success': forgiveness >= 100 }">
        <div class="result">{{ gameResult }}</div>
        <button class="restart-button" @click="resetGame">
          重新开始
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useDark } from '@vueuse/core'
import { PaperAirplaneIcon, HeartIcon } from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import { chatAPI } from '../services/api'

const isDark = useDark()
const messagesRef = ref(null)
const inputRef = ref(null)
const userInput = ref('')
const isStreaming = ref(false)
const currentChatId = ref(null)
const currentMessages = ref([])
const angerReason = ref('')
const isGameStarted = ref(false)
const isGameOver = ref(false)
const gameResult = ref('')
const MAX_ROUNDS = 10  // 添加最大轮次常量
const currentRound = ref(0)  // 添加当前轮次计数
const forgiveness = ref(0)

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

// 开始游戏
const startGame = async () => {
  isGameStarted.value = true
  isGameOver.value = false
  gameResult.value = ''
  currentChatId.value = Date.now().toString()
  currentMessages.value = []
  currentRound.value = 0
  forgiveness.value = 0  // 重置原谅值
  
  // 发送开始游戏请求
  const startPrompt = angerReason.value 
    ? `开始游戏，女友生气原因：${angerReason.value}`
    : '开始游戏'
  
  await sendMessage(startPrompt)
}

// 重置游戏
const resetGame = () => {
  isGameStarted.value = false
  isGameOver.value = false
  gameResult.value = ''
  currentMessages.value = []
  angerReason.value = ''
  userInput.value = ''
  currentRound.value = 0
  forgiveness.value = 0
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
  
  // 清空输入并增加轮次计数
  if (!content) {  // 只有在非传入内容时才清空输入框和计数
    userInput.value = ''
    adjustTextareaHeight()
    currentRound.value++  // 增加轮次计数
  }
  await scrollToBottom()
  
  // 添加助手消息占位
  const assistantMessage = {
    role: 'assistant',
    content: '',
    timestamp: new Date()
  }
  currentMessages.value.push(assistantMessage)
  isStreaming.value = true
  
  let accumulatedContent = ''
  
  try {
    // 确保使用正确的消息内容发送请求
    const reader = await chatAPI.sendGameMessage(messageContent, currentChatId.value)
    const decoder = new TextDecoder('utf-8')
    
    while (true) {
      try {
        const { value, done } = await reader.read()
        if (done) break
        
        // 累积新内容
        accumulatedContent += decoder.decode(value)
        
        // 尝试从回复中提取原谅值
        const forgivenessMatch = accumulatedContent.match(/原谅值[:：]\s*(\d+)/i)
        if (forgivenessMatch) {
          const newForgiveness = parseInt(forgivenessMatch[1])
          if (!isNaN(newForgiveness)) {
            forgiveness.value = Math.min(100, Math.max(0, newForgiveness))
            
            // 当原谅值达到100时，游戏胜利结束
            if (forgiveness.value >= 100) {
              isGameOver.value = true
              gameResult.value = '恭喜你！成功哄好了女友！💕'
            }
          }
        }

        // 更新消息内容
        await nextTick(() => {
          const updatedMessage = {
            ...assistantMessage,
            content: accumulatedContent
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

    // 检查是否达到最大轮次，并等待本轮回复完成后再判断
    if (currentRound.value >= MAX_ROUNDS) {
      isGameOver.value = true
      if (forgiveness.value >= 100) {
        gameResult.value = '恭喜你！在最后一轮成功哄好了女友！💕'
      } else {
        gameResult.value = `游戏结束：对话轮次已达上限(${MAX_ROUNDS}轮)，当前原谅值为${forgiveness.value}，很遗憾没能完全哄好女友`
      }
    }
    // 检查是否游戏结束
    else if (accumulatedContent.includes('游戏结束')) {
      isGameOver.value = true
      gameResult.value = accumulatedContent
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    assistantMessage.content = '抱歉，发生了错误，请稍后重试。'
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

// 添加计算属性显示剩余轮次
const remainingRounds = computed(() => MAX_ROUNDS - currentRound.value)

onMounted(() => {
  adjustTextareaHeight()
})
</script>

<style scoped lang="scss">
.game-chat {
  position: fixed;
  top: 80px;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--bg-color);
  overflow: hidden;

  .game-container {
    display: flex;
    flex-direction: column;
    max-width: 1200px;
    width: 100%;
    height: 100%;
    margin: 0 auto;
    padding: var(--space-6);
    position: relative;
  }

  .game-start {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: var(--space-8);
    min-height: 400px;
    padding: var(--space-10);
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-3xl);
    box-shadow: var(--card-shadow);
    backdrop-filter: blur(20px);
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
        rgba(255, 182, 193, 0.1) 0%,
        rgba(255, 105, 180, 0.1) 50%,
        rgba(255, 20, 147, 0.1) 100%
      );
      pointer-events: none;
    }

    h2 {
      font-size: var(--text-4xl);
      font-weight: 800;
      color: var(--text-color);
      margin: 0;
      text-align: center;
      position: relative;
      z-index: 1;

      &::after {
        content: '💕';
        position: absolute;
        top: -10px;
        right: -40px;
        font-size: var(--text-2xl);
        animation: heartBeat 2s ease-in-out infinite;
      }
    }

    .input-area {
      width: 100%;
      max-width: 600px;
      display: flex;
      flex-direction: column;
      gap: var(--space-6);
      position: relative;
      z-index: 1;

      textarea {
        width: 100%;
        padding: var(--space-5) var(--space-6);
        border: 2px solid var(--border-color);
        border-radius: var(--radius-2xl);
        resize: none;
        font-family: var(--font-family-sans);
        font-size: var(--text-base);
        line-height: var(--leading-relaxed);
        background: var(--card-bg);
        color: var(--text-color);
        backdrop-filter: blur(10px);
        transition: var(--transition-all);
        box-shadow: var(--shadow-md);

        &:focus {
          outline: none;
          border-color: #ff69b4;
          box-shadow: 0 0 0 4px rgba(255, 105, 180, 0.2);
          transform: translateY(-2px);
        }

        &::placeholder {
          color: var(--text-tertiary);
        }
      }

      .start-button {
        padding: var(--space-4) var(--space-8);
        background: linear-gradient(135deg, #ff69b4, #ff1493);
        color: white;
        border: none;
        border-radius: var(--radius-2xl);
        font-size: var(--text-lg);
        font-weight: 600;
        cursor: pointer;
        transition: var(--transition-all);
        box-shadow: var(--shadow-lg);
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

        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--shadow-2xl);

          &::before {
            transform: translateX(100%);
          }
        }

        &:active {
          transform: translateY(0);
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

    .game-stats {
      position: sticky;
      top: 0;
      background: linear-gradient(135deg, #ff69b4, #ff1493);
      color: white;
      padding: var(--space-6);
      z-index: 10;
      backdrop-filter: blur(10px);
      display: flex;
      gap: var(--space-8);
      justify-content: center;
      align-items: center;
      border-radius: var(--radius-2xl) var(--radius-2xl) 0 0;
      box-shadow: var(--shadow-lg);
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

      .stat-item {
        display: flex;
        align-items: center;
        gap: var(--space-3);
        position: relative;
        z-index: 1;

        .label {
          display: flex;
          align-items: center;
          gap: var(--space-2);
          font-weight: 600;
          font-size: var(--text-sm);

          .heart-icon {
            width: 20px;
            height: 20px;
            color: #ffb3d9;

            &.beating {
              animation: heartbeat 1.5s ease-in-out infinite;
              color: #fff;
              filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.8));
            }
          }
        }

        .value {
          font-size: var(--text-lg);
          font-weight: 700;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
        }

        .progress-bar {
          width: 200px;
          height: 12px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: var(--radius-full);
          overflow: hidden;
          box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.2);
          border: 1px solid rgba(255, 255, 255, 0.3);

          .progress {
            height: 100%;
            transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
            border-radius: var(--radius-full);
            position: relative;

            &::after {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
              animation: shimmer 2s infinite;
            }

            &.low {
              background: linear-gradient(90deg, #ff6b6b, #ff5252);
            }

            &.medium {
              background: linear-gradient(90deg, #ffd93d, #ffb74d);
            }

            &.high {
              background: linear-gradient(90deg, #4caf50, #66bb6a);
              box-shadow: 0 0 20px rgba(76, 175, 80, 0.5);
            }
          }
        }
      }
    }

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
        background: rgba(255, 105, 180, 0.3);
        border-radius: var(--radius-full);

        &:hover {
          background: rgba(255, 105, 180, 0.5);
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
          border-color: #ff69b4;
          box-shadow: 0 0 0 4px rgba(255, 105, 180, 0.2);
          transform: translateY(-1px);
        }

        &:disabled {
          background: var(--bg-tertiary);
          color: var(--text-tertiary);
          cursor: not-allowed;
          opacity: 0.6;
        }

        &::placeholder {
          color: var(--text-tertiary);
        }
      }

      .send-button {
        background: linear-gradient(135deg, #ff69b4, #ff1493);
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

  .game-over {
    position: absolute;
    bottom: var(--space-24);
    left: 50%;
    transform: translateX(-50%);
    background: var(--card-bg);
    border: 1px solid var(--border-color);
    color: var(--text-color);
    padding: var(--space-8) var(--space-10);
    border-radius: var(--radius-3xl);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: var(--space-6);
    box-shadow: var(--shadow-2xl);
    backdrop-filter: blur(20px);
    animation: slideInUp 0.5s ease-out;
    max-width: 400px;
    text-align: center;

    .result {
      font-size: var(--text-xl);
      font-weight: 600;
      line-height: var(--leading-relaxed);
    }

    .restart-button {
      padding: var(--space-3) var(--space-6);
      background: linear-gradient(135deg, #ff69b4, #ff1493);
      color: white;
      border: none;
      border-radius: var(--radius-xl);
      cursor: pointer;
      font-weight: 600;
      font-size: var(--text-base);
      transition: var(--transition-all);
      box-shadow: var(--shadow-md);

      &:hover {
        transform: translateY(-2px);
        box-shadow: var(--shadow-lg);
      }
    }

    &.success {
      border-color: var(--success-500);

      &::before {
        content: '🎉';
        position: absolute;
        top: -20px;
        left: 50%;
        transform: translateX(-50%);
        font-size: var(--text-4xl);
        animation: bounce 1s infinite;
      }

      .restart-button {
        background: linear-gradient(135deg, var(--success-500), var(--success-600));

        &:hover {
          background: linear-gradient(135deg, var(--success-600), var(--success-700));
        }
      }
    }
  }
}

/* 动画定义 */
@keyframes heartBeat {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

@keyframes heartbeat {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  40% {
    transform: translateX(-50%) translateY(-10px);
  }
  60% {
    transform: translateX(-50%) translateY(-5px);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .game-chat {
    top: 70px;

    .game-container {
      padding: var(--space-4);
    }

    .game-start {
      padding: var(--space-8);
      gap: var(--space-6);

      h2 {
        font-size: var(--text-3xl);

        &::after {
          right: -30px;
          font-size: var(--text-xl);
        }
      }

      .input-area {
        gap: var(--space-4);

        textarea {
          padding: var(--space-4);
          font-size: var(--text-sm);
        }

        .start-button {
          padding: var(--space-3) var(--space-6);
          font-size: var(--text-base);
        }
      }
    }

    .chat-main {
      .game-stats {
        flex-direction: column;
        gap: var(--space-4);
        padding: var(--space-4);

        .stat-item {
          width: 100%;
          justify-content: space-between;

          .progress-bar {
            width: 120px;
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

    .game-over {
      bottom: var(--space-16);
      padding: var(--space-6) var(--space-8);
      max-width: 320px;
      margin: 0 var(--space-4);

      .result {
        font-size: var(--text-lg);
      }

      .restart-button {
        padding: var(--space-2) var(--space-4);
        font-size: var(--text-sm);
      }
    }
  }
}

@media (max-width: 480px) {
  .game-chat {
    top: 60px;

    .game-container {
      padding: var(--space-3);
    }

    .game-start {
      padding: var(--space-6);

      h2 {
        font-size: var(--text-2xl);
      }
    }

    .chat-main {
      .game-stats {
        .stat-item {
          .progress-bar {
            width: 100px;
            height: 10px;
          }

          .value {
            font-size: var(--text-base);
          }
        }
      }
    }
  }
}


</style> 
