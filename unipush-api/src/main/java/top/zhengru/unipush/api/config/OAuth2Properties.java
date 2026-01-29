package top.zhengru.unipush.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OAuth2配置属性
 *
 * @author zhengru
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth2.client")
public class OAuth2Properties {

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 授权地址
     */
    private String authorizationUri = "https://accounts.zhengru.top/oauth2/authorize";

    /**
     * 令牌地址
     */
    private String tokenUri = "https://accounts.zhengru.top/oauth2/token";

    /**
     * 用户信息地址
     */
    private String userInfoUri = "https://accounts.zhengru.top/oauth2/userinfo";

    /**
     * 回调地址
     */
    private String redirectUri = "http://localhost:8080/api/web/oauth2/callback";

    /**
     * 授权范围
     */
    private String scope = "openid profile phone";

    /**
     * JWT密钥
     */
    private String jwtSecret = "unipush-jwt-secret-key-2024";

    /**
     * JWT过期时间（秒）
     */
    private Long jwtExpiration = 604800L; // 7天
}
