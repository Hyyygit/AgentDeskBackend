package com.agentdesk.user.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hyyy
 * @date 2026/5/2 15:11
 * @description 用户持久化对象
 */
@Schema(description = "用户持久化对象")
@Data
public class UserPO extends BaseEntity {

    @Schema(description = "用户编号")
    private String userNo;//用户编号

    @Schema(description = "用户名")
    private String userName;//用户名

    @Schema(description = "用户密码")
    private String password;//密码

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户所属部门")
    private String department;

    @Schema(description = "用户状态")
    private Integer status;
}
