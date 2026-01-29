package top.zhengru.unipush.common.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模板信息VO
 *
 * @author zhengru
 */
@Data
public class TemplateInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    private Long id;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 标题模板
     */
    private String title;

    /**
     * 内容模板
     */
    private String content;

    /**
     * 变量说明（JSON格式）
     */
    private String variables;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
