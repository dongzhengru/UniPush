package top.zhengru.unipush.common.enums;

/**
 * 消息状态枚举
 *
 * @author zhengru
 */
public enum MessageStatus {

    /**
     * 初始化
     */
    INIT("INIT", "初始化"),

    /**
     * 待发送
     */
    PENDING("PENDING", "待发送"),

    /**
     * 发送中
     */
    SENDING("SENDING", "发送中"),

    /**
     * 成功
     */
    SUCCESS("SUCCESS", "成功"),

    /**
     * 失败
     */
    FAILED("FAILED", "失败");

    private final String code;
    private final String desc;

    MessageStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
