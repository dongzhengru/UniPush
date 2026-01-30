package top.zhengru.unipush.core.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.zhengru.unipush.common.constant.RedisConstants;
import top.zhengru.unipush.common.model.entity.PushMessage;
import top.zhengru.unipush.core.service.PushMessageService;
import top.zhengru.unipush.core.util.RedisUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 消息持久化定时任务
 * 从Redis批量迁移消息到数据库
 *
 * @author zhengru
 */
@Slf4j
@Component
public class MessagePersistTask {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PushMessageService pushMessageService;

    /**
     * 每5秒执行一次，批量迁移Redis消息到数据库
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    public void persistMessagesToDatabase() {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 从持久化队列获取到期消息（score <= current_time）
            long currentTime = System.currentTimeMillis();
            Set<Object> messageIds = redisUtils.getRedisTemplate().opsForZSet()
                .rangeByScore(RedisConstants.PUSH_MESSAGE_BATCH_KEY, 0, currentTime, 0, 100);

            if (messageIds == null || messageIds.isEmpty()) {
                return;
            }

            log.info("定时迁移消息, 待迁移数量: {}", messageIds.size());

            // 2. 从Redis批量获取消息详情
            List<PushMessage> batch = new ArrayList<>();
            List<String> idsToRemove = new ArrayList<>();

            for (Object messageIdObj : messageIds) {
                String messageId = (String) messageIdObj;
                PushMessage pushMessage = redisUtils.getPushMessage(messageId);

                if (pushMessage != null) {
                    batch.add(pushMessage);
                    idsToRemove.add(messageId);
                } else {
                    // Redis中不存在，从队列中移除
                    redisUtils.removeFromPersistQueue(messageId);
                }
            }

            if (batch.isEmpty()) {
                return;
            }

            // 3. 批量写入数据库（使用 saveOrUpdateBatch 支持插入和更新）
            boolean success = pushMessageService.saveOrUpdateBatch(batch, 100);

            if (success) {
                // 4. 标记为已持久化（防止重复写回）
                for (PushMessage msg : batch) {
                    msg.setPersisted(1);
                    msg.setPersistedTime(java.time.LocalDateTime.now());
                    // 更新Redis中的状态
                    redisUtils.setPushMessage(msg.getMessageId(), msg, 7 * 24 * 3600);
                }

                // 5. 从持久化队列移除已迁移的消息
                redisUtils.removeFromPersistQueueBatch(idsToRemove.toArray());

                log.info("定时迁移完成, total: {}, cost: {}ms",
                    batch.size(), System.currentTimeMillis() - startTime);
            } else {
                log.error("批量持久化失败, count: {}", batch.size());
                // 失败不标记，下次继续重试
            }

        } catch (Exception e) {
            log.error("定时迁移异常, cost: {}ms",
                System.currentTimeMillis() - startTime, e);
        }
    }
}
