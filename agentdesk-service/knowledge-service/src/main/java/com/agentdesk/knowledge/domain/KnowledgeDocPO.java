package com.agentdesk.knowledge.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author hyyy
 * @date 2026/5/3 20:26
 * @description 知识文档持久化对象
 */
@Schema(description = "知识文档持久化对象")
@Data
public class KnowledgeDocPO extends BaseEntity {

    @Schema(description = "文档编号")
    private String docNo;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "文档标签，使用逗号分隔")
    private String tags;

    @Schema(description = "正文")
    private String content;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "来源类型")
    private String sourceType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "逻辑删除")
    private Boolean deleted;

    @Schema(description = "创建人")
    private Long createdBy;

    @Schema(description = "更新人")
    private Long updatedBy;
}
