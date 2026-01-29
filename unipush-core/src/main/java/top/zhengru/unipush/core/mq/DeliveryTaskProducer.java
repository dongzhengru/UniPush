package top.zhengru.unipush.core.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 投递任务Kafka生产者
 * 负责发送投递任务到统一的 unipush-delivery Topic
 *
 * @author zhengru
 */
@Slf4j
@Component
public class DeliveryTaskProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送投递任务到指定Topic
     *
     * @param topic Topic名称（统一使用 unipush-delivery）
     * @param key 消息Key（messageId，保证同一消息发送到同一分区）
     * @param value 消息体（JSON字符串，包含channelCode等字段）
     */
    public void sendDeliveryTask(String topic, String key, String value) {
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, value);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Kafka消息发送成功, topic: {}, key: {}, partition: {}, offset: {}",
                        topic, key,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    log.error("Kafka消息发送失败, topic: {}, key: {}, error: {}",
                        topic, key, ex.getMessage(), ex);
                    // TODO: 考虑降级处理或补偿机制
                }
            });
        } catch (Exception e) {
            log.error("Kafka发送异常, topic: {}, key: {}", topic, key, e);
            throw new RuntimeException("Kafka发送失败", e);
        }
    }
}
