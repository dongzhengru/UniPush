package top.zhengru.unipush.common.constant;

/**
 * Kafka常量定义
 *
 * @author zhengru
 */
public class KafkaConstants {

    /**
     * 推送投递任务Topic
     */
    public static final String TOPIC_DELIVERY = "unipush-delivery";

    /**
     * 投递结果回传Topic
     */
    public static final String TOPIC_RESULT = "unipush-result";

    /**
     * 推送日志Topic（可选）
     */
    public static final String TOPIC_LOG = "unipush-log";

    /**
     * 渠道编码常量
     */
    public static final String CHANNEL_WEBHOOK = "webhook";
    public static final String CHANNEL_SMS = "sms";
    public static final String CHANNEL_MAIL = "mail";
}
