package com.agentdesk.orchestrator.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgentNameEnum {
    COORDINATOR("COORDINATOR", "协调器"),
    TRIAGE("TRIAGE", "分诊"),
    KNOWLEDGE("KNOWLEDGE", "知识检索"),
    DECISION("DECISION", "决策"),
    TOOL("TOOL", "工具执行"),
    RESPONSE("RESPONSE", "响应生成"),
    CURATOR("CURATOR", "知识沉淀");

    private final String code;
    private final String description;
}
