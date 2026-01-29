package top.zhengru.unipush.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import top.zhengru.unipush.api.config.OAuth2Properties;
import top.zhengru.unipush.api.model.dto.OAuth2TokenResponse;
import top.zhengru.unipush.common.model.dto.OAuth2UserInfo;
import top.zhengru.unipush.common.model.entity.SysUser;
import top.zhengru.unipush.common.util.JsonUtils;

import java.time.LocalDateTime;

/**
 * OAuth2服务
 *
 * @author zhengru
 */
@Service
public class OAuth2Service {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Service.class);

    @Autowired
    private OAuth2Properties oAuth2Properties;

    @Autowired
    private UserService userService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    /**
     * 获取授权URL
     *
     * @param state 状态
     * @return 授权URL
     */
    public String getAuthorizationUrl(String state) {
        return String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                oAuth2Properties.getAuthorizationUri(),
                oAuth2Properties.getClientId(),
                oAuth2Properties.getRedirectUri(),
                oAuth2Properties.getScope(),
                state);
    }

    /**
     * 通过授权码获取访问令牌
     *
     * @param code 授权码
     * @return 访问令牌响应
     */
    public OAuth2TokenResponse getToken(String code) {
        try {
            WebClient client = webClientBuilder.build();

            String response = client.post()
                    .uri(oAuth2Properties.getTokenUri())
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue(String.format(
                            "grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
                            code,
                            oAuth2Properties.getRedirectUri(),
                            oAuth2Properties.getClientId(),
                            oAuth2Properties.getClientSecret()
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return JsonUtils.parseObject(response, OAuth2TokenResponse.class);
        } catch (Exception e) {
            logger.error("获取访问令牌失败", e);
            throw new RuntimeException("获取访问令牌失败: " + e.getMessage());
        }
    }

    /**
     * 通过访问令牌获取用户信息
     *
     * @param accessToken 访问令牌
     * @return 用户信息
     */
    public OAuth2UserInfo getUserInfo(String accessToken) {
        try {
            WebClient client = webClientBuilder.build();

            String response = client.get()
                    .uri(oAuth2Properties.getUserInfoUri())
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return JsonUtils.parseObject(response, OAuth2UserInfo.class);
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 通过OAuth2用户信息登录
     *
     * @param userInfo OAuth2用户信息
     * @return 系统用户
     */
    public SysUser loginWithOAuth2(OAuth2UserInfo userInfo) {
        // 通过手机号查找用户
        SysUser user = userService.getUserByPhone(userInfo.getPhoneNumber());

        if (user == null) {
            // 用户不存在，抛出异常
            logger.warn("用户不存在: phone={}", userInfo.getPhoneNumber());
            throw new RuntimeException("用户不存在，请联系管理员添加");
        }

        // 用户已存在，更新用户信息
        user.setNickname(userInfo.getNickname() != null ? userInfo.getNickname() : userInfo.getName());
        user.setAvatar(userInfo.getPicture());
        user.setEmail(userInfo.getEmail());

        // 更新角色
        if (Boolean.TRUE.equals(userInfo.getIsSuperAdmin())) {
            user.setRole("ADMIN");
        } else if (Boolean.TRUE.equals(userInfo.getIsAdmin())) {
            user.setRole("ADMIN");
        }

        userService.updateUser(user);
        logger.info("更新用户信息: {}", user.getPhone());

        return user;
    }
}
