package top.zhengru.unipush.api.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import top.zhengru.unipush.api.model.vo.LoginUserVO;
import top.zhengru.unipush.api.service.OAuth2Service;
import top.zhengru.unipush.api.service.UserService;
import top.zhengru.unipush.api.util.JwtUtils;
import top.zhengru.unipush.common.model.dto.OAuth2UserInfo;
import top.zhengru.unipush.common.model.entity.SysUser;
import top.zhengru.unipush.common.model.vo.ResponseVO;
import top.zhengru.unipush.common.util.SnowflakeIdUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2控制器
 *
 * @author zhengru
 */
@RestController
@RequestMapping("/api/web/oauth2")
public class OAuth2Controller {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);

    @Autowired
    private OAuth2Service oAuth2Service;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 获取OAuth2授权地址
     *
     * @return 授权地址
     */
    @GetMapping("/authorize")
    public ResponseVO<Map<String, String>> getAuthorizeUrl() {
        // 生成随机state，防止CSRF攻击
        String state = SnowflakeIdUtils.getInstance().nextIdStr();
        String authorizeUrl = oAuth2Service.getAuthorizationUrl(state);

        Map<String, String> result = new HashMap<>();
        result.put("authorizeUrl", authorizeUrl);
        result.put("state", state);

        return ResponseVO.ok(result);
    }

    /**
     * OAuth2回调
     *
     * @param code  授权码
     * @param state 状态
     * @return 重定向到前端
     */
    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("code") String code,
                                 @RequestParam("state") String state,
                                 HttpServletRequest request) {
        try {
            logger.info("OAuth2回调: code={}, state={}", code, state);

            // 1. 通过code换取access_token
            String accessToken = oAuth2Service.getToken(code).getAccessToken();
            logger.info("获取access_token成功");

            // 2. 获取用户信息
            OAuth2UserInfo userInfo = oAuth2Service.getUserInfo(accessToken);
            logger.info("获取用户信息成功: phone={}", userInfo.getPhoneNumber());

            // 3. 通过手机号关联用户
            SysUser user = oAuth2Service.loginWithOAuth2(userInfo);
            logger.info("用户登录成功: userId={}", user.getId());

            // 4. 生成本地JWT Token
            String token = jwtUtils.generateToken(user.getId(), user.getUsername());
            logger.info("生成JWT Token成功");

            // 5. 更新最后登录信息
            String ip = getClientIp(request);
            userService.updateLastLoginInfo(user.getId(), ip);

            // 6. 重定向到前端并携带Token
            String frontendUrl = "http://localhost:3000/login?token=" + token;
            return new RedirectView(frontendUrl);

        } catch (Exception e) {
            logger.error("OAuth2登录失败", e);
            String frontendUrl = "http://localhost:3000/login?error=" + e.getMessage();
            return new RedirectView(frontendUrl);
        }
    }

    /**
     * 获取当前登录用户信息
     *
     * @param token JWT Token
     * @return 用户信息
     */
    @GetMapping("/userinfo")
    public ResponseVO<LoginUserVO> getUserInfo(@RequestParam("token") String token) {
        try {
            // 验证Token
            if (!jwtUtils.validateToken(token)) {
                return ResponseVO.fail("Token无效或已过期");
            }

            // 从Token中获取用户信息
            Long userId = jwtUtils.getUserIdFromToken(token);
            String username = jwtUtils.getUsernameFromToken(token);

            // TODO: 调用UserService获取完整用户信息
            LoginUserVO loginUser = new LoginUserVO();
            loginUser.setId(userId);
            loginUser.setUsername(username);
            loginUser.setToken(token);

            return ResponseVO.ok(loginUser);
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return ResponseVO.fail("获取用户信息失败");
        }
    }

    /**
     * 获取客户端IP
     *
     * @param request HTTP请求
     * @return 客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
