package com.agentdesk.auditlog.service.impl;

import com.agentdesk.auditlog.domain.AgentRunLogPO;
import com.agentdesk.auditlog.mapper.AgentRunLogMapper;
import com.agentdesk.auditlog.service.IAgentRunLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author hyyy
 * @date 2026/5/3 21:05
 * @description Agent运行日志服务实现类
 */
@Service
public class AgentRunLogServiceImpl extends ServiceImpl<AgentRunLogMapper, AgentRunLogPO> implements IAgentRunLogService {
}
