package com.agentdesk.knowledge.service;

import com.agentdesk.knowledge.domain.KnowledgeDocPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hyyy
 * @date 2026/5/3 20:25
 * @description 知识服务接口
 */
public interface IKnowledgeDocService extends IService<KnowledgeDocPO> {

    KnowledgeDocPO createDoc(KnowledgeDocPO doc, Long userId);

    KnowledgeDocPO updateDoc(Long docId, KnowledgeDocPO doc, Long userId);

    KnowledgeDocPO getDoc(Long docId);

    List<KnowledgeDocPO> listDocs(String keyword, String category, String status);

    List<KnowledgeDocPO> searchDocs(String keyword, String category);

    void publishDoc(Long docId);

    void archiveDoc(Long docId);

    void deleteDoc(Long docId);
}
