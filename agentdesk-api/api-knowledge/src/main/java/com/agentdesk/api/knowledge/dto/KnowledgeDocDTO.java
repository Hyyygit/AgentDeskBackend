package com.agentdesk.api.knowledge.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class KnowledgeDocDTO {
    private Long id;
    private String docNo;
    private Long tenantId;
    private String title;
    private String category;
    private String tags;
    private String content;
    private String summary;
    private String sourceType;
    private String status;
    private Integer version;
    private Long createdBy;
    private Date createTime;
    private Date updateTime;
}
