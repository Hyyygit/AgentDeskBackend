package com.agentdesk.knowledge.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyyy
 * @date 2026/5/3 20:33
 * @description RAG知识管理
 */
@Tag(name = "RAG知识管理")
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {
}
