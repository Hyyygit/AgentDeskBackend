package com.agentdesk.common.security.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AuthUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String realName;
    private String department;
    private String roleCode;
    private Long tenantId;
    private List<String> permissions;
}
