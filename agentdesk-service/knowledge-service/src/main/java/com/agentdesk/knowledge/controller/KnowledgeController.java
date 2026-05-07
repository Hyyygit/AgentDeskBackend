package com.agentdesk.knowledge.controller;

import com.agentdesk.common.security.context.UserContext;
import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.page.TableDataInfo;
import com.agentdesk.common.web.result.AjaxResult;
import com.agentdesk.knowledge.converter.KnowledgeConverter;
import com.agentdesk.knowledge.domain.KnowledgeDocPO;
import com.agentdesk.knowledge.domain.KnowledgeDraftPO;
import com.agentdesk.knowledge.service.IKnowledgeDocService;
import com.agentdesk.knowledge.service.IKnowledgeDraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hyyy
 * @date 2026/5/3 20:33
 * @description RAG知识管理
 */
@Tag(name = "RAG知识管理")
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeController extends BaseController {

    private final IKnowledgeDocService knowledgeDocService;
    private final IKnowledgeDraftService knowledgeDraftService;

    @Operation(summary = "创建知识文档")
    @PostMapping("/docs")
    public AjaxResult createDoc(@RequestBody KnowledgeDocPO doc) {
        Long userId = UserContext.getCurrentUserId();
        KnowledgeDocPO result = knowledgeDocService.createDoc(doc, userId);
        return success("创建成功", KnowledgeConverter.toDocVO(result));
    }

    @Operation(summary = "更新知识文档")
    @PutMapping("/docs/{docId}")
    public AjaxResult updateDoc(@PathVariable Long docId, @RequestBody KnowledgeDocPO doc) {
        Long userId = UserContext.getCurrentUserId();
        KnowledgeDocPO result = knowledgeDocService.updateDoc(docId, doc, userId);
        if (result == null) {
            return error("文档不存在");
        }
        return success("更新成功", KnowledgeConverter.toDocVO(result));
    }

    @Operation(summary = "知识文档列表")
    @GetMapping("/docs")
    public AjaxResult listDocs(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String category,
                               @RequestParam(required = false) String status) {
        startPage();
        List<KnowledgeDocPO> list = knowledgeDocService.listDocs(keyword, category, status);
        List<?> voList = list.stream().map(KnowledgeConverter::toDocVO).collect(Collectors.toList());
        return success("查询成功", TableDataInfo.getTableDataInfo(voList));
    }

    @Operation(summary = "知识文档详情")
    @GetMapping("/docs/{docId}")
    public AjaxResult getDoc(@PathVariable Long docId) {
        KnowledgeDocPO doc = knowledgeDocService.getDoc(docId);
        if (doc == null) {
            return error("文档不存在");
        }
        return success(KnowledgeConverter.toDocVO(doc));
    }

    @Operation(summary = "发布知识文档")
    @PutMapping("/docs/{docId}/publish")
    public AjaxResult publishDoc(@PathVariable Long docId) {
        knowledgeDocService.publishDoc(docId);
        return success("发布成功");
    }

    @Operation(summary = "归档知识文档")
    @PutMapping("/docs/{docId}/archive")
    public AjaxResult archiveDoc(@PathVariable Long docId) {
        knowledgeDocService.archiveDoc(docId);
        return success("归档成功");
    }

    @Operation(summary = "删除知识文档")
    @DeleteMapping("/docs/{docId}")
    public AjaxResult deleteDoc(@PathVariable Long docId) {
        knowledgeDocService.deleteDoc(docId);
        return success("删除成功");
    }

    @Operation(summary = "搜索知识文档")
    @GetMapping("/search")
    public AjaxResult searchDocs(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String category) {
        List<KnowledgeDocPO> list = knowledgeDocService.searchDocs(keyword, category);
        List<?> voList = list.stream().map(KnowledgeConverter::toDocVO).collect(Collectors.toList());
        return success(voList);
    }

    @Operation(summary = "创建知识草稿")
    @PostMapping("/drafts")
    public AjaxResult createDraft(@RequestBody KnowledgeDraftPO draft) {
        KnowledgeDraftPO result = knowledgeDraftService.createDraft(draft);
        return success("创建成功", KnowledgeConverter.toDraftVO(result));
    }

    @Operation(summary = "待审核草稿列表")
    @GetMapping("/drafts")
    public AjaxResult listDrafts() {
        List<KnowledgeDraftPO> list = knowledgeDraftService.listPendingDrafts();
        List<?> voList = list.stream().map(KnowledgeConverter::toDraftVO).collect(Collectors.toList());
        return success(voList);
    }

    @Operation(summary = "草稿详情")
    @GetMapping("/drafts/{draftId}")
    public AjaxResult getDraft(@PathVariable Long draftId) {
        KnowledgeDraftPO draft = knowledgeDraftService.getDraft(draftId);
        if (draft == null) {
            return error("草稿不存在");
        }
        return success(KnowledgeConverter.toDraftVO(draft));
    }

    @Operation(summary = "审核草稿")
    @PostMapping("/drafts/{draftId}/review")
    public AjaxResult reviewDraft(@PathVariable Long draftId,
                                  @RequestParam String reviewStatus,
                                  @RequestParam(required = false) String reviewComment) {
        Long userId = UserContext.getCurrentUserId();
        knowledgeDraftService.reviewDraft(draftId, reviewStatus, reviewComment, userId);
        return success("审核完成");
    }

    @Operation(summary = "审核通过并发布")
    @PostMapping("/drafts/{draftId}/approve")
    public AjaxResult approveAndPublish(@PathVariable Long draftId) {
        Long userId = UserContext.getCurrentUserId();
        knowledgeDraftService.approveAndPublish(draftId, userId);
        return success("审核通过并发布成功");
    }
}
