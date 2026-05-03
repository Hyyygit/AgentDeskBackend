package com.agentdesk.conversation.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author hyyy
 * @date 2026/5/2 15:17
 * @description 会话持久化对象
 */
@Data
public class ConversationPO extends BaseEntity {

    @Schema(description = "会话编号")
    private String conversationNo;

    @Schema(description = "会话的发起用户")
    private Long userId;

    @Schema(description = "来源渠道")
    private Integer source;

    @Schema(description = "会话状态")
    private Integer status;

    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;
}
