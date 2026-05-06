package com.agentdesk.conversation.mapper;

import com.agentdesk.conversation.domain.ConversationMessagePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/6
 * @description 会话消息持久化对象
 */
public interface ConversationMessageMapper extends BaseMapper<ConversationMessagePO> {

    @Select("SELECT * FROM conversation_message WHERE conversation_id = #{conversationId} AND deleted = 0 ORDER BY create_time ASC")
    List<ConversationMessagePO> selectByConversationId(@Param("conversationId") Long conversationId);
}
