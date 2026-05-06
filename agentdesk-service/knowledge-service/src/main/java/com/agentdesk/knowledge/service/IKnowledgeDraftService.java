package com.agentdesk.knowledge.service;

import com.agentdesk.knowledge.domain.KnowledgeDraftPO;

import java.util.List;

public interface IKnowledgeDraftService {

    KnowledgeDraftPO createDraft(KnowledgeDraftPO draft);

    KnowledgeDraftPO updateDraft(Long draftId, KnowledgeDraftPO draft);

    KnowledgeDraftPO getDraft(Long draftId);

    List<KnowledgeDraftPO> listPendingDrafts();

    void reviewDraft(Long draftId, String reviewStatus, String reviewComment, Long reviewerId);

    void approveAndPublish(Long draftId, Long reviewerId);
}
