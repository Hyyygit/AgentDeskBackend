package com.agentdesk.user.service;

import com.agentdesk.user.domain.UserPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/3 14:52
 * @description 用户服务
 */
public interface IUserService extends IService<UserPO> {

    UserPO login(String username, String password);

    UserPO register(UserPO user);

    String refreshToken(String refreshToken);

    UserPO getCurrentUser(Long userId);

    List<UserPO> listUsers(String keyword, Integer pageNum, Integer pageSize);

    void updateProfile(UserPO user);

    void changePassword(Long userId, String oldPassword, String newPassword);
}
