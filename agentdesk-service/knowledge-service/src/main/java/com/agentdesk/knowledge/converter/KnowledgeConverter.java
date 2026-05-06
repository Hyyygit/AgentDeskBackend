package com.agentdesk.knowledge.converter;

import com.agentdesk.knowledge.domain.KnowledgeDocPO;
import com.agentdesk.knowledge.domain.KnowledgeDocVO;
import com.agentdesk.knowledge.domain.KnowledgeDraftPO;
import com.agentdesk.knowledge.domain.KnowledgeDraftVO;
import org.springframework.beans.BeanUtils;

public class KnowledgeConverter {

    public static KnowledgeDocVO toDocVO(KnowledgeDocPO po) {
        if (po == null) {
            return null;
        }
        KnowledgeDocVO vo = new KnowledgeDocVO();
        BeanUtils.copyProperties(po, vo);
        return vo;
    }

    public static KnowledgeDraftVO toDraftVO(KnowledgeDraftPO po) {
        if (po == null) {
            return null;
        }
        KnowledgeDraftVO vo = new KnowledgeDraftVO();
        BeanUtils.copyProperties(po, vo);
        return vo;
    }
}
