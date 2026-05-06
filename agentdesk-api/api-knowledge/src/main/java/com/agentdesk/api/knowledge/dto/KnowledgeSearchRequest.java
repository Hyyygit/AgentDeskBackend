package com.agentdesk.api.knowledge.dto;

import lombok.Data;

@Data
public class KnowledgeSearchRequest {
    private String keyword;
    private String category;
    private String status;
    private Integer pageNum;
    private Integer pageSize;
}
