package top.zhengru.unipush.common.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 渠道信息VO
 *
 * @author zhengru
 */
@Data
public class ChannelInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道ID
     */
    private Long id;

    /**
     * 渠道编码
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
     * 优先级
     */
    private Integer priority;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
