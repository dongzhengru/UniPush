package top.zhengru.unipush.common.constant;

/**
 * Redis常量定义
 *
 * @author zhengru
 */
public class RedisConstants {

    /**
     * 登录用户 Redis Key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 Redis Key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 Redis Key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 防重提交 Redis Key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 Redis Key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 Redis Key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 访问令牌 Redis Key
     */
    public static final String ACCESS_TOKEN_KEY = "access_token:";

    /**
     * 推送消息 Redis Key
     * 格式: push:message:{messageId}
     * 过期时间: 7天
     */
    public static final String PUSH_MESSAGE_KEY = "push:message:";

    /**
     * 推送消息批量写入缓存 Key（Sorted Set）
     * 用于定时任务批量迁移到数据库
     */
    public static final String PUSH_MESSAGE_BATCH_KEY = "push:message:batch";
}
