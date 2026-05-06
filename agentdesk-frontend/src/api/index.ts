import request from '@/utils/request'
import type { ApiResponse, LoginRequest, LoginResponse, UserInfo, Conversation, Message, Ticket, TicketQuery, KnowledgeDoc, AgentRunLog, SendMessageRequest, CreateTicketRequest } from '@/types/api'

// Auth
export const loginApi = (data: LoginRequest) => request.post<ApiResponse<LoginResponse>>('/user/login', data)
export const registerApi = (data: any) => request.post<ApiResponse<any>>('/user/register', data)
export const refreshTokenApi = (refreshToken: string) => request.post<ApiResponse<LoginResponse>>('/user/refresh', { refreshToken })

// User
export const getUserProfile = () => request.get<ApiResponse<UserInfo>>('/user/profile')
export const updateProfile = (data: any) => request.put<ApiResponse<any>>('/user/profile', data)
export const changePassword = (data: any) => request.put<ApiResponse<any>>('/user/password', data)

// Conversations
export const createConversation = () => request.post<ApiResponse<Conversation>>('/conversations')
export const getConversations = () => request.get<ApiResponse<Conversation[]>>('/conversations')
export const getConversation = (id: number) => request.get<ApiResponse<Conversation>>(`/conversations/${id}`)
export const sendMessage = (data: SendMessageRequest) => request.post<ApiResponse<Message>>('/conversations/messages', data)
export const getMessages = (conversationId: number) => request.get<ApiResponse<Message[]>>(`/conversations/${conversationId}/messages`)
export const closeConversation = (id: number) => request.put<ApiResponse<any>>(`/conversations/${id}/close`)
export const sendMessageFeedback = (messageId: number, data: any) => request.post<ApiResponse<any>>(`/conversations/${messageId}/feedback`, data)

// Tickets
export const createTicket = (data: CreateTicketRequest) => request.post<ApiResponse<Ticket>>('/tickets', data)
export const getTickets = (params: TicketQuery) => request.get<ApiResponse<{ total: number; rows: Ticket[] }>>('/tickets', { params })
export const getTicket = (id: number) => request.get<ApiResponse<Ticket>>(`/tickets/${id}`)
export const updateTicketStatus = (id: number, data: any) => request.put<ApiResponse<Ticket>>(`/tickets/${id}/status`, data)
export const assignTicket = (id: number, data: any) => request.put<ApiResponse<Ticket>>(`/tickets/${id}/assign`, data)
export const getTicketTimeline = (id: number) => request.get<ApiResponse<any[]>>(`/tickets/${id}/timeline`)
export const getTicketStats = () => request.get<ApiResponse<any>>('/tickets/stats')

// Knowledge
export const getKnowledgeDocs = (params: any) => request.get<ApiResponse<{ total: number; rows: KnowledgeDoc[] }>>('/knowledge/docs', { params })
export const getKnowledgeDoc = (id: number) => request.get<ApiResponse<KnowledgeDoc>>(`/knowledge/docs/${id}`)
export const createKnowledgeDoc = (data: any) => request.post<ApiResponse<KnowledgeDoc>>('/knowledge/docs', data)
export const updateKnowledgeDoc = (id: number, data: any) => request.put<ApiResponse<KnowledgeDoc>>(`/knowledge/docs/${id}`, data)
export const deleteKnowledgeDoc = (id: number) => request.delete<ApiResponse<any>>(`/knowledge/docs/${id}`)
export const publishKnowledgeDoc = (id: number) => request.put<ApiResponse<any>>(`/knowledge/docs/${id}/publish`)
export const searchKnowledge = (params: any) => request.get<ApiResponse<KnowledgeDoc[]>>('/knowledge/search', { params })

// Orchestrator
export const getAgentRuns = (params: any) => request.get<ApiResponse<{ total: number; rows: AgentRunLog[] }>>('/orchestrator/runs', { params })
export const getAgentRunTrace = (requestId: string) => request.get<ApiResponse<AgentRunLog[]>>(`/orchestrator/runs/${requestId}`)

// Audit
export const getAuditLogs = (params: any) => request.get<ApiResponse<{ total: number; rows: AgentRunLog[] }>>('/audit/agent-runs', { params })

// Admin
export const getDashboardStats = () => request.get<ApiResponse<any>>('/admin/dashboard')
export const getSystemHealth = () => request.get<ApiResponse<any>>('/admin/health')

// Notifications
export const getNotifications = () => request.get<ApiResponse<any[]>>('/notifications')
export const markNotificationRead = (id: number) => request.put<ApiResponse<any>>(`/notifications/${id}/read`)
export const getUnreadCount = () => request.get<ApiResponse<number>>('/notifications/unread-count')
