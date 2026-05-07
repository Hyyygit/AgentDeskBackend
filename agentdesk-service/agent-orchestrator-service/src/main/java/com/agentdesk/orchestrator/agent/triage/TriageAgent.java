package com.agentdesk.orchestrator.agent.triage;

import com.agentdesk.orchestrator.agent.AbstractAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class TriageAgent extends AbstractAgent {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String agentName() {
        return "TRIAGE";
    }

    public TriageResult triage(String userMessage, String conversationHistory) {
        String systemPrompt = """
            你是一个企业工单系统的分诊Agent。你的工作是：
            1. 将用户的问题分类为以下类别之一：
               ACCOUNT_ACCESS(账号访问), SYSTEM_BUG(系统故障), NETWORK_FAILURE(网络故障),
               FINANCE_PROCESS(财务流程), PERMISSION_REQUEST(权限申请), COMPLAINT(投诉),
               CONSULTATION(咨询), GENERAL_SUPPORT(通用支持)
            2. 确定优先级: P1(紧急/关键), P2(高), P3(中), P4(低)
            3. 生成一个简短的问题摘要
            4. 判断是否需要创建正式工单
            5. 检查内容是否包含敏感信息

            只返回一个JSON对象，字段为: category, priority, summary, requiresTicket(boolean), isSensitive(boolean), confidence(0.0-1.0)
            """;

        String userPrompt = "用户消息: " + userMessage;
        if (conversationHistory != null && !conversationHistory.isEmpty()) {
            userPrompt += "\n\n对话历史:\n" + conversationHistory;
        }

        try {
            String response = callLLM(systemPrompt, userPrompt);
            TriageResult result = mapper.readValue(response, TriageResult.class);
            return result;
        } catch (Exception e) {
            log.error("Triage failed, using fallback", e);
            TriageResult fallback = new TriageResult();
            fallback.setCategory("GENERAL_SUPPORT");
            fallback.setPriority("P3");
            fallback.setSummary(userMessage.substring(0, Math.min(100, userMessage.length())));
            fallback.setRequiresTicket(true);
            fallback.setIsSensitive(false);
            fallback.setConfidence(0.5);
            return fallback;
        }
    }
}
