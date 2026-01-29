-- ========================================
-- UniPush 统一消息推送平台 - 数据库初始化脚本
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `unipush` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `unipush`;

-- ========================================
-- 1. 系统用户表（管理后台）
-- ========================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)     NOT NULL COMMENT '用户名',
    `password`        VARCHAR(100)    NOT NULL COMMENT '密码（加密）',
    `nickname`        VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email`           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `avatar`          VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    `role`            VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员 USER-普通用户',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`   VARCHAR(50)           DEFAULT NULL COMMENT '最后登录IP',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统用户表';

-- ========================================
-- 2. 访问令牌表（开放接口）
-- ========================================
DROP TABLE IF EXISTS `access_token`;
CREATE TABLE `access_token`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `token`           VARCHAR(128)    NOT NULL COMMENT '访问令牌',
    `token_name`      VARCHAR(100)    NOT NULL COMMENT '令牌名称',
    `description`     VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `allowed_ips`     TEXT         DEFAULT NULL COMMENT 'IP白名单（逗号分隔）',
    `rate_limit`      INT          NOT NULL DEFAULT 1000 COMMENT '限流阈值（每分钟请求数）',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    `expire_time`     DATETIME              DEFAULT NULL COMMENT '过期时间（NULL表示永久）',
    `creator_id`      BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token` (`token`),
    KEY `idx_status` (`status`),
    KEY `idx_creator_id` (`creator_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='访问令牌表';

-- ========================================
-- 3. 推送渠道配置表
-- ========================================
DROP TABLE IF EXISTS `push_channel`;
CREATE TABLE `push_channel`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '渠道ID',
    `channel_code`    VARCHAR(50)     NOT NULL COMMENT '渠道编码（webhook、dingtalk、wechat、feishu等）',
    `channel_name`    VARCHAR(100)    NOT NULL COMMENT '渠道名称',
    `description`     VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `config`          TEXT         NOT NULL COMMENT '渠道配置（JSON格式）',
    `enabled`         TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用 0-禁用',
    `priority`        INT          NOT NULL DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
    `creator_id`      BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_channel_code` (`channel_code`),
    KEY `idx_enabled` (`enabled`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='推送渠道配置表';

-- ========================================
-- 4. 消息模板表
-- ========================================
DROP TABLE IF EXISTS `push_template`;
CREATE TABLE `push_template`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    `template_code`   VARCHAR(100)    NOT NULL COMMENT '模板编码',
    `template_name`   VARCHAR(100)    NOT NULL COMMENT '模板名称',
    `channel_code`    VARCHAR(50)     NOT NULL COMMENT '渠道编码',
    `title`           VARCHAR(200)    NOT NULL COMMENT '标题模板',
    `content`         TEXT         NOT NULL COMMENT '内容模板',
    `variables`       TEXT         DEFAULT NULL COMMENT '变量说明（JSON格式）',
    `description`     VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    `creator_id`      BIGINT               DEFAULT NULL COMMENT '创建人ID',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`template_code`),
    KEY `idx_channel_code` (`channel_code`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='消息模板表';

-- ========================================
-- 5. 推送消息主表
-- ========================================
DROP TABLE IF EXISTS `push_message`;
CREATE TABLE `push_message`
(
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `message_id`        VARCHAR(64)     NOT NULL COMMENT '消息唯一标识（UUID）',
    `title`             VARCHAR(200)    NOT NULL COMMENT '标题',
    `content`           TEXT            NOT NULL COMMENT '内容',
    `channel_code`      VARCHAR(50)     NOT NULL COMMENT '渠道编码',
    `target`            TEXT         NOT NULL COMMENT '推送目标（JSON格式）',
    `template_code`     VARCHAR(100) DEFAULT NULL COMMENT '使用的模板编码',
    `topic`             VARCHAR(100) DEFAULT NULL COMMENT '主题/分组',
    `callback_url`      VARCHAR(500) DEFAULT NULL COMMENT '回调URL',
    `app_key`           VARCHAR(64)  DEFAULT NULL COMMENT '调用方AppKey',
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'INIT' COMMENT '状态：INIT-初始化 PENDING-待发送 SENDING-发送中 SUCCESS-成功 FAILED-失败',
    `retry_count`       INT          NOT NULL DEFAULT 0 COMMENT '重试次数',
    `max_retry_count`   INT          NOT NULL DEFAULT 3 COMMENT '最大重试次数',
    `next_retry_time`   DATETIME              DEFAULT NULL COMMENT '下次重试时间',
    `error_message`     TEXT                  DEFAULT NULL COMMENT '错误信息',
    `ext_info`          TEXT                  DEFAULT NULL COMMENT '扩展信息（JSON格式）',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `send_time`         DATETIME              DEFAULT NULL COMMENT '发送时间',
    `success_time`      DATETIME              DEFAULT NULL COMMENT '成功时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_id` (`message_id`),
    KEY `idx_channel_code` (`channel_code`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_app_key` (`app_key`),
    KEY `idx_next_retry_time` (`next_retry_time`),
    KEY `idx_topic` (`topic`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='推送消息主表';

-- ========================================
-- 6. 推送日志表
-- ========================================
DROP TABLE IF EXISTS `push_log`;
CREATE TABLE `push_log`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `message_id`      VARCHAR(64)     NOT NULL COMMENT '消息ID',
    `channel_code`    VARCHAR(50)     NOT NULL COMMENT '渠道编码',
    `log_type`        VARCHAR(20)     NOT NULL COMMENT '日志类型：REQUEST-请求 RESPONSE-响应 RETRY-重试 CALLBACK-回调',
    `log_level`       VARCHAR(10)     NOT NULL DEFAULT 'INFO' COMMENT '日志级别：INFO WARN ERROR',
    `request_content` TEXT         DEFAULT NULL COMMENT '请求内容',
    `response_content` TEXT        DEFAULT NULL COMMENT '响应内容',
    `error_message`   TEXT         DEFAULT NULL COMMENT '错误信息',
    `cost_time`       BIGINT       DEFAULT NULL COMMENT '耗时（毫秒）',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_message_id` (`message_id`),
    KEY `idx_channel_code` (`channel_code`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_log_type` (`log_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='推送日志表';

-- ========================================
-- 初始化数据
-- ========================================

-- 初始化管理员账号（密码：admin123，实际使用时需要加密）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `role`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@example.com', 'ADMIN',
        1);

-- 初始化测试访问令牌
INSERT INTO `access_token` (`token`, `token_name`, `description`, `rate_limit`, `status`)
VALUES ('test_access_token_1234567890abcdef', '测试令牌',
        '用于测试的访问令牌', 1000, 1);

-- 初始化默认渠道配置（Webhook）
INSERT INTO `push_channel` (`channel_code`, `channel_name`, `description`, `config`, `enabled`, `priority`)
VALUES ('webhook', 'Webhook推送', '通用Webhook推送渠道',
        '{"timeout": 5000, "retryTimes": 3, "headers": {"Content-Type": "application/json"}}', 1, 100);

-- 初始化示例模板
INSERT INTO `push_template` (`template_code`, `template_name`, `channel_code`, `title`, `content`, `variables`,
                             `description`, `status`)
VALUES ('server_warning_template', '服务器告警模板', 'webhook', '系统告警通知',
        '服务器 {{serverName}} 的 {{metric}} 超过 {{threshold}}%，请及时处理。',
        '{"serverName": "服务器名称", "metric": "指标名称", "threshold": "阈值"}', '服务器告警通知模板', 1);
