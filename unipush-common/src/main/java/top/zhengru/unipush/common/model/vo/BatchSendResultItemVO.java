package top.zhengru.unipush.common.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 批量发送结果单项VO
 *
 * @author zhengru
 */
@Data
public class BatchSendResultItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 渠道编码
     */
    private String channel;
}
