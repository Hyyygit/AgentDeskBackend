package com.agentdesk.user.service.impl;

import com.agentdesk.common.core.enums.ErrorCodeEnum;
import com.agentdesk.common.core.exception.base.BaseException;
import com.agentdesk.common.redis.utils.RedisUtils;
import com.agentdesk.common.security.domain.AuthUser;
import com.agentdesk.common.security.utils.JwtUtils;
import com.agentdesk.user.domain.UserPO;
import com.agentdesk.user.mapper.UserMapper;
import com.agentdesk.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author hyyy
 * @date 2026/5/3 14:53
 * @description 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public UserPO login(String username, String password) {
        UserPO user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BaseException(ErrorCodeEnum.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorCodeEnum.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
        return user;
    }

    @Override
    public UserPO register(UserPO user) {
        UserPO existUser = userMapper.selectByUsername(user.getUserName());
        if (existUser != null) {
            throw new BaseException(ErrorCodeEnum.BAD_REQUEST.getCode(), "用户名已存在");
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            UserPO existEmail = userMapper.selectByEmail(user.getEmail());
            if (existEmail != null) {
                throw new BaseException(ErrorCodeEnum.BAD_REQUEST.getCode(), "邮箱已存在");
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoleCode() == null || user.getRoleCode().isEmpty()) {
            user.setRoleCode("USER");
        }
        if (user.getUserNo() == null || user.getUserNo().isEmpty()) {
            user.setUserNo(UUID.randomUUID().toString().replace("-", ""));
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        save(user);
        return user;
    }

    @Override
    public String refreshToken(String refreshToken) {
        Claims claims = jwtUtils.parseToken(refreshToken);
        Long userId = claims.get("userId", Long.class);
        UserPO user = getById(userId);
        if (user == null) {
            throw new BaseException(ErrorCodeEnum.UNAUTHORIZED.getCode(), "用户不存在");
        }
        AuthUser authUser = new AuthUser();
        authUser.setUserId(user.getId());
        authUser.setUsername(user.getUserName());
        authUser.setRealName(user.getRealName());
        authUser.setDepartment(user.getDepartment());
        authUser.setRoleCode(user.getRoleCode());
        authUser.setTenantId(user.getTenantId());
        return jwtUtils.generateAccessToken(authUser);
    }

    @Override
    public UserPO getCurrentUser(Long userId) {
        UserPO user = getById(userId);
        if (user == null) {
            throw new BaseException(ErrorCodeEnum.NOT_FOUND.getCode(), "用户不存在");
        }
        return user;
    }

    @Override
    public List<UserPO> listUsers(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(UserPO::getUserName, keyword)
                    .or().like(UserPO::getRealName, keyword)
                    .or().like(UserPO::getEmail, keyword));
        }
        wrapper.orderByDesc(UserPO::getCreateTime);
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return list(wrapper);
    }

    @Override
    public void updateProfile(UserPO user) {
        updateById(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        UserPO user = getById(userId);
        if (user == null) {
            throw new BaseException(ErrorCodeEnum.NOT_FOUND.getCode(), "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BaseException(ErrorCodeEnum.BAD_REQUEST.getCode(), "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }
}
