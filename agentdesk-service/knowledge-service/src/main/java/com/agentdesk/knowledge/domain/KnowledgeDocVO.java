package com.agentdesk.knowledge.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class KnowledgeDocVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Long updatedBy;
    private Date createTime;
    private Date updateTime;
}
