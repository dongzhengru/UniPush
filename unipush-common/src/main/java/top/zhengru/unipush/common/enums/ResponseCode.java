package top.zhengru.unipush.common.enums;

/**
 * 统一返回码枚举
 *
 * @author zhengru
 */
public enum ResponseCode {

    /**
     * 执行成功
     */
    SUCCESS(200, "执行成功"),

    /**
     * 未登录
     */
    UNAUTHORIZED(302, "未登录"),

    /**
     * 请求未授权
     */
    INVALID_REQUEST(401, "请求未授权"),

    /**
     * 请求IP未授权
     */
    IP_FORBIDDEN(403, "请求 IP 未授权"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(500, "系统异常，请稍后再试"),

    /**
     * 数据异常
     */
    DATA_ERROR(600, "数据异常，操作失败"),

    /**
     * 无权查看
     */
    NO_PERMISSION(805, "无权查看"),

    /**
     * 用户账号使用受限
     */
    ACCOUNT_RESTRICTED(900, "用户账号使用受限"),

    /**
     * 无效的用户令牌
     */
    INVALID_TOKEN(903, "无效的用户令牌"),

    /**
     * 服务端验证错误
     */
    VALIDATION_ERROR(999, "服务端验证错误"),

    /**
     * OAuth2认证失败
     */
    OAUTH2_AUTHORIZATION_FAILED(400, "OAuth2认证失败"),

    /**
     * OAuth2获取令牌失败
     */
    OAUTH2_TOKEN_ERROR(401, "获取访问令牌失败"),

    /**
     * OAuth2获取用户信息失败
     */
    OAUTH2_USER_INFO_ERROR(402, "获取用户信息失败"),

    /**
     * OAuth2用户不存在
     */
    OAUTH2_USER_NOT_FOUND(403, "用户不存在，请联系管理员添加");

    private final Integer code;
    private final String msg;

    ResponseCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
