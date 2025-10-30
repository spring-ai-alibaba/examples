<template>
  <div class="comfort-simulator">
    <!-- 背景装饰 -->
    <div class="bg-elements">
      <div class="bg-hearts">
        <div class="heart heart-1">💕</div>
        <div class="heart heart-2">💖</div>
        <div class="heart heart-3">💝</div>
        <div class="heart heart-4">💗</div>
        <div class="heart heart-5">💘</div>
      </div>
    </div>

    <div class="container">
      <!-- 头部区域 -->
      <div class="header-section">
        <div class="title-container">
          <h1 class="main-title">
            <span class="title-emoji">💕</span>
            <span class="title-text gradient-text">情感模拟器</span>
            <span class="title-emoji">💕</span>
          </h1>
          <p class="subtitle">练习情感表达，提升沟通技巧</p>
        </div>
      </div>

      <!-- 主要内容区域 -->
      <div class="main-content">
        <div class="simulator-card card glass">
          <div class="card-header">
            <div class="scenario-selector">
              <h3>选择情境</h3>
              <div class="scenario-grid">
                <div
                  v-for="scenario in scenarios"
                  :key="scenario.id"
                  class="scenario-item"
                  :class="{ active: selectedScenario?.id === scenario.id }"
                  @click="selectScenario(scenario)"
                >
                  <div class="scenario-icon">{{ scenario.emoji }}</div>
                  <div class="scenario-info">
                    <h4>{{ scenario.title }}</h4>
                    <p>{{ scenario.description }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="selectedScenario" class="card-body">
            <div class="scenario-details">
              <div class="scenario-background">
                <h4>情境背景</h4>
                <p>{{ selectedScenario.background }}</p>
              </div>

              <div class="emotion-meter">
                <h4>情绪指数</h4>
                <div class="meter-container">
                  <div class="meter-bar">
                    <div
                      class="meter-fill"
                      :style="{ width: `${emotionLevel}%` }"
                      :class="getMeterClass()"
                    ></div>
                  </div>
                  <div class="meter-labels">
                    <span>😢 低落</span>
                    <span>😐 平静</span>
                    <span>😊 开心</span>
                  </div>
                </div>
              </div>

              <div class="response-area">
                <h4>你的回应</h4>
                <textarea
                  v-model="userResponse"
                  placeholder="输入你的回应..."
                  class="response-input"
                  rows="4"
                ></textarea>

                <div class="action-buttons">
                  <button
                    @click="analyzeResponse"
                    class="btn btn-primary"
                    :disabled="!userResponse.trim()"
                  >
                    <span>分析回应</span>
                    <svg class="icon" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-8.293l-3-3a1 1 0 00-1.414 1.414L10.586 9.5 9.293 10.793a1 1 0 101.414 1.414l3-3a1 1 0 000-1.414z" clip-rule="evenodd" />
                    </svg>
                  </button>

                  <button
                    @click="getHint"
                    class="btn btn-secondary"
                  >
                    <span>获取提示</span>
                    <svg class="icon" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-3a1 1 0 00-.867.5 1 1 0 11-1.731-1A3 3 0 0113 8a3.001 3.001 0 01-2 2.83V11a1 1 0 11-2 0v-1a1 1 0 011-1 1 1 0 100-2zm0 8a1 1 0 100-2 1 1 0 000 2z" clip-rule="evenodd" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="analysis" class="analysis-section">
            <div class="analysis-card">
              <h4>分析结果</h4>
              <div class="analysis-content">
                <div class="score-section">
                  <div class="score-item">
                    <span class="score-label">情感理解</span>
                    <div class="score-bar">
                      <div class="score-fill" :style="{ width: `${analysis.empathy}%` }"></div>
                    </div>
                    <span class="score-value">{{ analysis.empathy }}%</span>
                  </div>

                  <div class="score-item">
                    <span class="score-label">表达技巧</span>
                    <div class="score-bar">
                      <div class="score-fill" :style="{ width: `${analysis.expression}%` }"></div>
                    </div>
                    <span class="score-value">{{ analysis.expression }}%</span>
                  </div>

                  <div class="score-item">
                    <span class="score-label">安慰效果</span>
                    <div class="score-bar">
                      <div class="score-fill" :style="{ width: `${analysis.comfort}%` }"></div>
                    </div>
                    <span class="score-value">{{ analysis.comfort }}%</span>
                  </div>
                </div>

                <div class="feedback-section">
                  <h5>反馈建议</h5>
                  <p>{{ analysis.feedback }}</p>
                </div>

                <div class="improvement-section">
                  <h5>改进建议</h5>
                  <ul>
                    <li v-for="tip in analysis.tips" :key="tip">{{ tip }}</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'

// 响应式数据
const selectedScenario = ref(null)
const userResponse = ref('')
const emotionLevel = ref(30)
const analysis = ref(null)

// 情境数据
const scenarios = ref([
  {
    id: 1,
    emoji: '😢',
    title: '工作压力',
    description: '朋友因为工作压力大而情绪低落',
    background: '你的朋友最近工作压力很大，经常加班到很晚，感到身心俱疲，向你倾诉说感觉快撑不下去了。',
    hints: [
      '表达理解和同情',
      '询问具体困难',
      '提供实际建议',
      '给予情感支持'
    ]
  },
  {
    id: 2,
    emoji: '💔',
    title: '感情困扰',
    description: '朋友遭遇感情挫折需要安慰',
    background: '你的朋友刚刚经历了一段感情的结束，感到非常难过和失落，觉得自己不值得被爱。',
    hints: [
      '倾听对方的感受',
      '肯定对方的价值',
      '避免批评前任',
      '鼓励积极面对未来'
    ]
  },
  {
    id: 3,
    emoji: '😰',
    title: '考试焦虑',
    description: '朋友面临重要考试感到焦虑',
    background: '你的朋友即将面临一场重要的考试，感到非常紧张和焦虑，担心自己会失败。',
    hints: [
      '帮助缓解紧张情绪',
      '分享应对策略',
      '给予信心鼓励',
      '提供实用建议'
    ]
  },
  {
    id: 4,
    emoji: '😔',
    title: '家庭矛盾',
    description: '朋友与家人发生矛盾感到困扰',
    background: '你的朋友与家人发生了激烈的争吵，感到很委屈和无助，不知道该如何处理这种关系。',
    hints: [
      '理解双方立场',
      '建议冷静沟通',
      '提供解决思路',
      '给予情感支持'
    ]
  }
])

// 选择情境
const selectScenario = (scenario) => {
  selectedScenario.value = scenario
  userResponse.value = ''
  analysis.value = null
  emotionLevel.value = 30
}

// 获取情绪指数样式类
const getMeterClass = () => {
  if (emotionLevel.value < 40) return 'low'
  if (emotionLevel.value < 70) return 'medium'
  return 'high'
}

// 分析回应
const analyzeResponse = () => {
  if (!userResponse.value.trim()) return

  // 模拟分析结果
  const empathy = Math.floor(Math.random() * 30) + 60
  const expression = Math.floor(Math.random() * 25) + 65
  const comfort = Math.floor(Math.random() * 35) + 55

  analysis.value = {
    empathy,
    expression,
    comfort,
    feedback: generateFeedback(empathy, expression, comfort),
    tips: generateTips()
  }

  // 更新情绪指数
  const avgScore = (empathy + expression + comfort) / 3
  emotionLevel.value = Math.min(avgScore + 10, 100)
}

// 生成反馈
const generateFeedback = (empathy, expression, comfort) => {
  const avgScore = (empathy + expression + comfort) / 3

  if (avgScore >= 80) {
    return '很棒的回应！你展现了很好的情感理解能力和表达技巧，能够有效地安慰和支持对方。'
  } else if (avgScore >= 60) {
    return '不错的回应！你基本理解了对方的感受，但在表达方式上还有提升空间。'
  } else {
    return '还需要改进。试着更多地关注对方的感受，用更温暖和理解的语言来回应。'
  }
}

// 生成建议
const generateTips = () => {
  const tips = [
    '使用"我理解你的感受"这样的表达来显示同理心',
    '避免立即给出解决方案，先倾听和理解',
    '用积极正面的语言来鼓励对方',
    '分享相似的经历来建立连接',
    '询问对方需要什么样的帮助',
    '给予具体而实用的建议',
    '表达你对对方的关心和支持'
  ]

  // 随机选择3-4个建议
  const shuffled = tips.sort(() => 0.5 - Math.random())
  return shuffled.slice(0, Math.floor(Math.random() * 2) + 3)
}

// 获取提示
const getHint = () => {
  if (!selectedScenario.value) return

  const hints = selectedScenario.value.hints
  const randomHint = hints[Math.floor(Math.random() * hints.length)]

  alert(`💡 提示：${randomHint}`)
}
</script>
<style scoped lang="scss">
.comfort-simulator {
  min-height: 100vh;
  background: var(--bg-color);
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰 */
.bg-elements {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  pointer-events: none;
}

.bg-hearts {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.heart {
  position: absolute;
  font-size: var(--text-4xl);
  opacity: 0.1;
  animation: floatHeart 15s ease-in-out infinite;

  &.heart-1 {
    top: 10%;
    left: 10%;
    animation-delay: 0s;
  }

  &.heart-2 {
    top: 20%;
    right: 15%;
    animation-delay: -3s;
  }

  &.heart-3 {
    bottom: 30%;
    left: 20%;
    animation-delay: -6s;
  }

  &.heart-4 {
    top: 60%;
    right: 25%;
    animation-delay: -9s;
  }

  &.heart-5 {
    bottom: 15%;
    right: 10%;
    animation-delay: -12s;
  }
}

@keyframes floatHeart {
  0%, 100% {
    transform: translateY(0px) rotate(0deg) scale(1);
  }
  25% {
    transform: translateY(-20px) rotate(5deg) scale(1.1);
  }
  50% {
    transform: translateY(-10px) rotate(-3deg) scale(0.9);
  }
  75% {
    transform: translateY(-30px) rotate(8deg) scale(1.05);
  }
}

/* 容器 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-8) var(--space-6);
}

/* 头部区域 */
.header-section {
  text-align: center;
  margin-bottom: var(--space-12);
  animation: fadeInUp 0.8s ease-out;
}

.title-container {
  position: relative;
}

.main-title {
  font-size: clamp(2.5rem, 5vw, 4rem);
  font-weight: 800;
  margin-bottom: var(--space-4);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);

  .title-emoji {
    animation: heartBeat 2s ease-in-out infinite;
  }

  .title-text {
    background: linear-gradient(135deg, #ff69b4, #ff1493, #dc143c);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.subtitle {
  font-size: var(--text-xl);
  color: var(--text-secondary);
  font-weight: 500;
}

/* 主要内容 */
.main-content {
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.simulator-card {
  max-width: 900px;
  margin: 0 auto;
  padding: var(--space-8);
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
      rgba(255, 105, 180, 0.05) 0%,
      rgba(255, 20, 147, 0.05) 50%,
      rgba(220, 20, 60, 0.05) 100%
    );
    pointer-events: none;
  }
}

.card-header {
  margin-bottom: var(--space-8);
  position: relative;
  z-index: 1;
}

.scenario-selector h3 {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: var(--space-6);
  text-align: center;

  &::before {
    content: '🎭 ';
    margin-right: var(--space-2);
  }
}

.scenario-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-4);
}

.scenario-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  background: var(--card-bg);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-2xl);
  cursor: pointer;
  transition: var(--transition-all);
  box-shadow: var(--shadow-sm);

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-lg);
    border-color: #ff69b4;
  }

  &.active {
    border-color: #ff1493;
    background: linear-gradient(135deg, rgba(255, 105, 180, 0.1), rgba(255, 20, 147, 0.1));
    box-shadow: 0 0 20px rgba(255, 20, 147, 0.3);
  }
}

.scenario-icon {
  font-size: var(--text-3xl);
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  border-radius: var(--radius-2xl);
  flex-shrink: 0;
}

.scenario-info {
  flex: 1;

  h4 {
    font-size: var(--text-lg);
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: var(--space-1);
  }

  p {
    font-size: var(--text-sm);
    color: var(--text-secondary);
    line-height: var(--leading-relaxed);
  }
}

/* 卡片主体 */
.card-body {
  position: relative;
  z-index: 1;
}

.scenario-details {
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

.scenario-background {
  background: var(--bg-secondary);
  padding: var(--space-6);
  border-radius: var(--radius-2xl);
  border: 1px solid var(--border-color);

  h4 {
    font-size: var(--text-lg);
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: var(--space-3);

    &::before {
      content: '📖 ';
      margin-right: var(--space-2);
    }
  }

  p {
    color: var(--text-secondary);
    line-height: var(--leading-relaxed);
  }
}

.emotion-meter {
  h4 {
    font-size: var(--text-lg);
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: var(--space-4);

    &::before {
      content: '💗 ';
      margin-right: var(--space-2);
    }
  }
}

.meter-container {
  background: var(--bg-secondary);
  padding: var(--space-5);
  border-radius: var(--radius-2xl);
  border: 1px solid var(--border-color);
}

.meter-bar {
  width: 100%;
  height: 20px;
  background: var(--bg-tertiary);
  border-radius: var(--radius-full);
  overflow: hidden;
  margin-bottom: var(--space-3);
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.meter-fill {
  height: 100%;
  border-radius: var(--radius-full);
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
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
    background: linear-gradient(90deg, #ff69b4, #ff1493);
    box-shadow: 0 0 20px rgba(255, 105, 180, 0.5);
  }
}

.meter-labels {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.response-area {
  h4 {
    font-size: var(--text-lg);
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: var(--space-4);

    &::before {
      content: '💬 ';
      margin-right: var(--space-2);
    }
  }
}

.response-input {
  width: 100%;
  padding: var(--space-4) var(--space-5);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-2xl);
  background: var(--card-bg);
  color: var(--text-color);
  font-family: var(--font-family-sans);
  font-size: var(--text-base);
  line-height: var(--leading-relaxed);
  resize: vertical;
  min-height: 120px;
  transition: var(--transition-all);

  &:focus {
    outline: none;
    border-color: #ff69b4;
    box-shadow: 0 0 0 4px rgba(255, 105, 180, 0.2);
    transform: translateY(-1px);
  }

  &::placeholder {
    color: var(--text-tertiary);
  }
}

.action-buttons {
  display: flex;
  gap: var(--space-4);
  margin-top: var(--space-4);

  .btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: var(--space-2);
    padding: var(--space-4) var(--space-6);
    border-radius: var(--radius-2xl);
    font-weight: 600;
    font-size: var(--text-base);
    transition: var(--transition-all);
    position: relative;
    overflow: hidden;

    .icon {
      width: 20px;
      height: 20px;
    }

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

    &:hover::before {
      transform: translateX(100%);
    }
  }

  .btn-primary {
    background: linear-gradient(135deg, #ff69b4, #ff1493);
    color: white;
    border: none;
    box-shadow: var(--shadow-lg);

    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: var(--shadow-2xl);
    }

    &:disabled {
      background: var(--bg-tertiary);
      color: var(--text-tertiary);
      cursor: not-allowed;
      transform: none;
      box-shadow: none;
    }
  }

  .btn-secondary {
    background: var(--card-bg);
    color: var(--text-color);
    border: 2px solid var(--border-color);

    &:hover {
      background: var(--bg-secondary);
      border-color: #ff69b4;
      transform: translateY(-1px);
    }
  }
}
/* 分析部分 */
.analysis-section {
  margin-top: var(--space-8);
  animation: slideInUp 0.6s ease-out;
}

.analysis-card {
  background: var(--bg-secondary);
  padding: var(--space-8);
  border-radius: var(--radius-3xl);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-lg);

  h4 {
    font-size: var(--text-2xl);
    font-weight: 700;
    color: var(--text-color);
    margin-bottom: var(--space-6);
    text-align: center;

    &::before {
      content: '📊 ';
      margin-right: var(--space-2);
    }
  }
}

.analysis-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

.score-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.score-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);

  .score-label {
    min-width: 100px;
    font-size: var(--text-sm);
    font-weight: 600;
    color: var(--text-color);
  }

  .score-bar {
    flex: 1;
    height: 12px;
    background: var(--bg-tertiary);
    border-radius: var(--radius-full);
    overflow: hidden;
    position: relative;

    .score-fill {
      height: 100%;
      background: linear-gradient(90deg, #ff69b4, #ff1493);
      border-radius: var(--radius-full);
      transition: width 1s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;

      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
        animation: shimmer 2s infinite;
      }
    }
  }

  .score-value {
    min-width: 50px;
    font-size: var(--text-sm);
    font-weight: 700;
    color: #ff1493;
    text-align: right;
  }
}

.feedback-section,
.improvement-section {
  background: var(--card-bg);
  padding: var(--space-6);
  border-radius: var(--radius-2xl);
  border: 1px solid var(--border-color);

  h5 {
    font-size: var(--text-lg);
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: var(--space-3);
  }

  p {
    color: var(--text-secondary);
    line-height: var(--leading-relaxed);
  }

  ul {
    list-style: none;
    padding: 0;

    li {
      position: relative;
      padding-left: var(--space-6);
      margin-bottom: var(--space-2);
      color: var(--text-secondary);
      line-height: var(--leading-relaxed);

      &::before {
        content: '💡';
        position: absolute;
        left: 0;
        top: 0;
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

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: var(--space-6) var(--space-4);
  }

  .main-title {
    font-size: var(--text-3xl);
    flex-direction: column;
    gap: var(--space-2);
  }

  .subtitle {
    font-size: var(--text-lg);
  }

  .simulator-card {
    padding: var(--space-6);
  }

  .scenario-grid {
    grid-template-columns: 1fr;
    gap: var(--space-3);
  }

  .scenario-item {
    padding: var(--space-4);
    gap: var(--space-3);
  }

  .scenario-icon {
    width: 50px;
    height: 50px;
    font-size: var(--text-2xl);
  }

  .scenario-details {
    gap: var(--space-6);
  }

  .action-buttons {
    flex-direction: column;
    gap: var(--space-3);
  }

  .score-item {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-2);

    .score-label {
      min-width: auto;
    }

    .score-value {
      text-align: left;
    }
  }

  .analysis-card {
    padding: var(--space-6);
  }

  .bg-hearts {
    display: none; // 移动端隐藏装饰元素
  }
}

@media (max-width: 480px) {
  .container {
    padding: var(--space-4) var(--space-3);
  }

  .main-title {
    font-size: var(--text-2xl);
  }

  .simulator-card {
    padding: var(--space-4);
  }

  .scenario-background,
  .meter-container {
    padding: var(--space-4);
  }

  .response-input {
    padding: var(--space-3) var(--space-4);
    min-height: 100px;
  }

  .action-buttons .btn {
    padding: var(--space-3) var(--space-4);
    font-size: var(--text-sm);
  }
}
</style>
