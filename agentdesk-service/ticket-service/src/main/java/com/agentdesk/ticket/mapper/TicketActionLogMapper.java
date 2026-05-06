package com.agentdesk.ticket.mapper;

import com.agentdesk.ticket.domain.TicketActionLogPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TicketActionLogMapper extends BaseMapper<TicketActionLogPO> {
    @Select("SELECT * FROM ticket_action_log WHERE ticket_id = #{ticketId} ORDER BY create_time ASC")
    List<TicketActionLogPO> selectByTicketId(@Param("ticketId") Long ticketId);
}
