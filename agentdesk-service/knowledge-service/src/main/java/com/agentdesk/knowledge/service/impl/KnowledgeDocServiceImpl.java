package com.agentdesk.knowledge.service.impl;

import com.agentdesk.knowledge.domain.KnowledgeDocPO;
import com.agentdesk.knowledge.mapper.KnowledgeDocMapper;
import com.agentdesk.knowledge.service.IKnowledgeDocService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author hyyy
 * @date 2026/5/3 20:30
 * @description 知识服务实现类
 */
@Service
public class KnowledgeDocServiceImpl extends ServiceImpl<KnowledgeDocMapper, KnowledgeDocPO> implements IKnowledgeDocService {
}
