package com.agentdesk.conversation.converter;

import com.agentdesk.conversation.domain.ConversationMessagePO;
import com.agentdesk.conversation.domain.ConversationMessageVO;
import com.agentdesk.conversation.domain.ConversationPO;
import com.agentdesk.conversation.domain.ConversationVO;

import java.time.ZoneId;
import java.util.Date;

/**
 * @author hyyy
 * @date 2026/5/6
 * @description 会话数据转换器
 */
public class ConversationConverter {

    public static ConversationVO toVO(ConversationPO po) {
        if (po == null) {
            return null;
        }
        ConversationVO vo = new ConversationVO();
        vo.setId(po.getId());
        vo.setConversationNo(po.getConversationNo());
        vo.setUserId(po.getUserId());
        vo.setSource(po.getSource());
        vo.setStatus(po.getStatus());
        if (po.getLastMessageTime() != null) {
            vo.setLastMessageTime(Date.from(po.getLastMessageTime().atZone(ZoneId.systemDefault()).toInstant()));
        }
        vo.setCreateTime(po.getCreateTime());
        return vo;
    }

    public static ConversationMessageVO toMessageVO(ConversationMessagePO po) {
        if (po == null) {
            return null;
        }
        ConversationMessageVO vo = new ConversationMessageVO();
        vo.setId(po.getId());
        vo.setConversationId(po.getConversationId());
        vo.setRequestId(po.getRequestId());
        vo.setSenderId(po.getSenderId());
        vo.setSenderType(po.getSenderType());
        vo.setMessageType(po.getMessageType());
        vo.setContent(po.getContent());
        vo.setExtraJson(po.getExtraJson());
        vo.setCreateTime(po.getCreateTime());
        return vo;
    }
}
