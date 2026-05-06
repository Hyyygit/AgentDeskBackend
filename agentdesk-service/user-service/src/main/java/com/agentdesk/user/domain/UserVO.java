package com.agentdesk.user.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyyy
 * @date 2026/5/3 15:02
 * @description 返回给前端的用户对象
 */
@Data
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String userNo;
    private String userName;
    private String realName;
    private String email;
    private String phone;
    private String department;
    private String roleCode;
    private Integer status;
    private Long tenantId;
    private Date createTime;
}
