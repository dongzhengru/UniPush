package top.zhengru.unipush.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 访问令牌实体
 *
 * @author zhengru
 */
@Data
@TableName("access_token")
public class AccessToken implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌名称
     */
    private String tokenName;

    /**
     * 描述
     */
    private String description;

    /**
     * IP白名单（逗号分隔）
     */
    private String allowedIps;

    /**
     * 限流阈值（每分钟请求数）
     */
    private Integer rateLimit;

    /**
     * 状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 过期时间（NULL表示永久）
     */
    private LocalDateTime expireTime;

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
