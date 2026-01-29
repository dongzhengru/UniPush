package top.zhengru.unipush.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 钉钉机器人投递请求
 *
 * @author zhengru
 */
@Data
public class DingTalkRequest implements Serializable {

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
     * 格式: {"url":"https://oapi.dingtalk.com/robot/send?access_token=xxxxxx"}
     */
    @JsonProperty("target")
    private String target;

    /**
     * 消息类型（钉钉专用，text/markdown/link等）
     */
    @JsonProperty("msgType")
    private String msgType = "text";

    /**
     * 时间戳
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * 从target中提取url的便捷方法
     * 假设target格式为: {"url":"https://oapi.dingtalk.com/robot/send?access_token=xxxxxx"}
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
}
