package top.zhengru.unipush.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Webhook投递请求
 *
 * @author zhengru
 */
@Data
public class WebhookRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @JsonProperty("messageId")
    private String messageId;

    /**
     * 渠道编码
     */
    @JsonProperty("channelCode")
    private String channelCode;

    /**
     * 标题
     */
    @JsonProperty("title")
    private String title;

    /**
     * 内容
     */
    @JsonProperty("content")
    private String content;

    /**
     * 推送目标（JSON字符串，包含url等信息）
     */
    @JsonProperty("target")
    private String target;

    /**
     * 时间戳
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * 从target中提取url的便捷方法
     * 假设target格式为: {"url":"https://example.com/webhook","token":"xxx"}
     *
     * @return webhook URL
     */
    public String getTargetUrl() {
        if (target != null && target.contains("\"url\"")) {
            int start = target.indexOf("\"url\":\"") + 7;
            int end = target.indexOf("\"", start);
            if (start > 6 && end > start) {
                return target.substring(start, end);
            }
        }
        throw new IllegalArgumentException("target中缺少url字段或格式错误: " + target);
    }

    /**
     * 从target中提取token的便捷方法（可选）
     *
     * @return token or null
     */
    public String getTargetToken() {
        if (target != null && target.contains("\"token\"")) {
            int start = target.indexOf("\"token\":\"") + 9;
            int end = target.indexOf("\"", start);
            if (start > 8 && end > start) {
                return target.substring(start, end);
            }
        }
        return null;
    }
}
