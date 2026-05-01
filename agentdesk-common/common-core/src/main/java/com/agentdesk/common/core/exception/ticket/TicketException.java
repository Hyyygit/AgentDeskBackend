package com.agentdesk.common.core.exception.ticket;

import com.agentdesk.common.core.exception.base.BaseException;

import java.io.Serial;

/**
 * @author hyyy
 * @date 2026/4/30 00:11
 * @description 工单异常类
 */
public class TicketException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public TicketException(int code, String message) {
        super(code, message, "Ticket");
    }
}
