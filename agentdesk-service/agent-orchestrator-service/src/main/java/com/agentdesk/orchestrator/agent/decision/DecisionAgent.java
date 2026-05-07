package com.agentdesk.orchestrator.agent.decision;

import com.agentdesk.orchestrator.agent.AbstractAgent;
import com.agentdesk.orchestrator.agent.knowledge.KnowledgeResult;
import com.agentdesk.orchestrator.agent.triage.TriageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class DecisionAgent extends AbstractAgent {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String agentName() {
        return "DECISION";
    }

    public DecisionResult decide(TriageResult triageResult, KnowledgeResult knowledgeResult, String userMessage) {
        if (triageResult.getIsSensitive() != null && triageResult.getIsSensitive()) {
            DecisionResult result = new DecisionResult();
            result.setAction("HUMAN_HANDOFF");
            result.setReason("检测到敏感内容");
            result.setConfidence(1.0);
            return result;
        }

        if (triageResult.getConfidence() != null && triageResult.getConfidence() < 0.6) {
            DecisionResult result = new DecisionResult();
            result.setAction("HUMAN_HANDOFF");
            result.setReason("分诊置信度过低: " + triageResult.getConfidence());
            result.setConfidence(triageResult.getConfidence());
            return result;
        }

        if (knowledgeResult.getHasAnswer() && knowledgeResult.getRelevanceScore() > 0.7) {
            DecisionResult result = new DecisionResult();
            result.setAction("AUTO_RESOLVE");
            result.setReason("知识库中找到了相关答案");
            result.setSuggestedResponse(knowledgeResult.getBestAnswer());
            result.setConfidence(knowledgeResult.getRelevanceScore());
            return result;
        }

        String systemPrompt = """
            你是一个决策Agent。根据分诊结果和知识库检索结果，决定下一步行动：
            - AUTO_RESOLVE: 知识库有合适的答案，可以直接回复用户
            - CREATE_TICKET: 需要创建正式工单进行跟踪处理
            - HUMAN_HANDOFF: 问题复杂或敏感，需要转人工处理

            只返回JSON: {"action": "...", "reason": "...", "suggestedResponse": "...", "confidence": 0.0}
            """;

        try {
            String context = "分诊结果: " + triageResult.getCategory() + " " + triageResult.getPriority() + "\n"
                + "知识库: " + (knowledgeResult.getHasAnswer() ? "找到相关文档" : "未找到相关文档");
            String response = callLLM(systemPrompt, context);
            return mapper.readValue(response, DecisionResult.class);
        } catch (Exception e) {
            log.error("Decision failed, using fallback", e);
            DecisionResult fallback = new DecisionResult();
            fallback.setAction("CREATE_TICKET");
            fallback.setReason("决策出错，使用降级方案: " + e.getMessage());
            fallback.setConfidence(0.5);
            return fallback;
        }
    }
}
