-- ============================================================
-- AgentDesk 智能工单系统 - 完整建表语句
-- 数据库: MySQL 8.0+
-- ============================================================

-- ============================================================
-- 1. agentdesk-ticket 库：工单管理
-- ============================================================
CREATE DATABASE IF NOT EXISTS `agentdesk-ticket` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `agentdesk-ticket`;

CREATE TABLE `ticket`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ticket_no`        VARCHAR(64)  NOT NULL COMMENT '工单编号',
    `tenant_id`        BIGINT       NOT NULL DEFAULT 0 COMMENT '租户ID',
    `user_id`          BIGINT       NULL COMMENT '提单的用户主键ID',
    `conversation_id`  BIGINT       NULL COMMENT '关联的会话的ID',
    `ticket_category`  INT          NOT NULL DEFAULT 2 COMMENT '工单类型(1账号访问 2系统Bug 3网络故障 4财务流程 5权限申请 6投诉 7咨询 8通用支持)',
    `priority`         INT          NOT NULL DEFAULT 4 COMMENT '工单优先级(1-P1 2-P2 3-P3 4-P4)',
    `status`           INT          NOT NULL DEFAULT 1 COMMENT '工单状态(1新建 2已分诊 3已决策 4处理中 5等待人工 6已解决 7已关闭)',
    `source`           INT          NOT NULL DEFAULT 1 COMMENT '工单来源(1-AI Agent)',
    `summary`          VARCHAR(255) NULL COMMENT '工单摘要',
    `description`      TEXT         NULL COMMENT '工单详细描述',
    `assigned_group`   VARCHAR(64)  NULL COMMENT '工单被分配去处理的组',
    `assigned_user_id` BIGINT       NULL COMMENT '工单被分配去处理的人的ID',
    `dead_line`        DATETIME     NULL COMMENT '工单预期处理截止时间(SLA)',
    `resolved_time`    DATETIME     NULL COMMENT '工单解决时间',
    `closed_time`      DATETIME     NULL COMMENT '工单关闭时间',
    `deleted`          INT          NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ticket_no` (`ticket_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_status` (`status`),
    KEY `idx_category` (`ticket_category`),
    KEY `idx_tenant_user` (`tenant_id`, `user_id`),
    KEY `idx_assigned_user` (`assigned_user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='工单主表';

CREATE TABLE `ticket_action_log`
(
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `ticket_id`     BIGINT      NOT NULL COMMENT '工单ID',
    `action_type`   INT         NOT NULL DEFAULT 1 COMMENT '动作类型(1创建 2分诊 3决策 4处理 5等待人工 6解决 7关闭)',
    `operator_type` INT         NOT NULL COMMENT '操作者类型(1-USER 2-AGENT 3-HUMAN 4-SYSTEM)',
    `operator_id`   BIGINT      NULL COMMENT '操作者ID',
    `action_detail` JSON        NULL COMMENT '动作详情',
    `deleted`       INT         NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_ticket_id` (`ticket_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='工单操作记录表';

-- ============================================================
-- 2. agentdesk-user 库：用户管理
-- ============================================================
CREATE DATABASE IF NOT EXISTS `agentdesk-user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `agentdesk-user`;

CREATE TABLE `user`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '租户ID',
    `user_no`     VARCHAR(64)  NOT NULL COMMENT '用户编号',
    `user_name`   VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL DEFAULT '123456' COMMENT '用户密码(BCrypt加密)',
    `real_name`   VARCHAR(64)  NULL COMMENT '真实姓名',
    `email`       VARCHAR(128) NULL COMMENT '用户邮箱',
    `phone`       VARCHAR(20)  NULL COMMENT '手机号',
    `department`  VARCHAR(64)  NULL COMMENT '用户所属部门',
    `role_code`   VARCHAR(64)  NULL DEFAULT 'USER' COMMENT '角色编码(ADMIN/MANAGER/AGENT/USER)',
    `status`      INT          NOT NULL DEFAULT 1 COMMENT '用户状态(1正常 0停用)',
    `deleted`     INT          NOT NULL DEFAULT 0 COMMENT '逻辑删除键',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_no` (`user_no`),
    UNIQUE KEY `uk_user_name` (`user_name`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_department` (`department`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `user_conversation_info`
(
    `id`              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`       BIGINT   NOT NULL DEFAULT 0 COMMENT '租户ID',
    `user_id`         BIGINT   NOT NULL COMMENT '用户主键ID',
    `conversation_id` BIGINT   NOT NULL COMMENT '该用户参与的会话的主键ID',
    `deleted`         INT      NOT NULL DEFAULT 0 COMMENT '逻辑删除键',
    `create_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_conversation_id` (`conversation_id`),
    UNIQUE KEY `uk_user_conversation` (`user_id`, `conversation_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='关联用户和其参与的会话的表';

-- ============================================================
-- 3. agentdesk-conversation 库：会话管理
-- ============================================================
CREATE DATABASE IF NOT EXISTS `agentdesk-conversation` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `agentdesk-conversation`;

CREATE TABLE `conversation`
(
    `id`                BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`         BIGINT      NOT NULL DEFAULT 0 COMMENT '租户ID',
    `conversation_no`   VARCHAR(64) NOT NULL COMMENT '会话编号',
    `user_id`           BIGINT      NULL COMMENT '会话的发起用户',
    `source`            INT         NULL DEFAULT 1 COMMENT '来源渠道(1-WEB)',
    `status`            INT         NULL DEFAULT 1 COMMENT '会话状态(1活跃 2已关闭)',
    `last_message_time` DATETIME    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后消息时间',
    `deleted`           INT         NOT NULL DEFAULT 0 COMMENT '逻辑删除键',
    `create_time`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_conversation_no` (`conversation_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tenant_user` (`tenant_id`, `user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='会话表';

CREATE TABLE `conversation_message`
(
    `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `conversation_id` BIGINT      NOT NULL COMMENT '消息所属的会话的主键ID',
    `request_id`      VARCHAR(64) NOT NULL COMMENT '本次请求的全局ID',
    `sender_id`       BIGINT      NOT NULL COMMENT '发送这条消息的人的主键ID(系统=0)',
    `sender_type`     INT         NOT NULL DEFAULT 1 COMMENT '消息发送者类型(1-USER 2-AGENT 3-HUMAN 4-SYSTEM)',
    `message_type`    INT         NOT NULL DEFAULT 1 COMMENT '消息的类型(1-文本 2-图片 3-文件)',
    `content`         TEXT        NOT NULL COMMENT '消息内容',
    `extra_json`      JSON        NULL COMMENT '扩展信息',
    `deleted`         INT         NOT NULL DEFAULT 0 COMMENT '逻辑删除键',
    `create_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_request_id` (`request_id`),
    KEY `idx_conversation_time` (`conversation_id`, `create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='会话消息表';

CREATE TABLE `conversation_ticket_info`
(
    `id`              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`       BIGINT   NOT NULL DEFAULT 0 COMMENT '租户ID',
    `conversation_id` BIGINT   NOT NULL COMMENT '会话主键ID',
    `ticket_id`       BIGINT   NOT NULL COMMENT '与之关联的工单主键ID',
    `deleted`         INT      NOT NULL DEFAULT 0 COMMENT '逻辑删除键',
    `create_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_ticket_id` (`ticket_id`),
    UNIQUE KEY `uk_conversation_ticket` (`conversation_id`, `ticket_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='关联会话和工单信息';

-- ============================================================
-- 4. agentdesk-knowledge 库：知识库管理
-- ============================================================
CREATE DATABASE IF NOT EXISTS `agentdesk-knowledge` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `agentdesk-knowledge`;

CREATE TABLE `knowledge_doc`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '租户ID',
    `doc_no`      VARCHAR(64)  NOT NULL COMMENT '文档编号',
    `title`       VARCHAR(255) NOT NULL COMMENT '标题',
    `category`    VARCHAR(64)  NOT NULL COMMENT '分类',
    `tags`        VARCHAR(512) NULL COMMENT '文档标签，使用逗号分隔',
    `content`     LONGTEXT     NOT NULL COMMENT '正文',
    `summary`     TEXT         NULL COMMENT '摘要',
    `source_type` VARCHAR(32)  NOT NULL DEFAULT 'MANUAL' COMMENT '来源类型(MANUAL/AI_GENERATED)',
    `status`      VARCHAR(32)  NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态(DRAFT/PUBLISHED/ARCHIVED)',
    `version`     INT          NOT NULL DEFAULT 1 COMMENT '版本号',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `created_by`  BIGINT       NULL COMMENT '创建人',
    `updated_by`  BIGINT       NULL COMMENT '更新人',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_doc_no` (`doc_no`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_tenant` (`tenant_id`),
    FULLTEXT KEY `ft_title_content` (`title`, `content`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='知识库文档表';

CREATE TABLE `knowledge_draft`
(
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`      BIGINT        NOT NULL DEFAULT 0 COMMENT '租户ID',
    `source_ticket_id` BIGINT      NULL COMMENT '来源工单ID',
    `title`          VARCHAR(255)  NOT NULL COMMENT '草稿标题',
    `category`       VARCHAR(64)   NOT NULL COMMENT '分类',
    `content`        LONGTEXT      NOT NULL COMMENT '草稿内容',
    `tags`           VARCHAR(512)  NULL COMMENT '标签',
    `confidence`     DECIMAL(5, 2) NULL COMMENT '置信度',
    `review_status`  VARCHAR(32)   NOT NULL DEFAULT 'PENDING' COMMENT '审核状态(PENDING/APPROVED/REJECTED)',
    `review_comment` VARCHAR(500)  NULL COMMENT '审核意见',
    `deleted`        TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_source_ticket_id` (`source_ticket_id`),
    KEY `idx_review_status` (`review_status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='知识草稿表';

-- ============================================================
-- 5. agentdesk-log 库：审计日志
-- ============================================================
CREATE DATABASE IF NOT EXISTS `agentdesk-log` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `agentdesk-log`;

CREATE TABLE `agent_run_log`
(
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `request_id`      VARCHAR(64)   NOT NULL COMMENT '本次请求的全局ID',
    `conversation_id` BIGINT        NULL COMMENT '会话ID',
    `ticket_id`       BIGINT        NULL COMMENT '工单ID',
    `agent_name`      VARCHAR(64)   NOT NULL COMMENT 'Agent名称',
    `run_status`      VARCHAR(32)   NOT NULL COMMENT '执行状态(SUCCESS/FAILED/TIMEOUT)',
    `input_payload`   JSON          NULL COMMENT '输入参数',
    `output_payload`  JSON          NULL COMMENT '输出参数',
    `confidence`      DECIMAL(5, 2) NULL COMMENT '置信度',
    `gate_result`     VARCHAR(32)   NULL COMMENT '门控结果(PASS/DEGRADED/BLOCKED/HUMAN_HANDOFF)',
    `cost_tokens`     INT           NULL COMMENT 'token消耗',
    `latency_ms`      INT           NULL COMMENT '耗时毫秒',
    `error_code`      VARCHAR(64)   NULL COMMENT '错误码',
    `error_message`   VARCHAR(500)  NULL COMMENT '错误信息',
    `deleted`         INT           NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_request_id` (`request_id`),
    KEY `idx_agent_name` (`agent_name`),
    KEY `idx_run_status` (`run_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Agent运行日志表';

CREATE TABLE `human_handoff_task`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `request_id`      VARCHAR(64)  NOT NULL COMMENT '本次请求的全局ID',
    `ticket_id`       BIGINT       NULL COMMENT '关联工单ID',
    `conversation_id` BIGINT       NULL COMMENT '关联会话ID',
    `handoff_reason`  VARCHAR(255) NOT NULL COMMENT '转人工原因',
    `handoff_type`    INT          NOT NULL COMMENT '转人工类型(1-LOW_CONFIDENCE 2-SENSITIVE 3-TOOL_FAILED)',
    `assigned_group`  VARCHAR(64)  NULL COMMENT '指派组',
    `status`          VARCHAR(32)  NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING/PROCESSING/RESOLVED)',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_request_id` (`request_id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='人工兜底任务表';

-- ============================================================
-- 6. agentdesk-tool 库：工具与配置
-- ============================================================
CREATE DATABASE IF NOT EXISTS `agentdesk-tool` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `agentdesk-tool`;

CREATE TABLE `prompt_template`
(
    `id`               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `template_code`    VARCHAR(64) NOT NULL COMMENT '模板编码',
    `agent_name`       VARCHAR(64) NOT NULL COMMENT 'Agent名称',
    `prompt_type`      VARCHAR(32) NOT NULL COMMENT '类型(SYSTEM/USER)',
    `template_content` LONGTEXT    NOT NULL COMMENT '模板内容',
    `version`          INT         NOT NULL DEFAULT 1 COMMENT '版本号',
    `status`           VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态(ENABLED/DISABLED)',
    `deleted`          INT         NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code_version` (`template_code`, `version`),
    KEY `idx_agent_name` (`agent_name`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Prompt模板表';
