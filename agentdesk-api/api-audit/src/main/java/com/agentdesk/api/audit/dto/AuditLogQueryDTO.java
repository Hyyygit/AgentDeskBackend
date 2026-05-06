package com.agentdesk.api.audit.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AuditLogQueryDTO {
    private String agentName;
    private String runStatus;
    private Date startTime;
    private Date endTime;
    private Integer pageNum;
    private Integer pageSize;
}
