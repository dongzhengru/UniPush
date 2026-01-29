package top.zhengru.unipush.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 推送渠道配置实体
 *
 * @author zhengru
 */
@Data
@TableName("push_channel")
public class PushChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 渠道编码（webhook、dingtalk、wechat、feishu等）
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 描述
     */
    private String description;

    /**
     * 渠道配置（JSON格式）
     */
    private String config;

    /**
     * 是否启用：1-启用 0-禁用
     */
    private Integer enabled;

    /**
     * 优先级（数字越大优先级越高）
     */
    private Integer priority;

    /**
     * 创建人ID
     */
    private Long creatorId;

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
}
