package com.agentdesk.common.web.controller;

import com.agentdesk.common.core.utils.ServletUtils;
import com.agentdesk.common.web.page.PageDomain;
import com.agentdesk.common.web.result.AjaxResult;
import com.github.pagehelper.PageHelper;

/**
 * @author hyyy
 * @date 2026/4/30 00:14
 * @description 控制器基类
 */

public class BaseController {
    //开启分页
    public void startPage(){
        // 先从请求中获取分页数据，并设置到分页对象中
        PageDomain pageDomain = tableSupport();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderByColumn = pageDomain.getOrderByColumn();
        // 开启分页操作
        PageHelper.startPage(pageNum, pageSize, orderByColumn);
    }
    public static PageDomain tableSupport(){//获取请求参数并设置PageDomain对象
        PageDomain pageDomain = new PageDomain();
        Integer pageNum = Integer.parseInt(ServletUtils.getParameter("pageNum", "1"));
        Integer pageSize = Integer.parseInt(ServletUtils.getParameter("pageSize", "10"));
        String orderByColumn = ServletUtils.getParameter("orderByColumn");
        pageDomain.setPageNum(pageNum);
        pageDomain.setPageSize(pageSize);
        pageDomain.setOrderByColumn(orderByColumn);
        return pageDomain;
    }

    public AjaxResult success() {//简单返回一个提示操作成功的信息
        return AjaxResult.success();
    }

    public AjaxResult success(String message) {//返回自定义成功的提示信息
        return AjaxResult.success(message);
    }

    public AjaxResult success(Object data) {//返回带数据的成功的提示信息
        return AjaxResult.success(data);
    }

    public AjaxResult success(String message, Object data) {//返回带数据和自定义成功消息的的成功的提示信息
        return AjaxResult.success(message, data);
    }

    public AjaxResult error() {//返回一个简单的提示操作失败的信息
        return AjaxResult.error();
    }

    public AjaxResult error(String message) {//返回自定义的提示操作失败的信息
        return AjaxResult.error(message);
    }

    public AjaxResult error(Object data) {//返回带数据的提示操作失败的信息
        return AjaxResult.error(data);
    }

    public AjaxResult error(String message, Object data) {//返回带数据和自定义提示操作失败的信息
        return AjaxResult.error(message, data);
    }

    public AjaxResult warn(String message) {//返回自定义的提示操作失败的信息
        return AjaxResult.warn(message);
    }

    public AjaxResult warn(String message, Object data) {//返回带数据和自定义提示操作失败的信息
        return AjaxResult.warn(message, data);
    }

    protected AjaxResult toAjax(int rows) {//判断数据库是否有关操作是否成功，如果数据库的影响行数为0，则视为失败
        return rows > 0 ? success() : error();
    }

    protected AjaxResult toAjax(boolean result) {//根据传进来的值判断是否成功，以此来返回对应的结果
        return result ? success() : error();
    }
}
