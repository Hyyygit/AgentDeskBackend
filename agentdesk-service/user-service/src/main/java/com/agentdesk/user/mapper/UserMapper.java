package com.agentdesk.user.mapper;

import com.agentdesk.user.domain.UserPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author hyyy
 * @date 2026/5/3 14:51
 * @description 用户持久化对象映射器
 */
public interface UserMapper extends BaseMapper<UserPO> {

    @Select("SELECT * FROM user WHERE user_name = #{username} AND deleted = 0")
    UserPO selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE email = #{email} AND deleted = 0")
    UserPO selectByEmail(@Param("email") String email);
}
