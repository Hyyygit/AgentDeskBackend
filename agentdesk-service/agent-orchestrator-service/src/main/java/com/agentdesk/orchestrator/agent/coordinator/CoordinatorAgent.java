package com.agentdesk.orchestrator.agent.coordinator;

import cn.hutool.core.util.IdUtil;
import com.agentdesk.api.orchestrator.dto.AgentResult;
import com.agentdesk.api.orchestrator.dto.OrchestrateRequest;
import com.agentdesk.api.orchestrator.dto.OrchestrateResponse;
import com.agentdesk.orchestrator.agent.curator.KnowledgeCuratorAgent;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CoordinatorAgent {
    private static final Logger log = LoggerFactory.getLogger(CoordinatorAgent.class);

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

    public OrchestrateResponse orchestrate(OrchestrateRequest request) {
        String requestId = request.getRequestId();
        if (requestId == null) {
            requestId = IdUtil.fastSimpleUUID();
        }

        OrchestrateResponse response = new OrchestrateResponse();
        response.setRequestId(requestId);
        List<AgentResult> agentResults = new ArrayList<>();

        // Step 1: Triage
        long start = System.currentTimeMillis();
        TriageResult triageResult = triageAgent.triage(request.getContent(), null);
        traceRecorder.record(requestId, "TRIAGE", triageResult, System.currentTimeMillis() - start);
        agentResults.add(buildAgentResult("TRIAGE", triageResult.getCategory(), triageResult.getConfidence()));

        // Step 2: Knowledge Retrieval
        start = System.currentTimeMillis();
        KnowledgeResult knowledgeResult = knowledgeAgent.retrieve(request.getContent(), triageResult.getCategory());
        traceRecorder.record(requestId, "KNOWLEDGE", knowledgeResult, System.currentTimeMillis() - start);
        agentResults.add(buildAgentResult("KNOWLEDGE", "retrieved", knowledgeResult.getRelevanceScore()));

        // Step 3: Decision
        start = System.currentTimeMillis();
        DecisionResult decisionResult = decisionAgent.decide(triageResult, knowledgeResult, request.getContent());
        traceRecorder.record(requestId, "DECISION", decisionResult, System.currentTimeMillis() - start);
        agentResults.add(buildAgentResult("DECISION", decisionResult.getAction(), decisionResult.getConfidence()));

        // Step 4: Tool Execution
        ToolResult toolResult = null;
        if ("CREATE_TICKET".equals(decisionResult.getAction()) || "HUMAN_HANDOFF".equals(decisionResult.getAction())) {
            start = System.currentTimeMillis();
            toolResult = toolAgent.execute(decisionResult.getAction(), triageResult, request.getUserId(), request.getConversationId());
            traceRecorder.record(requestId, "TOOL", toolResult, System.currentTimeMillis() - start);
            agentResults.add(buildAgentResult("TOOL", toolResult.getSuccess() ? "success" : "failed", toolResult.getSuccess() ? 1.0 : 0.0));
        }

        // Step 5: Response Generation
        start = System.currentTimeMillis();
        String reply = responseAgent.generateResponse(decisionResult, knowledgeResult, toolResult);
        traceRecorder.record(requestId, "RESPONSE", reply, System.currentTimeMillis() - start);
        agentResults.add(buildAgentResult("RESPONSE", "generated", 1.0));

        response.setReplyContent(reply);
        response.setTicketId(toolResult != null ? toolResult.getTicketId() : null);
        response.setTicketNo(toolResult != null ? toolResult.getTicketNo() : null);
        response.setAgentResults(agentResults);

        if (decisionResult.getConfidence() != null && decisionResult.getConfidence() < 0.6) {
            response.setNeedHuman(true);
            response.setConfidence(decisionResult.getConfidence().intValue());
        } else {
            response.setNeedHuman("HUMAN_HANDOFF".equals(decisionResult.getAction()));
            response.setConfidence(decisionResult.getConfidence() != null ? decisionResult.getConfidence().intValue() : 80);
        }

        return response;
    }

    private AgentResult buildAgentResult(String name, String output, Double confidence) {
        AgentResult result = new AgentResult();
        result.setAgentName(name);
        result.setOutput(output);
        result.setConfidence(confidence != null ? (int) (confidence * 100) : 100);
        result.setGateResult("PASS");
        return result;
    }
}
