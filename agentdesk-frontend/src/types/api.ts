export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
  timestamp: number
}

export interface PageResult<T> {
  total: number
  rows: T[]
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
  user: UserInfo
}

export interface UserInfo {
  id: number
  userNo: string
  userName: string
  realName: string
  email: string
  phone: string
  department: string
  roleCode: string
  status: number
}

export interface Conversation {
  id: number
  conversationNo: string
  userId: number
  source: number
  status: number
  lastMessageTime: string
  createTime: string
}

export interface Message {
  id: number
  conversationId: number
  requestId: string
  senderId: number
  senderType: number // 1-USER 2-AGENT 3-HUMAN 4-SYSTEM
  messageType: number
  content: string
  extraJson: string
  createTime: string
}

export interface Ticket {
  id: number
  ticketNo: string
  ticketCategory: number
  priority: number
  status: number
  summary: string
  description: string
  source: number
  assignedGroup: string
  assignedUserId: number
  deadLine: string
  resolvedTime: string
  closedTime: string
  userId: number
  conversationId: number
  createTime: string
  updateTime: string
}

export interface TicketQuery {
  ticketNo?: string
  status?: number
  priority?: number
  ticketCategory?: number
  pageNum?: number
  pageSize?: number
}

export interface KnowledgeDoc {
  id: number
  docNo: string
  title: string
  category: string
  tags: string
  content: string
  summary: string
  sourceType: string
  status: string
  version: number
  createTime: string
  updateTime: string
}

export interface AgentRunLog {
  id: number
  requestId: string
  agentName: string
  runStatus: string
  inputPayload: string
  outputPayload: string
  confidence: number
  gateResult: string
  costTokens: number
  latencyMs: number
  createTime: string
}

export interface SendMessageRequest {
  content: string
  conversationId?: number
}

export interface CreateTicketRequest {
  summary: string
  description?: string
  ticketCategory: number
  priority: number
  conversationId?: number
}
