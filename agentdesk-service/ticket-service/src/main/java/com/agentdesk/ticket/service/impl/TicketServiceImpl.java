package com.agentdesk.ticket.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agentdesk.ticket.domain.TicketQueryDTO;
import com.agentdesk.ticket.domain.TicketPO;
import com.agentdesk.ticket.domain.TicketVO;
import com.agentdesk.ticket.mapper.TicketMapper;
import com.agentdesk.ticket.service.ITicketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/1 23:08
 * @description 工单服务实现类
 */
@Service
@RequiredArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, TicketPO> implements ITicketService {

    private final TicketMapper ticketMapper;//先注入工单Mapper

    @Override
    public List<TicketVO> getTicketList(TicketQueryDTO ticketQueryDTO) {//根据前端传过来的查询信息，得到工单列表
        List<TicketPO> ticketList = ticketMapper.selectTicketList(ticketQueryDTO);
        return BeanUtil.copyToList(ticketList, TicketVO.class);
    }
}
