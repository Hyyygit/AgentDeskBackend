package com.agentdesk.knowledge.controller;

import com.agentdesk.api.knowledge.dto.KnowledgeDocDTO;
import com.agentdesk.api.knowledge.dto.KnowledgeSearchRequest;
import com.agentdesk.knowledge.converter.KnowledgeConverter;
import com.agentdesk.knowledge.domain.KnowledgeDocPO;
import com.agentdesk.knowledge.service.IKnowledgeDocService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/knowledge")
@RequiredArgsConstructor
public class KnowledgeInternalController {

    private final IKnowledgeDocService knowledgeDocService;

    @PostMapping("/retrieve")
    public List<KnowledgeDocDTO> retrieve(@RequestBody KnowledgeSearchRequest request) {
        List<KnowledgeDocPO> list = knowledgeDocService.searchDocs(request.getKeyword(), request.getCategory());
        return list.stream().map(po -> {
            KnowledgeDocDTO dto = new KnowledgeDocDTO();
            dto.setId(po.getId());
            dto.setDocNo(po.getDocNo());
            dto.setTenantId(po.getTenantId());
            dto.setTitle(po.getTitle());
            dto.setCategory(po.getCategory());
            dto.setTags(po.getTags());
            dto.setContent(po.getContent());
            dto.setSummary(po.getSummary());
            dto.setSourceType(po.getSourceType());
            dto.setStatus(po.getStatus());
            dto.setVersion(po.getVersion());
            dto.setCreatedBy(po.getCreatedBy());
            dto.setCreateTime(po.getCreateTime());
            dto.setUpdateTime(po.getUpdateTime());
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/docs/{docId}")
    public KnowledgeDocDTO getDoc(@PathVariable Long docId) {
        KnowledgeDocPO po = knowledgeDocService.getDoc(docId);
        if (po == null) {
            return null;
        }
        KnowledgeDocDTO dto = new KnowledgeDocDTO();
        dto.setId(po.getId());
        dto.setDocNo(po.getDocNo());
        dto.setTenantId(po.getTenantId());
        dto.setTitle(po.getTitle());
        dto.setCategory(po.getCategory());
        dto.setTags(po.getTags());
        dto.setContent(po.getContent());
        dto.setSummary(po.getSummary());
        dto.setSourceType(po.getSourceType());
        dto.setStatus(po.getStatus());
        dto.setVersion(po.getVersion());
        dto.setCreatedBy(po.getCreatedBy());
        dto.setCreateTime(po.getCreateTime());
        dto.setUpdateTime(po.getUpdateTime());
        return dto;
    }
}
