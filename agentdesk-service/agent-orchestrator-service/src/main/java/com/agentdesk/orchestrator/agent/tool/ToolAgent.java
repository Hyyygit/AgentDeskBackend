package com.agentdesk.orchestrator.agent.tool;

import com.agentdesk.api.ticket.dto.TicketCreateRequest;
import com.agentdesk.api.ticket.dto.TicketDTO;
import com.agentdesk.api.ticket.feign.TicketFeignClient;
import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.common.security.domain.AuthUser;
import com.agentdesk.orchestrator.agent.AbstractAgent;
import com.agentdesk.orchestrator.agent.triage.TriageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ToolAgent extends AbstractAgent {

    @Autowired
    private TicketFeignClient ticketFeignClient;

    @Override
    public String agentName() {
        return "TOOL";
    }

    public ToolResult execute(String action, TriageResult triageResult, Long userId, Long conversationId) {
        ToolResult result = new ToolResult();

        AuthUser authUser = UserContext.getCurrentUser();
        System.out.println(authUser.getUserId());

        try {
            if ("CREATE_TICKET".equals(action)) {
                Integer category = mapCategory(triageResult.getCategory());
                Integer priority = mapPriority(triageResult.getPriority());

                TicketCreateRequest request = new TicketCreateRequest();
                request.setSummary(triageResult.getSummary());
                request.setDescription("Auto-created by AI Agent. Category: " + triageResult.getCategory());
                request.setTicketCategory(category);
                request.setPriority(priority);
                request.setConversationId(conversationId);

                TicketDTO ticket = ticketFeignClient.createTicket(request).getData();
                System.out.println(ticket.getTicketNo());
                result.setToolName("CREATE_TICKET");
                result.setSuccess(true);
                result.setTicketId(ticket.getId());
                result.setTicketNo(ticket.getTicketNo());
                result.setResult("Ticket created: " + ticket.getTicketNo());
            } else {
                result.setToolName(action);
                result.setSuccess(false);
                result.setErrorMessage("Unknown action: " + action);
            }
        } catch (Exception e) {
            log.error("Tool execution failed: {}", action, e);
            result.setToolName(action);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    private Integer mapCategory(String category) {
        return switch (category) {
            case "ACCOUNT_ACCESS" -> 1;
            case "SYSTEM_BUG" -> 2;
            case "NETWORK_FAILURE" -> 3;
            case "FINANCE_PROCESS" -> 4;
            case "PERMISSION_REQUEST" -> 5;
            case "COMPLAINT" -> 6;
            case "CONSULTATION" -> 7;
            default -> 8;
        };
    }

    private Integer mapPriority(String priority) {
        return switch (priority) {
            case "P1" -> 1;
            case "P2" -> 2;
            case "P4" -> 4;
            default -> 3;
        };
    }
}
