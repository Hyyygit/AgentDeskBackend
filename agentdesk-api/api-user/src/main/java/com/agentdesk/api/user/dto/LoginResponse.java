package com.agentdesk.api.user.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    private UserDTO user;
}
