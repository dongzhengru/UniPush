package top.zhengru.unipush.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 发送消息请求DTO
 *
 * @author zhengru
 */
@Data
public class SendMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 渠道编码
     */
    @NotBlank(message = "渠道编码不能为空")
    private String channel;

    /**
     * 推送目标（JSON对象，依通道具体定义）
     */
    @NotNull(message = "推送目标不能为空")
    private Map<String, Object> target;

    /**
     * 主题/分组
     */
    private String topic;

    /**
     * 模板编码
     */
    private String template;

    /**
     * 结果异步回调URL
     */
    private String callbackUrl;

    /**
     * 时间戳
     */
    @NotNull(message = "时间戳不能为空")
    @JsonProperty("timestamp")
    private Long timestamp;
}
