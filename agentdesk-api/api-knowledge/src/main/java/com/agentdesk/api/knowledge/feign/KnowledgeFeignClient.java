package com.agentdesk.api.knowledge.feign;

import com.agentdesk.api.knowledge.dto.KnowledgeDocDTO;
import com.agentdesk.api.knowledge.dto.KnowledgeSearchRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "knowledge-service", path = "/internal/knowledge")
public interface KnowledgeFeignClient {

    @PostMapping("/retrieve")
    List<KnowledgeDocDTO> retrieve(@RequestBody KnowledgeSearchRequest request);

    @GetMapping("/docs/{docId}")
    KnowledgeDocDTO getDoc(@PathVariable Long docId);
}
