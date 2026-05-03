package com.agentdesk.user.service.impl;

import com.agentdesk.user.domain.UserPO;
import com.agentdesk.user.mapper.UserMapper;
import com.agentdesk.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author hyyy
 * @date 2026/5/3 14:53
 * @description 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO> implements IUserService {
}
