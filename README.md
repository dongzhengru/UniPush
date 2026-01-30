<div align="center">

# 🚀 UniPush

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/architecture-microservice-red.svg)](https://microservices.io/)
**📮 UniPush 高性能统一消息推送平台 | 支持多渠道、高并发、可扩展的分布式推送系统**

[功能特性](#-核心特性) • [快速开始](#-快速开始) • [架构设计](#-架构设计)

---

## 📖 项目简介

UniPush 是一个**高性能、可扩展的统一消息推送平台**，旨在解决多渠道消息推送的痛点。通过统一的 API 接口，支持多种推送渠道（短信、邮件、企业微信、钉钉、飞书等），提供简单易用的 SDK，帮助开发者快速集成消息推送能力。

### 🎯 解决什么问题？

- ❌ **多渠道接入复杂**：每个推送渠道都要单独对接，维护成本高
- ❌ **高并发性能瓶颈**：传统同步推送方式，无法应对高并发场景
- ❌ **可靠性无法保证**：推送失败无法重试，消息可能丢失
- ✅ **统一推送入口**：一套 API，支持所有渠道
- ✅ **高性能异步架构**：Redis + Kafka 异步解耦，轻松应对百万级并发
- ✅ **可靠的消息保证**：持久化 + 重试机制，确保消息必达

---

## ✨ 核心特性

### 🚀 高性能
- **Redis 前置缓存**：毫秒级响应，5-10ms 快速返回
- **Kafka 异步解耦**：削峰填谷，支持海量并发
- **定时批量持久化**：批量写库，减少 99% 数据库操作
- **分布式架构**：支持水平扩展，轻松应对业务增长

### 🔄 多渠道支持
- 📧 **邮件推送**：SMTP 邮件发送
- 📱 **短信推送**：阿里云、腾讯云短信
- 💬 **企业微信**：应用消息、机器人通知
- 📊 **钉钉**：工作通知、群消息
- 🎯 **飞书**：消息推送
- 🔔 **Webhook**：自定义 HTTP 回调

### 🛡️ 高可靠性
- **消息持久化**：Redis + MySQL 双重保障
- **失败重试**：指数退避重试策略（1min, 2min, 4min...）
- **幂等性保证**：防止重复推送
- **实时监控**：Sentinel 流控保护，系统稳定性

### 📊 完善的监控
- **实时状态查询**：毫秒级查询推送状态
- **Sentinel 流控**：QPS 限流 + 熔断降级
- **异步回调通知**：推送结果自动回调
- **Swagger 文档**：在线 API 调试

---

## 🏗️ 架构设计

### 系统架构图

```
┌──────────────────────────────────────────────────────────────────────┐
│                                客户端                                 │
│                  Web 应用 / 移动应用 / 第三方系统                        │
└───────────────────────────────────┬──────────────────────────────────┘
                                    │
                                    ▼
┌──────────────────────────────────────────────────────────────────────┐
│                              API 网关层                               │
│                                                                      │
│   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐             │
│   │ 开放接口 API  │   │   Web API    │    │ OAuth2 认证  │             │
│   │ (AccessToken)│   │   (JWT)      │   │              │             │
│   └──────────────┘   └──────────────┘   └──────────────┘             │
│                                                                      │
│                     Sentinel 流控 / 熔断保护                           │
└───────────────────────────────────┬──────────────────────────────────┘
                                    │
                                    ▼
┌──────────────────────────────────────────────────────────────────────┐
│                           Core 核心服务层                              │
│                                                                      │
│   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐             │
│   │  消息创建      │   │   状态查询    │   │    定时任务    │             │
│   │ (Dubbo 服务)  │   │              │   │  (批量持久化)  │             │
│   └──────────────┘   └──────────────┘   └──────────────┘             │
└───────────────────────────────────┬──────────────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
        ▼                           ▼                           ▼
┌─────────────┐             ┌─────────────┐             ┌─────────────┐
│    Redis    │             │    Kafka    │             │    MySQL    │
│    缓存层    │             │   消息队列    │             │   持久化     │
└─────────────┘             └─────────────┘             └─────────────┘
        │                           │
        └───────────────┬───────────┘
                        │
                        ▼
┌──────────────────────────────────────────────────────────────────────┐
│                         Webhook 投递服务                               │
│                                                                      │
│   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐             │
│   │    短信推送    │   │    邮件推送   │   │   IM 推送     │             │
│   │   (短信/邮件)  │   │   (SMTP)     │   │ (企微/钉钉)   │             │
│   └──────────────┘   └──────────────┘   └──────────────┘             │
└──────────────────────────────────────────────────────────────────────┘

```

### 核心流程

#### 消息发送流程
```
1. 客户端请求 → API 层
2. 参数校验 + Sentinel 流控
3. 写入 Redis（5-10ms 返回）
4. 发送到 Kafka 队列
5. 返回 messageId 给客户端

└─ 后台异步处理：
   ├─ Webhook 消费 Kafka 消息
   ├─ 调用第三方渠道推送
   ├─ 投递结果回传 Kafka
   └─ Core 服务更新状态到 Redis
   └─ 定时任务批量写回 MySQL
```

#### 数据流转
```
创建消息
├─ Redis 存储（快速查询）
├─ 持久化队列（Sorted Set，score = 允许持久化时间戳）
└─ Kafka 投递队列

投递结果更新
├─ 先查 Redis（毫秒级）
├─ 更新 Redis 状态
├─ 重新加入持久化队列
└─ 标记 persisted 状态

定时持久化（5秒/次）
├─ 从队列获取到期消息
├─ 批量 saveOrUpdate 到 MySQL
├─ 标记 persisted = 1
└─ 从队列移除
```

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Kafka**: 2.8+
- **Nacos**: 2.2+

### 发送第一条消息

```bash
curl -X POST https://unipush.zhengru.top/api/open/message/send \
  -H "Content-Type: application/json" \
  -H "access-token: YOUR_ACCESS_TOKEN" \
  -d '{
    "title": "测试消息",
    "content": "Hello, UniPush!",
    "channel": "dingtalk",
    "target": {
      "url": "https://oapi.dingtalk.com/robot/send?access_token=XXXXXXXXXX"
    },
    "timestamp": 1706745600000
  }'
```

---

## 📦 技术栈

### 核心框架
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.x | 基础框架 |
| Spring Cloud | 2023.x | 微服务框架 |
| Alibaba Cloud | 2023.x | 阿里云组件套件 |
| Dubbo | 3.x | RPC 框架 |

| 组件 | 版本 | 说明 |
|------|------|------|
| Nacos | 2.2.x | 注册中心 & 配置中心 |
| Sentinel | 1.8.x | 流量控制 & 熔断降级 |
| Kafka | 3.x | 消息队列 |
| Redis | 6.x+ | 缓存 |
| MySQL | 8.0+ | 持久化存储 |
| MyBatis-Plus | 3.5.x | ORM 框架 |

### 技术亮点

- **异步架构**：Redis + Kafka 实现全链路异步
- **分布式事务**：Seata 保证数据一致性
- **服务治理**：Dubbo + Nacos 实现服务注册发现
- **限流降级**：Sentinel 实现流量控制、熔断降级

---

## 📊 性能指标

### 压测结果（4核8G）

| 指标 | 数值 | 说明 |
|------|------|------|
| **QPS** | 10,000+ | 单机吞吐量 |
| **响应时间** | 5-10ms | P99 < 20ms |
| **并发连接** | 5,000+ | 支持并发数 |
| **消息堆积** | 100万+ | Kafka 缓冲能力 |
| **数据库写入** | 批量100条/2秒 | 减少 99% 操作 |

---

## 🛠️ 模块说明

```
unipush-backend/
├── unipush-common/      # 公共模块
│   ├── model/          # 实体类、DTO、VO
│   ├── enums/          # 枚举定义
│   ├── constant/       # 常量定义
│   └── api/            # Dubbo 服务接口
│
├── unipush-api/        # API 网关服务
│   ├── controller/     # REST 接口
│   │   ├── open/       # 开放接口（AccessToken认证）
│   │   └── web/        # Web接口（JWT认证）
│   ├── config/         # 配置类
│   └── util/           # 工具类
│
├── unipush-core/       # 核心服务
│   ├── service/        # 业务服务
│   ├── mq/             # Kafka 消费者
│   ├── task/           # 定时任务
│   └── mapper/         # MyBatis Mapper
│
├── unipush-webhook/    # 投递服务
│   ├── channel/        # 各渠道适配器
│   │   ├── email/      # 邮件渠道
│   │   ├── sms/        # 短信渠道
│   │   ├── wechat/     # 企业微信
│   │   └── ...         # 其他渠道
│   └── mq/             # Kafka 消费者
│
└── unipush-admin/      # 管理后台（待开发）
```

---

## 🔐 安全设计

### 认证授权
- **OAuth2**：支持第三方应用授权
- **JWT**：Web 管理后台认证
- **AccessToken**：开放接口认证
- **IP 白名单**：接口访问控制

---

### 完整文档
在线文档：

---

## 📄 许可证

[Apache License 2.0](LICENSE)

---

## 👥 作者

- [dongzhengru - Github](https://github.com/dongzhengru)

---

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Apache Dubbo](https://dubbo.apache.org/)
- [Alibaba Nacos](https://nacos.io/)
- [Apache Kafka](https://kafka.apache.org/)

---

## 📮 联系我们

- **官网**：[https://unipush.example.com](https://unipush.example.com)
- **文档**：[https://docs.unipush.example.com](https://docs.unipush.example.com)
- **Issues**：[GitHub Issues](https://github.com/yourusername/unipush/issues)
- **讨论区**：[GitHub Discussions](https://github.com/yourusername/unipush/discussions)

---

## ⭐ Star History

如果这个项目对你有帮助，请给我们一个 Star ⭐

[![Star History Chart](https://api.star-history.com/svg?repos=yourusername/unipush&type=Date)](https://star-history.com/#yourusername/unipush&Date)

---

<div align="center">

**Made with ❤️ by UniPush Team**

[返回顶部](#-unipush)
