package com.agentdesk.common.web.page;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/2 16:35
 * @description
 */
@AllArgsConstructor
@Getter
@Setter
public class TableDataInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long total;
    private List<?> rows;

    public static TableDataInfo getTableDataInfo(List<?> list){
        return new TableDataInfo(new PageInfo(list).getTotal(), list);
    }
}
