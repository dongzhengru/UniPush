package top.zhengru.unipush.webhook.mq;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import top.zhengru.unipush.webhook.model.WebhookRequest;
import top.zhengru.unipush.webhook.service.WebhookDeliveryService;

/**
 * Webhook投递任务消费者
 * 纯Worker角色,消费投递任务并执行HTTP投递
 *
 * @author zhengru
 */
@Slf4j
@Component
public class DeliveryTaskConsumer {

    @Autowired
    private WebhookDeliveryService webhookDeliveryService;

    /**
     * 消费webhook投递任务
     * 订阅统一的 unipush-delivery Topic，只处理 channelCode="webhook" 的消息
     *
     * @param record Kafka消费者记录
     * @param ack 手动确认对象
     */
    @KafkaListener(
        topics = "${kafka.topic.delivery:unipush-delivery}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeDeliveryTask(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String messageId = record.key();
        String message = record.value();

        log.info("收到投递任务, messageId: {}, partition: {}, offset: {}",
            messageId, record.partition(), record.offset());

        try {
            // 解析消息
            WebhookRequest request = JSON.parseObject(message, WebhookRequest.class);

            // 渠道过滤：只处理webhook渠道
            if (!"webhook".equals(request.getChannelCode())) {
                log.debug("忽略非webhook消息, channelCode: {}, messageId: {}",
                    request.getChannelCode(), messageId);
                // 提交offset，避免重复消费
                if (ack != null) {
                    ack.acknowledge();
                }
                return;
            }

            // 执行webhook投递
            webhookDeliveryService.deliver(messageId, message);

            // 投递成功后提交offset
            if (ack != null) {
                ack.acknowledge();
                log.debug("webhook投递完成,已提交offset, messageId: {}", messageId);
            }

        } catch (Exception e) {
            log.error("webhook投递失败, messageId: {}, 暂不提交offset等待重试",
                messageId, e);
            // 不提交offset,Kafka会重新投递
        }
    }
}
