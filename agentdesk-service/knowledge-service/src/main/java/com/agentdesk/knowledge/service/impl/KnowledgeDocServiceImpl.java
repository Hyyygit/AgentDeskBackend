package com.agentdesk.knowledge.service.impl;

import com.agentdesk.knowledge.domain.KnowledgeDocPO;
import com.agentdesk.knowledge.mapper.KnowledgeDocMapper;
import com.agentdesk.knowledge.service.IKnowledgeDocService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * @author hyyy
 * @date 2026/5/3 20:30
 * @description 知识服务实现类
 */
@Service
public class KnowledgeDocServiceImpl extends ServiceImpl<KnowledgeDocMapper, KnowledgeDocPO> implements IKnowledgeDocService {

    @Override
    public KnowledgeDocPO createDoc(KnowledgeDocPO doc, Long userId) {
        doc.setDocNo(generateDocNo());
        doc.setStatus("DRAFT");
        doc.setVersion(1);
        doc.setCreatedBy(userId);
        doc.setUpdatedBy(userId);
        save(doc);
        return doc;
    }

    @Override
    public KnowledgeDocPO updateDoc(Long docId, KnowledgeDocPO doc, Long userId) {
        KnowledgeDocPO existing = getById(docId);
        if (existing == null) {
            return null;
        }
        doc.setId(docId);
        doc.setDocNo(existing.getDocNo());
        doc.setVersion(existing.getVersion() + 1);
        doc.setUpdatedBy(userId);
        updateById(doc);
        return getById(docId);
    }

    @Override
    public KnowledgeDocPO getDoc(Long docId) {
        return getById(docId);
    }

    @Override
    public List<KnowledgeDocPO> listDocs(String keyword, String category, String status) {
        LambdaQueryWrapper<KnowledgeDocPO> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(KnowledgeDocPO::getTitle, keyword).or().like(KnowledgeDocPO::getContent, keyword));
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(KnowledgeDocPO::getCategory, category);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(KnowledgeDocPO::getStatus, status);
        }
        wrapper.orderByDesc(KnowledgeDocPO::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<KnowledgeDocPO> searchDocs(String keyword, String category) {
        return baseMapper.searchDocs(keyword, category);
    }

    @Override
    public void publishDoc(Long docId) {
        KnowledgeDocPO doc = getById(docId);
        if (doc != null) {
            doc.setStatus("PUBLISHED");
            updateById(doc);
        }
    }

    @Override
    public void archiveDoc(Long docId) {
        KnowledgeDocPO doc = getById(docId);
        if (doc != null) {
            doc.setStatus("ARCHIVED");
            updateById(doc);
        }
    }

    @Override
    public void deleteDoc(Long docId) {
        removeById(docId);
    }

    private String generateDocNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = 1000 + new Random().nextInt(9000);
        return "KD" + timestamp + random;
    }
}
