package com.agentdesk.user.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hyyy
 * @date 2026/5/3 15:02
 * @description 返回给前端的用户对象
 */
@Data
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userNo;//用户编号
    private String userName;//用户名
    private String email;
    private String department;
    private Integer status;

}
