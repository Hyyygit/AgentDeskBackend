package com.agentdesk.conversation.domain;

import com.agentdesk.common.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hyyy
 * @date 2026/5/3 20:21
 * @description 会话消息持久化对象
 */
@Data
public class ConversationMessagePO extends BaseEntity {

    @Schema(description = "消息所属的会话的主键id")
    private Long conversationId;

    @Schema(description = "本次请求的全局ID")
    private String requestId;

    @Schema(description = "发送这条消息的人的主键id")
    private Long sendId;

    @Schema(description = "这条消息是谁发出的")
    private Integer sendType;

    @Schema(description = "消息的类型")
    private Integer messageType;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "扩展信息")
    private String extraJson;
}
