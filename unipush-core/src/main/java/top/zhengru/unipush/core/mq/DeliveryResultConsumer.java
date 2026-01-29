package top.zhengru.unipush.core.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import top.zhengru.unipush.core.service.PushMessageService;

/**
 * 投递结果Kafka消费者
 * 消费unipush-result Topic,更新消息状态
 *
 * @author zhengru
 */
@Slf4j
@Component
public class DeliveryResultConsumer {

    @Autowired
    private PushMessageService pushMessageService;

    /**
     * 消费投递结果
     * manualImmediate模式: 手动立即提交offset
     *
     * @param record Kafka消费者记录
     * @param ack 手动确认对象
     */
    @KafkaListener(
        topics = "${kafka.topic.result:unipush-result}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeDeliveryResult(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String message = record.value();
            log.info("收到投递结果, topic: {}, partition: {}, offset: {}, key: {}",
                record.topic(), record.partition(), record.offset(), record.key());

            // 处理投递结果
            pushMessageService.handleDeliveryResult(message);

            // 手动提交offset
            if (ack != null) {
                ack.acknowledge();
                log.debug("投递结果处理完成,已提交offset, messageId: {}", record.key());
            }
        } catch (Exception e) {
            log.error("处理投递结果失败, offset: {}, 暂不提交offset等待重试", record.offset(), e);
            // 不提交offset,等待Kafka重新投递
        }
    }
}
