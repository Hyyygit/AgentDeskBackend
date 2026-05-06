<template>
  <div class="chat-container">
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <el-button type="primary" @click="startNewChat" :icon="Plus" style="width: 100%">
          新建对话
        </el-button>
      </div>
      <div class="conversation-list">
        <div
          v-for="conv in chatStore.conversations"
          :key="conv.id"
          :class="['conv-item', { active: chatStore.currentConversation?.id === conv.id }]"
          @click="selectConv(conv)"
        >
          <div class="conv-title text-ellipsis">{{ conv.conversationNo }}</div>
          <div class="conv-time">{{ formatTime(conv.lastMessageTime) }}</div>
        </div>
        <el-empty v-if="!chatStore.conversations.length" description="暂无会话" :image-size="60" />
      </div>
    </div>

    <div class="chat-main">
      <div class="chat-header" v-if="chatStore.currentConversation">
        <span>会话 {{ chatStore.currentConversation.conversationNo }}</span>
      </div>

      <div class="chat-messages" ref="msgContainer">
        <div v-if="!chatStore.currentConversation" class="chat-welcome">
          <h2>AgentDesk 智能助手</h2>
          <p>选择左侧会话或创建新对话开始交流</p>
        </div>

        <div v-for="msg in chatStore.messages" :key="msg.id" :class="['message', msg.senderType === 1 ? 'user-msg' : 'agent-msg']">
          <div class="msg-avatar">
            <el-avatar :icon="msg.senderType === 1 ? 'UserFilled' : 'Service'" :size="36" />
          </div>
          <div class="msg-content">
            <div class="msg-text" v-html="renderMarkdown(msg.content)" />
            <div class="msg-time">{{ formatTime(msg.createTime) }}</div>
            <div v-if="msg.senderType !== 1" class="msg-actions">
              <el-button text size="small" @click="copyText(msg.content)">复制</el-button>
              <el-button text size="small" @click="feedback(msg.id, 1)">👍</el-button>
              <el-button text size="small" @click="feedback(msg.id, 0)">👎</el-button>
            </div>
          </div>
        </div>

        <div v-if="chatStore.loading" class="message agent-msg">
          <div class="msg-avatar"><el-avatar icon="Service" :size="36" /></div>
          <div class="msg-content"><el-icon class="is-loading" :size="24"><Loading /></el-icon> 正在思考...</div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="3"
          placeholder="输入您的问题..."
          @keydown.enter.exact.prevent="handleSend"
          resize="none"
        />
        <el-button type="primary" :icon="Promotion" @click="handleSend" :loading="chatStore.loading" :disabled="!inputText.trim()">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { useChatStore } from '@/stores/chat'
import { ElMessage } from 'element-plus'
import { Plus, Promotion, Loading } from '@element-plus/icons-vue'
import { marked } from 'marked'
import 'highlight.js/styles/github.css'

const chatStore = useChatStore()
const inputText = ref('')
const msgContainer = ref<HTMLElement>()

marked.setOptions({ breaks: true })

const renderMarkdown = (text: string) => {
  if (!text) return ''
  return marked.parse(text) as string
}

const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const scrollToBottom = () => {
  nextTick(() => {
    if (msgContainer.value) {
      msgContainer.value.scrollTop = msgContainer.value.scrollHeight
    }
  })
}

const startNewChat = async () => {
  await chatStore.newConversation()
  scrollToBottom()
}

const selectConv = async (conv: any) => {
  await chatStore.selectConversation(conv)
  scrollToBottom()
}

const handleSend = async () => {
  const text = inputText.value.trim()
  if (!text || chatStore.loading) return
  inputText.value = ''
  try {
    await chatStore.sendUserMessage(text)
    scrollToBottom()
  } catch (e: any) {
    ElMessage.error('发送失败')
  }
}

const copyText = (text: string) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制')
  })
}

const feedback = (_messageId: number, _rating: number) => {
  ElMessage.success('感谢反馈')
}

watch(() => chatStore.messages, scrollToBottom, { deep: true })

chatStore.fetchConversations()
</script>

<style scoped>
.chat-container { display: flex; height: 100%; }
.chat-sidebar { width: 280px; background: #fff; border-right: 1px solid #e4e7ed; display: flex; flex-direction: column; }
.sidebar-header { padding: 12px; }
.conversation-list { flex: 1; overflow-y: auto; padding: 8px; }
.conv-item { padding: 12px; border-radius: 8px; cursor: pointer; margin-bottom: 4px; }
.conv-item:hover { background: #f5f7fa; }
.conv-item.active { background: #ecf5ff; }
.conv-title { font-size: 14px; color: #303133; }
.conv-time { font-size: 12px; color: #909399; margin-top: 4px; }

.chat-main { flex: 1; display: flex; flex-direction: column; background: #fff; }
.chat-header { padding: 12px 20px; border-bottom: 1px solid #e4e7ed; font-size: 16px; font-weight: 500; }
.chat-messages { flex: 1; overflow-y: auto; padding: 20px; }
.chat-welcome { text-align: center; padding-top: 100px; color: #909399; }
.chat-welcome h2 { color: #303133; margin-bottom: 8px; }

.message { display: flex; margin-bottom: 20px; gap: 12px; }
.user-msg { flex-direction: row-reverse; }
.msg-avatar { flex-shrink: 0; }
.msg-content { max-width: 70%; }
.msg-text { padding: 12px 16px; border-radius: 8px; line-height: 1.6; font-size: 14px; word-break: break-word; }
.user-msg .msg-text { background: #409eff; color: #fff; }
.agent-msg .msg-text { background: #f5f7fa; color: #303133; }
.msg-text :deep(pre) { background: #282c34; color: #abb2bf; padding: 12px; border-radius: 4px; overflow-x: auto; margin: 8px 0; }
.msg-text :deep(code) { font-family: 'Fira Code', monospace; font-size: 13px; }
.msg-text :deep(p) { margin: 4px 0; }
.msg-time { font-size: 12px; color: #c0c4cc; margin-top: 4px; }
.user-msg .msg-time { text-align: right; }
.msg-actions { margin-top: 4px; }

.chat-input { padding: 12px 20px; border-top: 1px solid #e4e7ed; display: flex; gap: 12px; align-items: flex-end; }
.chat-input .el-textarea { flex: 1; }
</style>
