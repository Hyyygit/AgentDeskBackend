package com.agentdesk.api.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class TicketCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String summary;

    private String description;

    @NotNull
    private Integer ticketCategory;

    @NotNull
    private Integer priority;

    private Long conversationId;
}
