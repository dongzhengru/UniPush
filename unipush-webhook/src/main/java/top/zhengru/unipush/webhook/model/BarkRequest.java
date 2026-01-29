package top.zhengru.unipush.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Bark推送投递请求
 *
 * @author zhengru
 */
@Data
public class BarkRequest implements Serializable {

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
     * 推送目标（JSON字符串，包含key等信息）
     * 格式: {"key":"abcd1234","sound":"alarm","icon":"https://example.com/icon.png","group":"backend","level":"critical"}
     */
    @JsonProperty("target")
    private String target;

    /**
     * 时间戳
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * 从target中提取key的便捷方法
     * 假设target格式为: {"key":"abcd1234",...}
     *
     * @return Bark key
     */
    public String getBarkKey() {
        if (target != null && target.contains("\"key\"")) {
            int start = target.indexOf("\"key\":\"") + 7;
            int end = target.indexOf("\"", start);
            if (start > 6 && end > start) {
                return target.substring(start, end);
            }
        }
        throw new IllegalArgumentException("target中缺少key字段或格式错误: " + target);
    }

    /**
     * 从target中提取sound参数（可选）
     *
     * @return sound or null
     */
    public String getSound() {
        return extractParam("sound");
    }

    /**
     * 从target中提取icon参数（可选）
     *
     * @return icon URL or null
     */
    public String getIcon() {
        return extractParam("icon");
    }

    /**
     * 从target中提取group参数（可选）
     *
     * @return group or null
     */
    public String getGroup() {
        return extractParam("group");
    }

    /**
     * 从target中提取level参数（可选）
     *
     * @return level or null
     */
    public String getLevel() {
        return extractParam("level");
    }

    /**
     * 从target中提取url参数（可选）
     *
     * @return url or null
     */
    public String getUrl() {
        return extractParam("url");
    }

    /**
     * 从target中提取click参数（可选）
     *
     * @return click or null
     */
    public String getClick() {
        return extractParam("click");
    }

    /**
     * 从target中提取autoCopy参数（可选）
     *
     * @return autoCopy or null
     */
    public String getAutoCopy() {
        return extractParam("autoCopy");
    }

    /**
     * 从target中提取copy参数（可选）
     *
     * @return copy or null
     */
    public String getCopy() {
        return extractParam("copy");
    }

    /**
     * 通用参数提取方法
     *
     * @param paramName 参数名
     * @return 参数值 or null
     */
    public String extractParam(String paramName) {
        if (target != null && target.contains("\"" + paramName + "\"")) {
            int start = target.indexOf("\"" + paramName + "\":");
            // 检查是字符串还是布尔值或数字
            if (target.charAt(start + paramName.length() + 2) == '"') {
                // 字符串类型
                start = start + paramName.length() + 3;
                int end = target.indexOf("\"", start);
                if (end > start) {
                    return target.substring(start, end);
                }
            } else {
                // 非字符串类型（布尔值、数字等）
                start = start + paramName.length() + 1;
                int end = target.indexOf(",", start);
                if (end == -1) {
                    end = target.indexOf("}", start);
                }
                if (end > start) {
                    return target.substring(start, end).trim();
                }
            }
        }
        return null;
    }
}
