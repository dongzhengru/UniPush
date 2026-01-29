package top.zhengru.unipush.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * OAuth2令牌响应DTO
 *
 * @author zhengru
 */
@Data
public class OAuth2TokenResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 令牌类型
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * 过期时间（秒）
     */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /**
     * 刷新令牌
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * 授权范围
     */
    private String scope;
}
