package com.agentdesk.knowledge.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class KnowledgeDraftVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sourceTicketId;
    private String title;
    private String category;
    private String content;
    private String tags;
    private BigDecimal confidence;
    private String reviewStatus;
    private String reviewComment;
    private Date createTime;
}
