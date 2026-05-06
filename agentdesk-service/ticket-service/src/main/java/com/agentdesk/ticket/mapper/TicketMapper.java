package com.agentdesk.ticket.mapper;

import com.agentdesk.ticket.domain.TicketQueryDTO;
import com.agentdesk.ticket.domain.TicketPO;
import com.agentdesk.ticket.domain.TicketVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/1 23:10
 * @description 工单持久化对象
 */
public interface TicketMapper extends BaseMapper<TicketPO> {
    List<TicketPO> selectTicketList(TicketQueryDTO ticketQueryDTO);

    TicketPO selectByTicketNo(@Param("ticketNo") String ticketNo);

    List<TicketPO> selectByUserId(@Param("userId") Long userId);

    Long countByStatus(@Param("status") Integer status);
}
