package com.agentdesk.ticket.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agentdesk.api.notification.dto.SendNotificationRequest;
import com.agentdesk.api.notification.feign.NotificationFeignClient;
import com.agentdesk.api.ticket.dto.TicketCreateRequest;
import com.agentdesk.common.core.enums.TicketActionTypeEnum;
import com.agentdesk.common.core.enums.TicketStatusEnum;
import com.agentdesk.common.redis.utils.RedisUtils;
import com.agentdesk.ticket.domain.TicketActionLogPO;
import com.agentdesk.ticket.domain.TicketQueryDTO;
import com.agentdesk.ticket.domain.TicketPO;
import com.agentdesk.ticket.mapper.TicketMapper;
import com.agentdesk.ticket.service.ITicketActionLogService;
import com.agentdesk.ticket.service.ITicketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hyyy
 * @date 2026/5/1 23:08
 * @description 工单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, TicketPO> implements ITicketService {

    private final TicketMapper ticketMapper;
    private final ITicketActionLogService actionLogService;
    private final RedisUtils redisUtils;
    private final NotificationFeignClient notificationFeignClient;

    private static final int OPERATOR_SYSTEM = 1;
    private static final int OPERATOR_USER = 2;
    private static final int OPERATOR_AGENT = 3;
    private static final int STATUS_CANCELLED = 8;

    private static final Set<String> VALID_TRANSITIONS = Set.of(
            "1->2", "2->3", "3->4", "4->5", "5->6", "4->6", "6->7"
    );

    @Override
    @Transactional
    public TicketPO createTicket(TicketCreateRequest request, Long userId) {
        TicketPO ticket = new TicketPO();
        ticket.setTicketNo(generateTicketNo());
        ticket.setTicketCategory(request.getTicketCategory());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatusEnum.NEW.getStatus());
        ticket.setSummary(request.getSummary());
        ticket.setDescription(request.getDescription());
        ticket.setSource(1);
        ticket.setUserId(userId);
        ticket.setConversationId(request.getConversationId());
        ticket.setTenantId(1L);
        save(ticket);

        actionLogService.logAction(ticket.getId(), TicketActionTypeEnum.CREATE.getType(),
                OPERATOR_USER, userId, "创建工单");

        try {
            SendNotificationRequest noti = new SendNotificationRequest();
            noti.setUserId(userId);
            noti.setTitle("工单创建成功");
            noti.setContent("您的工单 " + ticket.getTicketNo() + " 已创建");
            notificationFeignClient.send(noti);
        } catch (Exception e) {
            log.warn("发送通知失败: {}", e.getMessage());
        }

        return ticket;
    }

    @Override
    public TicketPO getTicket(Long ticketId) {
        return getById(ticketId);
    }

    @Override
    public TicketPO getTicketByNo(String ticketNo) {
        return ticketMapper.selectByTicketNo(ticketNo);
    }

    @Override
    public List<TicketPO> listTickets(TicketQueryDTO query) {
        return ticketMapper.selectTicketList(query);
    }

    @Override
    public List<TicketPO> listUserTickets(Long userId) {
        return ticketMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public TicketPO updateStatus(Long ticketId, Integer newStatus, Long operatorId, Integer operatorType, String remark) {
        TicketPO ticket = getById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }

        int oldStatus = ticket.getStatus();
        if (newStatus != STATUS_CANCELLED) {
            String transition = oldStatus + "->" + newStatus;
            if (!VALID_TRANSITIONS.contains(transition)) {
                throw new RuntimeException("不允许的状态流转: " + oldStatus + " -> " + newStatus);
            }
        }

        ticket.setStatus(newStatus);
        if (newStatus == TicketStatusEnum.RESOLVED.getStatus()) {
            ticket.setResolvedTime(new Date());
        }
        if (newStatus == TicketStatusEnum.CLOSED.getStatus()) {
            ticket.setClosedTime(new Date());
        }
        updateById(ticket);

        int actionType = mapStatusToActionType(newStatus);
        String detail = remark != null ? remark : ("状态变更: " + oldStatus + " -> " + newStatus);
        actionLogService.logAction(ticketId, actionType, operatorType != null ? operatorType : OPERATOR_SYSTEM,
                operatorId, detail);

        try {
            SendNotificationRequest noti = new SendNotificationRequest();
            noti.setUserId(ticket.getUserId());
            noti.setTitle("工单状态更新");
            noti.setContent("工单 " + ticket.getTicketNo() + " 状态已更新为 " + getStatusDesc(newStatus));
            notificationFeignClient.send(noti);
        } catch (Exception e) {
            log.warn("发送通知失败: {}", e.getMessage());
        }

        return ticket;
    }

    @Override
    @Transactional
    public TicketPO assignTicket(Long ticketId, Long assignedUserId, String assignedGroup, Long operatorId) {
        TicketPO ticket = getById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }

        ticket.setAssignedUserId(assignedUserId);
        ticket.setAssignedGroup(assignedGroup);
        updateById(ticket);

        actionLogService.logAction(ticketId, 4, OPERATOR_SYSTEM, operatorId,
                "分配工单: assignedUserId=" + assignedUserId + ", assignedGroup=" + assignedGroup);

        return ticket;
    }

    @Override
    @Transactional
    public TicketPO updatePriority(Long ticketId, Integer priority, Long operatorId) {
        TicketPO ticket = getById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }

        ticket.setPriority(priority);
        updateById(ticket);

        actionLogService.logAction(ticketId, 4, OPERATOR_SYSTEM, operatorId,
                "更新优先级: " + priority);

        return ticket;
    }

    @Override
    @Transactional
    public void addComment(Long ticketId, String comment, Long operatorId, Integer operatorType) {
        TicketPO ticket = getById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }

        actionLogService.logAction(ticketId, 4, operatorType != null ? operatorType : OPERATOR_USER,
                operatorId, "评论: " + comment);
    }

    @Override
    public List<TicketActionLogPO> getTimeline(Long ticketId) {
        return actionLogService.lambdaQuery()
                .eq(TicketActionLogPO::getTicketId, ticketId)
                .orderByAsc(TicketActionLogPO::getCreateTime)
                .list();
    }

    @Override
    public Map<String, Long> getStats(Long userId) {
        Map<String, Long> stats = new HashMap<>();
        for (TicketStatusEnum statusEnum : TicketStatusEnum.values()) {
            Long count = ticketMapper.countByStatus(statusEnum.getStatus());
            stats.put(statusEnum.getDescription(), count != null ? count : 0L);
        }
        return stats;
    }

    private String generateTicketNo() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = "ticket:seq:" + datePrefix;
        Long seq;
        try {
            seq = redisUtils.increment(key);
            redisUtils.expire(key, 86400);
        } catch (Exception e) {
            log.warn("Redis不可用，使用时间戳生成序号: {}", e.getMessage());
            seq = System.currentTimeMillis() % 100000;
        }
        return "TK" + datePrefix + String.format("%05d", seq % 100000);
    }

    private int mapStatusToActionType(int status) {
        if (status == TicketStatusEnum.NEW.getStatus()) return TicketActionTypeEnum.CREATE.getType();
        if (status == TicketStatusEnum.TRIAGED.getStatus()) return TicketActionTypeEnum.TRIAGE.getType();
        if (status == TicketStatusEnum.DECIDED.getStatus()) return TicketActionTypeEnum.DECIDE.getType();
        if (status == TicketStatusEnum.IN_PROGRESS.getStatus()) return TicketActionTypeEnum.PROCESS.getType();
        if (status == TicketStatusEnum.WAITING_HUMAN.getStatus()) return TicketActionTypeEnum.WAIT_HUMAN.getType();
        if (status == TicketStatusEnum.RESOLVED.getStatus()) return TicketActionTypeEnum.RESOLVE.getType();
        if (status == TicketStatusEnum.CLOSED.getStatus()) return TicketActionTypeEnum.CLOSE.getType();
        return 4;
    }

    private String getStatusDesc(int status) {
        for (TicketStatusEnum e : TicketStatusEnum.values()) {
            if (e.getStatus() == status) return e.getDescription();
        }
        if (status == STATUS_CANCELLED) return "已取消";
        return "未知";
    }
}
