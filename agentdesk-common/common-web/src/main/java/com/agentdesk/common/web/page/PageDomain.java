package com.agentdesk.common.web.page;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hyyy
 * @date 2026/5/1 21:58
 * @description 分页参数
 */
@Setter
@Getter
public class PageDomain {

    private Integer pageNum;
    private Integer pageSize;
    private String orderByColumn;
    private String isAsc;
}
