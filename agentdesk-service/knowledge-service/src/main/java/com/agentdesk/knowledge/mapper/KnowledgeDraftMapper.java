package com.agentdesk.knowledge.mapper;

import com.agentdesk.knowledge.domain.KnowledgeDraftPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface KnowledgeDraftMapper extends BaseMapper<KnowledgeDraftPO> {

    @Select("SELECT * FROM knowledge_draft WHERE review_status = #{reviewStatus} ORDER BY create_time DESC")
    List<KnowledgeDraftPO> selectByReviewStatus(@Param("reviewStatus") String reviewStatus);

    @Select("SELECT * FROM knowledge_draft WHERE source_ticket_id = #{ticketId}")
    List<KnowledgeDraftPO> selectBySourceTicketId(@Param("ticketId") Long ticketId);
}
