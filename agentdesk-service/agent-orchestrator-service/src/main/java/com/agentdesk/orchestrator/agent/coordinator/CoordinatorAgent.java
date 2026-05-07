package com.agentdesk.orchestrator.agent.coordinator;

import cn.hutool.core.util.IdUtil;
import com.agentdesk.api.orchestrator.dto.AgentResult;
import com.agentdesk.api.orchestrator.dto.OrchestrateRequest;
import com.agentdesk.api.orchestrator.dto.OrchestrateResponse;
import com.agentdesk.orchestrator.agent.decision.DecisionAgent;
import com.agentdesk.orchestrator.agent.decision.DecisionResult;
import com.agentdesk.orchestrator.agent.knowledge.KnowledgeAgent;
import com.agentdesk.orchestrator.agent.knowledge.KnowledgeResult;
import com.agentdesk.orchestrator.agent.response.ResponseAgent;
import com.agentdesk.orchestrator.agent.tool.ToolAgent;
import com.agentdesk.orchestrator.agent.tool.ToolResult;
import com.agentdesk.orchestrator.agent.trace.AgentTraceRecorder;
import com.agentdesk.orchestrator.agent.triage.TriageAgent;
import com.agentdesk.orchestrator.agent.triage.TriageResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class CoordinatorAgent {
    private static final Logger log = LoggerFactory.getLogger(CoordinatorAgent.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private TriageAgent triageAgent;

    @Autowired
    private KnowledgeAgent knowledgeAgent;

    @Autowired
    private DecisionAgent decisionAgent;

    @Autowired
    private ToolAgent toolAgent;

    @Autowired
    private ResponseAgent responseAgent;

    @Autowired
    private AgentTraceRecorder traceRecorder;

    @Autowired(required = false)
    private ChatModel chatModel;

    public OrchestrateResponse orchestrate(OrchestrateRequest request) {
        String requestId = request.getRequestId();
        if (requestId == null) requestId = IdUtil.fastSimpleUUID();

        OrchestrateResponse response = new OrchestrateResponse();
        response.setRequestId(requestId);
        List<AgentResult> agentResults = new ArrayList<>();

        TriageResult triageResult = null;
        KnowledgeResult knowledgeResult = null;
        DecisionResult decisionResult = null;
        ToolResult toolResult = null;

        List<String> plan = buildPlan(request.getContent());
        log.info("[{}] Execution plan: {}", requestId, plan);

        for (String step : plan) {
            long start = System.currentTimeMillis();
            try {
                switch (step) {
                    case "TRIAGE" -> {
                        triageResult = triageAgent.triage(request.getContent(), null);
                        traceRecorder.record(requestId, "TRIAGE", triageResult, System.currentTimeMillis() - start);
                        agentResults.add(result("TRIAGE", triageResult.getCategory(), triageResult.getConfidence()));
                    }
                    case "KNOWLEDGE" -> {
                        String category = triageResult != null ? triageResult.getCategory() : null;
                        knowledgeResult = knowledgeAgent.retrieve(request.getContent(), category);
                        traceRecorder.record(requestId, "KNOWLEDGE", knowledgeResult, System.currentTimeMillis() - start);
                        agentResults.add(result("KNOWLEDGE", knowledgeResult.getHasAnswer() ? "found" : "empty",
                            knowledgeResult.getRelevanceScore()));
                    }
                    case "DECISION" -> {
                        if (triageResult == null) triageResult = buildDefaultTriage(request.getContent());
                        if (knowledgeResult == null) knowledgeResult = new KnowledgeResult();
                        decisionResult = decisionAgent.decide(triageResult, knowledgeResult, request.getContent());
                        traceRecorder.record(requestId, "DECISION", decisionResult, System.currentTimeMillis() - start);
                        agentResults.add(result("DECISION", decisionResult.getAction(), decisionResult.getConfidence()));
                    }
                    case "TOOL" -> {
                        if (decisionResult != null && ("CREATE_TICKET".equals(decisionResult.getAction())
                            || "HUMAN_HANDOFF".equals(decisionResult.getAction()))) {
                            if (triageResult == null) triageResult = buildDefaultTriage(request.getContent());
                            toolResult = toolAgent.execute(decisionResult.getAction(), triageResult,
                                request.getUserId(), request.getConversationId());
                            traceRecorder.record(requestId, "TOOL", toolResult, System.currentTimeMillis() - start);
                            agentResults.add(result("TOOL",
                                toolResult.getSuccess() ? "success" : "failed",
                                toolResult.getSuccess() ? 1.0 : 0.0));
                        }
                    }
                    case "RESPONSE" -> {
                        if (decisionResult == null) {
                            decisionResult = new DecisionResult();
                            decisionResult.setAction("AUTO_RESOLVE");
                            decisionResult.setReason("直接回复");
                            decisionResult.setConfidence(0.8);
                        }
                        if (knowledgeResult == null) knowledgeResult = new KnowledgeResult();
                        String reply = responseAgent.generateResponse(decisionResult, knowledgeResult, toolResult);
                        traceRecorder.record(requestId, "RESPONSE", reply, System.currentTimeMillis() - start);
                        response.setReplyContent(reply);
                    }
                }
            } catch (Exception e) {
                log.error("[{}] Agent '{}' execution failed", requestId, step, e);
                agentResults.add(result(step, "error: " + e.getMessage(), 0.0));
            }
        }

        response.setTicketId(toolResult != null ? toolResult.getTicketId() : null);
        response.setTicketNo(toolResult != null ? toolResult.getTicketNo() : null);
        response.setAgentResults(agentResults);
        response.setNeedHuman(decisionResult != null && "HUMAN_HANDOFF".equals(decisionResult.getAction()));
        response.setConfidence(decisionResult != null && decisionResult.getConfidence() != null
            ? (int)(decisionResult.getConfidence() * 100) : 80);

        if (response.getReplyContent() == null) {
            response.setReplyContent("抱歉，系统暂时无法处理您的请求，已转接人工客服。");
            response.setNeedHuman(true);
        }

        return response;
    }

    public void orchestrateStream(OrchestrateRequest request, Consumer<String> progressConsumer) {
        String requestId = request.getRequestId();
        if (requestId == null) requestId = IdUtil.fastSimpleUUID();

        TriageResult triageResult = null;
        KnowledgeResult knowledgeResult = null;
        DecisionResult decisionResult = null;
        ToolResult toolResult = null;

        List<String> plan = buildPlan(request.getContent());
        log.info("[{}] Streaming execution plan: {}", requestId, plan);

        for (String step : plan) {
            long start = System.currentTimeMillis();
            try {
                switch (step) {
                    case "TRIAGE" -> {
                        progressConsumer.accept("正在分析您的问题类型...\n");
                        triageResult = triageAgent.triage(request.getContent(), null);
                        traceRecorder.record(requestId, "TRIAGE", triageResult, System.currentTimeMillis() - start);
                        progressConsumer.accept("分析完成: " + triageResult.getSummary() + "\n");
                    }
                    case "KNOWLEDGE" -> {
                        progressConsumer.accept("正在查询知识库...\n");
                        String category = triageResult != null ? triageResult.getCategory() : null;
                        knowledgeResult = knowledgeAgent.retrieve(request.getContent(), category);
                        traceRecorder.record(requestId, "KNOWLEDGE", knowledgeResult, System.currentTimeMillis() - start);
                        progressConsumer.accept(knowledgeResult.getHasAnswer()
                            ? "已找到相关解决方案\n" : "未找到现成答案，继续分析...\n");
                    }
                    case "DECISION" -> {
                        if (triageResult == null) triageResult = buildDefaultTriage(request.getContent());
                        if (knowledgeResult == null) knowledgeResult = new KnowledgeResult();
                        decisionResult = decisionAgent.decide(triageResult, knowledgeResult, request.getContent());
                        traceRecorder.record(requestId, "DECISION", decisionResult, System.currentTimeMillis() - start);
                        progressConsumer.accept("处理决策: " + decisionResult.getReason() + "\n");
                    }
                    case "TOOL" -> {
                        if (decisionResult != null && ("CREATE_TICKET".equals(decisionResult.getAction())
                            || "HUMAN_HANDOFF".equals(decisionResult.getAction()))) {
                            progressConsumer.accept("正在创建工单...\n");
                            if (triageResult == null) triageResult = buildDefaultTriage(request.getContent());
                            toolResult = toolAgent.execute(decisionResult.getAction(), triageResult,
                                request.getUserId(), request.getConversationId());
                            traceRecorder.record(requestId, "TOOL", toolResult, System.currentTimeMillis() - start);
                            if (toolResult.getSuccess() && toolResult.getTicketNo() != null) {
                                progressConsumer.accept("工单 " + toolResult.getTicketNo() + " 已创建\n");
                            }
                        }
                    }
                    case "RESPONSE" -> {
                        if (decisionResult == null) {
                            decisionResult = new DecisionResult();
                            decisionResult.setAction("AUTO_RESOLVE");
                            decisionResult.setReason("直接回复");
                        }
                        if (knowledgeResult == null) knowledgeResult = new KnowledgeResult();
                        progressConsumer.accept("\n---\n");
                        responseAgent.generateResponseStream(decisionResult, knowledgeResult, toolResult,
                            progressConsumer);
                    }
                }
            } catch (Exception e) {
                log.error("[{}] Agent '{}' streaming failed", requestId, step, e);
                progressConsumer.accept("\n[处理出错: " + e.getMessage() + "]\n");
            }
        }

        progressConsumer.accept("\n[DONE]");
    }

    private List<String> buildPlan(String userMessage) {
        if (chatModel == null) {
            return defaultPlan();
        }

        try {
            Message systemMsg = new SystemMessage("""
                你是一个智能工单系统的协调Agent。分析用户的问题，制定处理步骤计划。

                可用的步骤:
                - TRIAGE: 分类用户问题，确定优先级和类型
                - KNOWLEDGE: 检索知识库中相关文档和解决方案
                - DECISION: 综合分析信息，做出处理决策(AUTO_RESOLVE/CREATE_TICKET/HUMAN_HANDOFF)
                - TOOL: 执行具体操作，如创建工单
                - RESPONSE: 生成给用户的最终回复

                规则:
                1. 简单问候或闲聊, 只需要["RESPONSE"]
                2. 复杂问题, 需要["TRIAGE", "KNOWLEDGE", "DECISION", "RESPONSE"]
                3. 如果需要创建工单，在DECISION之后加上TOOL
                4. RESPONSE必须作为最后一步

                只返回JSON数组: {"reasoning": "分析理由", "steps": ["TRIAGE", "KNOWLEDGE", "DECISION", "RESPONSE"]}
                """);

            Message userMsg = new UserMessage("用户消息: " + userMessage);
            String llmResponse = chatModel.call(new Prompt(List.of(systemMsg, userMsg)))
                .getResult().getOutput().getText();

            JsonNode json = mapper.readTree(llmResponse);
            List<String> steps = new ArrayList<>();
            if (json.has("steps")) {
                for (JsonNode step : json.get("steps")) {
                    steps.add(step.asText());
                }
            }
            if (json.has("reasoning")) {
                log.info("LLM orchestration reasoning: {}", json.get("reasoning").asText());
            }

            if (steps.isEmpty() || !steps.get(steps.size() - 1).equals("RESPONSE")) {
                steps.add("RESPONSE");
            }

            return steps;
        } catch (Exception e) {
            log.warn("LLM plan generation failed, using default plan: {}", e.getMessage());
            return defaultPlan();
        }
    }

    private List<String> defaultPlan() {
        return List.of("TRIAGE", "KNOWLEDGE", "DECISION", "RESPONSE");
    }

    private TriageResult buildDefaultTriage(String content) {
        TriageResult result = new TriageResult();
        result.setCategory("GENERAL_SUPPORT");
        result.setPriority("P3");
        result.setSummary(content.substring(0, Math.min(100, content.length())));
        result.setRequiresTicket(true);
        result.setIsSensitive(false);
        result.setConfidence(0.6);
        return result;
    }

    private AgentResult result(String name, String output, Double confidence) {
        AgentResult r = new AgentResult();
        r.setAgentName(name);
        r.setOutput(output);
        r.setConfidence(confidence != null ? (int)(confidence * 100) : 100);
        r.setGateResult("PASS");
        return r;
    }
}
