import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Conversation, Message } from '@/types/api'
import { getConversations, getMessages, createConversation, sendMessage as sendMsg } from '@/api'

export const useChatStore = defineStore('chat', () => {
  const conversations = ref<Conversation[]>([])
  const currentConversation = ref<Conversation | null>(null)
  const messages = ref<Message[]>([])
  const loading = ref(false)

  const fetchConversations = async () => {
    const res = await getConversations()
    conversations.value = res.data.data || []
  }

  const fetchMessages = async (conversationId: number) => {
    loading.value = true
    try {
      const res = await getMessages(conversationId)
      messages.value = res.data.data || []
    } finally {
      loading.value = false
    }
  }

  const selectConversation = async (conv: Conversation) => {
    currentConversation.value = conv
    await fetchMessages(conv.id)
  }

  const newConversation = async () => {
    const res = await createConversation()
    const conv = res.data.data
    conversations.value.unshift(conv)
    currentConversation.value = conv
    messages.value = []
    return conv
  }

  const sendUserMessage = async (content: string) => {
    if (!currentConversation.value) {
      await newConversation()
    }
    
    const userMsg: Message = {
      id: Date.now(),
      conversationId: currentConversation.value!.id,
      requestId: '',
      senderId: 0,
      senderType: 1,
      messageType: 1,
      content,
      extraJson: '',
      createTime: new Date().toISOString()
    }
    messages.value.push(userMsg)

    loading.value = true
    try {
      const res = await sendMsg({
        content,
        conversationId: currentConversation.value!.id
      })
      const agentMsg = res.data.data
      if (Array.isArray(agentMsg)) {
        messages.value.push(...agentMsg)
      } else {
        messages.value.push(agentMsg)
      }
      return agentMsg
    } finally {
      loading.value = false
    }
  }

  return {
    conversations, currentConversation, messages, loading,
    fetchConversations, fetchMessages, selectConversation,
    newConversation, sendUserMessage
  }
})
