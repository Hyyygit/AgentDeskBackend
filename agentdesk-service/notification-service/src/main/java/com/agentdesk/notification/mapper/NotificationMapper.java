package com.agentdesk.notification.mapper;

import com.agentdesk.notification.domain.NotificationPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotificationMapper extends BaseMapper<NotificationPO> {

    List<NotificationPO> selectByUserId(@Param("userId") Long userId);
}
