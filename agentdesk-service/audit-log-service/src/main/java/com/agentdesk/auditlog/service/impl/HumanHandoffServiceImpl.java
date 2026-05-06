package com.agentdesk.auditlog.service.impl;

import com.agentdesk.auditlog.domain.HumanHandoffTaskPO;
import com.agentdesk.auditlog.mapper.HumanHandoffTaskMapper;
import com.agentdesk.auditlog.service.IHumanHandoffService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HumanHandoffServiceImpl extends ServiceImpl<HumanHandoffTaskMapper, HumanHandoffTaskPO> implements IHumanHandoffService {

    @Override
    public HumanHandoffTaskPO create(HumanHandoffTaskPO task) {
        task.setStatus("PENDING");
        save(task);
        log.info("创建人工转接任务: ticketId={}", task.getTicketId());
        return task;
    }

    @Override
    public List<HumanHandoffTaskPO> listPending() {
        return lambdaQuery()
                .eq(HumanHandoffTaskPO::getStatus, "PENDING")
                .orderByAsc(HumanHandoffTaskPO::getCreateTime)
                .list();
    }

    @Override
    public void assign(Long taskId, String group) {
        HumanHandoffTaskPO task = getById(taskId);
        if (task != null) {
            task.setAssignedGroup(group);
            task.setStatus("ASSIGNED");
            updateById(task);
            log.info("人工转接任务已分配: taskId={}, group={}", taskId, group);
        }
    }

    @Override
    public void resolve(Long taskId) {
        HumanHandoffTaskPO task = getById(taskId);
        if (task != null) {
            task.setStatus("RESOLVED");
            updateById(task);
            log.info("人工转接任务已解决: taskId={}", taskId);
        }
    }
}
