package com.agentdesk.orchestrator.mapper;

import com.agentdesk.orchestrator.domain.AgentRunLogPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AgentRunLogMapper extends BaseMapper<AgentRunLogPO> {
    @Select("SELECT * FROM agent_run_log WHERE request_id = #{requestId} ORDER BY create_time ASC")
    List<AgentRunLogPO> selectByRequestId(@Param("requestId") String requestId);
}
