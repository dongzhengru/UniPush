package top.zhengru.unipush.core.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zhengru.unipush.common.api.PushCoreService;
import top.zhengru.unipush.common.constant.KafkaConstants;
import top.zhengru.unipush.common.enums.MessageStatus;
import top.zhengru.unipush.common.model.dto.SendMessageDTO;
import top.zhengru.unipush.common.model.entity.PushMessage;
import top.zhengru.unipush.common.model.vo.MessageResultVO;
import top.zhengru.unipush.core.mapper.PushMessageMapper;
import top.zhengru.unipush.core.mq.DeliveryTaskProducer;
import top.zhengru.unipush.core.util.RedisUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 推送消息核心业务服务
 *
 * @author zhengru
 */
@Slf4j
@Service
@DubboService(
    interfaceClass = PushCoreService.class,
    timeout = 10000,
    retries = 0,
    loadbalance = "roundrobin"
)
public class PushMessageService extends ServiceImpl<PushMessageMapper, PushMessage> implements PushCoreService {

    @Autowired
    private DeliveryTaskProducer deliveryTaskProducer;

    @Autowired
    private RedisUtils redisUtils;

    private static final int DEFAULT_MAX_RETRY_COUNT = 3;

    @Override
    public String createPushTask(SendMessageDTO request) {
        log.info("创建推送任务, channel: {}, title: {}", request.getChannel(), request.getTitle());

        // 1. 构建PushMessage实体
        PushMessage pushMessage = new PushMessage();
        pushMessage.setMessageId(UUID.randomUUID().toString().replace("-", ""));
        pushMessage.setTitle(request.getTitle());
        pushMessage.setContent(request.getContent());
        pushMessage.setChannelCode(request.getChannel());
        pushMessage.setTarget(JSON.toJSONString(request.getTarget()));
        pushMessage.setTemplateCode(request.getTemplate());
        pushMessage.setTopic(request.getTopic());
        pushMessage.setCallbackUrl(request.getCallbackUrl());
        pushMessage.setStatus(MessageStatus.INIT.getCode());
        pushMessage.setRetryCount(0);
        pushMessage.setMaxRetryCount(DEFAULT_MAX_RETRY_COUNT);
        pushMessage.setPersisted(0);  // 标记为未持久化

        // 2. 写入Redis（快速存储，可立即查询）
        redisUtils.setPushMessage(pushMessage.getMessageId(), pushMessage, 7 * 24 * 3600);

        // 3. 添加到持久化队列（用于定时任务批量迁移）
        long score = System.currentTimeMillis() + 5000; // 5秒后允许迁移
        redisUtils.addToPersistQueue(pushMessage.getMessageId(), score);

        // 4. 发送到Kafka投递Topic
        sendToDeliveryTopic(pushMessage);

        // 5. 立即返回messageId（总耗时 5-10ms）
        log.info("推送任务创建成功, messageId: {}", pushMessage.getMessageId());
        return pushMessage.getMessageId();
    }

    /**
     * 发送到投递Topic(统一Topic: unipush-delivery)
     */
    private void sendToDeliveryTopic(PushMessage pushMessage) {
        // 更新内存中的状态（不写数据库）
        pushMessage.setStatus(MessageStatus.PENDING.getCode());
        pushMessage.setSendTime(LocalDateTime.now());

        // 同步更新Redis
        redisUtils.setPushMessage(pushMessage.getMessageId(), pushMessage, 7 * 24 * 3600);

        // 构建投递任务消息（包含完整的PushMessage数据）
        Map<String, Object> deliveryTask = new HashMap<>();
        deliveryTask.put("messageId", pushMessage.getMessageId());
        deliveryTask.put("channelCode", pushMessage.getChannelCode());
        deliveryTask.put("title", pushMessage.getTitle());
        deliveryTask.put("content", pushMessage.getContent());
        deliveryTask.put("target", pushMessage.getTarget());
        deliveryTask.put("templateCode", pushMessage.getTemplateCode());
        deliveryTask.put("topic", pushMessage.getTopic());
        deliveryTask.put("callbackUrl", pushMessage.getCallbackUrl());
        deliveryTask.put("status", pushMessage.getStatus());
        deliveryTask.put("retryCount", pushMessage.getRetryCount());
        deliveryTask.put("maxRetryCount", pushMessage.getMaxRetryCount());
        deliveryTask.put("timestamp", System.currentTimeMillis());

        // 发送到统一的投递Topic
        String message = JSON.toJSONString(deliveryTask);
        deliveryTaskProducer.sendDeliveryTask(
            KafkaConstants.TOPIC_DELIVERY,
            pushMessage.getMessageId(),
            message
        );

        log.info("投递任务已发送到统一Topic, messageId: {}, channelCode: {}",
            pushMessage.getMessageId(), pushMessage.getChannelCode());
    }

    @Override
    public MessageResultVO getMessageResult(String messageId) {
        log.info("查询消息状态, messageId: {}", messageId);

        // 1. 先查Redis（快速返回）
        PushMessage pushMessage = redisUtils.getPushMessage(messageId);

        // 2. Redis没有，再查数据库
        if (pushMessage == null) {
            pushMessage = this.lambdaQuery()
                .eq(PushMessage::getMessageId, messageId)
                .one();
        }

        // 3. 都没有，返回不存在
        if (pushMessage == null) {
            MessageResultVO result = new MessageResultVO();
            result.setMessageId(messageId);
            result.setStatus(-1);
            result.setErrorMessage("消息不存在");
            return result;
        }

        // 4. 返回结果
        MessageResultVO result = new MessageResultVO();
        result.setMessageId(messageId);
        result.setTitle(pushMessage.getTitle());
        result.setContent(pushMessage.getContent());
        result.setChannelCode(pushMessage.getChannelCode());
        result.setStatus(mapStatusToInt(pushMessage.getStatus()));
        result.setErrorMessage(pushMessage.getErrorMessage());
        result.setRetryCount(pushMessage.getRetryCount());
        result.setCreateTime(pushMessage.getCreateTime());
        result.setUpdateTime(pushMessage.getUpdateTime());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleDeliveryResult(String resultMessage) {
        log.info("处理投递结果: {}", resultMessage);

        // 解析结果消息
        Map<String, Object> result = JSON.parseObject(resultMessage, Map.class);
        String messageId = (String) result.get("messageId");
        Boolean success = (Boolean) result.get("success");
        String errorMessage = (String) result.get("errorMessage");

        // 1. 先从Redis查询（快速查询）
        PushMessage pushMessage = redisUtils.getPushMessage(messageId);

        if (pushMessage == null) {
            // Redis没有，再查数据库（可能是已经持久化的消息）
            pushMessage = this.lambdaQuery()
                .eq(PushMessage::getMessageId, messageId)
                .one();

            if (pushMessage == null) {
                log.error("消息不存在: {}", messageId);
                return;
            }

            // 查到数据库数据后，更新回Redis
            redisUtils.setPushMessage(messageId, pushMessage, 7 * 24 * 3600);
        }

        // 2. 更新消息状态
        if (Boolean.TRUE.equals(success)) {
            // 投递成功
            pushMessage.setStatus(MessageStatus.SUCCESS.getCode());
            pushMessage.setSuccessTime(LocalDateTime.now());
            pushMessage.setErrorMessage(null);
            log.info("消息投递成功, messageId: {}", messageId);
        } else {
            // 投递失败,判断是否需要重试
            int currentRetry = pushMessage.getRetryCount();
            int maxRetry = pushMessage.getMaxRetryCount();

            if (currentRetry < maxRetry) {
                // 需要重试
                pushMessage.setRetryCount(currentRetry + 1);
                pushMessage.setErrorMessage(errorMessage);

                // 计算下次重试时间(指数退避: 1min, 2min, 4min, 8min...)
                int delayMinutes = (int) Math.pow(2, currentRetry);
                pushMessage.setNextRetryTime(LocalDateTime.now().plusMinutes(delayMinutes));

                log.info("消息投递失败,将在{}分钟后重试, messageId: {}, retryCount: {}/{}",
                    delayMinutes, messageId, pushMessage.getRetryCount(), maxRetry);

                // 重新发送到投递队列
                sendToDeliveryTopic(pushMessage);
            } else {
                // 达到最大重试次数,标记为失败
                pushMessage.setStatus(MessageStatus.FAILED.getCode());
                pushMessage.setErrorMessage(errorMessage);
                log.error("消息投递失败且达到最大重试次数, messageId: {}, retryCount: {}/{}",
                    messageId, currentRetry, maxRetry);
            }
        }

        // 3. 更新Redis（快速更新）
        // 注意：如果已经持久化过（persisted=1），保持该状态不要重置
        // 这样可以区分：
        //   - persisted=0: 新消息，需要插入数据库
        //   - persisted=1: 已持久化过但状态更新，需要更新数据库
        redisUtils.setPushMessage(messageId, pushMessage, 7 * 24 * 3600);

        // 4. 添加到持久化队列（更新score为当前时间，优先持久化）
        // 即使已持久化，状态更新后也需要重新写回数据库
        redisUtils.addToPersistQueue(messageId, System.currentTimeMillis());

        // 注意：不直接写数据库，由定时任务批量写回

        // TODO: 如果配置了callbackUrl,发送回调通知
        // if (pushMessage.getCallbackUrl() != null) {
        //     sendCallback(pushMessage);
        // }
    }

    /**
     * 将状态码映射为数字
     */
    private Integer mapStatusToInt(String status) {
        return switch (status) {
            case "INIT" -> 1;
            case "PENDING" -> 2;
            case "SENDING" -> 3;
            case "SUCCESS" -> 4;
            case "FAILED" -> 5;
            default -> 0;
        };
    }
}
