<template>
  <div class="message" :class="{ 'message-user': isUser }">
    <div class="avatar">
      <UserCircleIcon v-if="isUser" class="icon" />
      <ComputerDesktopIcon v-else class="icon" :class="{ 'assistant': !isUser }" />
    </div>
    <div class="content">
      <div class="text-container">
        <button v-if="isUser" class="user-copy-button" @click="copyContent" :title="copyButtonTitle">
          <DocumentDuplicateIcon v-if="!copied" class="copy-icon" />
          <CheckIcon v-else class="copy-icon copied" />
        </button>
        <div class="text" ref="contentRef" v-if="isUser">
          {{ message.content }}
        </div>
        <div class="text markdown-content" ref="contentRef" v-else v-html="processedContent"></div>
      </div>
      <div class="message-footer" v-if="!isUser">
        <button class="copy-button" @click="copyContent" :title="copyButtonTitle">
          <DocumentDuplicateIcon v-if="!copied" class="copy-icon" />
          <CheckIcon v-else class="copy-icon copied" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, nextTick, ref, watch } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { UserCircleIcon, ComputerDesktopIcon, DocumentDuplicateIcon, CheckIcon } from '@heroicons/vue/24/outline'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const contentRef = ref(null)
const copied = ref(false)
const copyButtonTitle = computed(() => copied.value ? '已复制' : '复制内容')

// 配置 marked
marked.setOptions({
  breaks: true,
  gfm: true,
  sanitize: false
})

// 处理内容
const processContent = (content) => {
  if (!content) return ''

  // 分析内容中的 think 标签
  let result = ''
  let isInThinkBlock = false
  let currentBlock = ''

  // 逐字符分析，处理 think 标签
  for (let i = 0; i < content.length; i++) {
    if (content.slice(i, i + 7) === '<think>') {
      isInThinkBlock = true
      if (currentBlock) {
        // 将之前的普通内容转换为 HTML
        result += marked.parse(currentBlock)
      }
      currentBlock = ''
      i += 6 // 跳过 <think>
      continue
    }

    if (content.slice(i, i + 8) === '</think>') {
      isInThinkBlock = false
      // 将 think 块包装在特殊 div 中
      result += `<div class="think-block">${marked.parse(currentBlock)}</div>`
      currentBlock = ''
      i += 7 // 跳过 </think>
      continue
    }

    currentBlock += content[i]
  }

  // 处理剩余内容
  if (currentBlock) {
    if (isInThinkBlock) {
      result += `<div class="think-block">${marked.parse(currentBlock)}</div>`
    } else {
      result += marked.parse(currentBlock)
    }
  }

  // 净化处理后的 HTML
  const cleanHtml = DOMPurify.sanitize(result, {
    ADD_TAGS: ['think', 'code', 'pre', 'span'],
    ADD_ATTR: ['class', 'language']
  })
  
  // 在净化后的 HTML 中查找代码块并添加复制按钮
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = cleanHtml
  
  // 查找所有代码块
  const preElements = tempDiv.querySelectorAll('pre')
  preElements.forEach(pre => {
    const code = pre.querySelector('code')
    if (code) {
      // 创建包装器
      const wrapper = document.createElement('div')
      wrapper.className = 'code-block-wrapper'
      
      // 添加复制按钮
      const copyBtn = document.createElement('button')
      copyBtn.className = 'code-copy-button'
      copyBtn.title = '复制代码'
      copyBtn.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" class="code-copy-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
        </svg>
      `
      
      // 添加成功消息
      const successMsg = document.createElement('div')
      successMsg.className = 'copy-success-message'
      successMsg.textContent = '已复制!'
      
      // 组装结构
      wrapper.appendChild(copyBtn)
      wrapper.appendChild(pre.cloneNode(true))
      wrapper.appendChild(successMsg)
      
      // 替换原始的 pre 元素
      pre.parentNode.replaceChild(wrapper, pre)
    }
  })
  
  return tempDiv.innerHTML
}

// 修改计算属性
const processedContent = computed(() => {
  if (!props.message.content) return ''
  return processContent(props.message.content)
})

// 为代码块添加复制功能
const setupCodeBlockCopyButtons = () => {
  if (!contentRef.value) return;
  
  const codeBlocks = contentRef.value.querySelectorAll('.code-block-wrapper');
  codeBlocks.forEach(block => {
    const copyButton = block.querySelector('.code-copy-button');
    const codeElement = block.querySelector('code');
    const successMessage = block.querySelector('.copy-success-message');
    
    if (copyButton && codeElement) {
      // 移除旧的事件监听器
      const newCopyButton = copyButton.cloneNode(true);
      copyButton.parentNode.replaceChild(newCopyButton, copyButton);
      
      // 添加新的事件监听器
      newCopyButton.addEventListener('click', async (e) => {
        e.preventDefault();
        e.stopPropagation();
        try {
          const code = codeElement.textContent || '';
          await navigator.clipboard.writeText(code);
          
          // 显示成功消息
          if (successMessage) {
            successMessage.classList.add('visible');
            setTimeout(() => {
              successMessage.classList.remove('visible');
            }, 2000);
          }
        } catch (err) {
          console.error('复制代码失败:', err);
        }
      });
    }
  });
}

// 在内容更新后手动应用高亮和设置复制按钮
const highlightCode = async () => {
  await nextTick()
  if (contentRef.value) {
    contentRef.value.querySelectorAll('pre code').forEach((block) => {
      hljs.highlightElement(block)
    })
    
    // 设置代码块复制按钮
    setupCodeBlockCopyButtons()
  }
}

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

const isUser = computed(() => props.message.role === 'user')

// 复制内容到剪贴板
const copyContent = async () => {
  try {
    // 获取纯文本内容
    let textToCopy = props.message.content;
    
    // 如果是AI回复，需要去除HTML标签
    if (!isUser.value && contentRef.value) {
      // 创建临时元素来获取纯文本
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = processedContent.value;
      textToCopy = tempDiv.textContent || tempDiv.innerText || '';
    }
    
    await navigator.clipboard.writeText(textToCopy);
    copied.value = true;
    
    // 3秒后重置复制状态
    setTimeout(() => {
      copied.value = false;
    }, 3000);
  } catch (err) {
    console.error('复制失败:', err);
  }
}

// 监听内容变化
watch(() => props.message.content, () => {
  if (!isUser.value) {
    highlightCode()
  }
})

// 初始化时也执行一次
onMounted(() => {
  if (!isUser.value) {
    highlightCode()
  }
})

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleTimeString()
}
</script>

<style scoped lang="scss">
.message {
  display: flex;
  margin-bottom: var(--space-6);
  gap: var(--space-4);
  align-items: flex-start;
  animation: slideInUp 0.4s ease-out;

  &.message-user {
    flex-direction: row-reverse;

    .content {
      align-items: flex-end;

      .text-container {
        position: relative;

        .text {
          background: var(--gradient-primary);
          color: white;
          border-radius: var(--radius-2xl) var(--radius-2xl) var(--radius-lg) var(--radius-2xl);
          box-shadow: var(--shadow-lg);
          border: 1px solid rgba(255, 255, 255, 0.2);
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
        }

        .user-copy-button {
          position: absolute;
          left: -40px;
          top: 50%;
          transform: translateY(-50%);
          background: var(--card-bg);
          border: 2px solid var(--border-color);
          width: 32px;
          height: 32px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          opacity: 0;
          transition: var(--transition-all);
          box-shadow: var(--shadow-md);

          &:hover {
            background: var(--primary);
            border-color: var(--primary);
            transform: translateY(-50%) scale(1.1);
            box-shadow: var(--shadow-glow);

            .copy-icon {
              color: white;
            }
          }

          .copy-icon {
            width: 16px;
            height: 16px;
            color: var(--text-secondary);
            transition: var(--transition-all);

            &.copied {
              color: var(--success-500);
            }
          }
        }

        &:hover .user-copy-button {
          opacity: 1;
        }
      }

      .message-footer {
        flex-direction: row-reverse;
      }
    }

    .avatar {
      background: var(--gradient-primary);
      border: 2px solid white;
      box-shadow: var(--shadow-lg);

      .icon {
        color: white;
      }
    }
  }

  .avatar {
    width: 48px;
    height: 48px;
    flex-shrink: 0;
    border-radius: 50%;
    background: var(--card-bg);
    border: 2px solid var(--border-color);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: var(--shadow-md);
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
      background: var(--gradient-secondary);
      opacity: 0.1;
      transition: var(--transition-all);
    }

    &:hover {
      transform: scale(1.05);
      box-shadow: var(--shadow-lg);

      &::before {
        opacity: 0.2;
      }
    }

    .icon {
      width: 24px;
      height: 24px;
      color: var(--text-secondary);
      z-index: 1;
      position: relative;
      transition: var(--transition-all);

      &.assistant {
        color: var(--primary);
      }
    }
  }

  .content {
    display: flex;
    flex-direction: column;
    gap: var(--space-2);
    max-width: calc(100% - 64px);
    flex: 1;

    .text-container {
      position: relative;
    }

    .message-footer {
      display: flex;
      align-items: center;
      margin-top: var(--space-2);
      gap: var(--space-3);

      .time {
        font-size: var(--text-xs);
        color: var(--text-tertiary);
        font-weight: 500;
      }

      .copy-button {
        display: flex;
        align-items: center;
        gap: var(--space-2);
        background: var(--bg-secondary);
        border: 1px solid var(--border-color);
        font-size: var(--text-xs);
        color: var(--text-secondary);
        padding: var(--space-2) var(--space-3);
        border-radius: var(--radius-full);
        cursor: pointer;
        transition: var(--transition-all);
        font-weight: 500;

        &:hover {
          background: var(--primary);
          color: white;
          border-color: var(--primary);
          transform: translateY(-1px);
          box-shadow: var(--shadow-md);
        }

        .copy-icon {
          width: 14px;
          height: 14px;
          transition: var(--transition-all);

          &.copied {
            color: var(--success-500);
          }
        }

        .copy-text {
          font-size: var(--text-xs);
        }
      }
    }

    .text {
      padding: var(--space-5) var(--space-6);
      border-radius: var(--radius-2xl) var(--radius-2xl) var(--radius-2xl) var(--radius-lg);
      line-height: var(--leading-relaxed);
      white-space: pre-wrap;
      color: var(--text-color);
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      box-shadow: var(--shadow-md);
      backdrop-filter: blur(20px);
      transition: var(--transition-all);
      position: relative;

      &:hover {
        box-shadow: var(--shadow-lg);
        border-color: var(--primary);
      }

      .cursor {
        animation: blink 1s infinite;
        color: var(--primary);
        font-weight: bold;
      }

      :deep(.think-block) {
        position: relative;
        padding: var(--space-4) var(--space-5) var(--space-4) var(--space-6);
        margin: var(--space-4) 0;
        color: var(--text-tertiary);
        font-style: italic;
        border-left: 4px solid var(--primary);
        background: var(--bg-secondary);
        border-radius: 0 var(--radius-xl) var(--radius-xl) 0;
        border: 1px solid var(--border-color);
        border-left: 4px solid var(--primary);
        box-shadow: var(--shadow-sm);
        opacity: 1;
        transform: translateX(0);
        transition: var(--transition-all);

        &::before {
          content: '💭 AI思考中';
          position: absolute;
          top: -12px;
          left: var(--space-4);
          padding: var(--space-1) var(--space-3);
          font-size: var(--text-xs);
          background: var(--primary);
          color: white;
          border-radius: var(--radius-full);
          font-style: normal;
          font-weight: 600;
          box-shadow: var(--shadow-sm);
        }

        &:not(:first-child) {
          animation: slideIn 0.3s ease forwards;
        }

        &:hover {
          background: var(--bg-tertiary);
          transform: translateX(4px);
        }
      }

      :deep(pre) {
        background: var(--neutral-900);
        padding: var(--space-5);
        border-radius: var(--radius-xl);
        overflow-x: auto;
        margin: var(--space-4) 0;
        border: 1px solid var(--border-color);
        box-shadow: var(--shadow-lg);
        position: relative;

        code {
          background: transparent;
          padding: 0;
          font-family: var(--font-family-mono);
          font-size: var(--text-sm);
          line-height: var(--leading-relaxed);
          tab-size: 2;
          color: var(--neutral-100);
        }

        &:hover {
          box-shadow: var(--shadow-xl);
        }
      }

      :deep(.hljs) {
        color: var(--neutral-100);
        background: transparent;
      }

      :deep(.hljs-keyword) {
        color: #ff79c6;
        font-weight: 600;
      }

      :deep(.hljs-built_in) {
        color: #8be9fd;
      }

      :deep(.hljs-type) {
        color: #bd93f9;
      }

      :deep(.hljs-string) {
        color: #f1fa8c;
      }

      :deep(.hljs-number) {
        color: #bd93f9;
      }

      :deep(.hljs-comment) {
        color: #6272a4;
        font-style: italic;
      }

      :deep(.hljs-function) {
        color: #50fa7b;
      }

      :deep(.hljs-literal) {
        color: #bd93f9;
      }

      :deep(.hljs-regexp) {
        color: #f1fa8c;
      }

      :deep(.hljs-subst) {
        color: var(--neutral-100);
      }

      :deep(.hljs-symbol) {
        color: #ffb86c;
      }

      :deep(.hljs-class) {
        color: #8be9fd;
      }

      :deep(.hljs-title) {
        color: #50fa7b;
        font-weight: 600;
      }

      :deep(.hljs-params) {
        color: #ffb86c;
      }

      :deep(.hljs-doctag) {
        color: #ff79c6;
      }

      :deep(.hljs-meta) {
        color: #6272a4;
      }

      :deep(.hljs-section) {
        color: #8be9fd;
      }

      :deep(.hljs-name) {
        color: #50fa7b;
      }

      :deep(.hljs-attribute) {
        color: #ffb86c;
      }

      :deep(.hljs-variable) {
        color: #f8f8f2;
      }
    }
  }
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
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

/* 表格样式 */
:deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: var(--space-4) 0;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

:deep(th),
:deep(td) {
  padding: var(--space-3) var(--space-4);
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

:deep(th) {
  background: var(--bg-secondary);
  font-weight: 600;
  color: var(--text-color);
}

:deep(td) {
  color: var(--text-secondary);
}

/* 引用样式 */
:deep(blockquote) {
  margin: var(--space-4) 0;
  padding: var(--space-4) var(--space-6);
  border-left: 4px solid var(--primary);
  background: var(--bg-secondary);
  border-radius: 0 var(--radius-lg) var(--radius-lg) 0;
  color: var(--text-secondary);
  font-style: italic;
}

/* 内联代码样式 */
:deep(code:not(pre code)) {
  background: var(--bg-secondary);
  color: var(--primary);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-base);
  font-family: var(--font-family-mono);
  font-size: 0.9em;
  border: 1px solid var(--border-color);
}

/* 列表样式 */
:deep(ul),
:deep(ol) {
  margin: var(--space-3) 0;
  padding-left: var(--space-6);
}

:deep(li) {
  margin: var(--space-2) 0;
  color: var(--text-secondary);
}

/* 链接样式 */
:deep(a) {
  color: var(--primary);
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: var(--transition-all);

  &:hover {
    border-bottom-color: var(--primary);
  }
}

/* 代码块复制按钮样式 */

.markdown-content {
  :deep(p) {
    margin: var(--space-3) 0;
    line-height: var(--leading-relaxed);

    &:first-child {
      margin-top: 0;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }

  :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
    margin: var(--space-4) 0 var(--space-2) 0;
    color: var(--text-color);
    font-weight: 600;

    &:first-child {
      margin-top: 0;
    }
  }

  :deep(h1) { font-size: var(--text-2xl); }
  :deep(h2) { font-size: var(--text-xl); }
  :deep(h3) { font-size: var(--text-lg); }

  :deep(strong) {
    font-weight: 600;
    color: var(--text-color);
  }

  :deep(em) {
    font-style: italic;
    color: var(--text-secondary);
  }

  :deep(.code-block-wrapper) {
    position: relative;
    margin: var(--space-4) 0;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-lg);

    .code-copy-button {
      position: absolute;
      top: var(--space-3);
      right: var(--space-3);
      background: var(--bg-secondary);
      border: 1px solid var(--border-color);
      color: var(--text-secondary);
      cursor: pointer;
      padding: var(--space-2);
      border-radius: var(--radius-lg);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: var(--transition-all);
      z-index: 10;
      width: 32px;
      height: 32px;

      &:hover {
        background: var(--primary);
        color: white;
        border-color: var(--primary);
        transform: scale(1.1);
      }

      .code-copy-icon {
        width: 16px;
        height: 16px;
      }
    }

    &:hover .code-copy-button {
      opacity: 1;
    }

    .copy-success-message {
      position: absolute;
      top: var(--space-3);
      right: var(--space-3);
      background: var(--success-500);
      color: white;
      padding: var(--space-2) var(--space-3);
      border-radius: var(--radius-lg);
      font-size: var(--text-xs);
      font-weight: 600;
      opacity: 0;
      transform: translateY(-10px);
      transition: var(--transition-all);
      pointer-events: none;
      z-index: 20;
      box-shadow: var(--shadow-md);

      &.visible {
        opacity: 1;
        transform: translateY(0);
      }
    }
  }
}
</style>
