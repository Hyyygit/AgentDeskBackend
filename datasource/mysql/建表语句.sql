# agentdesk-ticket库

create table ticket
(
    id               bigint auto_increment comment '主键'
        primary key,
    ticket_no        varchar(64)                        not null comment '工单编号',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    deleted          int      default 0                 not null comment '是否删除，0为未删除，1为已删除',
    ticket_category  int      default 2                 not null comment '工单的类型',
    priority         int      default 4                 not null comment '工单的优先级',
    status           int      default 1                 not null comment '工单状态',
    user_id          bigint                             null comment '提单的用户主键ID',
    conversation_id  bigint                             null comment '关联的会话的ID',
    source           int      default 1                 not null comment '工单来源',
    summary          varchar(255)                       null comment '工单摘要',
    description      text                               null comment '工单详细描述',
    assigned_group   varchar(32)                        null comment '工单被分配去处理的组',
    assigned_user_id bigint                             null comment '工单被分配去处理的人的ID',
    dead_line        datetime                           null comment '工单预期处理截止时间',
    resolved_time    datetime                           null comment '工单解决时间',
    closed_time      datetime                           null comment '工单关闭时间',
    constraint ticket_no
        unique (ticket_no)
)
    comment '保存工单信息的表';

# agentdesk-user库

create table user
(
    id          bigint auto_increment comment '主键id'
        primary key,
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     int         default 0                 not null comment '逻辑删除键',
    user_no     varchar(64)                           not null comment '用户编号',
    user_name   varchar(32)                           not null comment '用户名',
    password    varchar(32) default '123456'          not null comment '用户密码',
    email       varchar(128)                          null comment '用户邮箱',
    department  varchar(64)                           not null comment '用户所属部门',
    status      int                                   null comment '用户状态',
    constraint email
        unique (email),
    constraint user_pk
        unique (user_no),
    constraint user_pk_2
        unique (user_name)
)
    comment '保存用户信息的表';

create table user_conversation_info
(
    id              bigint auto_increment comment '主键id'
        primary key,
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted         int      default 0                 not null comment '逻辑删除键',
    user_id         bigint                             not null comment '用户主键id',
    conversation_id bigint                             not null comment '该用户参与的会话的主键id'
)
    comment '关联用户和其参与的会话的表';

# agentdesk-conversation库

create table conversation
(
    id                bigint auto_increment comment '主键id'
        primary key,
    create_time       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted           int      default 0                 not null comment '逻辑删除键',
    conversation_no   varchar(64)                        not null comment '会话编号',
    user_id           bigint                             null comment '会话的发起用户',
    source            int      default 1                 null comment '来源渠道',
    status            int      default 1                 null comment '会话状态',
    last_message_time datetime default CURRENT_TIMESTAMP null comment '最后消息时间',
    constraint conversation_no
        unique (conversation_no)
)
    comment '存储会话的表';

create table conversation_message
(
    id              bigint auto_increment comment '主键id'
        primary key,
    created_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted         int      default 0                 not null comment '逻辑删除键',
    conversation_id bigint                             not null comment '消息所属的会话的主键id',
    request_id      varchar(64)                        not null comment '本次请求的全局ID',
    send_id         bigint                             not null comment '发送这条消息的人的主键id',
    send_type       int      default 1                 not null comment '这条消息是谁发出的',
    message_type    int      default 1                 not null comment '消息的类型',
    content         text                               not null comment '消息内容',
    extra_json      json                               null comment '扩展信息'
)
    comment '存储会话中的消息的表';

create table conversation_ticket_info
(
    id              bigint auto_increment comment '主键id'
        primary key,
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted         int      default 0                 not null comment '逻辑删除键',
    conversation_id bigint                             not null comment '会话主键id',
    ticket_id       bigint                             not null comment '与之关联的工单主键id'
)
    comment '关联会话和工单信息';

# agentdesk-knowledge库

create table knowledge_doc
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    doc_no       varchar(64)                           not null comment '文档编号',
    title        varchar(255)                          not null comment '标题',
    category     varchar(64)                           not null comment '分类',
    tags         varchar(512)                          null comment '文档标签，使用逗号分隔',
    content      longtext                              not null comment '正文',
    summary      text                                  null comment '摘要',
    source_type  varchar(32) default 'MANUAL'          not null comment '来源类型',
    status       varchar(32) default 'PUBLISHED'       not null comment '状态',
    version      int         default 1                 not null comment '版本号',
    deleted      tinyint     default 0                 not null comment '逻辑删除',
    created_by   bigint                                null comment '创建人',
    updated_by   bigint                                null comment '更新人',
    created_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_time datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint doc_no
        unique (doc_no)
)
    comment '知识库文档表';

create table knowledge_draft
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    ticket_id      bigint                                null comment '来源工单ID',
    title          varchar(255)                          not null comment '草稿标题',
    category       varchar(64)                           not null comment '分类',
    content        longtext                              not null comment '草稿内容',
    tags           varchar(512)                          null comment '标签',
    confidence     decimal(5, 2)                         null comment '置信度',
    review_status  varchar(32) default 'PENDING'         not null comment '审核状态',
    review_comment varchar(500)                          null comment '审核意见',
    created_time   datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_time   datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '知识草稿表';

# agentdesk-log库

create table agent_run_log
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    request_id      varchar(64)                        not null comment '本次请求的全局ID',
    conversation_id bigint                             null comment '会话ID',
    ticket_id       bigint                             null comment '工单ID',
    agent_name      varchar(64)                        not null comment 'Agent名称',
    run_status      varchar(32)                        not null comment '执行状态',
    input_payload   json                               null comment '输入参数',
    output_payload  json                               null comment '输出参数',
    confidence      decimal(5, 2)                      null comment '置信度',
    gate_result     varchar(32)                        null comment '门控结果',
    cost_tokens     int                                null comment 'token消耗',
    latency_ms      int                                null comment '耗时毫秒',
    error_code      varchar(64)                        null comment '错误码',
    error_message   varchar(500)                       null comment '错误信息',
    created_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment 'Agent运行日志表';

create table human_handoff_task
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    request_id      varchar(64)                           not null comment '本次请求的全局ID',
    ticket_id       bigint                                null comment '关联工单ID',
    conversation_id bigint                                null comment '关联会话ID',
    handoff_reason  varchar(255)                          not null comment '转人工原因',
    handoff_type    int                                   not null comment '转人工类型 LOW_CONFIDENCE/SENSITIVE/TOOL_FAILED',
    assigned_group  varchar(32)                           null comment '指派组',
    status          varchar(32) default 'PENDING'         not null comment '状态',
    deleted         tinyint     default 0                 not null comment '逻辑删除',
    created_time    datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_time    datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '人工兜底任务表';

create table ticket_action_log
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    ticket_id     bigint                             not null comment '工单ID',
    action_type   int      default 1                 not null comment '动作类型',
    operator_type int                                not null comment '操作者类型 USER/AGENT/HUMAN/SYSTEM',
    operator_id   bigint                             null comment '操作者ID',
    action_detail json                               null comment '动作详情',
    created_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '工单操作记录表';

# agentdesk-tool库

create table prompt_template
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    template_code    varchar(64)                           not null comment '模板编码',
    agent_name       varchar(64)                           not null comment 'Agent名称',
    prompt_type      varchar(32)                           not null comment 'SYSTEM/USER',
    template_content longtext                              not null comment '模板内容',
    version          int         default 1                 not null comment '版本号',
    status           varchar(32) default 'ENABLED'         not null comment '状态',
    created_time     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_time     datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_template_code_version
        unique (template_code, version)
)
    comment 'Prompt模板表';
