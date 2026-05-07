package com.agentdesk.user.controller;

import com.agentdesk.api.user.dto.LoginRequest;
import com.agentdesk.api.user.dto.LoginResponse;
import com.agentdesk.api.user.dto.RegisterRequest;
import com.agentdesk.common.security.domain.AuthUser;
import com.agentdesk.common.security.utils.JwtUtils;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.result.AjaxResult;
import com.agentdesk.user.converter.UserConverter;
import com.agentdesk.user.domain.UserPO;
import com.agentdesk.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author hyyy
 * @date 2026/5/6
 * @description 认证相关操作控制器
 */
@Tag(name = "用户认证")
@RestController
@RequestMapping("/user")
public class AuthController extends BaseController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public AjaxResult login(@Valid @RequestBody LoginRequest request) {
        UserPO user = userService.login(request.getUsername(), request.getPassword());

        AuthUser authUser = new AuthUser();
        authUser.setUserId(user.getId());
        authUser.setUsername(user.getUserName());
        authUser.setRealName(user.getRealName());
        authUser.setDepartment(user.getDepartment());
        authUser.setRoleCode(user.getRoleCode());
        authUser.setTenantId(user.getTenantId());

        String accessToken = jwtUtils.generateAccessToken(authUser);
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtils.getAccessTokenExpiration());
        response.setUser(UserConverter.toDTO(user));

        return success("登录成功", response);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public AjaxResult register(@Valid @RequestBody RegisterRequest request) {
        UserPO user = new UserPO();
        user.setUserName(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRealName(request.getRealName());
        user.setDepartment(request.getDepartment());
        userService.register(user);
        return success("注册成功");
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    public AjaxResult refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        String newAccessToken = userService.refreshToken(refreshToken);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtils.getAccessTokenExpiration());

        return success("刷新成功", response);
    }
}
