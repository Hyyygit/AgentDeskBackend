package com.agentdesk.api.user.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDTO implements Serializable {

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

    private Date createTime;
}
