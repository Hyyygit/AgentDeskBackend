package com.agentdesk.auditlog.service;

import com.agentdesk.auditlog.domain.HumanHandoffTaskPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IHumanHandoffService extends IService<HumanHandoffTaskPO> {

    HumanHandoffTaskPO create(HumanHandoffTaskPO task);

    List<HumanHandoffTaskPO> listPending();

    void assign(Long taskId, String group);

    void resolve(Long taskId);
}
