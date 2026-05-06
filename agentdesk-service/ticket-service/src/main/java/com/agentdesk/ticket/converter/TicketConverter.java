package com.agentdesk.ticket.converter;

import cn.hutool.core.bean.BeanUtil;
import com.agentdesk.ticket.domain.TicketPO;
import com.agentdesk.ticket.domain.TicketVO;

public class TicketConverter {

    private TicketConverter() {}

    public static TicketVO toVO(TicketPO po) {
        return BeanUtil.copyProperties(po, TicketVO.class);
    }

    public static TicketVO toDTO(TicketPO po) {
        return BeanUtil.copyProperties(po, TicketVO.class);
    }
}
