package com.agentdesk.knowledge.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author hyyy
 * @date 2026/5/3 20:36
 * @description 知识草稿持久化对象
 */
@Schema(description = "知识草稿持久化对象")
@Data
public class KnowledgeDraftPO extends BaseEntity {

    @Schema(description = "来源工单ID")
    private Long ticketId;

    @Schema(description = "草稿标题")
    private String title;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "草稿内容")
    private String content;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "置信度")
    private BigDecimal confidence;

    @Schema(description = "审核状态")
    private String reviewStatus;

    @Schema(description = "审核意见")
    private String reviewComment;
}
