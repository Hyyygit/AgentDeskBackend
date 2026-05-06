package com.agentdesk.api.ticket.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class TicketStatusUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer status;

    private String remark;
}
