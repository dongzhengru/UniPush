package top.zhengru.unipush.webhook.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 投递结果Kafka生产者
 * 负责发送投递结果到 unipush-result Topic
 *
 * @author zhengru
 */
@Slf4j
@Component
public class DeliveryResultProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.result:unipush-result}")
    private String resultTopic;

    /**
     * 发送投递结果到unipush-result Topic
     *
     * @param messageId 消息ID
     * @param resultJson 投递结果JSON字符串
     */
    public void sendDeliveryResult(String messageId, String resultJson) {
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(resultTopic, messageId, resultJson);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("投递结果已发送, messageId: {}, topic: {}, partition: {}, offset: {}",
                        messageId, resultTopic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    log.error("投递结果发送失败, messageId: {}, topic: {}, error: {}",
                        messageId, resultTopic, ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("投递结果发送异常, messageId: {}", messageId, e);
        }
    }
}
