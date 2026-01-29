package top.zhengru.unipush.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 推送日志实体
 *
 * @author zhengru
 */
@Data
@TableName("push_log")
public class PushLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 日志类型：REQUEST-请求 RESPONSE-响应 RETRY-重试 CALLBACK-回调
     */
    private String logType;

    /**
     * 日志级别：INFO WARN ERROR
     */
    private String logLevel;

    /**
     * 请求内容
     */
    private String requestContent;

    /**
     * 响应内容
     */
    private String responseContent;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 耗时（毫秒）
     */
    private Long costTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
