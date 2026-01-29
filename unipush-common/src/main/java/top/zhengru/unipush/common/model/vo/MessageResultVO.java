package top.zhengru.unipush.common.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息发送结果VO
 *
 * @author zhengru
 */
@Data
public class MessageResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态：1-待处理 2-处理中 3-成功 4-失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
