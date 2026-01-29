package top.zhengru.unipush.common.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 令牌信息VO
 *
 * @author zhengru
 */
@Data
public class TokenInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 令牌ID
     */
    private Long id;

    /**
     * 令牌（脱敏）
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
     * IP白名单
     */
    private String allowedIps;

    /**
     * 限流阈值
     */
    private Integer rateLimit;

    /**
     * 状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
