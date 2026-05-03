package com.agentdesk.common.tool.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hyyy
 * @date 2026/5/3 21:15
 * @description
 */
@Data
public class PromptTemplatePO extends BaseEntity {

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "Agent名称")
    private String agentName;

    @Schema(description = "SYSTEM/USER")
    private String promptType;

    @Schema(description = "模板内容")
    private String templateContent;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "状态")
    private String status;
}
