package com.agentdesk.api.ticket.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class TicketAssignRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long assignedUserId;

    private String assignedGroup;
}
