package com.agentdesk.user.controller;

import com.agentdesk.common.core.enums.ErrorCodeEnum;
import com.agentdesk.common.core.exception.base.BaseException;
import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.page.TableDataInfo;
import com.agentdesk.common.web.result.AjaxResult;
import com.agentdesk.user.converter.UserConverter;
import com.agentdesk.user.domain.UserPO;
import com.agentdesk.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author hyyy
 * @date 2026/5/3 14:47
 * @description 用户相关操作控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;
    
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/profile")
    public AjaxResult profile() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BaseException(ErrorCodeEnum.UNAUTHORIZED.getCode(), "请先登录");
        }
        UserPO user = userService.getCurrentUser(userId);
        return success(UserConverter.toVO(user));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/profile")
    public AjaxResult updateProfile(@RequestBody UserPO user) {
        Long userId = UserContext.getCurrentUserId();
        user.setId(userId);
        userService.updateProfile(user);
        return success("更新成功");
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public AjaxResult changePassword(@RequestBody Map<String, String> body) {
        Long userId = UserContext.getCurrentUserId();
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        userService.changePassword(userId, oldPassword, newPassword);
        return success("密码修改成功");
    }

    @Operation(summary = "用户列表")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(required = false) String keyword) {
        startPage();
        List<UserPO> list = userService.listUsers(keyword, null, null);
        TableDataInfo pageInfo = TableDataInfo.getTableDataInfo(list);
        return success("查询成功", pageInfo);
    }
}
