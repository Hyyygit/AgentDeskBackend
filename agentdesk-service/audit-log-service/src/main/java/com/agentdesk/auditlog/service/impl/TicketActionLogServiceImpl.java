package com.agentdesk.auditlog.service.impl;

import com.agentdesk.auditlog.domain.TicketActionLogPO;
import com.agentdesk.auditlog.mapper.TicketActionLogMapper;
import com.agentdesk.auditlog.service.ITicketActionLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author hyyy
 * @date 2026/5/3 21:05
 * @description 工单操作日志服务实现类
 */
@Service
public class TicketActionLogServiceImpl extends ServiceImpl<TicketActionLogMapper, TicketActionLogPO> implements ITicketActionLogService {
}
