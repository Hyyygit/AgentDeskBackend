package com.agentdesk.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String eventType;
    private Long ticketId;
    private String ticketNo;
    private Integer oldStatus;
    private Integer newStatus;
    private Long userId;
    private String summary;
    private Integer ticketCategory;
    private Integer priority;
    private LocalDateTime timestamp;
}
