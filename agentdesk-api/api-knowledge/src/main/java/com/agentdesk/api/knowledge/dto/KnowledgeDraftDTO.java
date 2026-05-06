package com.agentdesk.api.knowledge.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class KnowledgeDraftDTO {
    private Long id;
    private Long sourceTicketId;
    private String title;
    private String category;
    private String content;
    private String tags;
    private BigDecimal confidence;
    private String reviewStatus;
    private Date createTime;
}
