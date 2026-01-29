package top.zhengru.unipush.common.exception;

import top.zhengru.unipush.common.enums.ResponseCode;

/**
 * OAuth2认证异常
 *
 * @author zhengru
 */
public class OAuth2Exception extends BaseException {

    private static final long serialVersionUID = 1L;

    public OAuth2Exception(Integer code, String message) {
        super(code, message);
    }

    public OAuth2Exception(Integer code, String message, String detailMessage) {
        super(code, message);
        this.detailMessage = detailMessage;
    }

    public OAuth2Exception(ResponseCode responseCode) {
        super(responseCode.getCode(), responseCode.getMsg());
    }

    public OAuth2Exception(String message) {
        super(ResponseCode.OAUTH2_AUTHORIZATION_FAILED.getCode(), message);
    }

    public OAuth2Exception(String message, String detailMessage) {
        super(ResponseCode.OAUTH2_AUTHORIZATION_FAILED.getCode(), message);
    }
}
