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
import top.zhengru.unipush.webhook.service.DingTalkDeliveryService;
import top.zhengru.unipush.webhook.service.BarkDeliveryService;

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

    @Autowired
    private DingTalkDeliveryService dingTalkDeliveryService;

    @Autowired
    private BarkDeliveryService barkDeliveryService;

    /**
     * 消费webhook投递任务
     * 订阅统一的 unipush-delivery Topic，处理 channelCode="webhook"、"dingtalk" 和 "bark" 的消息
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
            // 解析消息获取渠道编码
            String channelCode = parseChannelCode(message);

            // 渠道分发：根据channelCode投递到不同的服务
            if ("webhook".equals(channelCode)) {
                // 执行webhook投递
                webhookDeliveryService.deliver(messageId, message);
            } else if ("dingtalk".equals(channelCode)) {
                // 执行钉钉机器人投递
                dingTalkDeliveryService.deliver(messageId, message);
            } else if ("bark".equals(channelCode)) {
                // 执行Bark推送投递
                barkDeliveryService.deliver(messageId, message);
            } else {
                log.debug("忽略不支持的消息, channelCode: {}, messageId: {}",
                    channelCode, messageId);
                // 提交offset，避免重复消费
                if (ack != null) {
                    ack.acknowledge();
                }
                return;
            }

            // 投递成功后提交offset
            if (ack != null) {
                ack.acknowledge();
                log.debug("投递完成,已提交offset, messageId: {}, channelCode: {}",
                    messageId, channelCode);
            }

        } catch (Exception e) {
            log.error("投递失败, messageId: {}, 暂不提交offset等待重试",
                messageId, e);
            // 不提交offset,Kafka会重新投递
        }
    }

    /**
     * 解析消息获取渠道编码
     *
     * @param message JSON消息
     * @return 渠道编码
     */
    private String parseChannelCode(String message) {
        try {
            com.alibaba.fastjson2.JSONObject json = JSON.parseObject(message);
            return json.getString("channelCode");
        } catch (Exception e) {
            log.error("解析channelCode失败, message: {}", message, e);
            return "";
        }
    }
}
