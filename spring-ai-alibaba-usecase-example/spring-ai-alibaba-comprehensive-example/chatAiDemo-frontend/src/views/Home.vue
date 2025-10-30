<template>
  <div class="home">
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-badge animate-fade-in">
          <span class="badge-text">🚀 AI 驱动的智能对话平台</span>
        </div>

        <h1 class="hero-title animate-fade-in">
          <span class="title-line">探索无限可能的</span>
          <span class="title-highlight gradient-text">AI 对话世界</span>
        </h1>

        <p class="hero-description animate-fade-in">
          集成多种AI功能，为您提供智能聊天、文档分析、游戏互动等全方位的AI体验
        </p>

        <div class="hero-stats animate-fade-in">
          <div class="stat-item">
            <div class="stat-number">4+</div>
            <div class="stat-label">AI 应用</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-number">24/7</div>
            <div class="stat-label">在线服务</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-number">∞</div>
            <div class="stat-label">无限可能</div>
          </div>
        </div>
      </div>
    </div>

    <div class="apps-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">选择您的AI助手</h2>
          <p class="section-description">每个应用都经过精心设计，为您提供独特的AI体验</p>
        </div>

        <div class="apps-grid">
          <div
            v-for="(app, index) in aiApps"
            :key="app.id"
            class="app-card"
            :style="{ animationDelay: `${index * 0.1}s` }"
            @click="navigateToApp(app.route)"
          >
            <div class="card-background">
              <div class="card-gradient" :class="`gradient-${app.id}`"></div>
              <div class="card-pattern"></div>
            </div>

            <div class="card-content">
              <div class="card-header">
                <div class="icon-container" :class="`icon-${app.id}`">
                  <component :is="app.icon" class="app-icon" />
                  <div class="icon-glow"></div>
                </div>
                <div class="card-badge" v-if="app.badge">{{ app.badge }}</div>
              </div>

              <div class="card-body">
                <h3 class="app-title">{{ app.title }}</h3>
                <p class="app-description">{{ app.description }}</p>

                <div class="app-features">
                  <span
                    v-for="feature in app.features"
                    :key="feature"
                    class="feature-tag"
                  >
                    {{ feature }}
                  </span>
                </div>
              </div>

              <div class="card-footer">
                <div class="launch-button">
                  <span>立即体验</span>
                  <svg class="arrow-icon" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd" />
                  </svg>
                </div>
              </div>
            </div>

            <div class="card-hover-effect"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ChatBubbleLeftRightIcon,
  HeartIcon,
  UserGroupIcon,
  DocumentTextIcon
} from '@heroicons/vue/24/outline'

const router = useRouter()

const aiApps = ref([
  {
    id: 1,
    title: 'AI 智能对话',
    description: '支持多模态交互的智能对话机器人，可处理文本、图片、音频等多种输入',
    route: '/ai-chat',
    icon: ChatBubbleLeftRightIcon,
    badge: 'HOT',
    features: ['多模态', '实时对话', '智能回复']
  },
  {
    id: 2,
    title: '情感模拟器',
    description: '通过AI模拟真实情感交互，帮助您提升沟通技巧和情商',
    route: '/game',
    icon: HeartIcon,
    badge: 'NEW',
    features: ['情感分析', '互动游戏', '技能提升']
  },
  {
    id: 3,
    title: '智能客服助手',
    description: '24小时在线的专业客服机器人，提供即时、准确的咨询服务',
    route: '/customer-service',
    icon: UserGroupIcon,
    features: ['24/7在线', '专业咨询', '快速响应']
  },
  {
    id: 4,
    title: 'PDF 智能分析',
    description: '上传PDF文档，与您的知识库进行智能对话，快速获取信息',
    route: '/chat-pdf',
    icon: DocumentTextIcon,
    badge: 'PRO',
    features: ['文档解析', '智能问答', '知识提取']
  }
])

const navigateToApp = (route) => {
  router.push(route)
}
</script>

<style scoped lang="scss">
.home {
  min-height: 100vh;
  background: var(--bg-color);
}

/* Hero Section */
.hero-section {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-20) var(--space-6);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(
      ellipse at center,
      rgba(59, 130, 246, 0.1) 0%,
      transparent 70%
    );
    pointer-events: none;
  }
}

.hero-content {
  max-width: 800px;
  text-align: center;
  z-index: 1;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: var(--space-2) var(--space-4);
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-full);
  margin-bottom: var(--space-6);
  backdrop-filter: blur(10px);
  animation-delay: 0.1s;

  .badge-text {
    font-size: var(--text-sm);
    font-weight: 500;
    color: var(--text-secondary);
  }
}

.hero-title {
  font-size: clamp(2.5rem, 5vw, 4rem);
  font-weight: 800;
  line-height: var(--leading-tight);
  margin-bottom: var(--space-6);
  animation-delay: 0.2s;

  .title-line {
    display: block;
    color: var(--text-color);
    margin-bottom: var(--space-2);
  }

  .title-highlight {
    display: block;
    font-size: clamp(3rem, 6vw, 5rem);
  }
}

.hero-description {
  font-size: var(--text-xl);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
  margin-bottom: var(--space-10);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  animation-delay: 0.3s;
}

.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-8);
  animation-delay: 0.4s;
}

.stat-item {
  text-align: center;

  .stat-number {
    font-size: var(--text-3xl);
    font-weight: 700;
    color: var(--primary);
    line-height: var(--leading-none);
    margin-bottom: var(--space-1);
  }

  .stat-label {
    font-size: var(--text-sm);
    color: var(--text-tertiary);
    font-weight: 500;
  }
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--border-color);
}

/* Apps Section */
.apps-section {
  padding: var(--space-20) var(--space-6);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: var(--space-16);
}

.section-title {
  font-size: var(--text-4xl);
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: var(--space-4);
}

.section-description {
  font-size: var(--text-lg);
  color: var(--text-secondary);
  max-width: 600px;
  margin: 0 auto;
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--space-8);
  margin-top: var(--space-12);
}

.app-card {
  position: relative;
  background: var(--card-bg);
  border-radius: var(--radius-3xl);
  overflow: hidden;
  cursor: pointer;
  transition: var(--transition-all);
  border: 1px solid var(--border-color);
  animation: fadeInUp 0.6s ease-out both;

  &:hover {
    transform: translateY(-8px) scale(1.02);
    box-shadow: var(--shadow-2xl);
    border-color: var(--primary);

    .card-hover-effect {
      opacity: 1;
    }

    .icon-glow {
      opacity: 1;
      transform: scale(1.2);
    }

    .launch-button {
      background: var(--primary);
      color: white;
      transform: translateX(4px);
    }

    .arrow-icon {
      transform: translateX(4px);
    }
  }
}

.card-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.card-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0.05;

  &.gradient-1 {
    background: var(--gradient-primary);
  }

  &.gradient-2 {
    background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
  }

  &.gradient-3 {
    background: linear-gradient(135deg, #4ecdc4 0%, #44a08d 100%);
  }

  &.gradient-4 {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  }
}

.card-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.1) 0%, transparent 50%),
                    radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.1) 0%, transparent 50%);
}

.card-content {
  position: relative;
  z-index: 1;
  padding: var(--space-8);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: var(--space-6);
}

.icon-container {
  position: relative;
  width: 64px;
  height: 64px;
  border-radius: var(--radius-2xl);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition-all);

  &.icon-1 {
    background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
  }

  &.icon-2 {
    background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  }

  &.icon-3 {
    background: linear-gradient(135deg, #4ecdc4, #44a08d);
  }

  &.icon-4 {
    background: linear-gradient(135deg, #a8edea, #fed6e3);
  }
}

.app-icon {
  width: 32px;
  height: 32px;
  color: white;
  z-index: 2;
  position: relative;
}

.icon-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: var(--radius-2xl);
  background: inherit;
  opacity: 0;
  filter: blur(20px);
  transition: var(--transition-all);
  z-index: 0;
}

.card-badge {
  padding: var(--space-1) var(--space-3);
  background: var(--accent-500);
  color: white;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.card-body {
  flex: 1;
  margin-bottom: var(--space-6);
}

.app-title {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: var(--space-3);
  line-height: var(--leading-tight);
}

.app-description {
  font-size: var(--text-base);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
  margin-bottom: var(--space-4);
}

.app-features {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.feature-tag {
  padding: var(--space-1) var(--space-3);
  background: var(--bg-secondary);
  color: var(--text-tertiary);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 500;
  border: 1px solid var(--border-color);
}

.card-footer {
  margin-top: auto;
}

.launch-button {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: var(--space-4) var(--space-6);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  color: var(--text-color);
  font-weight: 600;
  transition: var(--transition-all);

  .arrow-icon {
    width: 20px;
    height: 20px;
    transition: var(--transition-all);
  }
}

.card-hover-effect {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: var(--gradient-glass);
  opacity: 0;
  transition: var(--transition-all);
  pointer-events: none;
  z-index: 1;
}

/* Animations */
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

/* Responsive Design */
@media (max-width: 768px) {
  .hero-section {
    min-height: 50vh;
    padding: var(--space-16) var(--space-4);
  }

  .hero-stats {
    flex-direction: column;
    gap: var(--space-4);

    .stat-divider {
      width: 40px;
      height: 1px;
    }
  }

  .apps-section {
    padding: var(--space-16) var(--space-4);
  }

  .apps-grid {
    grid-template-columns: 1fr;
    gap: var(--space-6);
  }

  .section-title {
    font-size: var(--text-3xl);
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: var(--space-12) var(--space-3);
  }

  .card-content {
    padding: var(--space-6);
  }

  .hero-description {
    font-size: var(--text-lg);
  }
}
</style>
