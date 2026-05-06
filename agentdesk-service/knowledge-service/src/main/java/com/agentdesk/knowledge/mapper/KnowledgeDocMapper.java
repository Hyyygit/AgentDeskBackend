package com.agentdesk.knowledge.mapper;

import com.agentdesk.knowledge.domain.KnowledgeDocPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/3 20:31
 * @description 知识文档Mapper
 */
public interface KnowledgeDocMapper extends BaseMapper<KnowledgeDocPO> {

    @Select("SELECT * FROM knowledge_doc WHERE deleted = 0 AND status = 'PUBLISHED' ORDER BY create_time DESC")
    List<KnowledgeDocPO> selectPublishedDocs();

    List<KnowledgeDocPO> searchDocs(@Param("keyword") String keyword, @Param("category") String category);

    @Select("SELECT * FROM knowledge_doc WHERE doc_no = #{docNo} AND deleted = 0")
    KnowledgeDocPO selectByDocNo(@Param("docNo") String docNo);
}
