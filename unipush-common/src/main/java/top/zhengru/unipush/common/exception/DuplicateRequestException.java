package top.zhengru.unipush.common.exception;

/**
 * 重复请求异常
 *
 * @author zhengru
 */
public class DuplicateRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateRequestException(String message) {
        super(message);
    }

    public DuplicateRequestException() {
        super("重复请求，请稍后再试");
    }
}
