package com.agentdesk.knowledge.service.impl;

import com.agentdesk.knowledge.domain.KnowledgeDocPO;
import com.agentdesk.knowledge.domain.KnowledgeDraftPO;
import com.agentdesk.knowledge.mapper.KnowledgeDraftMapper;
import com.agentdesk.knowledge.service.IKnowledgeDocService;
import com.agentdesk.knowledge.service.IKnowledgeDraftService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeDraftServiceImpl extends ServiceImpl<KnowledgeDraftMapper, KnowledgeDraftPO> implements IKnowledgeDraftService {

    private final IKnowledgeDocService knowledgeDocService;

    @Override
    public KnowledgeDraftPO createDraft(KnowledgeDraftPO draft) {
        save(draft);
        return draft;
    }

    @Override
    public KnowledgeDraftPO updateDraft(Long draftId, KnowledgeDraftPO draft) {
        KnowledgeDraftPO existing = getById(draftId);
        if (existing == null) {
            return null;
        }
        draft.setId(draftId);
        updateById(draft);
        return getById(draftId);
    }

    @Override
    public KnowledgeDraftPO getDraft(Long draftId) {
        return getById(draftId);
    }

    @Override
    public List<KnowledgeDraftPO> listPendingDrafts() {
        return baseMapper.selectByReviewStatus("PENDING");
    }

    @Override
    public void reviewDraft(Long draftId, String reviewStatus, String reviewComment, Long reviewerId) {
        KnowledgeDraftPO draft = getById(draftId);
        if (draft != null) {
            draft.setReviewStatus(reviewStatus);
            draft.setReviewComment(reviewComment);
            updateById(draft);
        }
    }

    @Override
    @Transactional
    public void approveAndPublish(Long draftId, Long reviewerId) {
        KnowledgeDraftPO draft = getById(draftId);
        if (draft == null) {
            return;
        }
        draft.setReviewStatus("APPROVED");
        updateById(draft);

        KnowledgeDocPO doc = new KnowledgeDocPO();
        doc.setTitle(draft.getTitle());
        doc.setContent(draft.getContent());
        doc.setCategory(draft.getCategory());
        doc.setTags(draft.getTags());
        doc.setSourceType("AI_GENERATED");
        knowledgeDocService.createDoc(doc, reviewerId);
    }
}
