package top.zhengru.unipush.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 推送消息实体
 *
 * @author zhengru
 */
@Data
@TableName("push_message")
public class PushMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息唯一标识（UUID）
     */
    private String messageId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 推送目标（JSON格式）
     */
    private String target;

    /**
     * 使用的模板编码
     */
    private String templateCode;

    /**
     * 主题/分组
     */
    private String topic;

    /**
     * 回调URL
     */
    private String callbackUrl;

    /**
     * 调用方令牌
     */
    private String accessToken;

    /**
     * 状态：INIT-初始化 PENDING-待发送 SENDING-发送中 SUCCESS-成功 FAILED-失败
     */
    private String status;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;

    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 扩展信息（JSON格式）
     */
    private String extInfo;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 成功时间
     */
    private LocalDateTime successTime;
}
