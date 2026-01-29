package top.zhengru.unipush.common.exception;

/**
 * 基础异常
 *
 * @author zhengru
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细
     */
    private String detailMessage;

    public BaseException() {
    }

    public BaseException(String message) {
        this.message = message;
    }

    public BaseException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
