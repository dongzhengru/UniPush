package top.zhengru.unipush.common.exception;

import top.zhengru.unipush.common.enums.ResponseCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * @author zhengru
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误提示
     */
    private final String message;

    /**
     * 错误明细
     */
    private final String detailMessage;

    public BusinessException() {
        this(ResponseCode.SYSTEM_ERROR);
    }

    public BusinessException(String message) {
        this(ResponseCode.SYSTEM_ERROR.getCode(), message);
    }

    public BusinessException(Integer code, String message) {
        this(code, message, null);
    }

    public BusinessException(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getMsg(), null);
    }

    public BusinessException(ResponseCode responseCode, String message) {
        this(responseCode.getCode(), message, null);
    }

    public BusinessException(Integer code, String message, String detailMessage) {
        super(message);
        this.code = code;
        this.message = message;
        this.detailMessage = detailMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
