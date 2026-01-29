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
    protected Integer code;

    /**
     * 错误提示
     */
    protected String message;

    /**
     * 错误明细
     */
    protected String detailMessage;

    public BaseException() {
    }

    public BaseException(String message) {
        this.message = message;
    }

    public BaseException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseException(Integer code, String message, String detailMessage) {
        this.code = code;
        this.message = message;
        this.detailMessage = detailMessage;
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
