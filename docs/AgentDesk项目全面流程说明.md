# AgentDesk 智能工单系统 — 全面流程说明

> 本文档面向初级开发者，从**前端发起一个请求**开始，详细讲解后端每一步的处理流程、涉及到的模块和组件。

---

## 目录

- [1. 项目概览](#1-项目概览)
- [2. 技术栈速览](#2-技术栈速览)
- [3. 项目模块结构](#3-项目模块结构)
- [4. 基础设施与部署](#4-基础设施与部署)
- [5. 全局基础设施：请求如何进入系统](#5-全局基础设施请求如何进入系统)
  - [5.1 Nginx 反向代理](#51-nginx-反向代理)
  - [5.2 Spring Cloud Gateway 网关](#52-spring-cloud-gateway-网关)
  - [5.3 JWT 鉴权流程](#53-jwt-鉴权流程)
  - [5.4 追踪与日志](#54-追踪与日志)
- [6. 流程一：用户登录与注册](#6-流程一用户登录与注册)
  - [6.1 注册流程](#61-注册流程)
  - [6.2 登录流程](#62-登录流程)
  - [6.3 Token 刷新](#63-token-刷新)
- [7. 流程二：用户发起一次智能对话（核心主流程）](#7-流程二用户发起一次智能对话核心主流程)
  - [7.1 创建会话](#71-创建会话)
  - [7.2 发送消息](#72-发送消息)
  - [7.3 AI 智能编排流水线（Orchestrator Pipeline）](#73-ai-智能编排流水线orchestrator-pipeline)
    - [7.3.1 第一步：分诊 Agent（TriageAgent）](#731-第一步分诊-agenttriageagent)
    - [7.3.2 第二步：知识检索 Agent（KnowledgeAgent）](#732-第二步知识检索-agentknowledgeagent)
    - [7.3.3 第三步：决策 Agent（DecisionAgent）](#733-第三步决策-agentdecisionagent)
    - [7.3.4 第四步：工具执行 Agent（ToolAgent）](#734-第四步工具执行-agenttoolagent)
    - [7.3.5 第五步：响应生成 Agent（ResponseAgent）](#735-第五步响应生成-agentresponseagent)
  - [7.4 流式消息返回（SSE）](#74-流式消息返回sse)
  - [7.5 整个对话流程的完整时序图](#75-整个对话流程的完整时序图)
- [8. 流程三：工单管理](#8-流程三工单管理)
  - [8.1 工单创建](#81-工单创建)
  - [8.2 工单状态流转](#82-工单状态流转)
  - [8.3 工单分配、优先级、评论](#83-工单分配优先级评论)
  - [8.4 工单统计](#84-工单统计)
  - [8.5 Redis 在工单模块的使用](#85-redis-在工单模块的使用)
- [9. 流程四：知识库管理](#9-流程四知识库管理)
  - [9.1 知识文档管理](#91-知识文档管理)
  - [9.2 知识草稿与审批](#92-知识草稿与审批)
  - [9.3 知识检索（供 AI Agent 调用）](#93-知识检索供-ai-agent-调用)
  - [9.4 全文检索](#94-全文检索)
- [10. 流程五：通知服务](#10-流程五通知服务)
- [11. 流程六：审计日志](#11-流程六审计日志)
- [12. 流程七：运营管理后台](#12-流程七运营管理后台)
- [13. 微服务间通信详解](#13-微服务间通信详解)
  - [13.1 OpenFeign 声明式调用](#131-openfeign-声明式调用)
  - [13.2 Feign 请求拦截器](#132-feign-请求拦截器)
  - [13.3 服务间内部 Controller](#133-服务间内部-controller)
  - [13.4 异常处理](#134-异常处理)
- [14. 通用模块详解](#14-通用模块详解)
  - [14.1 common-core：核心基础](#141-common-core核心基础)
  - [14.2 common-web：Web 层通用](#142-common-webweb-层通用)
  - [14.3 common-security：安全认证](#143-common-security安全认证)
  - [14.4 common-redis：Redis 工具](#144-common-redisredis-工具)
  - [14.5 common-ai：大模型调用](#145-common-ai大模型调用)
  - [14.6 common-log：日志追踪](#146-common-log日志追踪)
  - [14.7 common-feign：服务调用](#147-common-feign服务调用)
  - [14.8 common-tool：提示词模板](#148-common-tool提示词模板)
- [15. 数据库表设计总览](#15-数据库表设计总览)
- [16. Docker 部署架构](#16-docker-部署架构)
- [17. 项目关键设计模式](#17-项目关键设计模式)

---

## 1. 项目概览

**AgentDesk** 是一个**AI 驱动的智能工单系统**。它允许用户通过网页对话界面提交问题，后端通过多个 AI Agent（智能体）自动分析、检索知识库、做出决策，最终自动回复或创建工单转交人工处理。

项目采用 **微服务架构**，包含 **8 个业务服务** + **1 个网关**，服务之间通过 **OpenFeign** 进行通信。

---

## 2. 技术栈速览

| 类别 | 技术 | 用途 |
|------|------|------|
| 框架 | Spring Boot 3.5.13 | 基础框架 |
| 微服务 | Spring Cloud 2025.0.2 + Gateway | 服务注册、路由、负载均衡 |
| 数据库 | MySQL 8.0 | 主数据存储 |
| ORM | MyBatis-Plus 3.5.16 | 数据库操作 |
| 缓存 | Redis 7 + Redisson 3.34.0 | 缓存、序列号生成 |
| 认证 | Spring Security + JJWT 0.12.6 | JWT 令牌认证 |
| 服务调用 | OpenFeign 4.3.2 | 微服务间 HTTP 调用 |
| AI/LLM | DeepSeek API（通过 OkHttp） | 大模型对话 |
| 分页 | PageHelper 5.3.2 | 分页查询 |
| 工具 | Hutool 5.8.40 | 通用工具库 |
| API 文档 | Knife4j 4.5.0 | Swagger UI |
| 前端 | Vue 3 + TypeScript + Vite | Web 用户界面 |
| 反向代理 | Nginx | 前端静态资源 + API 代理 |
| 部署 | Docker Compose | 容器化部署 |

---

## 3. 项目模块结构

```
AgentDeskBackend (根项目)
│
├── agentdesk-common/          ← 通用库（被所有服务依赖）
│   ├── common-core/           ← 基础实体、枚举、异常
│   ├── common-web/            ← AjaxResult、BaseController、分页
│   ├── common-mybatis/        ← MyBatis-Plus 配置
│   ├── common-redis/          ← Redis 工具类
│   ├── common-feign/          ← Feign 配置、拦截器
│   ├── common-security/       ← JWT 工具、Spring Security
│   ├── common-ai/             ← 大模型（DeepSeek）调用封装
│   ├── common-rag/            ← RAG 检索增强（预留）
│   ├── common-tool/           ← 提示词模板
│   └── common-log/            ← TraceId、日志切面
│
├── agentdesk-api/             ← API 契约（Feign 接口 + DTO）
│   ├── api-user/              ← 用户服务接口
│   ├── api-ticket/            ← 工单服务接口
│   ├── api-conversation/      ← 会话服务接口
│   ├── api-knowledge/         ← 知识库服务接口
│   ├── api-orchestrator/      ← 编排服务接口
│   ├── api-notification/      ← 通知服务接口
│   └── api-audit/             ← 审计服务接口
│
├── agentdesk-service/         ← 业务服务（可独立运行的 Spring Boot 应用）
│   ├── gateway-service/       ← API 网关（端口 8080）
│   ├── user-service/          ← 用户服务（端口 9001）
│   ├── ticket-service/        ← 工单服务（端口 9000）
│   ├── conversation-service/  ← 会话服务（端口 9002）
│   ├── knowledge-service/     ← 知识库服务（端口 9003）
│   ├── agent-orchestrator-service/ ← AI 编排服务（端口 9005）
│   ├── audit-log-service/     ← 审计日志服务（端口 9004）
│   ├── notification-service/  ← 通知服务（端口 9006）
│   ├── ops-admin-service/     ← 运营管理服务（端口 9007）
│   └── tool-gateway-service/  ← 工具网关服务（预留）
│
├── agentdesk-frontend/        ← Vue 3 前端项目
├── datasource/mysql/          ← SQL 建表语句
└── deploy/                    ← Docker Compose + Nginx 配置
```

---

## 4. 基础设施与部署

系统依赖以下基础组件：

| 组件 | 容器名 | 端口 | 用途 |
|------|--------|------|------|
| MySQL 8.0 | agentdesk-mysql | 3307 | 主数据库 |
| Redis 7 | agentdesk-redis | 6379 | 缓存、分布式序列号 |
| Nginx | agentdesk-frontend | 80 | 前端 + API 反向代理 |

所有服务通过 Docker Compose 统一编排（详见第 16 节）。

---

## 5. 全局基础设施：请求如何进入系统

### 5.1 Nginx 反向代理

前端 Vue 应用打包成静态文件，由 Nginx 托管。用户访问 `http://localhost` 时：

```
浏览器 → Nginx (端口 80) → 判断请求路径
    ├── / (前端页面)  → 返回 Vue 静态文件
    └── /api/ (后端接口) → 转发到 gateway-service:8080
```

关键配置（`deploy/nginx.conf`）：
- `/api/` 路径代理到 `gateway-service:8080`
- `/api/conversations/messages/stream` 单独配置，支持 SSE 长连接

### 5.2 Spring Cloud Gateway 网关

当请求到达网关（`gateway-service`，端口 8080），网关根据 **路径前缀** 将请求路由到对应的后端服务：

| 路径前缀 | 路由目标 | 说明 |
|----------|----------|------|
| `/api/user/**` | user-service:9001 | 用户认证 |
| `/api/conversations/**` | conversation-service:9002 | 会话管理 |
| `/api/tickets/**` | ticket-service:9000 | 工单管理 |
| `/api/knowledge/**` | knowledge-service:9003 | 知识库 |
| `/api/orchestrator/**` | orchestrator-service:9005 | AI 编排 |
| `/api/audit/**` `/api/log/**` | audit-log-service:9004 | 审计日志 |

### 5.3 JWT 鉴权流程

网关中有一个 **全局过滤器** `JwtAuthFilter`（优先级 `order=-100`），它在请求到达后端服务之前执行：

```
1. 检查请求路径是否在白名单中（登录、注册、刷新Token、Swagger文档）
   ├── 是 → 直接放行
   └── 否 → 进入第 2 步

2. 从请求头中取出 "Authorization: Bearer <token>"
   ├── 没有 → 返回 401 未授权
   └── 有 → 进入第 3 步

3. 解析 JWT Token，提取 userId、username、tenantId
   ├── 解析成功 → 将用户信息注入请求头 (X-User-Id, X-Username, X-Tenant-Id)
   └── 解析失败 → 返回 401

4. 放行请求到后端服务
```

**各个服务内部** 还有一个 `JwtAuthenticationFilter`（Spring Security 的 `OncePerRequestFilter`），它会再次解析 Token 并将用户信息设置到 `SecurityContextHolder` 中。

### 5.4 追踪与日志

**TraceIdFilter** 是一个 Servlet 过滤器（优先级最高），每个请求到达时：
1. 生成唯一 `traceId`（通过 Hutool 的 `IdUtil.fastSimpleUUID()`）
2. 将 `traceId` 存入 MDC（日志上下文）
3. 响应的 Header 中也会带上 `X-Trace-Id`

**WebLogAspect** 是一个 AOP 切面，拦截所有 Controller 方法，记录：
- 请求信息：URL、HTTP 方法、来源 IP、类名+方法名、请求参数
- 响应信息：返回结果（截断到 1000 字符）、耗时

这使得你可以在日志中通过 `traceId` 追踪一个请求的完整链路。

---

## 6. 流程一：用户登录与注册

> **涉及模块**：`common-security`、`common-redis`、`user-service`

### 6.1 注册流程

```
前端 → POST /api/user/register
     ↓
网关 → 该路径在白名单中，跳过 JWT 校验，直接放行
     ↓
user-service AuthController.register()
     ↓
UserServiceImpl.register()
     ├── 1. 检查用户名是否已存在（UserMapper.selectByUsername）
     ├── 2. 检查邮箱是否已存在（UserMapper.selectByEmail）
     ├── 3. 密码使用 BCrypt 加密（PasswordEncoder.encode）
     ├── 4. 默认角色设为 "USER"
     ├── 5. 生成用户编号（UUID 去掉横线）
     ├── 6. 用户状态设为 1（正常）
     └── 7. 调用 MyBatis-Plus 的 save() 存入 user 表
     ↓
返回 AjaxResult.success("注册成功")
```

### 6.2 登录流程

```
前端 → POST /api/user/login { username, password }
     ↓
网关 → 白名单路径，放行
     ↓
user-service AuthController.login()
     ↓
UserServiceImpl.login()
     ├── 1. 根据用户名查询用户（UserMapper.selectByUsername）
     ├── 2. 验证密码（passwordEncoder.matches）
     └── 3. 返回 UserPO 对象
     ↓
AuthController 组装 AuthUser 对象
     ↓
JwtUtils.generateAccessToken()
     ├── 将 userId、username、roleCode 存入 JWT Claims
     ├── 设置过期时间（默认 30 分钟）
     └── 使用 HMAC-SHA256 签名
     ↓
JwtUtils.generateRefreshToken()
     ├── 将 userId 存入 JWT Claims
     ├── 设置过期时间（默认 7 天）
     └── 用于后续刷新 access token
     ↓
返回 LoginResponse = accessToken + refreshToken + 过期时间 + 用户信息
```

### 6.3 Token 刷新

```
前端 → POST /api/user/refresh { refreshToken }
     ↓
AuthController.refresh()
     ↓
UserServiceImpl.refreshToken()
     ├── 解析 refreshToken 获取 userId
     ├── 查数据库验证用户是否存在
     ├── 重新构建 AuthUser
     └── 调用 JwtUtils.generateAccessToken() 生成新 Token
     ↓
返回新的 accessToken
```

---

## 7. 流程二：用户发起一次智能对话（核心主流程）

> 这是整个系统最核心的流程，涉及 `conversation-service`、`agent-orchestrator-service`、`knowledge-service`、`ticket-service`、`notification-service`、`audit-log-service` 等多个服务。

### 7.1 创建会话

```
前端 → POST /api/conversations
     ↓
网关 → 解析 JWT，将 X-User-Id 注入请求头，路由到 conversation-service:9002
     ↓
ConversationController.createConversation()
     ├── 从 UserContext.getCurrentUserId() 获取当前用户 ID
     └── 调用 conversationService.createConversation(userId, source=1)
     ↓
ConversationServiceImpl.createConversation()
     ├── 1. 生成会话编号（"CONV" + UUID）
     ├── 2. 创建 ConversationPO 对象：
     │      - userId = 当前用户
     │      - source = 1 (网页)
     │      - status = 1 (活跃)
     │      - lastMessageTime = 当前时间
     ├── 3. 调用 MyBatis-Plus 的 save() 存入 conversation 表
     └── 4. 返回 ConversationPO
     ↓
转换为 ConversationVO 返回给前端
```

### 7.2 发送消息

这是最关键的入口，用户输入消息后触发整个 AI 流水线：

```
前端 → POST /api/conversations/messages
       { conversationId: 123, content: "我的电脑连不上网了" }
     ↓
网关 → JWT 校验，注入用户信息
     ↓
ConversationController.sendMessage()
     ├── 调用 conversationService.sendMessage(request)
     ↓
ConversationServiceImpl.sendMessage()
     ├── 1. 验证会话存在且属于当前用户
     ├── 2. 生成 requestId（UUID）
     ├── 3. 保存用户消息到 conversation_message 表
     │      - senderType = 1 (USER)
     │      - messageType = 1 (文本)
     │      - content = 用户输入
     ├── 4. 拼装 OrchestrateRequest：
     │      - content = "我的电脑连不上网了"
     │      - conversationId = 123
     │      - userId = 当前用户
     │      - requestId = 生成的 UUID
     ├── 5. 通过 Feign 调用 orchestrator-service
     │      OrchestratorFeignClient.orchestrate(request)
     ↓
     [进入 AI 智能编排流水线，详见 7.3]
     ↓
6. 收到 OrchestrateResponse（包含 AI 回复）
     ├── 7. 保存 AI 回复消息到 conversation_message 表
     │      - senderType = 2 (AGENT)
     └── 8. 更新会话的 lastMessageTime
     ↓
返回 AI 回复消息给前端
```

### 7.3 AI 智能编排流水线（Orchestrator Pipeline）

> 这是系统的 **核心大脑**，由 `agent-orchestrator-service` 实现。

```
OrchestratorServiceImpl.orchestrate(request)
     ↓
CoordinatorAgent.orchestrate(request)
     ├── 记录开始时间
     ├── 如果没有 requestId，生成一个
     │
     ├── Step 1: TriageAgent.triage(userMessage)
     ├── Step 2: KnowledgeAgent.retrieve(userMessage, category)
     ├── Step 3: DecisionAgent.decide(triageResult, knowledgeResult, userMessage)
     ├── Step 4: ToolAgent.execute(action, triageResult, userId, conversationId)
     │           （仅当 action 是 CREATE_TICKET 或 HUMAN_HANDOFF 时执行）
     ├── Step 5: ResponseAgent.generateResponse(decision, knowledge, tool)
     │
     └── 如果出现任何异常 → 返回降级回复："抱歉，系统处理您的请求时遇到了问题..."
```

每一步执行后，`AgentTraceRecorder` 都会记录日志（存到 `agent_run_log` 表），包含 Agent 名称、输入输出、置信度、耗时等。

#### 7.3.1 第一步：分诊 Agent（TriageAgent）

**作用**：理解用户问题，进行分类和优先级判断。

```
TriageAgent.triage(userMessage, conversationHistory)
     ↓
构建 System Prompt（告诉 AI 它的角色和任务）
     ↓
调用 DeepSeek API（通过 AbstractAgent.callLLM()）
     ↓
DeepSeekLLMService.chat()
     ├── 构建 ChatRequest（model, messages, temperature, maxTokens）
     ├── 通过 OkHttp 发送 POST 请求到 DeepSeek API
     ├── 解析 JSON 响应
     └── 返回 AI 文本回复
     ↓
将 AI 返回的 JSON 解析为 TriageResult：
     { "category": "NETWORK_FAILURE",
       "priority": "P2",
       "summary": "用户电脑无法连接网络",
       "requiresTicket": true,
       "isSensitive": false,
       "confidence": 0.85 }
     ↓
如果 AI 调用失败 → 返回降级结果：
     category="GENERAL_SUPPORT", priority="P3", confidence=0.5
```

**依赖的外部服务**：无（纯 LLM 调用）

#### 7.3.2 第二步：知识检索 Agent（KnowledgeAgent）

**作用**：从知识库中搜索相关文档，提取最佳答案。

```
KnowledgeAgent.retrieve(userMessage, category)
     ↓
通过 Feign 调用 knowledge-service 的内部接口：
     KnowledgeFeignClient.retrieve(searchRequest)
          { keyword: "我的电脑连不上网了", category: "NETWORK_FAILURE" }
     ↓
knowledge-service KnowledgeInternalController.retrieve()
     ↓
KnowledgeDocServiceImpl.search()
     ├── 构建 LambdaQueryWrapper（按 keyword 和 category 过滤）
     ├── 返回匹配的知识文档列表（KnowledgeDocDTO）
     ↓
回到 KnowledgeAgent，如果有结果：
     ├── 1. 将文档拼接成文本
     ├── 2. 再次调用 DeepSeek API，让 AI 从文档中提取最相关的答案
     └── 3. 设置 relevanceScore=0.8, hasAnswer=true
     ↓
没有结果：
     ├── relevanceScore=0.0, hasAnswer=false, bestAnswer=null
```

**依赖的外部服务**：`knowledge-service`（通过 Feign）

#### 7.3.3 第三步：决策 Agent（DecisionAgent）

**作用**：综合分诊和知识检索的结果，决定下一步行动。

```
DecisionAgent.decide(triageResult, knowledgeResult, userMessage)
     ↓
规则判断（不依赖 AI 的情况）：
     ├── isSensitive=true → 直接 HUMAN_HANDOFF（避免处理敏感信息）
     ├── confidence<0.6   → 直接 HUMAN_HANDOFF（AI 不太确定）
     └── hasAnswer=true && relevanceScore>0.7 → 直接 AUTO_RESOLVE
     ↓
如果以上规则都不匹配 → 调用 DeepSeek API 决策
     ↓
返回 DecisionResult，可能的 action：
     ├── "AUTO_RESOLVE"   → 知识库有答案，直接回复用户
     ├── "CREATE_TICKET"  → 需要创建工单
     └── "HUMAN_HANDOFF"  → 需要转人工处理
```

**依赖的外部服务**：无（纯规则判断 + LLM 调用）

#### 7.3.4 第四步：工具执行 Agent（ToolAgent）

**作用**：当决策为 CREATE_TICKET 时，真正去创建工单。

```
ToolAgent.execute(action, triageResult, userId, conversationId)
     ↓
仅当 action="CREATE_TICKET" 时执行：
     ├── 1. 将分类名称映射为数字编号（如 NETWORK_FAILURE → 3）
     ├── 2. 将优先级映射为数字编号（如 P2 → 2）
     ├── 3. 构造 TicketCreateRequest：
     │      - summary = triageResult.summary
     │      - description = "Auto-created by AI Agent"
     │      - ticketCategory = 映射后的分类编号
     │      - priority = 映射后的优先级
     │      - conversationId = 当前会话ID
     ├── 4. 通过 Feign 调用 ticket-service 创建工单
     │      ticketFeignClient.createTicket(request)
     └── 5. 返回 ToolResult（含 ticketId, ticketNo）
     ↓
如果 Feign 调用失败 → 返回 ToolResult(success=false, errorMessage)
```

**依赖的外部服务**：`ticket-service`（通过 Feign）

#### 7.3.5 第五步：响应生成 Agent（ResponseAgent）

**作用**：根据决策结果和工具执行结果，生成给用户的自然语言回复。

```
ResponseAgent.generateResponse(decision, knowledge, tool)
     ↓
构建 System Prompt（告诉 AI 如何组织回复语言）
     ↓
构建 User Prompt（包含：action、知识库答案、工单号、决策原因）
     ↓
调用 DeepSeek API 生成回复
     ↓
如果 AI 调用失败 → 使用降级回复：
     - 创了工单 → "已为您创建工单 TK20260101XXXXX，我们的团队将尽快处理"
     - 知识库有答案 → 直接用知识库答案
     - 其他 → "收到您的消息，我们正在为您处理。如有紧急问题，请联系人工客服。"
```

**依赖的外部服务**：无（纯 LLM 调用）

### 7.4 流式消息返回（SSE）

除了普通的同步调用，系统还支持 **SSE（Server-Sent Events）流式返回**：

```
前端 → GET /api/conversations/messages/stream?conversationId=123&content=...
     ↓
ConversationController.streamMessages()
     ├── 创建 SseEmitter(超时时间: 5分钟)
     └── 调用 conversationService.sendStreamMessage()
     ↓
发送初始事件 → orchestrator → 收到结果 → 发送结果事件 → 完成
```

Nginx 对此路径做了特殊配置：关闭缓冲、关闭缓存、设置为 HTTP 1.1 长连接。

### 7.5 整个对话流程的完整时序图

```
用户浏览器                    Nginx              Gateway        Conv-Service    Orchestrator    Knowledge-Svc    Ticket-Svc    DeepSeek-API
    │                          │                    │                │               │               │              │              │
    │──POST /api/conversations─────────────────────→│                │               │               │              │              │
    │                          │                    │──路由────────→│               │               │              │              │
    │                          │                    │               │──createConv──→│               │              │              │
    │←────────────会话创建成功─────────────────────────────────────│               │               │              │              │
    │                          │                    │               │               │               │              │              │
    │──POST /api/conversations/messages─────────────→│                │               │               │              │              │
    │                          │                    │──路由────────→│               │               │              │              │
    │                          │                    │               │──sendMessage─→│               │              │              │
    │                          │                    │               │               │──Feign调用───→│              │              │
    │                          │                    │               │               │──orchestrate→│              │              │
    │                          │                    │               │               │               │              │              │
    │                          │                    │               │               │──Triage───────│──────────────│──────────────│──DeepSeek──→
    │                          │                    │               │               │←──TriageResult│              │              │              │
    │                          │                    │               │               │               │              │              │
    │                          │                    │               │               │──Knowledge────│──Feign检索──→│              │
    │                          │                    │               │               │←──KnowledgeRs─│              │              │
    │                          │                    │               │               │               │              │              │
    │                          │                    │               │               │──Decision─────│──────────────│──────────────│──DeepSeek──→
    │                          │                    │               │               │←──DecisionRs─-│              │              │              │
    │                          │                    │               │               │               │              │              │
    │                          │                    │               │               │──Tool─────────│──────────────│──Feign建单──→│
    │                          │                    │               │               │←──ToolResult──│              │              │
    │                          │                    │               │               │               │              │              │
    │                          │                    │               │               │──Response─────│──────────────│──────────────│──DeepSeek──→
    │                          │                    │               │               │←──Response────│              │              │
    │                          │                    │               │←──OrchestrateResponse         │              │              │
    │                          │                    │               │──保存AI回复──→│               │              │              │
    │←────────────AI回复消息──────────────────────────────────────────────────────────│               │              │              │
```

---

## 8. 流程三：工单管理

> **涉及模块**：`ticket-service`、`common-redis`、`notification-service`

### 8.1 工单创建

工单可以由**用户直接创建**，也可以由 **AI Agent 自动创建**：

**用户直接创建**：
```
前端 → POST /api/tickets/
       { summary, description, ticketCategory, priority }
     ↓
TicketController.createTicket()
     ↓
TicketServiceImpl.createTicket()
     ├── 1. 调用 generateTicketNo() 生成工单号
     │      - 格式："TK" + 日期(yyyyMMdd) + 5位序号
     │      - 序号通过 Redis 自增生成
     │      - 如果 Redis 不可用，降级为时间戳取模
     ├── 2. 创建 TicketPO（status=新建, source=AI Agent）
     ├── 3. save() 存入数据库
     ├── 4. 记录操作日志（TicketActionLog）："创建工单"
     └── 5. 通过 Feign 发送通知给用户
     ↓
返回创建的工单信息
```

**AI Agent 自动创建**：
```
orchestrator-service 的 ToolAgent
     ↓
通过 TicketFeignClient.createTicket() 调用
     ↓
ticket-service 的 TicketInternalController.createTicket()
     ├── 从请求头获取 X-User-Id
     └── 调用相同的 TicketServiceImpl.createTicket()
```

### 8.2 工单状态流转

工单有 **7 种状态**，状态转换有严格的规则：

```
NEW(新建) ──→ TRIAGED(已分诊) ──→ DECIDED(已决策) ──→ IN_PROGRESS(处理中)
                                                             │
                                    ┌────────────────────────┤
                                    ↓                        ↓
                           WAITING_HUMAN(等待人工)    RESOLVED(已解决) ──→ CLOSED(已关闭)
```

**只允许以下转换**（TicketServiceImpl.VALID_TRANSITIONS）：
- 1→2（新建→分诊）、2→3（分诊→决策）、3→4（决策→处理中）
- 4→5（处理中→等待人工）、5→6（等待人工→解决）
- 4→6（处理中→直接解决）、6→7（解决→关闭）

任何不在此列表的状态转换都会被 `updateStatus()` 拒绝。

### 8.3 工单分配、优先级、评论

```
PUT /api/tickets/{ticketId}/assign    → 分配工单给某个人或某个组
PUT /api/tickets/{ticketId}/priority  → 修改优先级
POST /api/tickets/{ticketId}/comments → 添加评论（本质是创建一条 TicketActionLog）
GET /api/tickets/{ticketId}/timeline  → 查看工单操作时间线（按时间升序）
GET /api/tickets/stats                → 工单统计（各状态的工单数量）
```

所有操作都会：
1. 修改数据库对应字段
2. 记录 `ticket_action_log`
3. 尝试通过 Feign 发送通知（失败不影响主流程）

### 8.4 工单统计

`TicketServiceImpl.getStats()` 遍历所有 `TicketStatusEnum`，统计每个状态的工单数量，返回 Map：
```
{ "新建": 5, "已分诊": 2, "已决策": 1, "处理中": 3, "等待人工": 1, "已解决": 10, "已关闭": 20 }
```

### 8.5 Redis 在工单模块的使用

**工单号生成** 使用 Redis 自增（`redisUtils.increment()`）：
- Key 格式：`ticket:seq:20260506`
- 每天一个 Key，自动过期（86400秒）
- 如果 Redis 不可用，降级使用时间戳取模

---

## 9. 流程四：知识库管理

> **涉及模块**：`knowledge-service`

### 9.1 知识文档管理

知识文档（`knowledge_doc`）是知识库的核心数据。支持：

```
GET    /api/knowledge/docs/{id}         → 查询单个文档
GET    /api/knowledge/docs              → 分页列表（支持 keyword/category/status 过滤）
POST   /api/knowledge/docs              → 创建文档
PUT    /api/knowledge/docs/{id}         → 更新文档
PUT    /api/knowledge/docs/{id}/publish → 发布文档
PUT    /api/knowledge/docs/{id}/archive → 归档文档
DELETE /api/knowledge/docs/{id}         → 删除文档
```

文档编号生成规则：`"KD" + System.currentTimeMillis() + RandomUtil.randomNumbers(4)`

### 9.2 知识草稿与审批

当一个工单被解决后，`KnowledgeCuratorAgent` 会从工单中提取经验生成知识草稿：

```
KnowledgeCuratorAgent.generateDraft(ticketId, summary, description, resolution)
     ↓
调用 DeepSeek API，生成 { title, category, tags, content }
     ↓
创建 KnowledgeDraftPO (reviewStatus=PENDING)
```

管理员可以通过以下接口审核草稿：

```
PUT /api/knowledge/drafts/{id}/approve  → 批准 → 自动发布为正式文档（sourceType=AI_GENERATED）
PUT /api/knowledge/drafts/{id}/reject   → 驳回
```

### 9.3 知识检索（供 AI Agent 调用）

这是 `KnowledgeAgent` 使用的内部接口：

```
POST /internal/knowledge/retrieve
     { keyword: "网络故障", category: "NETWORK_FAILURE" }
     ↓
KnowledgeDocServiceImpl.search()
     ├── 构建条件：keyword 匹配 title/content（LIKE 查询）
     ├── 条件：category 匹配
     ├── 条件：status = PUBLISHED（只查已发布文档）
     └── 返回 List<KnowledgeDocDTO>
```

### 9.4 全文检索

`knowledge_doc` 表有一个 **MySQL 全文索引**：
```sql
FULLTEXT KEY `ft_title_content` (`title`, `content`)
```
可以通过 `KnowledgeDocMapper.searchByFulltext()` 进行全文搜索（依赖 MyBatis XML 映射）。

---

## 10. 流程五：通知服务

> **涉及模块**：`notification-service`

通知服务提供一个内部接口供其他服务调用：

```
ticket-service 创建/更新工单后
     ↓
通过 Feign 调用 NotificationFeignClient.send(request)
     { userId: 123, title: "工单创建成功", content: "...", type: "SYSTEM" }
     ↓
notification-service NotificationInternalController.send()
     ↓
NotificationServiceImpl.send()
     ├── 创建 NotificationPO
     ├── type 默认为 "SYSTEM"
     ├── isRead = false
     └── save() 存入 notification 表
```

用户可以从前端查询通知：
```
GET /api/notifications           → 列出当前用户的通知
PUT /api/notifications/{id}/read → 标记已读
GET /api/notifications/unread-count → 未读数量
```

---

## 11. 流程六：审计日志

> **涉及模块**：`audit-log-service`、`orchestrator-service`

**每次 AI Agent 执行后**，`AgentTraceRecorder` 都会记录日志：

```
AgentTraceRecorder.record(requestId, agentName, output, latencyMs)
     ↓
AgentRunLogServiceImpl.saveLog()
     ├── 创建一个 AgentRunLogPO
     ├── 记录：requestId, agentName, runStatus(SUCCESS/FAILED)
     ├── 记录：inputPayload（JSON）, outputPayload（JSON）
     ├── 记录：confidence, gateResult, latencyMs
     ├── 如果有错误 → errorCode, errorMessage
     └── save() 存入 agent_run_log 表
```

**前端可以查询审计日志**：
```
GET /api/audit/runs?agentName=TRIAGE&runStatus=SUCCESS      → 按条件查询
GET /api/orchestrator/runs/{requestId}                      → 按 requestId 查询完整链路
GET /api/log/ticket/{ticketId}                              → 按工单 ID 查询操作日志
```

**人工兜底任务**（`human_handoff_task`）也在此处管理：
```
POST /api/audit/handoffs           → 创建人工兜底任务
PUT  /api/audit/handoffs/{id}/assign   → 分配任务
PUT  /api/audit/handoffs/{id}/resolve  → 解决任务
```

---

## 12. 流程七：运营管理后台

> **涉及模块**：`ops-admin-service`

运营管理系统提供最简单的监控和管理功能：

```
GET /api/admin/health   → 健康检查
     ├── 返回 9 个服务的状态（全部 UP）
     └── 包含：gateway, user, conversation, ticket, knowledge,
                orchestrator, audit, notification, ops-admin

GET /api/admin/dashboard → 仪表盘统计
     └── 返回：服务数量、用户数量、工单数量、知识库文档数量（暂为占位数据）

GET /api/admin/config    → 系统配置列表
     └── 返回：maxUploadSize, sessionTimeout 等配置
PUT /api/admin/config    → 更新配置
```

---

## 13. 微服务间通信详解

### 13.1 OpenFeign 声明式调用

项目使用 **OpenFeign** 实现微服务间的 HTTP 调用。调用方只需定义接口：

**定义 Feign 接口**（在 `api-*` 模块中）：
```java
// api-ticket/src/main/java/.../TicketFeignClient.java
@FeignClient(name = "ticket-service", path = "/internal/tickets")
public interface TicketFeignClient {
    @PostMapping
    TicketDTO createTicket(@RequestBody TicketCreateRequest request);
}
```

**被调用方实现**（在对应 service 中）：
```java
// ticket-service/.../TicketInternalController.java
@RestController
@RequestMapping("/internal/tickets")
public class TicketInternalController {
    @PostMapping
    public AjaxResult createTicket(@RequestBody TicketCreateRequest request,
                                   @RequestHeader("X-User-Id") Long userId) {
        // 处理逻辑
    }
}
```

### 13.2 Feign 请求拦截器

`FeignConfig` 中定义了一个 `RequestInterceptor`，每次 Feign 调用自动注入以下 Header：
- `X-User-Id`：从 `UserContext.getCurrentUserId()` 获取
- `X-Tenant-Id`：从 `UserContext.getCurrentTenantId()` 获取
- `X-Trace-Id`：从 MDC 获取（链路追踪）

这样 A 服务调用 B 服务时，用户身份和 TraceId 会自动传递。

### 13.3 服务间内部 Controller

每个业务模块通常有 **两套 Controller**：

| 类型 | 路径前缀 | 用途 | 示例 |
|------|----------|------|------|
| 外部 Controller | `/api/xxx` | 供前端调用 | `TicketController` |
| 内部 Controller | `/internal/xxx` | 供其他微服务调用 | `TicketInternalController` |

内部 Controller 通常：
- 从 `X-User-Id` 请求头获取用户 ID（而不是从 SecurityContext）
- 不需要 JWT 校验（网关已经做过了）
- 返回 `AjaxResult` 或直接返回 DTO

### 13.4 异常处理

`FeignExceptionDecoder` 是自定义的 Feign 错误解码器，当 Feign 调用失败时：
1. 尝试从响应 Body 中提取错误信息
2. 否则根据 HTTP 状态码生成默认错误信息

---

## 14. 通用模块详解

### 14.1 common-core：核心基础

| 类 | 作用 |
|----|------|
| `BaseEntity` | 所有实体类的基类，定义了 id、tenantId、createTime、updateTime、deleted（逻辑删除）|
| `TicketStatusEnum` | 工单状态枚举（新建→已关闭 共7种）|
| `TicketCategoryEnum` | 工单分类枚举（账号访问、系统故障等8种）|
| `TicketPriorityEnum` | 优先级枚举（P1紧急 ~ P4低）|
| `MessageTypeEnum` | 消息类型（文本、图片、文件）|
| `MessageSourceTypeEnum` | 消息发送者类型（用户、智能体、人工、系统）|
| `ConversationStatusEnum` | 会话状态（活跃、已关闭）|
| `HumanHandoffTypeEnum` | 转人工原因（置信度过低、敏感信息、工具失败）|
| `ErrorCodeEnum` | 错误码枚举（200/400/401/403/404/500/503）|
| `BaseException` | 基础异常类（包含 code、message、service 三个字段）|
| `TicketException` | 工单专用异常 |
| `ServletUtils` | Servlet 工具类（获取请求参数、Header、IP 等）|

### 14.2 common-web：Web 层通用

| 类 | 作用 |
|----|------|
| `AjaxResult` | **标准的 API 返回格式**。本质是一个 `HashMap`，包含 code、message、data、timestamp 四个字段。提供了 success/error/warn 静态方法。 |
| `ServiceInnerResult` | 微服务间调用的返回格式，比 AjaxResult 更简洁。有 success/fail 静态方法，以及 `isSuccess()` / `isFail()` 判断工具。 |
| `BaseController` | 所有 Controller 的父类。提供了分页（`startPage()`）方法和成功/失败/警告的快捷返回方法。 |
| `TableDataInfo` | 分页查询的返回格式，包含 total（总数）和 rows（数据列表）。 |
| `PageDomain` | 前端传来的分页参数（pageNum、pageSize、orderByColumn）。 |

**AjaxResult 示例**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": { "accessToken": "xxx", "user": {...} },
  "timestamp": 1715000000000
}
```

### 14.3 common-security：安全认证

| 类 | 作用 |
|----|------|
| `JwtUtils` | JWT 令牌工具。生成 accessToken（30分钟过期）和 refreshToken（7天过期）。解析和验证 Token。 |
| `JwtAuthenticationFilter` | 服务内部的认证过滤器。从 Authorization Header 提取 Token，解析并设置 SecurityContext。 |
| `SecurityConfig` | Spring Security 配置。无状态 Session、CSRF 禁用、白名单（登录/注册/Swagger）、BCrypt 密码加密。 |
| `AuthUser` | 当前登录用户的信息（userId, username, roleCode, tenantId, permissions）。 |
| `UserContext` | 基于 ThreadLocal 的用户上下文工具，用于获取当前用户信息。 |

### 14.4 common-redis：Redis 工具

| 类 | 作用 |
|----|------|
| `RedisConfig` | Redis 配置（使用 Spring Data Redis + Redisson）。 |
| `RedisUtils` | 封装了 Redis 的各种操作：String（set/get/expire）、List（leftPush/rightPop）、Set（add/members）、Hash（put/get/entries）、自增/自减。 |

**项目中 Redis 的实际用途**：
- 工单号生成（自增序列号）
- Token 缓存（预留，目前未使用）
- 其他临时数据（预留扩展）

### 14.5 common-ai：大模型调用

| 类 | 作用 |
|----|------|
| `LLMService` | LLM 接口，定义了 chat() 和 chatStream() 方法。 |
| `DeepSeekLLMService` | DeepSeek 的实现类。使用 OkHttp 发送 HTTP 请求，支持同步和流式两种调用方式。 |
| `LLMConfig` | 配置类，读取 `llm.deepseek` 开头的配置（apiKey, baseUrl, model, maxTokens, temperature, timeout）。 |
| `ChatRequest` | 请求模型（model, messages, temperature, maxTokens, stream）。 |
| `ChatResponse` | 响应模型（choices 列表，每个 choice 包含 message/delta、finishReason）。 |
| `ChatMessage` | 消息模型（role + content），有 system/user/assistant 三种角色。 |

**LLM 调用流程**：
```
AbstractAgent.callLLM(systemPrompt, userPrompt)
     ↓
构建 List<ChatMessage> = [System消息, User消息]
     ↓
DeepSeekLLMService.chat(chatRequest)
     ├── 设置 model, temperature, maxTokens（使用 LLMConfig 默认值）
     ├── 序列化为 JSON
     ├── OkHttp POST 到 DeepSeek API（带 Bearer Token）
     ├── 解析 JSON 响应
     └── 返回 choices[0].message.content（即 AI 的回复文本）
```

### 14.6 common-log：日志追踪

| 类 | 作用 |
|----|------|
| `TraceIdFilter` | Servlet 过滤器，生成 traceId 并存入 MDC。 |
| `WebLogAspect` | AOP 切面，记录每个 Controller 方法的请求和响应日志。 |
| `AuditLogAspect` | AOP 切面，处理 `@AuditLog` 注解的方法审计。 |
| `@AuditLog` | 自定义注解（module, operation, description），用于标记需要审计的方法。 |

### 14.7 common-feign：服务调用

| 类 | 作用 |
|----|------|
| `FeignConfig` | Feign 配置：请求拦截器（自动注入用户信息、TraceId）、日志级别、重试策略。 |
| `FeignExceptionDecoder` | Feign 调用失败时的错误解析器。 |

### 14.8 common-tool：提示词模板

| 类 | 作用 |
|----|------|
| `PromptTemplatePO` | 提示词模板实体（templateCode, agentName, promptType, templateContent, version, status）。 |

---

## 15. 数据库表设计总览

系统使用了 **6 个独立数据库**：

| 数据库 | 服务 | 核心表 |
|--------|------|--------|
| `agentdesk-ticket` | ticket-service | `ticket`（工单主表）、`ticket_action_log`（操作日志） |
| `agentdesk-user` | user-service | `user`（用户表）、`user_conversation_info`（用户-会话关联） |
| `agentdesk-conversation` | conversation-service | `conversation`（会话表）、`conversation_message`（消息表）、`conversation_ticket_info`（会话-工单关联） |
| `agentdesk-knowledge` | knowledge-service | `knowledge_doc`（知识文档）、`knowledge_draft`（知识草稿） |
| `agentdesk-log` | audit-log + notification + orchestrator | `agent_run_log`（Agent运行日志）、`human_handoff_task`（人工兜底任务） |
| `agentdesk-tool` | tool-gateway-service | `prompt_template`（提示词模板） |

所有表都使用了：
- **逻辑删除**（`deleted` 字段，0=未删除，1=已删除），由 MyBatis-Plus 自动处理
- **自动填充**：`create_time` 插入时自动填充，`update_time` 更新时自动填充

---

## 16. Docker 部署架构

所有服务通过 `docker-compose.yml` 统一编排：

```
                    ┌─────────────────────────────────────┐
                    │         Nginx (端口 80)              │
                    │    /api/* → Gateway (端口 8080)     │
                    └──────────┬──────────────────────────┘
                               │
                    ┌──────────▼──────────────────────────┐
                    │    Gateway Service (端口 8080)       │
                    └──────┬───────┬───────┬──────────────┘
                           │       │       │
        ┌──────────────────┼───────┼───────┼──────────────────┐
        │                  │       │       │                  │
        ▼                  ▼       ▼       ▼                  ▼
  user:9001         conv:9002  ticket:9000  knowledge:9003  orchestrator:9005
                                                                     │
  audit:9004        notification:9006    ops-admin:9007              │
        │                  │                   │                    │
        └──────────────────┴───────────────────┴────────────────────┘
                           │
                ┌──────────┴──────────┐
                │                     │
        MySQL:3307              Redis:6379
```

---

## 17. 项目关键设计模式

1. **微服务分层架构**：每个服务遵循 Controller → Service → Mapper 三层结构
2. **双 Controller 模式**：每个服务有外部 Controller（`/api/xxx`）和内部 Controller（`/internal/xxx`）
3. **API 契约分离**：Feign 接口和 DTO 定义在独立的 `api-*` 模块中，实现和调用方都依赖这个契约模块
4. **公共库复用**：`common-*` 模块提供通用能力，通过 Maven 依赖引入
5. **统一下沉返回格式**：前端使用 `AjaxResult`，服务间使用 `ServiceInnerResult`
6. **通过 ThreadLocal 传递上下文**：`UserContext` 用于获取当前用户，避免层层传参
7. **Agent 编排模式**：多个 Agent 通过 `CoordinatorAgent` 串联成流水线，每个 Agent 职责单一
8. **优雅降级**：每个 Agent 都有 try-catch + 降级逻辑，确保系统不会因为某个环节失败而崩溃
9. **链路追踪**：通过 TraceId 贯穿整个请求链路，方便排查问题
10. **逻辑删除**：所有表使用 MyBatis-Plus 的逻辑删除而非物理删除，数据可恢复

---

> 文档生成时间：2026年5月
>
> 如果你对某个模块还想深入了解，可以告诉我具体的类名或文件名，我可以帮你进一步分析。
