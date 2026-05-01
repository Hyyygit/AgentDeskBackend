package com.agentdesk.common.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyyy
 * @date 2026/4/29 21:01
 * @description 基础实体类
 */
@Data
public class BaseEntity implements Serializable {

    // 序列化版本号
    @Serial
    private static final long serialVersionUID = 1L;

    // 主键
    @TableId(type = IdType.AUTO)
    private Long id;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 更新时间
    @TableField(fill = FieldFill.UPDATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    // 逻辑删除
    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleted;
}
