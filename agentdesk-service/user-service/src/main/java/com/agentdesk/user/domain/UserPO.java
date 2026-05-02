package com.agentdesk.user.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import lombok.Data;

/**
 * @author hyyy
 * @date 2026/5/2 15:11
 * @description 用户持久化对象
 */
@Data
public class UserPO extends BaseEntity {

    private String userNo;//用户编号
    private String userName;//用户名
    private String password;//密码
}
