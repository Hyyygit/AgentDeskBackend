package com.agentdesk.conversation.mapper;

import com.agentdesk.conversation.domain.ConversationPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/3 15:07
 * @description 会话持久化对象
 */
public interface ConversationMapper extends BaseMapper<ConversationPO> {

    @Select("SELECT * FROM conversation WHERE user_id = #{userId} AND deleted = 0 ORDER BY last_message_time DESC")
    List<ConversationPO> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM conversation WHERE conversation_no = #{conversationNo} AND deleted = 0")
    ConversationPO selectByConversationNo(@Param("conversationNo") String conversationNo);
}
