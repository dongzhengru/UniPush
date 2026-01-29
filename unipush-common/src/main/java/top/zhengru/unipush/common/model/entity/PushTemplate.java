package top.zhengru.unipush.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息模板实体
 *
 * @author zhengru
 */
@Data
@TableName("push_template")
public class PushTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
